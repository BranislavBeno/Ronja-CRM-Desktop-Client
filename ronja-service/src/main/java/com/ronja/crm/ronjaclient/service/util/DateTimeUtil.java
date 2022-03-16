package com.ronja.crm.ronjaclient.service.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private DateTimeUtil() {
    }

    public static LocalDate parse(String stringDate) {
        return LocalDate.parse(stringDate, DATE_TIME_FORMATTER);
    }
}
