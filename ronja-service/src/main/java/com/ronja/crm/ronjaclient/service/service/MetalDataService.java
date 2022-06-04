package com.ronja.crm.ronjaclient.service.service;

import com.ronja.crm.ronjaclient.service.clientapi.MetalDataWebClient;
import com.ronja.crm.ronjaclient.service.domain.MetalData;
import com.ronja.crm.ronjaclient.service.validation.FetchException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class MetalDataService {

    private final int dailyLimit;
    private final int weeklyLimit;
    private final MetalDataWebClient webClient;

    public MetalDataService(int dailyLimit, int weeklyLimit, MetalDataWebClient webClient) {
        this.dailyLimit = dailyLimit;
        this.weeklyLimit = weeklyLimit;
        this.webClient = webClient;
    }

    public Stream<MetalData> fetchData() {
        try {
            MetalData[] metalData = Objects.requireNonNull(webClient.fetchMetalData());
            return Arrays.stream(metalData).sorted(Comparator.comparing(MetalData::getFetched));
        } catch (Exception e) {
            throw new FetchException("""
                    Nepodarilo sa získať dáta o cene kovov.
                    Preverte spojenie so serverom.""", e);
        }
    }

    private Stream<MetalData> fetchData(int limit) {
        return fetchData()
                .filter(m -> m.getFetched().isAfter(LocalDate.now().minusMonths(limit)));
    }

    public Stream<MetalData> fetchDailyData() {
        return fetchData(dailyLimit);
    }

    public Stream<MetalData> fetchWeeklyData() {
        return fetchData(weeklyLimit)
                .filter(m -> m.getFetched().getDayOfWeek().equals(DayOfWeek.FRIDAY));
    }
}
