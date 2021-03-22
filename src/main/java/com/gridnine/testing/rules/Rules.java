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

    /**General auxiliary methods.*/
    private static Stream<Interval> intervalsFrom(Flight flight) {
        Long[] dates = longFrom(flight).toArray(Long[]::new);
        List<Interval> intervals = new ArrayList<>();
        //start from i=1 and go to i=length-1 to exclude from the processing
        // the departure of the 1st segment and the arrival of the last segment of the flight
        for (int i = 1; i < dates.length - 1; i += 2) {
            intervals.add(new Interval(dates[i + 1] - dates[i]));
        }
        return intervals.stream();
    }

    private static Stream<Long> longFrom(Flight flight) {
        return flight.getSegments().stream()
                .flatMap(Rules::segmentToDate)
                .map(Rules::dateToLong);
    }

    private static Stream<LocalDateTime> segmentToDate(Segment segment) {
        return Stream.of(segment.getDepartureDate(), segment.getArrivalDate());
    }

    private static Long dateToLong(LocalDateTime date) {
        return Timestamp.valueOf(date).getTime();
    }
}