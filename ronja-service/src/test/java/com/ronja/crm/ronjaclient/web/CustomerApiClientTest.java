package com.ronja.crm.ronjaclient.web;

import com.ronja.crm.ronjaclient.web.domain.Customer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerApiClientTest {

  private static CustomerApiClient customerApiClient;

  @BeforeAll
  static void setUp() {
    customerApiClient = new CustomerApiClient();
  }

  @Test
  void testSuccessfulCommunication() {
    Customer[] customers = customerApiClient.fetchCustomers();
    assertThat(customers).hasSize(5);
  }
}