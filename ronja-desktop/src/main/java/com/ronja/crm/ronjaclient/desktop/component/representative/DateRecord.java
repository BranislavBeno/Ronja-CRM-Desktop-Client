package com.ronja.crm.ronjaclient.desktop.component.representative;

import java.time.LocalDate;

public record DateRecord(LocalDate startDate, LocalDate endDate) {

    public DateRecord() {
        this(null, null);
    }

    public DateRecord(DateRecord dateRecord) {
        this(dateRecord.startDate, dateRecord.endDate);
    }

    public boolean isNotEmpty() {
        return startDate != null && endDate != null;
    }
}
