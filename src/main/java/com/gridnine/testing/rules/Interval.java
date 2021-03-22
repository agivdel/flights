package com.gridnine.testing.rules;

public class Interval {
    private final long millis;

    Interval(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return millis;
    }

    public static Interval zero() {
        return new Interval(0L);
    }

    public static Interval sum(Interval a, Interval b) {
        return new Interval(a.getMillis() + b.getMillis());
    }
}