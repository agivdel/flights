package com.gridnine.testing.util;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlightBuilderEnlarged {
    private static final LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3);

    public static final Flight normalFlight = createFlight(
            threeDaysFromNow, threeDaysFromNow.plusHours(2));
    public static final Flight normalMultiSegmentFlight = createFlight(
            threeDaysFromNow, threeDaysFromNow.plusHours(2),
            threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(5));
    public static final Flight less1HourGroundTimeFlight = createFlight(
            threeDaysFromNow, threeDaysFromNow.plusMinutes(120),
            threeDaysFromNow.plusMinutes(165), threeDaysFromNow.plusHours(5));
    public static final Flight departingInPastFlight = createFlight(
            threeDaysFromNow.minusDays(6), threeDaysFromNow);
    public static final Flight departureAfterArrivalFlight = createFlight(
            threeDaysFromNow, threeDaysFromNow.minusHours(6));
    public static final Flight threeHoursGroundTimeFlight = createFlight(
            threeDaysFromNow, threeDaysFromNow.plusHours(2),
            threeDaysFromNow.plusHours(5), threeDaysFromNow.plusHours(6));
    public static final Flight sixHoursGroundTimeFlight = createFlight(
            threeDaysFromNow, threeDaysFromNow.plusHours(2),
            threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(4),
            threeDaysFromNow.plusHours(9), threeDaysFromNow.plusHours(10));

    public static List<Flight> createFlights(Flight...flights) {
        return Arrays.asList(flights);
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