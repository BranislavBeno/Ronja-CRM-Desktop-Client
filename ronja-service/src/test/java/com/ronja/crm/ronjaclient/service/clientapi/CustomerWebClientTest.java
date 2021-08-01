package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Status;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CustomerWebClientTest {

  private static final String LIST_RESPONSE = """
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

  private static final String SINGLE_RESPONSE = """
      {
         "id": 0,
         "companyName": "TestCompany",
         "category": "LEVEL_1",
         "focus": "BUILDER",
         "status": "ACTIVE"
      }""";

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
  public void testFetchingCustomerList() {
    mockResponse(LIST_RESPONSE);

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
  public void testExceptionsOnFetchingCustomerList() {
    assertAll(() -> {
      assertThatThrownBy(() -> fetchCustomers(400)).isInstanceOf(RuntimeException.class);
      assertThatThrownBy(() -> fetchCustomers(500)).isExactlyInstanceOf(FetchException.class);
    });
  }

  @Test
  public void testCreatingCustomer() {
    mockResponse(SINGLE_RESPONSE);

    Customer customer = provideCustomer();
    Customer newCustomer = customerWebClient.createCustomer(customer).block();

    assertAll(() -> {
      assertThat(newCustomer).isNotNull();
      assertThat(newCustomer).hasToString(customer.toString());
    });
  }

  @NotNull
  private Customer provideCustomer() {
    Customer customer = new Customer();
    customer.setCompanyName("TestCompany");
    customer.setCategory(Category.LEVEL_1);
    customer.setFocus(Focus.BUILDER);
    customer.setStatus(Status.ACTIVE);
    return customer;
  }

  @Test
  public void testExceptionsOnCreatingCustomer() {
    assertAll(() -> {
      assertThatThrownBy(() -> propagateExceptionWith500ServerError(
          () -> customerWebClient.createCustomer(new Customer()).block()))
          .isExactlyInstanceOf(SaveException.class);
      assertThatThrownBy(() -> propagateExceptionWith400ServerError(
          () -> customerWebClient.createCustomer(new Customer()).block()))
          .isInstanceOf(RuntimeException.class);
    });
  }

  @Test
  public void testUpdatingCustomer() {
    mockResponse(SINGLE_RESPONSE);
    Customer updatedCustomer = customerWebClient.updateCustomer(new Customer()).block();
    assertThat(updatedCustomer).isNotNull();
  }

  private void propagateExceptionWith400ServerError(Supplier<Customer> supplier) {
    provideResponse(400, "Error occurred.");
    supplier.get();
  }

  private void propagateExceptionWith500ServerError(Supplier<Customer> supplier) {
    provideResponse(500, "System is down.");
    supplier.get();
  }

  private void fetchCustomers(int i) {
    provideResponse(i, "Error occurred.");
    customerWebClient.fetchAllCustomers().block();
  }

  private void provideResponse(int i, String s) {
    this.mockWebServer.enqueue(new MockResponse()
        .setResponseCode(i)
        .setBody(s));
  }

  private void mockResponse(String listResponse) {
    MockResponse mockResponse = new MockResponse()
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .setBody(listResponse);
    this.mockWebServer.enqueue(mockResponse);
  }
}