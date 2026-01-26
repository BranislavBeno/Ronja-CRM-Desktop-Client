package com.ronja.crm.ronjaclient.service.domain;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class ContactTypeEnumSerializer extends StdSerializer<ContactType> {

    public ContactTypeEnumSerializer() {
        super(ContactType.class);
    }

    @Override
    public void serialize(ContactType value, JsonGenerator generator, SerializationContext provider) throws JacksonException {
        generator.writeString(value.name());
    }
}
