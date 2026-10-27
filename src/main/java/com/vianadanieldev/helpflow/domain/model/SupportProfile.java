package com.vianadanieldev.helpflow.domain.model;

public enum SupportProfile {
    LEVEL_1, LEVEL_2, LEVEL_3;

    public boolean isAtLeast(SupportProfile other) {
        return ordinal() >= other.ordinal();
    }
}
