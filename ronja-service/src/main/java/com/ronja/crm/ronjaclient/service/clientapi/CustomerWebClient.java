package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.Customer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
        return webClient.get()
                .uri("/list")
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> response.bodyToMono(Throwable.class))
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    Mono<String> errorMsg = response.bodyToMono(String.class);
                    return errorMsg.flatMap(msg -> {
                        throw new FetchException(msg);
                    });
                })
                .bodyToMono(Customer[].class);
    }

    public Mono<Customer> createCustomer(Customer customer) {
        return webClient.post()
                .uri("/add")
                .body(Mono.just(customer), Customer.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> response.bodyToMono(Throwable.class))
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    Mono<String> errorMsg = response.bodyToMono(String.class);
                    return errorMsg.flatMap(msg -> {
                        throw new SaveException(msg);
                    });
                })
                .bodyToMono(Customer.class);
    }

    public Mono<Customer> updateCustomer(Customer customer) {
        return webClient.post()
                .uri("/update")
                .body(Mono.just(customer), Customer.class)
                .retrieve()
                .bodyToMono(Customer.class);
    }

    public Mono<Void> deleteCustomer(int id) {
        return webClient.delete()
                .uri("/delete/" + id)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
