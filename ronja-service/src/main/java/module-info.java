module ronja.web {
    requires com.fasterxml.jackson.databind;
    requires reactor.core;
    requires spring.context;
    requires spring.webflux;
    requires spring.web;

    exports com.ronja.crm.ronjaclient.web;
    exports com.ronja.crm.ronjaclient.web.domain;
}