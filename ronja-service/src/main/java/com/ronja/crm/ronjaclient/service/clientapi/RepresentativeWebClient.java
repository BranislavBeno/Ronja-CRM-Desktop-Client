package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.Representative;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class RepresentativeWebClient {

  private final WebClient webClient;

  public RepresentativeWebClient(String baseUrl) {
    this.webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  public Mono<Representative[]> fetchAllRepresentatives() {
    return webClient.get()
        .uri("/list")
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, ClientApiUtils::handleEntityError)
        .onStatus(HttpStatus::is5xxServerError, ClientApiUtils::handleFetchingError)
        .bodyToMono(Representative[].class);
  }
}
