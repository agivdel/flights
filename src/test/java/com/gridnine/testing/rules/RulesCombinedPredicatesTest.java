package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.gridnine.testing.rules.Rules.*;
import static com.gridnine.testing.rules.TimeMeasure.*;
import static com.gridnine.testing.rules.Predicates.*;
import static com.gridnine.testing.util.FlightBuilderEnlarged.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RulesCombinedPredicatesTest {
    private List<Flight> flights;
    private List<Flight> result;

    @Before
    public void createLists() {
        flights = new ArrayList<>();
        result = new ArrayList<>();
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
                lessThen(ofHours(1)).or(moreThen(ofHours(2))))
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
                lessThen(ofHours(1)).or(equal(ofHours(5))))
                .fromSource(flights);

        assertNotEquals(flights, result);
        assertEquals(2, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(2), result.get(1));
    }
}