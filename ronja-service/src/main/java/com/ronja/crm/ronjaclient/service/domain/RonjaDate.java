package com.ronja.crm.ronjaclient.service.domain;

import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import com.ronja.crm.ronjaclient.service.util.DateTimeUtil;

import java.time.LocalDate;
import java.time.format.TextStyle;

public record RonjaDate(LocalDate date) {

    public RonjaDate(String stringDate) {
        this(DateTimeUtil.parse(stringDate));
    }

    @Override
    public String toString() {
        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, I18N.getLocale());
        return date.format(DateTimeUtil.DATE_TIME_FORMATTER) + " " + dayOfWeek;
    }
}
