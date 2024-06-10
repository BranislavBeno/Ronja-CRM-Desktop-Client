package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Status;
import com.ronja.crm.ronjaclient.service.validation.DeleteException;
import com.ronja.crm.ronjaclient.service.validation.FetchException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.springframework.web.server.ServerErrorException;

import java.io.IOException;
import java.util.function.Supplier;

class CustomerWebClientTest implements WithAssertions {

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
    void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        this.customerWebClient = new CustomerWebClient(mockWebServer.url("/").toString());
    }

    @AfterEach
    void shutdown() throws IOException {
        this.mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Customer list fetching: happy path test")
    void testCustomerListFetching() {
        mockResponse(LIST_RESPONSE);

        Customer[] customers = customerWebClient.fetchAllCustomers().block();
        assertThat(customers).isNotNull().satisfies(cs -> {
            assertThat(cs).hasSize(2);

            Customer customer = cs[0];
            assertThat(customer).isNotNull().satisfies(c ->
                    Assertions.assertAll(() -> {
                        assertThat(c.getId()).isEqualTo(2);
                        assertThat(c.getCompanyName()).isEqualTo("EmmaCorp");
                        assertThat(c.getCategory()).isEqualTo(Category.LEVEL_2);
                        assertThat(c.getFocus()).isEqualTo(Focus.MANUFACTURE);
                        assertThat(c.getStatus()).isEqualTo(Status.INACTIVE);
                    })
            );
        });
    }

    @Test
    @DisplayName("Customer creating: happy path test")
    void testCustomerCreating() {
        mockResponse(SINGLE_RESPONSE);

        Customer customer = provideCustomer();
        Customer newCustomer = customerWebClient.createCustomer(customer).block();

        Assertions.assertAll(() -> {
            assertThat(newCustomer).isNotNull();
            assertThat(newCustomer).hasToString(customer.toString());
        });
    }

    @Test
    @DisplayName("Customer updating: happy path test")
    void testCustomerUpdating() {
        mockResponse(SINGLE_RESPONSE);
        Customer updatedCustomer = customerWebClient.updateCustomer(new Customer()).block();
        assertThat(updatedCustomer).isNotNull();
    }

    @Test
    @DisplayName("Customer deleting: happy path test")
    void testCustomerDeleting() {
        mockResponse();
        assertThatCode(() -> customerWebClient.deleteCustomer(0).block()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Customer data handling: failure test")
    void testExceptionsOnCustomerDataHandling() {
        Assertions.assertAll(() -> {
            // customer list fetching failures
            assertThatThrownBy(this::fetchCustomers).isExactlyInstanceOf(FetchException.class);
            // customer creating failures
            assertThatThrownBy(() -> propagateExceptionWith400ServerError(
                    () -> customerWebClient.createCustomer(new Customer()).block()))
                    .isExactlyInstanceOf(ServerErrorException.class);
            // customer updating failures
            assertThatThrownBy(() -> propagateExceptionWith400ServerError(
                    () -> customerWebClient.updateCustomer(new Customer()).block()))
                    .isExactlyInstanceOf(ServerErrorException.class);
            // customer deleting failures
            assertThatThrownBy(this::deleteCustomer).isExactlyInstanceOf(DeleteException.class);
        });
    }

    private void propagateExceptionWith400ServerError(Supplier<Customer> supplier) {
        provideResponse();
        supplier.get();
    }

    private void fetchCustomers() {
        provideResponse();
        customerWebClient.fetchAllCustomers().block();
    }

    private void deleteCustomer() {
        provideResponse();
        customerWebClient.deleteCustomer(0).block();
    }

    private void provideResponse() {
        this.mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("Error occurred."));
    }

    private void mockResponse(String listResponse) {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(listResponse);
        this.mockWebServer.enqueue(mockResponse);
    }

    private void mockResponse() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8");
        this.mockWebServer.enqueue(mockResponse);
    }

    private Customer provideCustomer() {
        Customer customer = new Customer();
        customer.setCompanyName("TestCompany");
        customer.setCategory(Category.LEVEL_1);
        customer.setFocus(Focus.BUILDER);
        customer.setStatus(Status.ACTIVE);
        return customer;
    }
}