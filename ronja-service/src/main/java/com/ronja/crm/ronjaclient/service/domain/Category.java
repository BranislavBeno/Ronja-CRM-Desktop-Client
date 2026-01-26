package com.ronja.crm.ronjaclient.service.domain;

import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = CategoryEnumSerializer.class)
@JsonDeserialize(using = CategoryEnumDeserializer.class)
public enum Category {
    LEVEL_1("category.level.1"),
    LEVEL_2("category.level.2"),
    LEVEL_3("category.level.3");

    private final String key;

    Category(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return I18N.get(key);
    }
}
