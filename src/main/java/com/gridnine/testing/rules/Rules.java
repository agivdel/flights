package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Rules {
    public static final LocalDateTime now = LocalDateTime.now();
    public static final Predicate<Segment> departureInPast = s -> s.getDepartureDate().isBefore(now);
    public static final Predicate<Segment> departureAfterArrival = s -> s.getDepartureDate().isAfter(s.getArrivalDate());


    public static Rule<List<Flight>, List<Flight>> removeFlightIf(Predicate<Segment> predicate) {
        return new Rule<List<Flight>, List<Flight>>() {
            @Override
            public List<Flight> filter(List<Flight> flights) {
                return flights.stream()
                        .filter(f -> f.getSegments()
                                .stream()
                                .noneMatch(predicate))
                        .collect(toList());
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

//            BiFunction<Segment, LocalDateTime> accumulator = new BiFunction() {
//                @Override
//                public Object apply(Object o, Object o2) {
//                    return null;
//                }
//            };
//
//            BinaryOperator<> accumulator = new BinaryOperator() {
//                @Override
//                public Object apply(Object o, Object o2) {
//                    return null;
//                }
//            };
//
//            BiFunction<LocalDateTime, LocalDateTime, Long> combiner = ChronoUnit.HOURS::between;
//
//            Long stream = resultFlights.stream()
//                    .map(f -> f.getSegments()
//                            .stream()
//                            .reduce(0, combiner)
//                            .
//                    )
//
//            Stream<Object> stream = resultFlights.stream().map(f -> f.getSegments().stream().map(s -> s.getDepartureDate()));



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

    public List<Flight> stayOnGroundOverIterator(long limit, List<Flight> flights) {
        final Rule<List<Flight>, List<Flight>> stayOnGroundOver2Hours = new Rule<List<Flight>, List<Flight>>() {
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
                        if (time >= limit) {
                            iterator.remove();
                        }
                    }
                }
                return resultFlights;
            }
        };

        return stayOnGroundOver2Hours.filter(flights);
    }
}