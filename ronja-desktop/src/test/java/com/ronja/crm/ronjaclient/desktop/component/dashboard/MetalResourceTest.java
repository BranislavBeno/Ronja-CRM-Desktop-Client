package com.ronja.crm.ronjaclient.desktop.component.dashboard;

import com.ronja.crm.ronjaclient.service.domain.MetalData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class MetalResourceTest {

    private static final List<MetalData> METAL_DATA = new ArrayList<>();
    private static MetalResource metalResource;

    @BeforeAll
    static void setupAll() {
        IntStream.rangeClosed(1, 93).forEach(i -> METAL_DATA.add(createMetalData(i)));
        metalResource = new MetalResource(2, 10, 40, METAL_DATA);
    }

    @Test
    void fetchLatestData() {
        metalResource.getLatestData()
                .ifPresent(m -> assertThat(m.getFetched()).isEqualTo(LocalDate.now().minusDays(93)));
    }

    @Test
    void getDailyData() {
        List<MetalData> data = metalResource.getDailyData();
        assertThat(data).hasSizeBetween(55, 60);
    }

    @Test
    void getWeeklyData() {
        List<MetalData> data = metalResource.getWeeklyData();
        assertThat(data).hasSizeBetween(8, 11);
    }

    @Test
    void getMonthlyData() {
        List<MetalData> data = metalResource.getMonthlyData();
        assertThat(data).hasSizeBetween(0, 4);
    }

    private static MetalData createMetalData(int offset) {
        MetalData data = new MetalData();
        data.setFetched(LocalDate.now().minusDays(offset));

        return data;
    }

}