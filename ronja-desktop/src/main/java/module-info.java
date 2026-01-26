module ronja.desktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.jspecify;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires spring.web;
    requires ronja.service;

    exports com.ronja.crm.ronjaclient.desktop;
    exports com.ronja.crm.ronjaclient.desktop.controller;
    exports com.ronja.crm.ronjaclient.desktop.component.common;
    exports com.ronja.crm.ronjaclient.desktop.component.customer;
    exports com.ronja.crm.ronjaclient.desktop.component.dashboard;
    exports com.ronja.crm.ronjaclient.desktop.component.representative;
    exports com.ronja.crm.ronjaclient.desktop.component.splashscreen;

    opens com.ronja.crm.ronjaclient.desktop;
    opens com.ronja.crm.ronjaclient.desktop.controller;
    opens com.ronja.crm.ronjaclient.desktop.component.common;
    opens com.ronja.crm.ronjaclient.desktop.component.configuration;
    opens com.ronja.crm.ronjaclient.desktop.component.customer;
    opens com.ronja.crm.ronjaclient.desktop.component.representative;
}