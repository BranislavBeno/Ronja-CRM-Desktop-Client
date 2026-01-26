package com.ronja.crm.ronjaclient.service.domain;

import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = FocusEnumSerializer.class)
@JsonDeserialize(using = FocusEnumDeserializer.class)
public enum Focus {
    BUILDER("focus.builder"),
    MANUFACTURE("focus.manufacture"),
    SPECIALIZED_MANUFACTURE("focus.specialized-manufacture"),
    TRADE("focus.trade");

    private final String key;

    Focus(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return I18N.get(key);
    }
}
