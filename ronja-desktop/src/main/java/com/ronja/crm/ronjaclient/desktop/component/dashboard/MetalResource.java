package com.ronja.crm.ronjaclient.desktop.component.dashboard;

import com.ronja.crm.ronjaclient.service.domain.MetalData;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class MetalResource {

    private final int dailyLimit;
    private final int weeklyLimit;
    private final int monthlyLimit;
    private final List<MetalData> metalData;

    public MetalResource(int dailyLimit, int weeklyLimit, int monthlyLimit, List<MetalData> metalData) {
        this.dailyLimit = dailyLimit;
        this.weeklyLimit = weeklyLimit;
        this.monthlyLimit = monthlyLimit;
        this.metalData = Objects.requireNonNull(metalData);
    }

    public Optional<MetalData> getLatestData() {
        return metalData
                .stream()
                .reduce((_, second) -> second);
    }

    public List<MetalData> getDailyData() {
        return filterData(dailyLimit)
                .toList();
    }

    public List<MetalData> getWeeklyData() {
        return filterData(weeklyLimit)
                .filter(m -> {
                    LocalDate fetched = m.getFetched();
                    DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
                    return switch (dayOfWeek) {
                        case SATURDAY -> fetched.getDayOfWeek().equals(DayOfWeek.FRIDAY);
                        case SUNDAY -> fetched.getDayOfWeek().equals(DayOfWeek.MONDAY);
                        default -> fetched.getDayOfWeek().equals(dayOfWeek);
                    };
                })
                .toList();
    }

    public List<MetalData> getMonthlyData() {
        List<MetalData> list = filterMonthlyData();
        return reduceMonthlyData(list);
    }

    private List<MetalData> filterMonthlyData() {
        return filterData(monthlyLimit)
                .filter(m -> {
                    LocalDate fetched = m.getFetched();
                    int fetchedDayOfMonth = fetched.getDayOfMonth();
                    int currentDayOfMonth = LocalDate.now().getDayOfMonth() - 1;
                    if (fetchedDayOfMonth == currentDayOfMonth) {
                        return true;
                    } else if (fetchedDayOfMonth == currentDayOfMonth - 1) {
                        return true;
                    } else {
                        return fetchedDayOfMonth == currentDayOfMonth - 2;
                    }
                })
                .toList();
    }

    private static List<MetalData> reduceMonthlyData(List<MetalData> list) {
        List<MetalData> reduced = new ArrayList<>();
        Month month = null;
        for (MetalData data : list) {
            if (month == null || !month.equals(data.getFetched().getMonth())) {
                reduced.add(data);
            }
            month = data.getFetched().getMonth();
        }

        return reduced;
    }

    private Stream<MetalData> filterData(int limit) {
        return metalData.stream().filter(m -> m.getFetched().isAfter(LocalDate.now().minusMonths(limit)));
    }
}
