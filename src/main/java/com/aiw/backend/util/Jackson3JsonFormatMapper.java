package com.aiw.backend.util;

import java.lang.reflect.Type;
import org.hibernate.type.format.AbstractJsonFormatMapper;
import tools.jackson.databind.ObjectMapper;


public class Jackson3JsonFormatMapper extends AbstractJsonFormatMapper {

    private final ObjectMapper objectMapper;

    public Jackson3JsonFormatMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T fromString(final CharSequence charSequence, final Type type) {
        if (charSequence == null) {
            return null;
        }
        return objectMapper.readValue(charSequence.toString(), objectMapper.constructType(type));
    }

    @Override
    public <T> String toString(final T value, final Type type) {
        if (value == null) {
            return null;
        }
        return objectMapper.writerFor(objectMapper.constructType(type)).writeValueAsString(value);
    }

}
