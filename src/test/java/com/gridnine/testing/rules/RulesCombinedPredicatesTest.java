package com.gridnine.testing.rules;

import com.gridnine.testing.entities.Flight;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.gridnine.testing.rules.PredicateConstants.*;
import static com.gridnine.testing.rules.PredicateConstants.moreTwo;
import static com.gridnine.testing.rules.Rules.*;
import static com.gridnine.testing.rules.Rules.skipFlightIfAnyGroundTime;
import static com.gridnine.testing.util.FlightBuilderEnlarged.*;
import static com.gridnine.testing.util.FlightBuilderEnlarged.sixHoursGroundTimeFlight;
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
                ((Predicate<Long>) t -> t < TimeMeasure.ofHours(1))
                        .or(t -> t > TimeMeasure.ofHours(2)))
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
                ((Predicate<Long>) t -> t < TimeMeasure.ofHours(1))
                        .or(t -> t == TimeMeasure.ofHours(5)))
                .fromSource(flights);

        assertNotEquals(flights, result);
        assertEquals(2, result.size());
        assertEquals(flights.get(0), result.get(0));
        assertEquals(flights.get(2), result.get(1));
    }
}