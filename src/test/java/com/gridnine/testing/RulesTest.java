package com.gridnine.testing;

import org.junit.Test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RulesTest {
    private final LocalDateTime dayFromNow = LocalDateTime.now().plusDays(1);
    private final List<Flight> flights = new ArrayList<>();
    private final Rules rules = new Rules();


    private static Flight createFlight(final LocalDateTime... dates) {
        List<Segment> segments = new ArrayList<>(dates.length / 2);
        for (int i = 0; i < dates.length - 1; i += 2) {
            segments.add(new Segment(dates[i], dates[i + 1]));
        }
        return new Flight(segments);
    }

    @Test
    public void filterFlightWithDepInPast_the_normal_flights_remain_unchanged() {
        flights.add(createFlight(dayFromNow, dayFromNow.plusHours(1)));
        List<Flight> result = rules.filterFlightWithDepInPast(flights);

        assertEquals(result, flights);
    }

    @Test
    public void filterFlightWithDepInPast_flights_with_departures_in_the_past_are_filtered_off() {
        flights.add(createFlight(dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.minusDays(4), dayFromNow.minusDays(4).plusHours(6)));
        System.out.println(flights);
        List<Flight> result = rules.filterFlightWithDepInPast(flights);
        System.out.println(result);

        assertNotEquals(result, flights);
        assertEquals(1, result.size());
        assertEquals(dayFromNow.plusHours(1), result.get(0).getSegments().get(0).getArrivalDate());
    }

    @Test
    public void departureInPast2_the_normal_flights_remain_unchanged2() {
        flights.add(createFlight(dayFromNow, dayFromNow.plusHours(1)));
        List<Flight> result = rules.departureInPast2.filter(flights);

        assertEquals(result, flights);
    }

    @Test
    public void departureInPast2_flights_with_departures_in_the_past_are_filtered_off() {
        flights.add(createFlight(dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.minusDays(4), dayFromNow.minusDays(4).plusHours(6)));
        List<Flight> result = rules.departureInPast2.filter(flights);

        assertNotEquals(result, flights);
        assertEquals(1, result.size());
        assertEquals(dayFromNow.plusHours(1), result.get(0).getSegments().get(0).getArrivalDate());
    }
}