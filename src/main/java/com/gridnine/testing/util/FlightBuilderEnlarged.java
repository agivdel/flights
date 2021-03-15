package com.gridnine.testing.util;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlightBuilderEnlarged {
    public static List<Flight> createFlights() {
        LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3);
        return Arrays.asList(
                //A normal flight with two hour duration
                createFlight(
                        threeDaysFromNow, threeDaysFromNow.plusHours(2)),
                //A normal multi segment flight
                createFlight(
                        threeDaysFromNow, threeDaysFromNow.plusHours(2),
                        threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(5)),
                //A normal multi segment flight with ground time less 1 hours
                createFlight(
                        threeDaysFromNow, threeDaysFromNow.plusMinutes(120),
                        threeDaysFromNow.plusMinutes(165), threeDaysFromNow.plusHours(5)),
                //A flight departing in the past
                createFlight(
                        threeDaysFromNow.minusDays(6), threeDaysFromNow),
                //A flight that departs after it arrives
                createFlight(
                        threeDaysFromNow, threeDaysFromNow.minusHours(6)),
                //A flight with one hour ground time
                createFlight(
                        threeDaysFromNow, threeDaysFromNow.plusHours(2),
                        threeDaysFromNow.plusHours(5), threeDaysFromNow.plusHours(6)),
                //Another flight with more than five hours ground time
                createFlight(
                        threeDaysFromNow, threeDaysFromNow.plusHours(2),
                        threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(4),
                        threeDaysFromNow.plusHours(9), threeDaysFromNow.plusHours(10))
        );
    }

    private static Flight createFlight(final LocalDateTime... dates) {
        if (dates.length % 2 != 0) {
            throw new IllegalArgumentException("you must pass an even number of dates");
        }
        List<Segment> segments = new ArrayList<>(dates.length / 2);
        for (int i = 0; i < dates.length - 1; i += 2) {
            segments.add(new Segment(dates[i], dates[i + 1]));
        }
        return new Flight(segments);
    }
}