module ronja.web {
  requires com.fasterxml.jackson.databind;
  requires spring.context;
  requires spring.web;
  requires spring.boot;
  requires spring.beans;
  requires spring.core;

  exports com.ronja.crm.ronjaclient.web;
  exports com.ronja.crm.ronjaclient.web.domain;
}