package com.gridnine.testing;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;
import com.gridnine.testing.rules.Rule;
import com.gridnine.testing.rules.Rules;
import com.gridnine.testing.util.FlightBuilder;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.gridnine.testing.rules.Rules.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RulesTest {
    private final LocalDateTime dayFromNow = LocalDateTime.now().plusDays(1);
    private List<Flight> flights = new ArrayList<>();
    private final Rules rules = new Rules();

    @Test
    public void departureInPastIterator_the_normal_flights_are_remain_unchanged() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        List<Flight> result = rules.departureInPastIterator.filter(flights);

        assertEquals(flights, result);
    }

    @Test
    public void departureInPast_flights_with_departures_in_the_past_are_filtered_off() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.minusDays(4), dayFromNow.minusDays(4).plusHours(6)));

        List<Flight> result = ((Rule<List<Flight>, List<Flight>>) flights ->
                rules.removeFlightIf(departureInPast, flights)).filter(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void departureInPastIterator_flights_with_departures_in_the_past_are_filtered_off() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.minusDays(4), dayFromNow.minusDays(4).plusHours(6)));
        List<Flight> result = rules.departureInPastIterator.filter(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void iterateAndRemoveFlightIf_flights_with_departures_in_the_past_are_filtered_off() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(2),
                dayFromNow.minusDays(4), dayFromNow.minusDays(4).plusHours(6)));
//        List<Flight> result = rules.removeFlightIf(departureInPast, flights);
        List<Flight> result = Rules.removeFlightIf(departureInPast).filter(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void departureAfterArrival_the_normal_flights_are_remain_unchanged() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        List<Flight> result = rules.departureAfterArrivalIterator.filter(flights);

        assertEquals(flights, result);
    }

    @Test
    public void departureAfterArrival_flights_with_departures_after_arrival_are_filtered_off() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(
                dayFromNow, dayFromNow.minusDays(2)));
        List<Flight> result = rules.departureAfterArrivalIterator.filter(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void iterateAndRemoveFlightIf_flights_with_departures_after_arrival_are_filtered_off() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusHours(1)));
        flights.add(createFlight(
                dayFromNow, dayFromNow.minusDays(2)));
        List<Flight> result = rules.removeFlightIf(departureAfterArrival, flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void stayOnGroundOver2Hours_flights_with_staying_on_the_ground_less_than_2_hours_are_remain_unchanged() {
        flights.add(createFlight(
                dayFromNow, dayFromNow.plusMinutes(60),
                dayFromNow.plusMinutes(90), dayFromNow.plusMinutes(180),
                dayFromNow.plusMinutes(225), dayFromNow.plusMinutes(300)));
        List<Flight> result = rules.stayOnGroundOver2HoursIterator.filter(flights);

        assertEquals(flights, result);
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
        List<Flight> result = rules.stayOnGroundOver2HoursIterator.filter(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void combine_some_filters_with_anonymous_classes() {
        flights = FlightBuilder.createFlights();
        List<Flight> result = rules.departureInPastIterator
                .andThen(rules.departureAfterArrivalIterator)
                .andThen(rules.stayOnGroundOver2HoursIterator)
                .filter(flights);

        assertNotEquals(flights, result);
        assertEquals(2, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(1), result.get(1));
    }

    @Test
    public void combine_some_filters_with_methods() {
        flights = FlightBuilder.createFlights();
        List<Flight> result = removeFlightIf(departureInPast)
                .andThen(removeFlightIf(departureAfterArrival))
                .filter(flights);

        assertNotEquals(flights, result);
        assertEquals(4, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(1), result.get(1));
        assertEquals(flights.get(4), result.get(2));
        assertEquals(flights.get(5), result.get(3));
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
        List<Flight> result = removeFlightIfOnGroundMoreThan(t -> t > limit).filter(flights);

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
        List<Flight> result = removeFlightIfOnGroundMoreThan(t -> t >= limit).filter(flights);

        assertNotEquals(flights, result);
        assertEquals(2, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(1), result.get(1));
    }

    @Test
    public void removeFlightIfOnGroundLessThan_flights_that_satisfy_the_predicate_condition_are_remain_unchanged() {
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
        long limit = 1;
        List<Flight> result = removeFlightIfOnGroundLessThan(t -> t < limit).filter(flights);
        System.out.println(result);

        assertEquals(flights, result);
    }


    private static Flight createFlight(final LocalDateTime... dates) {
        List<Segment> segments = new ArrayList<>(dates.length / 2);
        for (int i = 0; i < dates.length - 1; i += 2) {
            segments.add(new Segment(dates[i], dates[i + 1]));
        }
        return new Flight(segments);
    }
}