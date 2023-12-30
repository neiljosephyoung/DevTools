package com.example.devtools;

import atlantafx.base.theme.*;
import javafx.css.*;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            //sanatise
            rawText = rawText.replaceFirst("\n","");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(rawText)));

            // Use Transformer to format the XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("indent", "yes");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new java.io.StringWriter());
            transformer.transform(source, result);


            return result.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String parseJsonToPretty(String rawJson) {
        String parsedData = "";
        try{
            JSONObject obj = new JSONObject(rawJson);
            System.out.println(obj.toString(5));
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


    public static String readFile(String fileName) {
        StringBuilder data = new StringBuilder();
        String path = "src/main/java/com/example/devtools/tempFiles/"+fileName;
        try (InputStream inputStream = new FileInputStream(path)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    data.append(line).append(System.lineSeparator()); // Append newline character after each line
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(data);
        return data.toString();
    }


    public static void writeFile(String fileName, String data) {

        String path = "src/main/java/com/example/devtools/tempFiles/"+fileName;
        System.out.println("path: "+path);
        try (OutputStream outputStream = new FileOutputStream(path);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            System.out.println("file path: " + path);
            System.out.println("writing data: " + data);
            writer.write(data);
            System.out.println("Data written successfully.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String minifyText(String rawJson) {
        try{
            String minify = rawJson.replaceAll("\n","").replaceAll("\r","").replaceAll("\t","").replaceAll("    ", "").trim();
            System.out.println(minify);
            return minify;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static void removeEntriesWithNilValue(String data) throws Exception {
        Document document = parseStringToXml(data);
        NodeList entryList = document.getElementsByTagName("entry");
        for (int i = entryList.getLength() - 1; i >= 0; i--) {
            Element entry = (Element) entryList.item(i);
            String value = entry.getAttribute("value");
            if ("Nil".equals(value)) {
                Node parent = entry.getParentNode();
                parent.removeChild(entry);
            }
        }
    }

    public static Document parseStringToXml(String xmlData) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(xmlData));
        return builder.parse(inputSource);
    }


}
