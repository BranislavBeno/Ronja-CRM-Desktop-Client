module ronja.desktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;

    exports com.ronja.crm.ronjaclient.desktop;
    exports com.ronja.crm.ronjaclient.desktop.controller;

    opens com.ronja.crm.ronjaclient.desktop;
}