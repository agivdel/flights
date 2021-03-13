package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class Rules {
    public static final LocalDateTime now = LocalDateTime.now();
    public static final Predicate<Segment> departureInPast = s -> s.getDepartureDate().isBefore(now);
    public static final Predicate<Segment> departureAfterArrival = (s) -> s.getDepartureDate().isAfter(s.getArrivalDate());

    /**
     * 1.1) удаление полетов с вылетами в будущем: вариант №1
     */
    public final Rule<List<Flight>, List<Flight>> departureInPastIterator = new Rule<List<Flight>, List<Flight>>() {
        @Override
        public List<Flight> filter(List<Flight> flights) {
            return removeFlightIf(departureInPast, flights);
        }
    };

    /**
     * 2.1) удаление полетов с вылетами позже прилетов: вариант №1
     */
    public final Rule<List<Flight>, List<Flight>> departureAfterArrivalIterator = new Rule<List<Flight>, List<Flight>>() {
        @Override
        public List<Flight> filter(List<Flight> flights) {
            return removeFlightIf(departureAfterArrival, flights);
        }
    };

    public List<Flight> removeFlightIf(Predicate<Segment> predicate, List<Flight> flights) {
        List<Flight> resultFlights = new ArrayList<>(flights);
        Iterator<Flight> iterator = resultFlights.iterator();
        while (iterator.hasNext()) {
            Flight flight = iterator.next();
            for (Segment segment : flight.getSegments()) {
                if (predicate.test(segment)) {
                    iterator.remove();
                }
            }
        }
        return resultFlights;
    }

    public static Rule<List<Flight>, List<Flight>> removeFlightIf(Predicate<Segment> predicate) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> filter(List<Flight> flights1) {
                List<Flight> resultFlights = new ArrayList<>(flights1);
                Iterator<Flight> iterator = resultFlights.iterator();
                while (iterator.hasNext()) {
                    Flight flight = iterator.next();
                    for (Segment segment : flight.getSegments()) {
                        if (predicate.test(segment)) {
                            iterator.remove();
                        }
                    }
                }
                return resultFlights;
            }
        };
    }

    /**
     * 3) удаление полетов с общим временем на земле свыше 2 часов
     */
    public final Rule<List<Flight>, List<Flight>> stayOnGroundOver2HoursIterator = new Rule<List<Flight>, List<Flight>>() {
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
                    if (time >= 2) {
                        iterator.remove();
                    }
                }
            }
            return resultFlights;
        }
    };

    public List<Flight> removeFlightIfOnGroundMoreThan(long limit, List<Flight> flights) {
        List<Flight> resultFlights = new ArrayList<>(flights);
        Iterator<Flight> iterator = resultFlights.iterator();
        while (iterator.hasNext()) {
            Flight flight = iterator.next();
            long time = 0;
            LocalDateTime from = resultFlights.get(0).getSegments().get(0).getDepartureDate();//чтобы для первого segment время на земле = 0
            for (Segment segment : flight.getSegments()) {
                time += ChronoUnit.HOURS.between(from, segment.getDepartureDate());
                from = segment.getArrivalDate();
                if (time >= limit) {
                    iterator.remove();
                }
            }
        }
        return resultFlights;
    }

    public static Rule<List<Flight>, List<Flight>> removeFlightIfOnGroundMoreThan(Predicate<Long> predicate) {
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

    public static Rule<List<Flight>, List<Flight>> removeFlightIfOnGroundLessThan(Predicate<Long> predicate) {
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