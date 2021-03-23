package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;

import java.sql.Timestamp;
import java.time.Duration;
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

    public static Rule<List<Flight>, List<Flight>> removeFlightIfTotalGroundTime(Predicate<GroundTime> predicate) {
        return flights -> flights.stream()
                .filter(f -> !ifTotalGroundTime(f, predicate))
                .collect(toList());
    }

    public static Rule<List<Flight>, List<Flight>> skipFlightIfTotalGroundTime(Predicate<GroundTime> predicate) {
        return flights -> flights.stream()
                .filter(f -> ifTotalGroundTime(f, predicate))
                .collect(toList());
    }

    public static Rule<List<Flight>, List<Flight>> removeFlightIfAnyGroundTime(Predicate<GroundTime> predicate) {
        return flights -> flights.stream()
                .filter(f -> !ifAnyGroundTime(f, predicate))
                .collect(toList());
    }

    public static Rule<List<Flight>, List<Flight>> skipFlightIfAnyGroundTime(Predicate<GroundTime> predicate) {
        return flights -> flights.stream()
                .filter(f -> ifAnyGroundTime(f, predicate))
                .collect(toList());
    }

    /**Auxiliary method for counting the total ground time for each flight.
     * Returns whether the total ground time of this flight match the provided predicate.*/
    private static boolean ifTotalGroundTime(Flight flight, Predicate<GroundTime> predicate) {
        GroundTime totalGroundTime = groundTimesOf(flight).reduce(GroundTime.zero(), GroundTime::sum);
        return predicate.test(totalGroundTime);
    }

    /**Auxiliary method for checking each transfers
     * (time between two neighboring segments of a flight)
     * for compliance with the predicate.
     * Returns whether any transfer of this flight match the provided predicate.*/
    private static boolean ifAnyGroundTime(Flight flight, Predicate<GroundTime> predicate) {
        return groundTimesOf(flight).anyMatch(predicate);
    }

    /**General auxiliary methods.*/
    private static Stream<GroundTime> groundTimesOf(Flight flight) {
        Segment prevSegment = null;
        List<GroundTime> groundTimes = new ArrayList<>();
        for (Segment segment : flight.getSegments()) {
            if (prevSegment != null) {
                final LocalDateTime start = prevSegment.getArrivalDate();
                final LocalDateTime end = segment.getDepartureDate();
                groundTimes.add(new GroundTime(Duration.between(start, end).toMillis()));
            }
            prevSegment = segment;
        }
        return groundTimes.stream();
    }
}