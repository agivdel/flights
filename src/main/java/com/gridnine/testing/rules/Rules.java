package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**All skip-methods can work with combined predicates using "or"*/
public class Rules {

    public static Rule<List<Flight>, List<Flight>> removeFlightIfDate(Predicate<Segment> predicate) {
        return flights -> flights.stream()
                .filter(f -> f.getSegments().stream().noneMatch(predicate))
                .collect(toList());
    }

    public static Rule<List<Flight>, List<Flight>> skipFlightIfDate(Predicate<Segment> predicate) {
        return flights -> flights.stream()
                .filter(f -> f.getSegments().stream().allMatch(predicate))
                .collect(toList());
    }

    public static Rule<List<Flight>, List<Flight>> removeFlightIfSegment(Predicate<Flight> predicate) {
        return flights -> flights.stream()
                .filter(predicate.negate())
                .collect(toList());
    }

    public static Rule<List<Flight>, List<Flight>> skipFlightIfSegment(Predicate<Flight> predicate) {
        return flights -> flights.stream()
                .filter(predicate)
                .collect(toList());
    }

    public static Rule<List<Flight>, List<Flight>> removeFlightIfTotalGroundTime(Predicate<Interval> predicate) {
        return flights -> flights.stream()
                .filter(f -> !ifTotalGroundTime(f, predicate))
                .collect(toList());
    }

    public static Rule<List<Flight>, List<Flight>> skipFlightIfTotalGroundTime(Predicate<Interval> predicate) {
        return flights -> flights.stream()
                .filter(f -> ifTotalGroundTime(f, predicate))
                .collect(toList());
    }

    public static Rule<List<Flight>, List<Flight>> removeFlightIfAnyGroundTime(Predicate<Interval> predicate) {
        return flights -> flights.stream()
                .filter(f -> !ifAnyGroundTime(f, predicate))
                .collect(toList());
    }


    public static Rule<List<Flight>, List<Flight>> skipFlightIfAnyGroundTime(Predicate<Interval> predicate) {
        return flights -> flights.stream()
                .filter(f -> ifAnyGroundTime(f, predicate))
                .collect(toList());
    }

    /**Auxiliary method for counting the total ground time for each flight.
     * Returns whether the total ground time of this flight match the provided predicate.*/
    private static boolean ifTotalGroundTime(Flight flight, Predicate<Interval> predicate) {
        Interval totalGroundTime = intervalsFrom(flight).reduce(Interval.zero(), Interval::sum);
        return predicate.test(totalGroundTime);
    }

    /**Auxiliary method for checking each transfers
     * (time between two neighboring segments of a flight)
     * for compliance with the predicate.
     * Returns whether any transfer of this flight match the provided predicate.*/
    private static boolean ifAnyGroundTime(Flight flight, Predicate<Interval> predicate) {
        return intervalsFrom(flight).anyMatch(predicate);
    }

    /**General auxiliary methods and classes.*/
    private static Stream<Interval> intervalsFrom(Flight flight) {
        Long[] longArray = longFrom(flight).toArray(Long[]::new);
        List<Interval> intervals = new ArrayList<>();
        for (int i = 0; i < longArray.length; i += 2) {
            Interval interval = new Interval(longArray[i + 1] - longArray[i]);
            intervals.add(interval);
        }
        return intervals.stream();
    }

    private static Stream<Long> longFrom(Flight flight) {
        return flight.getSegments().stream()
                .flatMap(Rules::toDate)
                .skip(1)//skip departure of the first segment
                .limit(flight.getSegments().size() * 2L - 2)//remove arrival of the last segment
                .map(Rules::toLong);
    }

    private static Stream<LocalDateTime> toDate(Segment segment) {
        return Stream.of(segment.getDepartureDate(), segment.getArrivalDate());
    }

    private static Long toLong(LocalDateTime date) {
        return Timestamp.valueOf(date).getTime();
    }
}