package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class Rules {
    public static final LocalDateTime now = LocalDateTime.now();
    public static final Predicate<Segment> departureInPast = s -> s.getDepartureDate().isBefore(now);
    public static final Predicate<Segment> departureAfterArrival = (s) -> s.getDepartureDate().isAfter(s.getArrivalDate());
    public static final Predicate<Flight> notOne = f -> f.getSegments().size() != 1;
    public static final Predicate<Flight> moreThanOne = f -> f.getSegments().size() > 1;
    public static final Predicate<Flight> empty = f -> f.getSegments().isEmpty();

    public static Rule<List<Flight>, List<Flight>> removeFlightIfDate(Predicate<Segment> predicate) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> filter(List<Flight> flights) {
                return flights.stream()
                        .filter(f -> f.getSegments().stream().noneMatch(predicate))
                        .collect(toList());
            }
        };
    }

    public static Rule<List<Flight>, List<Flight>> slipFlightIfDate(Predicate<Segment> predicate) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> filter(List<Flight> flights) {
                return flights.stream()
                        .filter(f -> f.getSegments().stream().allMatch(predicate))
                        .collect(toList());
            }
        };
    }

    public static Rule<List<Flight>, List<Flight>> removeFlightIfSegment(Predicate<Flight> predicate) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> filter(List<Flight> flights) {
                return flights.stream()
                        .filter(predicate.negate())
                        .collect(toList());
            }
        };
    }

    public static Rule<List<Flight>, List<Flight>> skipFlightIfSegment(Predicate<Flight> predicate) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> filter(List<Flight> flights) {
                return flights.stream()
                        .filter(predicate)
                        .collect(toList());
            }
        };
    }

    public static Rule<List<Flight>, List<Flight>> removeFlightIfHoursOnGroundMore(Predicate<Long> predicate) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> filter(List<Flight> flights) {
                List<Flight> resultFlights = new ArrayList<>(flights);
                Iterator<Flight> iterator = resultFlights.iterator();
                while (iterator.hasNext()) {
                    Flight flight = iterator.next();
                    long time = 0;
                    LocalDateTime from = resultFlights.get(0).getSegments().get(0).getDepartureDate();//чтобы для первого segment время на земле = 0
                    for (Segment segment : flight.getSegments()) {
                        time += ChronoUnit.HOURS.between(from, segment.getDepartureDate());
                        from = segment.getArrivalDate();
                        if (predicate.test(time)) {
                            iterator.remove();
                        }
                    }
                }
                return resultFlights;
            }
        };
    }
}