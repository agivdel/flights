package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.util.FlightBuilderEnlarged;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.gridnine.testing.rules.Rules.*;
import static com.gridnine.testing.util.FlightBuilderEnlarged.*;

import java.util.ArrayList;
import java.util.List;

public class RulesTest {
    private List<Flight> flights = new ArrayList<>();

    @Test
    public void removeFlightIfDate_departureInPast_the_normal_flights_are_remain_unchanged() {
        flights = FlightBuilderEnlarged.createFlights(normalOneSegmentFlight);
        List<Flight> result = removeFlightIfDate(departureInPast).filter(flights);

        assertEquals(flights, result);
    }

    @Test
    public void removeFlightIfDate_departureInPast_flights_with_departures_in_the_past_are_filtered_off() {
        flights = createFlights(normalOneSegmentFlight, departureInPastFlight);
        List<Flight> result = removeFlightIfDate(departureInPast).filter(flights);


        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void removeFlightIfDate_departureAfterArrival_the_normal_flights_are_remain_unchanged() {
        flights = createFlights(normalOneSegmentFlight, oneHourGroundTimeFlight);
        List<Flight> result = removeFlightIfDate(departureAfterArrival).filter(flights);

        assertEquals(flights, result);
    }

    @Test
    public void removeFlightIfDate_departureAfterArrival_flights_with_departures_in_the_past_are_filtered_off() {
        flights = createFlights(normalOneSegmentFlight, departureAfterArrivalFlight);
        List<Flight> result = removeFlightIfDate(departureAfterArrival).filter(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void removeFlightIfSegment_moreOne_the_normal_flights_are_remain_unchanged() {
        flights = createFlights(normalOneSegmentFlight);
        List<Flight> result = removeFlightIfSegment(moreOne).filter(flights);

        assertEquals(flights, result);
    }

    @Test
    public void removeFlightIfSegment_moreOne_the_flights_with_more_one_segment_are_filtered_off() {
        flights = createFlights(normalOneSegmentFlight, oneHourGroundTimeFlight);
        List<Flight> result = removeFlightIfSegment(moreOne).filter(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void removeFlightIfTotalGroundTime_the_normal_flights_are_remain_unchanged() {
        flights = createFlights(normalOneSegmentFlight, less1HourGroundTimeFlight);
        List<Flight> result = removeFlightIfTotalGroundTime(t -> t > 2).filter(flights);
        result.forEach(System.out::println);

        assertEquals(flights, result);
    }

    @Test
    public void removeFlightIfTotalGroundTime_the_flights_that_didnt_satisfy_predicate_are_filtered_off() {
        flights = createFlights(threeHoursGroundTimeFlight, sixHoursGroundTimeFlight);
        List<Flight> result = removeFlightIfTotalGroundTime(t -> t > 2).filter(flights);

        assertTrue(result.isEmpty());
    }

    @Test
    public void removeFlightIfTotalGroundTime_the_flights_that_didnt_satisfy_predicate_are_filtered_of2() {
        flights = createFlights(oneHourGroundTimeFlight, less1HourGroundTimeFlight);
        List<Flight> result = removeFlightIfTotalGroundTime(t -> t < 1).filter(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void combine_some_filters_with_methods() {
        flights = createFlights(
                normalOneSegmentFlight,
                oneHourGroundTimeFlight,
                less1HourGroundTimeFlight,
                departureInPastFlight,
                departureAfterArrivalFlight,
                threeHoursGroundTimeFlight,
                sixHoursGroundTimeFlight
        );
        List<Flight> result = removeFlightIfDate(departureInPast)
                .andThen(removeFlightIfDate(departureAfterArrival))
                .andThen(removeFlightIfTotalGroundTime(t -> t>= 1))
                .filter(flights);

        assertNotEquals(flights, result);
        assertEquals(2, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(2), result.get(1));
    }
}