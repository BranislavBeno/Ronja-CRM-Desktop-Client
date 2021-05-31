module ronja.desktop {
  requires javafx.controls;
  requires javafx.fxml;
  requires spring.beans;
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.core;
  requires spring.web;
  requires ronja.service;

  exports com.ronja.crm.ronjaclient.desktop;
  exports com.ronja.crm.ronjaclient.desktop.controller;
  exports com.ronja.crm.ronjaclient.desktop.component.customer;

  opens com.ronja.crm.ronjaclient.desktop;
  opens com.ronja.crm.ronjaclient.desktop.controller;
  opens com.ronja.crm.ronjaclient.desktop.component.customer;
}