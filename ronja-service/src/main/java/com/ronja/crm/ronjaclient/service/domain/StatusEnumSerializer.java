package com.ronja.crm.ronjaclient.service.domain;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class StatusEnumSerializer extends StdSerializer<Status> {

    public StatusEnumSerializer() {
        super(Status.class);
    }

    @Override
    public void serialize(Status value, JsonGenerator generator, SerializationContext provider) throws JacksonException {
        generator.writeString(value.name());
    }
}
