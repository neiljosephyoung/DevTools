package com.example.goodbodytools.datastore;


public class TempData {

    private StringBuilder jsonBuilder;
    private StringBuilder xmlBuilder;

    public TempData() {
        jsonBuilder = new StringBuilder();
        xmlBuilder = new StringBuilder();
    }

    public String getXmlText() {
        return xmlBuilder.toString();
    }

    public void setXmlText(String xmlText) {
        xmlBuilder.setLength(0);
        this.xmlBuilder.append(xmlText) ;
    }

    public String getJsonText() {
        return jsonBuilder.toString();
    }

    public void setJsonText(String jsonText) {
        jsonBuilder.setLength(0);
        this.jsonBuilder.append(jsonText);
    }
}
