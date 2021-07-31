package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Status;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomerWebClientTest {

  private static final String VALID_RESPONSE = """
      [
       {
          "id": 2,
          "companyName": "EmmaCorp",
          "category": "LEVEL_2",
          "focus": "MANUFACTURE",
          "status": "INACTIVE"
       },
       {
          "id": 1,
          "companyName": "LeslieCorp",
          "category": "LEVEL_1",
          "focus": "MANUFACTURE",
          "status": "ACTIVE"
       }
      ]""";

  private MockWebServer mockWebServer;

  private CustomerWebClient customerWebClient;

  @BeforeEach
  public void setUp() throws IOException {
    this.mockWebServer = new MockWebServer();
    this.mockWebServer.start();
    this.customerWebClient = new CustomerWebClient(mockWebServer.url("/").toString());
  }

  @AfterEach
  public void shutdown() throws IOException {
    this.mockWebServer.shutdown();
  }

  @Test
  public void testAvailability() {
    assertThat(customerWebClient).isNotNull();
    assertThat(mockWebServer).isNotNull();
  }

  @Test
  public void testFetchingCustomerList() {
    MockResponse mockResponse = new MockResponse()
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .setBody(VALID_RESPONSE);

    this.mockWebServer.enqueue(mockResponse);

    Customer[] customers = customerWebClient.fetchAllCustomers().block();
    assertAll(() -> {
      assertThat(customers).isNotNull();
      assertThat(customers).hasSize(2);
    });

    Customer customer = customers[0];
    assertAll(() -> {
      assertThat(customer.getId()).isEqualTo(2);
      assertThat(customer.getCompanyName()).isEqualTo("EmmaCorp");
      assertThat(customer.getCategory()).isEqualTo(Category.LEVEL_2);
      assertThat(customer.getFocus()).isEqualTo(Focus.MANUFACTURE);
      assertThat(customer.getStatus()).isEqualTo(Status.INACTIVE);
    });
  }

  @Test
  public void testExceptionPropagationWhenRemoteSystemIsDown() {
    assertThrows(RuntimeException.class, this::propagateException);
  }

  private void propagateException() {
    this.mockWebServer.enqueue(new MockResponse()
        .setResponseCode(500)
        .setBody("Sorry, system is down :("));
    customerWebClient.fetchAllCustomers().block();
  }
}