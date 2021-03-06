package com.gridnine.testing.rules;

/**
 * The unit of measurement of time is millisecond.
 * One minute is equals 60000 ms, one hours - 3600000 ms.*/
public enum TimeMeasure {
    HOURS(3600000),
    MINUTES(60000);

    private final long value;

    TimeMeasure(long value) {
        this.value = value;
    }

    public static GroundTime ofHours(long value) {
        return new GroundTime(HOURS.value * value);
    }

    public static GroundTime ofMinutes(long value) {
        return new GroundTime(MINUTES.value * value);
    }
}