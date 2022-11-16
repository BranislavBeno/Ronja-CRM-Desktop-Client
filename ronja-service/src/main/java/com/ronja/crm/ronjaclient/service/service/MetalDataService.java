package com.ronja.crm.ronjaclient.service.service;

import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import com.ronja.crm.ronjaclient.service.clientapi.MetalDataWebClient;
import com.ronja.crm.ronjaclient.service.domain.MetalData;
import com.ronja.crm.ronjaclient.service.validation.FetchException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MetalDataService {

    private final MetalDataWebClient webClient;

    public MetalDataService(MetalDataWebClient webClient) {
        this.webClient = Objects.requireNonNull(webClient);
    }

    public List<MetalData> fetchData() {
        try {
            MetalData[] metalData = Objects.requireNonNull(webClient.fetchMetalData());
            return Arrays.stream(metalData).sorted(Comparator.comparing(MetalData::getFetched)).toList();
        } catch (Exception e) {
            String message = I18N.get("exception.metal.fetch")
                    + System.lineSeparator()
                    + I18N.get("exception.server.connection");
            throw new FetchException(message, e);
        }
    }
}
