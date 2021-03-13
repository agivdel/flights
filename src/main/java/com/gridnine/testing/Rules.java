package com.gridnine.testing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Rules {
    public static final LocalDateTime now = LocalDateTime.now();

    public List<Flight> filterFlight(List<Flight> flights) {
        return departureInPast.filter(flights);
    }


    /**вариант №1*/
    private final Rule<List<Flight>, List<Flight>> departureInPast = new Rule<List<Flight>, List<Flight>>() {
        @Override
        public List<Flight> filter(List<Flight> flights) {
            List<Flight> newFlights = new ArrayList<>();
            final List<Flight> oldFlights = new ArrayList<>(flights);
            for (Flight oldFlight : oldFlights) {
                List<Segment> newSegments = new ArrayList<>();
                List<Segment> oldSegments = oldFlight.getSegments();
                for (Segment oldSegment : oldSegments) {
                    if (oldSegment.getDepartureDate().isAfter(now)) {
                        newSegments.add(oldSegment);
                    }
                }
                if (!newSegments.isEmpty()) {
                    newFlights.add(new Flight(newSegments));
                }
            }
            return newFlights;
        }
    };


    /**вариант №2*/
    final Rule<List<Flight>, List<Flight>> departureInPast2 = new Rule<List<Flight>, List<Flight>>() {
        @Override
        public List<Flight> filter(List<Flight> flights) {
            return flights.stream()
                    .map(f -> removeFlightsWithDepInPast(f))
                    .filter(f -> !f.getSegments().isEmpty())//убираем flight с пустыми списками
                    .collect(toList());
        }
    };

    private Flight removeFlightsWithDepInPast(Flight flight) {
        List<Segment> list = flight.getSegments()
                .stream()
                .filter(s -> s.getDepartureDate().isAfter(now))
                .collect(toList());
        return new Flight(list);
    }
}