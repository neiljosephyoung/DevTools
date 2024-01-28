package com.example.goodbodytools;

import atlantafx.base.theme.*;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.goodbodytools.MainApp.LOGGER;

public class DataUtils {

    private static final Map<String,String> fontsAvailable = new LinkedHashMap<>();
    public static Map<String,String> getFontsAvailable(){
        fontsAvailable.put(new CupertinoDark().getName(),new CupertinoDark().getUserAgentStylesheet());
        fontsAvailable.put(new CupertinoLight().getName(),new CupertinoLight().getUserAgentStylesheet());

        fontsAvailable.put(new PrimerLight().getName(),new PrimerLight().getUserAgentStylesheet());
        fontsAvailable.put(new PrimerDark().getName(),new PrimerDark().getUserAgentStylesheet());

        fontsAvailable.put(new NordLight().getName(),new NordLight().getUserAgentStylesheet());
        fontsAvailable.put(new NordDark().getName(),new NordDark().getUserAgentStylesheet());

        fontsAvailable.put(new Dracula().getName(),new Dracula().getUserAgentStylesheet());

        return fontsAvailable;
    }
    public static String parseXmlToPretty(String rawText) {
        try {
            //rawText = rawText.replaceFirst("\n", "");
            return printXml(parseStringToXmlDocument(rawText));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Document parseStringToXmlDocument(String xmlString) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlString));
        return builder.parse(is);
    }

    private static boolean isXmlFormatted(Document document) {
        // Check if the document has indentation in its content
        var xmlString = documentToString(document);
        System.out.println("var: "+xmlString);
        return xmlString.contains("\n") || xmlString.contains("\r") || xmlString.contains("\t");
    }
    private static String documentToString(Document document) {
        // Convert the Document to String
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            return writer.toString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
    private static String printXml(Document document) throws Exception {
        // Use a transformer to print the XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Print the XML content to the console
        DOMSource source = new DOMSource(document);
        System.out.println("dom: "+source);
        StreamResult result = new StreamResult(new java.io.StringWriter());
        if (!isXmlFormatted(document)) {
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
        } else {
            return documentToString(document);
        }

        return result.getWriter().toString();
    }

    public static String parseJsonToPretty(String rawJson) {
        String parsedData = "";
        try{
            JSONObject obj = new JSONObject(rawJson);
            System.out.println(obj.toString(4));
            parsedData = obj.toString(4);

            return parsedData;
        }catch (Exception e){
            try {
                // Try parsing as a JSON array
                JSONArray arr = new JSONArray(rawJson);
                parsedData = arr.toString(4);

                return parsedData;
            } catch (JSONException e2) {
                return parsedData;
            }
        }
    }

    public static boolean isJSON(String data) {
        try {
            // Try parsing as a JSON object
            new JSONObject(data);
            return true;
        } catch (JSONException e1) {
            try {
                // Try parsing as a JSON array
                new JSONArray(data);
                return true;
            } catch (JSONException e2) {
                return false;
            }
        }
    }

    public static boolean isXML(String data) {
        // Sanitize the input data to remove null bytes and trim extra spaces
        String sanitizedData = data.replaceAll("\\x00", "").replaceAll("\n","").trim();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(sanitizedData)));
            return true; // Parsing successful, the string is a valid XML
        } catch (Exception e) {
            return false; // Parsing failed, the string is not a valid XML
        }
    }

public static String toggleCase(String data){
        // Handle Ctrl + Shift + U shortcut here
        System.out.println("Ctrl + Shift + U pressed");

        System.out.println("data: "+data);
        if (StringUtils.isAllUpperCase(data.replaceAll("[\\d\\p{Punct}\\s]",""))){
            //already up do gown
            data = data.toLowerCase();

            System.out.println("in lower if: "+data);
        }else if (StringUtils.isAllLowerCase(data.replaceAll("[\\d\\p{Punct}\\s]",""))){
            //already up do gown
            data = data.toUpperCase();

            System.out.println("in upper if: "+data);
        }else{
            //just set it all to upper
            data = data.toUpperCase();

        }

        System.out.println("out if: "+data);
        return data;
    }

    public static String minifyText(String rawJson) {
        try{
            String minify = rawJson.replaceAll("\n","").replaceAll("\r","").replaceAll("\t","").replaceAll(" {4}", "").trim();
            System.out.println(minify);
            return minify;
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
        return ""; 
    }

    public static String removeEntriesWithNilValue(String data) throws Exception {
        System.out.println("data: "+data);
        Document document = parseStringToXmlDocument(data);
        NodeList nodeList = document.getElementsByTagName("*");
        System.out.println(document);

        for (int i = nodeList.getLength() - 1; i >= 0; i--) {
            Element element = (Element) nodeList.item(i);
            String value = element.getAttribute("value");

            if ("Nil".equals(value)) {
                System.out.println("removing");
                Node parent = element.getParentNode();
                parent.removeChild(element);
            }
        }
        return documentToString(document);
    }
}
