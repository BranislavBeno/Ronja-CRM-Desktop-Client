package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Status;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerWebClientIT extends BasicWebClientIT {

    private CustomerWebClient customerWebClient;

    @BeforeEach
    void setUp() {
        String url = "http://%s:%d/customers".formatted(RONJA_SERVER.getHost(), RONJA_SERVER.getMappedPort(8087));
        customerWebClient = new CustomerWebClient(url);
    }

    @Test
    @Order(1)
    @DisplayName("Test-containers: Test whether new customer is created successfully")
    void testAddNewCustomer() {
        Customer customer = provideCustomer().block();

        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isNotZero();
    }

    @Test
    @Order(2)
    @DisplayName("Test-containers: Test whether customers are fetched successfully")
    void testFetchCustomer() {
        Customer[] customers = customerWebClient.fetchAllCustomers().block();
        assertThat(customers).isNotEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("Test-containers: Test whether existing customer is updated successfully")
    void testUpdateCustomer() {
        Customer[] customers = customerWebClient.fetchAllCustomers().block();
        Customer customer = Objects.requireNonNull(customers)[0];
        assertThat(customer.getCategory()).isEqualTo(Category.LEVEL_1);

        customer.setCategory(Category.LEVEL_3);
        Customer updatedCustomer = customerWebClient.updateCustomer(customer).block();
        assertThat(Objects.requireNonNull(updatedCustomer).getCategory()).isEqualTo(Category.LEVEL_3);
    }

    @Test
    @Order(4)
    @DisplayName("Test-containers: Test whether customer is deleted successfully")
    void testDeleteCustomer() {
        Customer[] customers = customerWebClient.fetchAllCustomers().block();
        assertThat(customers).hasSize(1);

        customerWebClient.deleteCustomer(1).block();
        customers = customerWebClient.fetchAllCustomers().block();
        assertThat(customers).isEmpty();
    }

    private Mono<Customer> provideCustomer() {
        Customer customer = new Customer();
        customer.setCompanyName("test");
        customer.setCategory(Category.LEVEL_1);
        customer.setFocus(Focus.BUILDER);
        customer.setStatus(Status.ACTIVE);

        return Objects.requireNonNull(customerWebClient.createCustomer(customer));
    }
}
