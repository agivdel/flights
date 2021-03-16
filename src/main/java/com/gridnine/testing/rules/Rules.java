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

public class Rules {
    private static final LocalDateTime now = LocalDateTime.now();

    public static final Predicate<Segment> departureInPast = s -> s.getDepartureDate().isBefore(now);
    public static final Predicate<Segment> departureAfterArrival = (s) -> s.getDepartureDate().isAfter(s.getArrivalDate());

    public static final Predicate<Flight> moreOne = f -> f.getSegments().size() > 1;

    public static Rule<List<Flight>, List<Flight>> removeFlightIfDate(Predicate<Segment> predicate) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> fromSource(List<Flight> flights) {
                return flights.stream()
                        .filter(f -> f.getSegments().stream().noneMatch(predicate))
                        .collect(toList());
            }
        };
    }

    public static Rule<List<Flight>, List<Flight>> skipFlightIfDate(Predicate<Segment> predicate) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> fromSource(List<Flight> flights) {
                return flights.stream()
                        .filter(f -> f.getSegments().stream().allMatch(predicate))
                        .collect(toList());
            }
        };
    }

    public static Rule<List<Flight>, List<Flight>> removeFlightIfSegment(Predicate<Flight> predicate) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> fromSource(List<Flight> flights) {
                return flights.stream()
                        .filter(predicate.negate())
                        .collect(toList());
            }
        };
    }

    public static Rule<List<Flight>, List<Flight>> skipFlightIfSegment(Predicate<Flight> predicate) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> fromSource(List<Flight> flights) {
                return flights.stream()
                        .filter(predicate)
                        .collect(toList());
            }
        };
    }

    public static Rule<List<Flight>, List<Flight>> removeFlightIfTotalGroundTime(Predicate<Long> predicate, TimeUnit unit) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> fromSource(List<Flight> flights) {
                return new ArrayList<>(flights).stream()
                        .filter(f -> !ifTotalGroundTime(f, predicate, unit))
                        .collect(toList());
            }
        };
    }

    public static Rule<List<Flight>, List<Flight>> skipFlightIfTotalGroundTime(Predicate<Long> predicate, TimeUnit unit) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> fromSource(List<Flight> flights) {
                return new ArrayList<>(flights).stream()
                        .filter(f -> ifTotalGroundTime(f, predicate, unit))
                        .collect(toList());
            }
        };
    }

    private static boolean ifTotalGroundTime(Flight flight, Predicate<Long> predicate, TimeUnit unit) {
        return predicate.test(totalGroundTime(flight) / unit.getValue());
    }

    private static long totalGroundTime(Flight flight) {
        return flight.getSegments().stream()
                .flatMap(Rules::toDate)
                .skip(1)//skip departure of the first segment
                .limit(flight.getSegments().size() * 2L - 2)//remove arrival of the last segment
                .map(Rules::toLong)
                .reduce(0L, (arr, dep) -> dep - arr);//count the total ground time
    }

    private static Stream<LocalDateTime> toDate(Segment segment) {
        return Stream.of(segment.getDepartureDate(), segment.getArrivalDate());
    }

    private static long toLong(LocalDateTime date) {
        return Timestamp.valueOf(date).getTime();
    }
}