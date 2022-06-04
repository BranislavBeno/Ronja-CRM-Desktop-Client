package com.ronja.crm.ronjaclient.service.service;

import com.ronja.crm.ronjaclient.service.clientapi.MetalDataWebClient;
import com.ronja.crm.ronjaclient.service.domain.MetalData;
import com.ronja.crm.ronjaclient.service.validation.FetchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

class MetalDataServiceTest {

    private MetalDataWebClient webClient;
    private MetalDataService cut;

    @BeforeEach
    void setUp() {
        webClient = Mockito.mock(MetalDataWebClient.class);
        cut = new MetalDataService(2, 10, webClient);
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
        MetalData[] array = provideMetalData(0);
        Mockito.when(webClient.fetchMetalData()).thenReturn(array);
        Stream<MetalData> stream = cut.fetchData();
        assertThat(stream.toList()).hasSize(1);
    }

    @Test
    void testFetchDailyData() {
        MetalData[] array = provideMetalData(0);
        Mockito.when(webClient.fetchMetalData()).thenReturn(array);
        Stream<MetalData> stream = cut.fetchDailyData();
        assertThat(stream.toList()).hasSize(1);
    }

    @ParameterizedTest
    @CsvSource({"0, 1", "1, 0"})
    void testFetchWeeklyData(int offset, int size) {
        MetalData[] array = provideMetalData(offset);
        Mockito.when(webClient.fetchMetalData()).thenReturn(array);
        Stream<MetalData> stream = cut.fetchWeeklyData();
        assertThat(stream.toList()).hasSize(size);
    }

    private MetalData[] provideMetalData(int offset) {
        LocalDate date = LocalDate.now();
        int days = getDayReduction(date);
        MetalData metalData = new MetalData();
        metalData.setFetched(date.minusDays(days + offset));

        return new MetalData[]{metalData};
    }

    private int getDayReduction(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case FRIDAY -> 0;
            case SATURDAY -> 1;
            case SUNDAY -> 2;
            case MONDAY -> 3;
            case TUESDAY -> 4;
            case WEDNESDAY -> 5;
            case THURSDAY -> 6;
        };
    }
}