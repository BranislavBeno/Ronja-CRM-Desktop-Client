package com.ronja.crm.ronjaclient.service.domain;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class StatusEnumDeserializer extends StdDeserializer<Status> {

    public StatusEnumDeserializer() {
        super(Status.class);
    }

    @Override
    public Status deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        String name = parser.getString();
        for (Status status : Status.values()) {
            if (status.name().equals(name)) {
                return status;
            }
        }

        return null;
    }
}
