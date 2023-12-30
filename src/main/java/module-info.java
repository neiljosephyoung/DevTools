module com.example.devtools {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.json;
    requires java.sql;
    requires kernel;
    requires layout;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires atlantafx.base;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.materialdesign2;
    requires java.desktop;
    requires org.kordamp.ikonli.material2;
    requires org.apache.commons.lang3;
    requires jdk.xml.dom;


    opens com.example.devtools to javafx.fxml;
    exports com.example.devtools;
}