package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.entities.Segment;
import com.gridnine.testing.util.FlightBuilder;
import com.gridnine.testing.util.FlightBuilderEnlarged;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.gridnine.testing.rules.Rules.*;
import static com.gridnine.testing.util.FlightBuilderEnlarged.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RulesTest {
    private final LocalDateTime dayFromNow = LocalDateTime.now().plusDays(1);
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
    public void removeFlightIfHoursOnGroundMore_the_normal_flights_are_remain_unchanged() {
        flights = createFlights(normalOneSegmentFlight, oneHourGroundTimeFlight);
        long limit = 2;
        List<Flight> result = removeFlightIfHoursOnGroundMore(t -> t > limit).filter(flights);

        assertEquals(flights, result);
    }

    @Test
    public void removeFlightIfHoursOnGroundMore_flights_with_ground_time_more_limit_are_filtered_off() {
        flights = createFlights(oneHourGroundTimeFlight, threeHoursGroundTimeFlight, sixHoursGroundTimeFlight);
        long limit = 3;
        List<Flight> result = removeFlightIfHoursOnGroundMore(t -> t > limit).filter(flights);

        assertNotEquals(flights, result);
        assertEquals(2, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(1), result.get(1));
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
}