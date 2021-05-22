package com.ronja.crm.ronjaclient.service;

import com.ronja.crm.ronjaclient.service.communication.CustomerApiClient;
import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Status;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerApiClientTest {

    private static CustomerApiClient customerApiClient;

    @BeforeAll
    public static void setUp() {
        customerApiClient = new CustomerApiClient(new RestTemplateBuilder());
    }

    @Test
    @Disabled
    public void testSuccessfulFetching() {
        Customer[] customers = customerApiClient.fetchAllCustomers();
        assertThat(customers).hasSize(5);
    }

    @Test
    @Disabled
    public void testSuccessfulAdding() {
        Customer customer = new Customer();
        customer.setCompanyName("TestCorp");
        customer.setCategory(Category.LEVEL_3);
        customer.setFocus(Focus.TRADE);
        customer.setStatus(Status.INACTIVE);
        HttpStatus code = customerApiClient.createCustomer(customer);
        assertThat(code.is2xxSuccessful()).isTrue();
    }

    @Test
    @Disabled
    public void testSuccessfulUpdating() {
        Customer customer = new Customer();
        customer.setCompanyName("TestTestCorp");
        customer.setId(7);
        customer.setCategory(Category.LEVEL_1);
        customer.setFocus(Focus.BUILDER);
        customer.setStatus(Status.ACTIVE);
        HttpStatus code = customerApiClient.updateCustomer(customer);
        assertThat(code.is2xxSuccessful()).isTrue();
    }

    @Test
    @Disabled
    public void testSuccessfulDeletion() {
        Customer[] customers = customerApiClient.fetchAllCustomers();
        assertThat(customers).hasSize(6);
        customerApiClient.deleteCustomer(7);
        customers = customerApiClient.fetchAllCustomers();
        assertThat(customers).hasSize(5);
    }
}