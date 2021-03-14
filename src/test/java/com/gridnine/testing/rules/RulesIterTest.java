package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;
import com.gridnine.testing.util.FlightBuilder;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.gridnine.testing.rules.RulesIter.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RulesIterTest {
    private final LocalDateTime dayFromNow = LocalDateTime.now().plusDays(1);
    private List<Flight> flights = new ArrayList<>();
//    private final RulesIter rules = new RulesIter();
    private final RulesStream rules = new RulesStream();

    @Test
    public void removeFlightIf_departureInPast_the_normal_flights_are_remain_unchanged() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        List<Flight> result = rules.removeFlightIf(departureInPast).filter(flights);

        assertEquals(flights, result);
    }

    @Test
    public void removeFlightIf_departureInPast_flights_with_departures_in_the_past_are_filtered_off() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.minusDays(4), dayFromNow.minusDays(4).plusHours(6)));
        List<Flight> result = rules.removeFlightIf(departureInPast).filter(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void removeFlightIf_departureAfterArrival_the_normal_flights_are_remain_unchanged() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        List<Flight> result = rules.removeFlightIf(departureAfterArrival).filter(flights);

        assertEquals(flights, result);
    }

    @Test
    public void removeFlightIf_departureAfterArrival_flights_with_departures_in_the_past_are_filtered_off() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.minusDays(4), dayFromNow.minusDays(6)));
        List<Flight> result = rules.removeFlightIf(departureAfterArrival).filter(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void removeFlightIfOnGroundMoreThan_flights_that_satisfy_the_predicate_condition_are_remain_unchanged() {
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
        List<Flight> result = rules.removeFlightIfHoursOnGround(t -> t > limit).filter(flights);

        assertEquals(flights, result);
    }

    @Test
    public void removeFlightIfOnGroundMoreThan_flights_that_dont_satisfy_the_predicate_condition_are_filtered_off() {
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
        List<Flight> result = rules.removeFlightIfHoursOnGround(t -> t >= limit).filter(flights);

        assertNotEquals(flights, result);
        assertEquals(2, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(1), result.get(1));
    }

    @Test
    public void combine_some_filters_with_methods() {
        flights = FlightBuilder.createFlights();
        List<Flight> result = rules.removeFlightIf(departureInPast)
                .andThen(rules.removeFlightIf(departureAfterArrival))
                .andThen(rules.removeFlightIfHoursOnGround(t -> t >= 2))
                .filter(flights);

        assertNotEquals(flights, result);
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