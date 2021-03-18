package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;

import java.time.LocalDateTime;
import java.util.function.Predicate;

/**Examples of some prepared predicates.*/
public class PredicateConstants {
    private static final LocalDateTime now = LocalDateTime.now();
    //TODO такое решение требует перезапуска каждый день. Исправить.


    public static final Predicate<Segment> departureInPast = s -> s.getDepartureDate().isBefore(now);
    public static final Predicate<Segment> departureAfterArrival = (s) -> s.getDepartureDate().isAfter(s.getArrivalDate());

    public static final Predicate<Flight> one = f -> f.getSegments().size() == 1;
    public static final Predicate<Flight> moreOne = f -> f.getSegments().size() > 1;
    public static final Predicate<Flight> moreTwo = f -> f.getSegments().size() > 2;

    //    public static final Predicate<Interval>

//    class Interval {
//        private long millis;
//
//    }
//
//    public <T> Predicate<T> lessThen(TimeMeasure unit) {
//        Predicate<T> predicate = new Predicate<T>() {
//            @Override
//            public boolean test(T t) {
//                t < unit;
//            }
//        };
//        return predicate;
//    }
//
//    public static Rule<List<Flight>, List<Flight>> example(Predicate<Interval> predicate) {
//        return flights -> flights.stream()
//                .filter(f -> example2(f, predicate))
//                .collect(toList());
//    }
//
//    private static boolean example2(Flight flight, Predicate<Interval> predicate) {
//        return toPairs(flight)
//                .map(Pair::getDifference)
//                .anyMatch(predicate);
//    }
}