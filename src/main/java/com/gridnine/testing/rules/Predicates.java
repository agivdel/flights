package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;

import java.time.LocalDateTime;
import java.util.function.Predicate;

/**Examples of some prepared predicates.*/
public class Predicates {
    private static final LocalDateTime now = LocalDateTime.now();
    //TODO такое решение требует перезапуска каждый день. Исправить.


    public static final Predicate<Segment> departureInPast = s -> s.getDepartureDate().isBefore(now);
    public static final Predicate<Segment> departureAfterArrival = (s) -> s.getDepartureDate().isAfter(s.getArrivalDate());

    public static final Predicate<Flight> one = f -> f.getSegments().size() == 1;
    public static final Predicate<Flight> moreOne = f -> f.getSegments().size() > 1;
    public static final Predicate<Flight> moreTwo = f -> f.getSegments().size() > 2;

    public static Predicate<Interval> lessThen(Interval limit) {
        return interval -> interval.getMillis() < limit.getMillis();
    }

    public static Predicate<Interval> moreThen(Interval limit) {
        return interval -> interval.getMillis() > limit.getMillis();
    }

    public static Predicate<Interval> notLessThen(Interval limit) {
        return interval -> interval.getMillis() >= limit.getMillis();
    }

    public static Predicate<Interval> notMoreThen(Interval limit) {
        return interval -> interval.getMillis() <= limit.getMillis();
    }

    public static Predicate<Interval> equal(Interval limit) {
        return interval -> interval.getMillis() == limit.getMillis();
    }
}