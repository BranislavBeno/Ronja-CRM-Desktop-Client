package com.ronja.crm.ronjaclient.service.service;

import com.ronja.crm.ronjaclient.service.clientapi.MetalDataWebClient;
import com.ronja.crm.ronjaclient.service.domain.MetalData;
import com.ronja.crm.ronjaclient.service.validation.FetchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MetalDataServiceTest {

    private MetalDataWebClient webClient;
    private MetalDataService cut;

    @BeforeEach
    void setUp() {
        webClient = Mockito.mock(MetalDataWebClient.class);
        cut = new MetalDataService(webClient);
    }

    @Test
    void testNotNull() {
        assertThat(webClient).isNotNull();
        assertThat(cut).isNotNull();
    }

    @Test
    void testFetchDataFailing() {
        Mockito.when(webClient.fetchMetalData()).thenThrow(RuntimeException.class);
        assertThrows(FetchException.class, () -> cut.fetchData());
    }

    @Test
    void testFetchDataReturningNull() {
        Mockito.when(webClient.fetchMetalData()).thenReturn(null);
        assertThrows(FetchException.class, () -> cut.fetchData());
    }

    @Test
    void testFetchData() {
        MetalData[] array = provideMetalData();
        Mockito.when(webClient.fetchMetalData()).thenReturn(array);
        List<MetalData> data = cut.fetchData();
        assertThat(data).hasSize(1);
    }

    private MetalData[] provideMetalData() {
        return new MetalData[]{new MetalData()};
    }
}