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

    /**Examples of some prepared predicates */
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
                return flights.stream()
                        .filter(f -> !ifTotalGroundTime(f, predicate, unit))
                        .collect(toList());
            }
        };
    }

    public static Rule<List<Flight>, List<Flight>> skipFlightIfTotalGroundTime(Predicate<Long> predicate, TimeUnit unit) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> fromSource(List<Flight> flights) {
                return flights.stream()
                        .filter(f -> ifTotalGroundTime(f, predicate, unit))
                        .collect(toList());
            }
        };
    }

    public static Rule<List<Flight>, List<Flight>> removeFlightIfAnyGroundTime(Predicate<Long> predicate, TimeUnit unit) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> fromSource(List<Flight> flights) {
                return flights.stream()
                        .filter(f -> !ifAnyGroundTime(f, predicate, unit))
                        .collect(toList());
            }
        };
    }

    public static Rule<List<Flight>, List<Flight>> skipFlightIfAnyGroundTime(Predicate<Long> predicate, TimeUnit unit) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> fromSource(List<Flight> flights) {
                return flights.stream()
                        .filter(f -> ifAnyGroundTime(f, predicate, unit))
                        .collect(toList());
            }
        };
    }

    /**Auxiliary methods for counting the total ground time for each flight.
     * Returns whether the total ground time of this flight match the provided predicate.*/
    private static boolean ifTotalGroundTime(Flight flight, Predicate<Long> predicate, TimeUnit unit) {
        return predicate.test(totalGroundTime(flight) / unit.getValue());
    }

    private static long totalGroundTime(Flight flight) {
        return streamOfLongFrom(flight)
                .reduce(0L, (arr, dep) -> dep - arr);//count the total ground time
    }

    /**Auxiliary methods for checking each transfers
     * (time between two neighboring segments of a flight)
     * for compliance with the predicate
     * Returns whether any transfer of this flight match the provided predicate.*/
    private static boolean ifAnyGroundTime(Flight flight, Predicate<Long> predicate, TimeUnit unit) {
        List<Long> list = toLongList(flight);
        return toPairs(list).stream()
                .map(Pair::getDifference)
                .map(d -> d / unit.getValue())
                .anyMatch(predicate);
    }

    private static List<Long> toLongList(Flight flight) {
        return streamOfLongFrom(flight).collect(toList());
    }

    private static List<Pair> toPairs(List<Long> list) {
        List<Pair> pairs = new ArrayList<>();
        for (int i = 0; i < list.size(); i += 2) {
            Pair pair = new Pair(list.get(i), list.get(i + 1));
            pairs.add(pair);
        }
        return pairs;
    }

    /**General auxiliary methods and classes */
    private static Stream<Long> streamOfLongFrom(Flight flight) {
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

    static class Pair {
        private final long left;
        private final long right;

        public Pair(long left, long right) {
            this.left = left;
            this.right = right;
        }

        public static long getDifference(Pair pair) {
            return pair.right - pair.left;
        }
    }
}