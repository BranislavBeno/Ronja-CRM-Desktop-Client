package com.ronja.crm.ronjaclient.web;

import com.ronja.crm.ronjaclient.web.domain.Customer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomerApiClient {

  private static final String URL = "http://localhost:8080/customers";

  private final RestTemplate restTemplate;

  public CustomerApiClient() {
    restTemplate = new RestTemplate();
  }

  public Customer[] fetchCustomers() {
    return restTemplate.getForEntity(URL + "/list", Customer[].class).getBody();
  }
}
