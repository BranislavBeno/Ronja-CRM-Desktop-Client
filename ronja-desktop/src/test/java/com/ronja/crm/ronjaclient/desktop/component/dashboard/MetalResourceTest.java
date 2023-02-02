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
        IntStream.rangeClosed(1, 60).forEach(i -> METAL_DATA.add(createMetalData(i)));
        metalResource = new MetalResource(2, 10, 40, METAL_DATA);
    }

    @Test
    void fetchLatestData() {
        metalResource.getLatestData()
                .ifPresent(m -> assertThat(m.getFetched()).isEqualTo(LocalDate.now().minusDays(60)));
    }

    @Test
    void getDailyData() {
        List<MetalData> data = metalResource.getDailyData();
        assertThat(data).hasSize(60);
    }

    @Test
    void getWeeklyData() {
        List<MetalData> data = metalResource.getWeeklyData();
        assertThat(data).hasSize(8);
    }

    @Test
    void getMonthlyData() {
        List<MetalData> data = metalResource.getMonthlyData();
        assertThat(data).hasSizeBetween(1, 3);
    }

    private static MetalData createMetalData(int offset) {
        MetalData data = new MetalData();
        data.setFetched(LocalDate.now().minusDays(offset));

        return data;
    }

}