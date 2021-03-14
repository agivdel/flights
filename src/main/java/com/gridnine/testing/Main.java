package com.gridnine.testing;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.rules.RulesStream;
import com.gridnine.testing.util.FlightBuilder;

import java.util.List;

import static com.gridnine.testing.rules.RulesStream.*;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();
        System.out.println("flights before filter: " + flights);
        RulesStream rules = new RulesStream();
//        RulesStream rules = new RulesIter();
        List<Flight> result;

        //1. вылет до текущего момента времени
        result = rules.removeFlightIf(departureInPast).filter(flights);
        System.out.println("\nflights without departures in the past: " + result);

        //2. имеются сегменты с датой вылета позже даты вылета
        result = rules.removeFlightIf(departureAfterArrival).filter(flights);
        System.out.println("\nflights without departures after arrival: " + result);

        //3. общее время, проведённое на земле превышает определенное значение (два часа)
        result = rules.removeFlightIfHoursOnGround(t -> t >= 2).filter(flights);
        System.out.println("\nwithout flights with a total time on ground 2 hours and more: " + result);


        //4. применение нескольких фильтров одновременно
        result = rules.removeFlightIf(departureInPast)
                .andThen(rules.removeFlightIf(departureAfterArrival))
                .andThen(rules.removeFlightIfHoursOnGround(t -> t >= 2))
                .filter(flights);
        System.out.println("\nflights after working the several filters: " + result);

        //5. имеются полеты с общем временем на земле более определенного значения

        System.out.println("\nwithout flights with a total time on the ground 1 hour and less: " + result);
    }
}