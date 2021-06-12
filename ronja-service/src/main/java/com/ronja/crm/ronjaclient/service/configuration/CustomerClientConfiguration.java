package com.ronja.crm.ronjaclient.service.configuration;

import com.ronja.crm.ronjaclient.service.clientapi.CustomerApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerClientConfiguration {

  @Bean
  public CustomerApiClient customerApiClient(
      @Value("${client.customers.base-url}") String baseUrl,
      RestTemplateBuilder restTemplateBuilder) {
    return new CustomerApiClient(baseUrl, restTemplateBuilder);
  }

  @Bean
  @ConditionalOnMissingBean
  public RestTemplateBuilder restTemplateBuilder() {
    return new RestTemplateBuilder();
  }
}
