package com.ronja.crm.ronjaclient.service.clientapi;

import org.springframework.web.reactive.function.client.ClientResponse;
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
}
