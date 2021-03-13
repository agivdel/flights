package com.gridnine.testing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Rules {
    public static final LocalDateTime now = LocalDateTime.now();

    /**1) удаление полетов с вылетами в будущем: вариант №1*/
    public List<Flight> filterFlightWithDepInPast(List<Flight> flights) {
        return departureInPast.filter(flights);
    }

    private final Rule<List<Flight>, List<Flight>> departureInPast = new Rule<List<Flight>, List<Flight>>() {
        @Override
        public List<Flight> filter(List<Flight> flights) {
            List<Flight> resultFlights = new ArrayList<>(flights);
            Iterator<Flight> iterator = resultFlights.iterator();
            while (iterator.hasNext()) {
                Flight flight = iterator.next();
                for (Segment segment : flight.getSegments()) {
                    if (segment.getDepartureDate().isBefore(now)) {
                        iterator.remove();
                    }
                }
            }
            return resultFlights;
        }
    };


    /**1) удаление полетов с вылетами в будущем: вариант №2*/
    final Rule<List<Flight>, List<Flight>> departureInPast2 = new Rule<List<Flight>, List<Flight>>() {
        @Override
        public List<Flight> filter(List<Flight> flights) {
            return flights.stream()
                    .filter(f -> f.getSegments()
                            .stream()
                            .allMatch(s -> s.getDepartureDate().isAfter(now)))
                    .collect(toList());
        }
    };

    /**2) удаление полетов с вылетами позже прилетов: вариант №1*/
    final Rule<List<Flight>, List<Flight>> departureAfterArrival = new Rule<List<Flight>, List<Flight>>() {
        @Override
        public List<Flight> filter(List<Flight> flights) {
            List<Flight> resultFlights = new ArrayList<>(flights);
            Iterator<Flight> iterator = resultFlights.iterator();
            while (iterator.hasNext()) {
                Flight flight = iterator.next();
                for (Segment segment : flight.getSegments()) {
                    if (segment.getDepartureDate().isAfter(segment.getArrivalDate())) {
                        iterator.remove();
                    }
                }
            }
            return resultFlights;
        }
    };

    /**2) удаление полетов с вылетами позже прилетов:: вариант №2*/
    final Rule<List<Flight>, List<Flight>> departureAfterArrival2 = new Rule<List<Flight>, List<Flight>>() {
        @Override
        public List<Flight> filter(List<Flight> flights) {
            return flights.stream()
                    .filter(f -> f.getSegments()
                            .stream()
                            .allMatch(s -> s.getDepartureDate().isBefore(s.getArrivalDate())))
                    .collect(toList());
        }
    };
}