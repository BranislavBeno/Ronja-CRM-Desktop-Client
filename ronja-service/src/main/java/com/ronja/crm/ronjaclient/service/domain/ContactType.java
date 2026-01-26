package com.ronja.crm.ronjaclient.service.domain;

import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ContactTypeEnumSerializer.class)
@JsonDeserialize(using = ContactTypeEnumDeserializer.class)
public enum ContactType {
    PERSONAL("contact.type.personal"),
    MAIL("contact.type.mail"),
    PHONE("contact.type.phone"),
    ON_LINE("contact.type.online");

    private final String key;

    ContactType(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return I18N.get(key);
    }
}
