package com.gridnine.testing;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;
import com.gridnine.testing.rules.RulesStream;
import com.gridnine.testing.util.FlightBuilder;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.gridnine.testing.rules.RulesStream.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RulesStreamTest {
    private final LocalDateTime dayFromNow = LocalDateTime.now().plusDays(1);
    private List<Flight> flights = new ArrayList<>();
    private final RulesStream rulesStream = new RulesStream();

    @Test
    public void removeFlightIf_departureInPast_the_normal_flights_are_remain_unchanged2() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        List<Flight> result = removeFlightIf(departureInPast).filter(flights);

        assertEquals(result, flights);
    }

    @Test
    public void removeFlightIf_departureInPast_flights_with_departures_in_the_past_are_filtered_off() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.minusDays(4), dayFromNow.minusDays(4).plusHours(6)));
        List<Flight> result = removeFlightIf(departureInPast).filter(flights);

        assertNotEquals(result, flights);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void removeFlightIf_departureAfterArrival_the_normal_flights_are_remain_unchanged() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        List<Flight> result = removeFlightIf(departureAfterArrival).filter(flights);

        assertEquals(result, flights);
    }

    @Test
    public void removeFlightIf_departureAfterArrival_flights_with_departures_after_arrival_are_filtered_off() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(
                dayFromNow, dayFromNow.minusDays(2)));
        List<Flight> result = removeFlightIf(departureAfterArrival).filter(flights);

        assertNotEquals(result, flights);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void stayOnGroundOver2Hours_the_normal_flights_are_remain_unchanged() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusMinutes(60),
                dayFromNow.plusMinutes(90), dayFromNow.plusMinutes(180),
                dayFromNow.plusMinutes(225), dayFromNow.plusMinutes(300)));
        List<Flight> result = rulesStream.stayOnGroundOver2HoursIterator.filter(flights);

        assertEquals(result, flights);
    }

    @Test
    public void stayOnGroundOver2Hours_flights_with_staying_on_the_ground_more_2_hours_are_filtered_off() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.plusHours(3), dayFromNow.plusHours(5),
                dayFromNow.plusHours(6), dayFromNow.plusHours(8)
        ));
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.plusHours(5), dayFromNow.plusHours(6)
        ));
        List<Flight> result = rulesStream.stayOnGroundOver2HoursIterator.filter(flights);

        assertNotEquals(result, flights);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void combine_some_filters() {
        flights = FlightBuilder.createFlights();
        List<Flight> result = removeFlightIf(departureInPast)
                .andThen(removeFlightIf(departureAfterArrival))
                .andThen(rulesStream.stayOnGroundOver2HoursIterator)
                .filter(flights);

        assertNotEquals(result, flights);
        assertEquals(2, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(1), result.get(1));
    }

    @Test
    public void stayOnGroundOver_flights_with_staying_on_the_ground_shorter_than_limit_are_remain_unchanged() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.plusHours(3), dayFromNow.plusHours(5),
                dayFromNow.plusHours(6), dayFromNow.plusHours(8)
        ));
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.plusHours(5), dayFromNow.plusHours(6)
        ));
        long limit = 4;
        List<Flight> result = rulesStream.stayOnGroundOverIterator(limit, flights);

        assertEquals(result, flights);
    }

    @Test
    public void stayOnGroundOver_flights_with_staying_on_the_ground_more_limit_are_filtered_off() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.plusHours(3), dayFromNow.plusHours(5),
                dayFromNow.plusHours(6), dayFromNow.plusHours(8)
        ));
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.plusHours(5), dayFromNow.plusHours(6)
        ));
        long limit = 3;
        List<Flight> result = rulesStream.stayOnGroundOverIterator(limit, flights);

        assertNotEquals(result, flights);
        assertEquals(2, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(1), result.get(1));
    }


    private static Flight createFlight(final LocalDateTime... dates) {
        List<Segment> segments = new ArrayList<>(dates.length / 2);
        for (int i = 0; i < dates.length - 1; i += 2) {
            segments.add(new Segment(dates[i], dates[i + 1]));
        }
        return new Flight(segments);
    }
}