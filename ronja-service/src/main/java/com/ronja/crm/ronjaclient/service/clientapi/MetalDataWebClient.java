package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.MetalData;
import com.ronja.crm.ronjaclient.service.util.ClientApiUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class MetalDataWebClient {

    private final WebClient webClient;

    public MetalDataWebClient(String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public MetalData[] fetchMetalData() {
        return ClientApiUtils.fetchEntities(webClient, MetalData[].class).block();
    }
}
