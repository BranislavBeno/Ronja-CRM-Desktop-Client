package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.Representative;
import org.springframework.http.HttpHeaders;
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
    return ClientApiUtils.fetchEntities(webClient, Representative[].class);
  }

  public Mono<Representative> updateRepresentative(Representative representative) {
    return ClientApiUtils.putEntity(webClient, representative, Representative.class);
  }

  public Mono<Representative> createRepresentative(Representative representative) {
    return ClientApiUtils.postEntity(webClient, representative, Representative.class);
  }

  public Mono<Void> deleteRepresentative(int id) {
    return ClientApiUtils.deleteEntity(webClient, id);
  }
}
