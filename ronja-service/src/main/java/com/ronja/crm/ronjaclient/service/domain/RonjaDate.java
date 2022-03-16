package com.ronja.crm.ronjaclient.service.domain;

import com.ronja.crm.ronjaclient.service.util.DateTimeUtil;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public record RonjaDate(LocalDate date) {

    public RonjaDate(String stringDate) {
        this(DateTimeUtil.parse(stringDate));
    }

    @Override
    public String toString() {
        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("sk", "SK"));
        return date.format(DateTimeUtil.DATE_TIME_FORMATTER) + " " + dayOfWeek;
    }
}
