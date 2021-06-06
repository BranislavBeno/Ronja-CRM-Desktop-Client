package com.ronja.crm.ronjaclient.service.clientapi;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CustomerTestConfiguration {

  @Bean
  public RestTemplateBuilder restTemplateBuilder() {
    return new RestTemplateBuilder();
  }
}
