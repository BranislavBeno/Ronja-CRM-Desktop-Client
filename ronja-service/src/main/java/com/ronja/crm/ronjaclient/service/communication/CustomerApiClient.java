package com.ronja.crm.ronjaclient.service.communication;

import com.ronja.crm.ronjaclient.service.domain.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomerApiClient {

  private static final String URL = "http://localhost:8080/customers";

  private final RestTemplate restTemplate;

  public CustomerApiClient() {
    restTemplate = new RestTemplate();
  }

  public Customer[] fetchAllCustomers() {
    ResponseEntity<Customer[]> response = restTemplate.getForEntity(URL + "/list", Customer[].class);
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new FetchException("Fetching customer's list failed.");
    }
    return response.getBody();
  }

  public HttpStatus createCustomer(Customer customer) {
    ResponseEntity<Customer> response = restTemplate.postForEntity(URL + "/add", customer, Customer.class);
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new SaveException("Saving new customer failed.");
    }
    return response.getStatusCode();
  }

  public HttpStatus updateCustomer(Customer customer) {
    ResponseEntity<Customer> response = restTemplate.postForEntity(URL + "/update", customer, Customer.class);
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new SaveException("Updating existing customer failed.");
    }
    return response.getStatusCode();
  }

  public void deleteCustomer(int id) {
    restTemplate.delete(URL + "/delete/" + id);
  }
}
