package com.gridnine.testing.rules;

public enum TimeMeasure {
    HOURS(3600000),
    MINUTES(60000);

    private final long value;

    TimeMeasure(long value) {
        this.value = value;
    }

    public long getMillis() {
        return value;
    }
}