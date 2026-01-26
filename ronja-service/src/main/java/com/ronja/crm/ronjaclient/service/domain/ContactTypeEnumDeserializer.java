package com.ronja.crm.ronjaclient.service.domain;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class ContactTypeEnumDeserializer extends StdDeserializer<ContactType> {

    public ContactTypeEnumDeserializer() {
        super(ContactType.class);
    }

    @Override
    public ContactType deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        String name = parser.getString();
        for (ContactType contactType : ContactType.values()) {
            if (contactType.name().equals(name)) {
                return contactType;
            }
        }

        return null;
    }
}
