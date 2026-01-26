package com.ronja.crm.ronjaclient.service.domain;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class CategoryEnumDeserializer extends StdDeserializer<Category> {

    public CategoryEnumDeserializer() {
        super(Category.class);
    }

    @Override
    public Category deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        String name = parser.getString();
        for (Category category : Category.values()) {
            if (category.name().equals(name)) {
                return category;
            }
        }

        return null;
    }
}
