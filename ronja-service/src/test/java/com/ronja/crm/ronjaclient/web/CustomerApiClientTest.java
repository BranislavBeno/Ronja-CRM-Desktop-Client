package com.ronja.crm.ronjaclient.web;

import com.ronja.crm.ronjaclient.web.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class CustomerApiClientTest {

    private final WebClient webClient = WebClient.builder().build();

    @Test
    void testSuccessfulCommunication() {
        CustomerApiClient customerApiClient = new CustomerApiClient(webClient);
        Customer customer = customerApiClient.fetchCustomerByName("Emma");
    }
}