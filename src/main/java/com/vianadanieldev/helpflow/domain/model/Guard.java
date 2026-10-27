package com.vianadanieldev.helpflow.domain.model;

import com.vianadanieldev.helpflow.domain.exception.InvalidDataException;

public final class Guard {
    private Guard() {}

    public static String notBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(field + " is required");
        }
        return value.trim();
    }

    public static <T> T notNull(T value, String field) {
        if (value == null) {
            throw new InvalidDataException(field + " is required");
        }
        return value;
    }
}
