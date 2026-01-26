package com.ronja.crm.ronjaclient.service.domain;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class FocusEnumDeserializer extends StdDeserializer<Focus> {

    public FocusEnumDeserializer() {
        super(Focus.class);
    }

    @Override
    public Focus deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        String name = parser.getString();
        for (Focus focus : Focus.values()) {
            if (focus.name().equals(name)) {
                return focus;
            }
        }

        return null;
    }
}
