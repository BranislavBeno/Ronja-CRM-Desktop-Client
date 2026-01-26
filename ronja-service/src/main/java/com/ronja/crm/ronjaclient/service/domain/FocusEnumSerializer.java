package com.ronja.crm.ronjaclient.service.domain;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class FocusEnumSerializer extends StdSerializer<Focus> {

    public FocusEnumSerializer() {
        super(Focus.class);
    }

    @Override
    public void serialize(Focus value, JsonGenerator generator, SerializationContext provider) throws JacksonException {
        generator.writeString(value.name());
    }
}
