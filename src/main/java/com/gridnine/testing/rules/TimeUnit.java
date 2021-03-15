package com.gridnine.testing.rules;

public enum TimeUnit {
    HOURS(3600000),
    MINUTES(60000);

    private final long value;

    TimeUnit(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}