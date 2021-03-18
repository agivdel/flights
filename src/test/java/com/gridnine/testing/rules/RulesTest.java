package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.gridnine.testing.rules.Rules.*;
import static com.gridnine.testing.util.FlightBuilderEnlarged.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class RulesTest {
    private List<Flight> flights = new ArrayList<>();
    private List<Flight> result = new ArrayList<>();

    @Test
    public void removeFlightIfDate_departureInPast_the_normal_flights_are_remain_unchanged() {
        flights = createFlights(normalOneSegmentFlight);
        result = removeFlightIfDate(departureInPast).fromSource(flights);

        assertEquals(flights, result);
    }

    @Test
    public void removeFlightIfDate_departureInPast_flights_with_departures_in_the_past_are_filtered_off() {
        flights = createFlights(normalOneSegmentFlight, departureInPastFlight);
        result = removeFlightIfDate(departureInPast).fromSource(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void removeFlightIfDate_departureAfterArrival_the_normal_flights_are_remain_unchanged() {
        flights = createFlights(normalOneSegmentFlight, oneHourGroundTimeFlight);
        result = removeFlightIfDate(departureAfterArrival).fromSource(flights);

        assertEquals(flights, result);
    }

    @Test
    public void removeFlightIfDate_departureAfterArrival_flights_with_departures_in_the_past_are_filtered_off() {
        flights = createFlights(normalOneSegmentFlight, departureAfterArrivalFlight);
        result = removeFlightIfDate(departureAfterArrival).fromSource(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void skipFlightIfDate_departureInPast_skip_the_flights_with_departures_in_past() {
        flights = createFlights(normalOneSegmentFlight, oneHourGroundTimeFlight, departureInPastFlight);
        result = skipFlightIfDate(departureInPast).fromSource(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(2), result.get(0));
    }

    @Test
    public void removeFlightIfSegment_moreOne_the_normal_flights_are_remain_unchanged() {
        flights = createFlights(normalOneSegmentFlight);
        result = removeFlightIfSegment(moreOne).fromSource(flights);

        assertEquals(flights, result);
    }

    @Test
    public void removeFlightIfSegment_moreOne_the_flights_with_more_one_segment_are_filtered_off() {
        flights = createFlights(normalOneSegmentFlight, oneHourGroundTimeFlight);
        List<Flight> result = removeFlightIfSegment(moreOne).fromSource(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void skipFlightIfSegment_moreOne_the_flights_with_more_one_segment_are_remain_unchanged() {
        flights = createFlights(oneHourGroundTimeFlight, less1HourGroundTimeFlight);
        result = skipFlightIfSegment(moreOne).fromSource(flights);

        assertEquals(flights, result);
    }

    @Test
    public void removeFlightIfTotalGroundTime_the_normal_flights_are_remain_unchanged() {
        flights = createFlights(normalOneSegmentFlight, less1HourGroundTimeFlight);
        List<Flight> result = removeFlightIfTotalGroundTime(t -> t > 2 * TimeMeasure.HOURS.getMillis()).fromSource(flights);

        assertEquals(flights, result);
    }

    @Test
    public void removeFlightIfTotalGroundTime_the_flights_that_didnt_satisfy_predicate_are_filtered_off() {
        flights = createFlights(threeHoursGroundTimeFlight, sixHoursGroundTimeFlight);
        result = removeFlightIfTotalGroundTime(t -> t > 2 * TimeMeasure.HOURS.getMillis()).fromSource(flights);

        assertTrue(result.isEmpty());
    }

    @Test
    public void removeFlightIfTotalGroundTime_the_flights_that_didnt_satisfy_predicate_are_filtered_off2() {
        flights = createFlights(oneHourGroundTimeFlight, less1HourGroundTimeFlight);
        result = removeFlightIfTotalGroundTime(t -> t < 60 * TimeMeasure.MINUTES.getMillis()).fromSource(flights);

        assertNotEquals(flights, result);
        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void skipFlightIfTotalGroundTime_the_flights_that_didnt_satisfy_predicate_are_filtered_off() {
        flights = createFlights(
                less1HourGroundTimeFlight,
                oneHourGroundTimeFlight,
                threeHoursGroundTimeFlight,
                sixHoursGroundTimeFlight);
        result = skipFlightIfTotalGroundTime(t -> t < 60 * TimeMeasure.MINUTES.getMillis()).fromSource(flights);

        assertEquals(1, result.size());
        assertEquals(flights.get(0), result.get(0));
    }

    @Test
    public void removeFlightIfAnyGroundTime_the_flights_that_didnt_satisfy_predicate_are_filtered_off() {
        flights = createFlights(
                less1HourGroundTimeFlight,
                oneHourGroundTimeFlight,
                threeHoursGroundTimeFlight,
                sixHoursGroundTimeFlight);
        result = removeFlightIfAnyGroundTime(t -> t < 60 * TimeMeasure.MINUTES.getMillis()).fromSource(flights);

        assertEquals(3, result.size());
        assertEquals(flights.get(1), result.get(0));
        assertEquals(flights.get(2), result.get(1));
        assertEquals(flights.get(3), result.get(2));
    }

    @Test
    public void skipFlightIfAnyGroundTime_the_flights_that_didnt_satisfy_predicate_are_filtered_off() {
        flights = createFlights(
                less1HourGroundTimeFlight,
                oneHourGroundTimeFlight,
                threeHoursGroundTimeFlight,
                sixHoursGroundTimeFlight);
        result = skipFlightIfAnyGroundTime(t -> t < 60 * TimeMeasure.MINUTES.getMillis()).fromSource(flights);

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
                sixHoursGroundTimeFlight);
        result = removeFlightIfDate(departureInPast)
                .andThen(removeFlightIfDate(departureAfterArrival))
                .andThen(removeFlightIfTotalGroundTime(t -> t >= 60 * TimeMeasure.MINUTES.getMillis()))
                .fromSource(flights);

        assertNotEquals(flights, result);
        assertEquals(2, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(2), result.get(1));
    }

    @Test
    public void skipFlightIfDate_combine_some_predicates_with_or() {
        flights = createFlights(
                normalOneSegmentFlight,
                departureInPastFlight,
                departureAfterArrivalFlight);
        result = skipFlightIfDate(departureInPast.or(departureAfterArrival))
                .fromSource(flights);

        assertNotEquals(flights, result);
        assertEquals(2, result.size());
        assertEquals(flights.get(1), result.get(0));
        assertEquals(flights.get(2), result.get(1));
    }

    @Test
    public void skipFlightIfSegment_combine_some_predicates_with_or() {
        flights = createFlights(
                normalOneSegmentFlight,
                oneHourGroundTimeFlight,
                sixHoursGroundTimeFlight);
        result = skipFlightIfSegment(one.or(moreTwo))
                .fromSource(flights);

        assertNotEquals(flights, result);
        assertEquals(2, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(2), result.get(1));
    }

    @Test
    public void skipFlightIfTotalGroundTime_combine_some_predicates_with_or() {
        flights = createFlights(
                less1HourGroundTimeFlight,
                oneHourGroundTimeFlight,
                sixHoursGroundTimeFlight);
        result = skipFlightIfTotalGroundTime(
                ((Predicate<Long>) t -> t < TimeMeasure.HOURS.getMillis())
                        .or(t -> t > 2 * TimeMeasure.HOURS.getMillis()))
                .fromSource(flights);

        assertNotEquals(flights, result);
        assertEquals(2, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(2), result.get(1));
    }

    @Test
    public void skipFlightIfAnyGroundTime_combine_some_predicates_with_or() {
        flights = createFlights(
                less1HourGroundTimeFlight,
                oneHourGroundTimeFlight,
                sixHoursGroundTimeFlight);
        result = skipFlightIfAnyGroundTime(
                ((Predicate<Long>) t -> t < TimeMeasure.HOURS.getMillis())
                .or(t -> t == 5 * TimeMeasure.HOURS.getMillis()))
                .fromSource(flights);

        assertNotEquals(flights, result);
        assertEquals(2, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(2), result.get(1));
    }
}