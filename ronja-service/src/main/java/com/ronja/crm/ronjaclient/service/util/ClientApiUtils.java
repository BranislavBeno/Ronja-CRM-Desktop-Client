package com.ronja.crm.ronjaclient.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import com.ronja.crm.ronjaclient.service.domain.Representative;
import com.ronja.crm.ronjaclient.service.domain.Scheduled;
import com.ronja.crm.ronjaclient.service.validation.*;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerErrorException;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class ClientApiUtils {

    private ClientApiUtils() {
    }

    static Mono<Throwable> propagateServerError(ClientResponse response) {
        return response.bodyToMono(Throwable.class);
    }

    static Mono<Throwable> propagateDeletingError(ClientResponse response) {
        Mono<String> errorMsg = response.bodyToMono(String.class);
        return errorMsg.flatMap(_ -> {
            throw new DeleteException(I18N.get("exception.server.delete"));
        });
    }

    static Mono<Throwable> propagateFetchingError(ClientResponse response) {
        Mono<String> errorMsg = response.bodyToMono(String.class);
        return errorMsg.flatMap(_ -> {
            throw new FetchException(I18N.get("exception.server.fetch"));
        });
    }

    static Mono<Throwable> propagateSavingError(ClientResponse response) {
        Mono<String> errorMsg = response.bodyToMono(String.class);
        return errorMsg.flatMap(msg -> {
            ObjectMapper mapper = new ObjectMapper();
            ObjectReader objectReader = mapper.readerFor(ValidationErrorResponse.class);
            try {
                ValidationErrorResponse errorResponse = objectReader.readValue(msg);
                Violation violation = errorResponse.violations().stream().findFirst().orElse(null);
                String message = violation != null ? violation.message() : "";
                throw new SaveException(message);
            } catch (JsonProcessingException e) {
                throw new ServerErrorException(I18N.get("exception.server.error"), e);
            }
        });
    }

    public static <T> Mono<T> fetchEntities(WebClient webClient, Class<T> clazz) {
        return webClient.get()
                .uri("/list")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ClientApiUtils::propagateFetchingError)
                .onStatus(HttpStatusCode::is5xxServerError, ClientApiUtils::propagateServerError)
                .bodyToMono(clazz)
                .timeout(Duration.ofSeconds(20));
    }

    public static Mono<Representative[]> fetchParticularEntities(WebClient webClient, int id) {
        return webClient.get()
                .uri("/search?customerId=" + id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ClientApiUtils::propagateFetchingError)
                .onStatus(HttpStatusCode::is5xxServerError, ClientApiUtils::propagateServerError)
                .bodyToMono(Representative[].class)
                .timeout(Duration.ofSeconds(20));
    }

    public static Mono<Scheduled[]> fetchScheduledEntities(WebClient webClient, int id) {
        return webClient.get()
                .uri("/scheduled?days=" + id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ClientApiUtils::propagateFetchingError)
                .onStatus(HttpStatusCode::is5xxServerError, ClientApiUtils::propagateServerError)
                .bodyToMono(Scheduled[].class)
                .timeout(Duration.ofSeconds(20));
    }

    public static Mono<Void> deleteEntity(WebClient webClient, int id) {
        return webClient.delete()
                .uri("/delete/" + id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ClientApiUtils::propagateDeletingError)
                .onStatus(HttpStatusCode::is5xxServerError, ClientApiUtils::propagateServerError)
                .bodyToMono(Void.class)
                .timeout(Duration.ofSeconds(20));
    }

    public static <T, U> Mono<T> postEntity(WebClient webClient, U u, Class<T> clazz) {
        return webClient.post()
                .uri("/add")
                .body(Mono.just(u), clazz)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ClientApiUtils::propagateSavingError)
                .onStatus(HttpStatusCode::is5xxServerError, ClientApiUtils::propagateServerError)
                .bodyToMono(clazz)
                .timeout(Duration.ofSeconds(20));
    }

    public static <T, U> Mono<T> putEntity(WebClient webClient, U u, Class<T> clazz) {
        return webClient.put()
                .uri("/update")
                .body(Mono.just(u), clazz)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ClientApiUtils::propagateSavingError)
                .onStatus(HttpStatusCode::is5xxServerError, ClientApiUtils::propagateServerError)
                .bodyToMono(clazz)
                .timeout(Duration.ofSeconds(20));
    }
}
