package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.Customer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

public class CustomerApiClient {

  private final RestTemplate restTemplate;

  public CustomerApiClient(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder
        .rootUri("http://localhost:8087/customers")
        .setConnectTimeout(Duration.ofSeconds(2))
        .setReadTimeout(Duration.ofSeconds(2))
        .build();
  }

  public Customer[] fetchAllCustomers() {
    ResponseEntity<Customer[]> response = restTemplate.getForEntity("/list", Customer[].class);
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new FetchException("Fetching customer's list failed.");
    }
    return response.getBody();
  }

  public HttpStatus createCustomer(Customer customer) {
    ResponseEntity<Customer> response = restTemplate.postForEntity("/add", customer, Customer.class);
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new SaveException("Saving new customer failed.");
    }
    return response.getStatusCode();
  }

  public HttpStatus updateCustomer(Customer customer) {
    ResponseEntity<Customer> response = restTemplate.postForEntity("/update", customer, Customer.class);
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new SaveException("Updating existing customer failed.");
    }
    return response.getStatusCode();
  }

  public void deleteCustomer(int id) {
    restTemplate.delete("/delete/" + id);
  }
}
