package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.Customer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class CustomerWebClient {

  public static final String SERVER_ERROR_OCCURRED = "Server error occurred";
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
        .onStatus(HttpStatus::is4xxClientError, this::handleClientError)
        .onStatus(HttpStatus::is5xxServerError, this::handleFetchingError)
        .bodyToMono(Customer[].class);
  }

  public Mono<Customer> createCustomer(Customer customer) {
    return webClient.post()
        .uri("/add")
        .body(Mono.just(customer), Customer.class)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, this::handleClientError)
        .onStatus(HttpStatus::is5xxServerError, this::handleSavingError)
        .bodyToMono(Customer.class);
  }

  public Mono<Customer> updateCustomer(Customer customer) {
    return webClient.post()
        .uri("/update")
        .body(Mono.just(customer), Customer.class)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, this::handleClientError)
        .onStatus(HttpStatus::is5xxServerError, this::handleSavingError)
        .bodyToMono(Customer.class);
  }

  public Mono<Void> deleteCustomer(int id) {
    return webClient.delete()
        .uri("/delete/" + id)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, this::handleClientError)
        .onStatus(HttpStatus::is5xxServerError, this::handleDeletingError)
        .bodyToMono(Void.class);
  }

  private Mono<Throwable> handleClientError(ClientResponse response) {
    return response.bodyToMono(Throwable.class);
  }

  private Mono<Throwable> handleDeletingError(ClientResponse response) {
    Mono<String> errorMsg = response.bodyToMono(String.class);
    return errorMsg.flatMap(msg -> {
      throw new DeleteException(SERVER_ERROR_OCCURRED);
    });
  }

  private Mono<Throwable> handleSavingError(ClientResponse response) {
    Mono<String> errorMsg = response.bodyToMono(String.class);
    return errorMsg.flatMap(msg -> {
      throw new SaveException(SERVER_ERROR_OCCURRED);
    });
  }

  private Mono<Throwable> handleFetchingError(ClientResponse response) {
    Mono<String> errorMsg = response.bodyToMono(String.class);
    return errorMsg.flatMap(msg -> {
      throw new FetchException(SERVER_ERROR_OCCURRED);
    });
  }
}
