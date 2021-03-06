package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.Customer;
import com.ronja.crm.ronjaclient.service.util.ClientApiUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class CustomerWebClient {

    private final WebClient webClient;

    public CustomerWebClient(String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<Customer[]> fetchAllCustomers() {
        return ClientApiUtils.fetchEntities(webClient, Customer[].class);
    }

    public Mono<Customer> updateCustomer(Customer customer) {
        return ClientApiUtils.putEntity(webClient, customer, Customer.class);
    }

    public Mono<Customer> createCustomer(Customer customer) {
        return ClientApiUtils.postEntity(webClient, customer, Customer.class);
    }

    public Mono<Void> deleteCustomer(int id) {
        return ClientApiUtils.deleteEntity(webClient, id);
    }
}
