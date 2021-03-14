package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class RulesIter {
    public static final LocalDateTime now = LocalDateTime.now();
    public static final Predicate<Segment> departureInPast = s -> s.getDepartureDate().isBefore(now);
    public static final Predicate<Segment> departureAfterArrival = (s) -> s.getDepartureDate().isAfter(s.getArrivalDate());

    public Rule<List<Flight>, List<Flight>> removeFlightIf(Predicate<Segment> predicate) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> filter(List<Flight> flights) {
                List<Flight> result = new ArrayList<>(flights);
                Iterator<Flight> iterator = result.iterator();
                while (iterator.hasNext()) {
                    Flight flight = iterator.next();
                    for (Segment segment : flight.getSegments()) {
                        if (predicate.test(segment)) {
                            iterator.remove();
                        }
                    }
                }
                return result;
            }

        };
    }

    interface Handler<T, R> {
        R run(T t);
    }

    <R> R flightIterator(List<Flight> flights, Handler<Flight, R> handler) {
        List<Flight> resultFlights = new ArrayList<>(flights);
        Iterator<Flight> iterator = resultFlights.iterator();
        R result = null;
        while (iterator.hasNext()) {
            Flight flight = iterator.next();
            result = handler.run(flight);
        }
        return result;
    }

    <R> R segmentIterator(List<Flight> flights, Handler<Segment, R> handler) {
        List<Flight> resultFlights = new ArrayList<>(flights);
        Iterator<Flight> iterator = resultFlights.iterator();
        R result = null;
        while (iterator.hasNext()) {
            Flight flight = iterator.next();
            for (Segment segment : flight.getSegments()) {
                result = handler.run(segment);
            }
        }
        return result;
    }

    public Rule<List<Flight>, List<Flight>> removeFlightIfHoursOnGround(Predicate<Long> predicate) {
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

    public Rule<List<Flight>, List<Flight>> removeFlightIfOnGroundLessThan(Predicate<Long> predicate) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> filter(List<Flight> flights) {
                List<Flight> resultFlights = new ArrayList<>(flights);
                Iterator<Flight> iterator = resultFlights.iterator();
                while (iterator.hasNext()) {
                    Flight flight = iterator.next();
                    LocalDateTime from = resultFlights.get(0).getSegments().get(0).getDepartureDate();//чтобы для первого segment время на земле = 0
                    for (Segment segment : flight.getSegments()) {
                        long time = ChronoUnit.HOURS.between(from, segment.getDepartureDate());
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