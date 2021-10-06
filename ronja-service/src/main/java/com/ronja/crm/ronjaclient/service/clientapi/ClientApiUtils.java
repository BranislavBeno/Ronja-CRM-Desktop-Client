package com.ronja.crm.ronjaclient.service.clientapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ClientApiUtils {

  public static final String SERVER_ERROR_OCCURRED = "Server error occurred";

  private ClientApiUtils() {
  }

  static Mono<Throwable> handleEntityError(ClientResponse response) {
    return response.bodyToMono(Throwable.class);
  }

  static Mono<Throwable> handleDeletingError(ClientResponse response) {
    Mono<String> errorMsg = response.bodyToMono(String.class);
    return errorMsg.flatMap(msg -> {
      throw new DeleteException(SERVER_ERROR_OCCURRED);
    });
  }

  static Mono<Throwable> handleSavingError(ClientResponse response) {
    Mono<String> errorMsg = response.bodyToMono(String.class);
    return errorMsg.flatMap(msg -> {
      throw new SaveException(SERVER_ERROR_OCCURRED);
    });
  }

  static Mono<Throwable> handleFetchingError(ClientResponse response) {
    Mono<String> errorMsg = response.bodyToMono(String.class);
    return errorMsg.flatMap(msg -> {
      throw new FetchException(SERVER_ERROR_OCCURRED);
    });
  }

  static <T> Mono<T> fetchEntities(WebClient webClient, Class<T> clazz) {
    return webClient.get()
        .uri("/list")
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, ClientApiUtils::handleEntityError)
        .onStatus(HttpStatus::is5xxServerError, ClientApiUtils::handleFetchingError)
        .bodyToMono(clazz);
  }

  static Mono<Void> deleteEntity(WebClient webClient, int id) {
    return webClient.delete()
        .uri("/delete/" + id)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, ClientApiUtils::handleEntityError)
        .onStatus(HttpStatus::is5xxServerError, ClientApiUtils::handleDeletingError)
        .bodyToMono(Void.class);
  }

  static <T> Mono<T> postEntity(WebClient webClient, T t, Class<T> clazz) {
    return webClient.post()
        .uri("/add")
        .body(Mono.just(t), clazz)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, ClientApiUtils::handleEntityError)
        .onStatus(HttpStatus::is5xxServerError, ClientApiUtils::handleSavingError)
        .bodyToMono(clazz);
  }

  static <T> Mono<T> putEntity(WebClient webClient, T t, Class<T> clazz) {
    return webClient.put()
        .uri("/update")
        .body(Mono.just(t), clazz)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, ClientApiUtils::handleEntityError)
        .onStatus(HttpStatus::is5xxServerError, ClientApiUtils::handleSavingError)
        .bodyToMono(clazz);
  }
}
