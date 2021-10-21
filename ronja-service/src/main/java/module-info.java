module ronja.service {
  requires com.fasterxml.jackson.databind;
  requires reactor.core;
  requires spring.context;
  requires spring.web;
  requires spring.webflux;
  requires spring.boot;
  requires spring.beans;
  requires spring.core;
  requires spring.boot.autoconfigure;

  exports com.ronja.crm.ronjaclient.service.clientapi;
  exports com.ronja.crm.ronjaclient.service.domain;
  exports com.ronja.crm.ronjaclient.service.util;
  exports com.ronja.crm.ronjaclient.service.dto;

  opens com.ronja.crm.ronjaclient.service.clientapi;
  opens com.ronja.crm.ronjaclient.service.configuration;
  opens com.ronja.crm.ronjaclient.service.domain;
}