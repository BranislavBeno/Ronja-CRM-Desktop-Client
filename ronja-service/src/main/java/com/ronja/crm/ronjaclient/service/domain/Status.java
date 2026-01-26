package com.ronja.crm.ronjaclient.service.domain;

import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = StatusEnumSerializer.class)
@JsonDeserialize(using = StatusEnumDeserializer.class)
public enum Status {
    ACTIVE("status.active"),
    INACTIVE("status.inactive");

    private final String key;

    Status(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return I18N.get(key);
    }
}
