package com.gridnine.testing.rules;

public class GroundTime {
    private final long millis;

    GroundTime(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return millis;
    }

    public static GroundTime zero() {
        return new GroundTime(0L);
    }

    public static GroundTime sum(GroundTime a, GroundTime b) {
        return new GroundTime(a.getMillis() + b.getMillis());
    }
}