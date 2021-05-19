module ronja.service {
  requires com.fasterxml.jackson.databind;
  requires spring.context;
  requires spring.web;
  requires spring.boot;
  requires spring.beans;
  requires spring.core;
  requires spring.boot.autoconfigure;

  exports com.ronja.crm.ronjaclient.service.domain;
  exports com.ronja.crm.ronjaclient.service.communication;

  opens com.ronja.crm.ronjaclient.service.configuration;
}