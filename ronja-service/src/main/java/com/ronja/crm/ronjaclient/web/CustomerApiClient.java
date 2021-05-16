package com.ronja.crm.ronjaclient.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ronja.crm.ronjaclient.web.domain.Category;
import com.ronja.crm.ronjaclient.web.domain.Customer;
import com.ronja.crm.ronjaclient.web.domain.Focus;
import com.ronja.crm.ronjaclient.web.domain.Status;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.Logger;
import reactor.util.Loggers;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;

@Component
public class CustomerApiClient {

    private final static Logger log = Loggers.getLogger(CustomerApiClient.class);

    private final WebClient webClient;

    public CustomerApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Customer fetchCustomerByName(String name) {
        ObjectNode result = webClient.get().uri("http://localhost:8080/customers/search",
                uriBuilder -> uriBuilder.queryParam("companyName", name)
                        .build())
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(5)))
                .doOnError(IOException.class, e -> log.error(e.getMessage()))
                .block();

        JsonNode content = result.get("name:" + name);

        return convertCustomer(name, content);
    }

    private Customer convertCustomer(String name, JsonNode content) {
        Customer customer = new Customer();
        customer.setCompanyName(name);
        customer.setId(content.get("id").asInt());
        customer.setCategory(Category.valueOf(content.get("category").asText()));
        customer.setFocus(Focus.valueOf(content.get("focus").asText()));
        customer.setStatus(Status.valueOf(content.get("status").asText()));
        return customer;
    }
}
