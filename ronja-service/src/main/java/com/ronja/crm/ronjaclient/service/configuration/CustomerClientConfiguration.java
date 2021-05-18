package com.ronja.crm.ronjaclient.service.configuration;

import com.ronja.crm.ronjaclient.service.communication.CustomerApiClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerClientConfiguration {

  @Bean
  public CustomerApiClient customerApiClient(RestTemplateBuilder restTemplateBuilder) {
    return new CustomerApiClient(restTemplateBuilder);
  }

  @Bean
  @ConditionalOnMissingBean
  public RestTemplateBuilder restTemplateBuilder() {
    return new RestTemplateBuilder();
  }
}
