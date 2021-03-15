package com.gridnine.testing;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.rules.Rules;
import com.gridnine.testing.util.FlightBuilder;

import java.util.List;

import static com.gridnine.testing.rules.Rules.*;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();
        System.out.println("flights before filter: " + flights);
        Rules rules = new Rules();
        List<Flight> result;

        //раздел фильтров №1: фильтруем полеты на уровне полей класса Segment
        //1. убираем вылеты до текущего момента времени
        result = rules.removeFlightIfDate(departureInPast).filter(flights);
        System.out.println("\nflights without departures in the past: " + result);

        //2. убираем сегменты с датой вылета позже даты вылета
        result = rules.removeFlightIfDate(departureAfterArrival).filter(flights);
        System.out.println("\nflights without departures after arrival: " + result);

        //3. убираем полеты с общим временем на земле свыше определенного значения (два часа)
        result = rules.removeFlightIfHoursOnGroundMore(t -> t >= 2).filter(flights);
        System.out.println("\nwithout flights with a total time on ground 2 hours and more: " + result);


        //4. применение нескольких фильтров одновременно
        result = rules.removeFlightIfDate(departureInPast)
                .andThen(rules.removeFlightIfDate(departureAfterArrival))
                .andThen(rules.removeFlightIfHoursOnGroundMore(t -> t >= 2))
                .filter(flights);
        System.out.println("\nflights after working the several filters: " + result);

        //раздел фильтров №2: фильтруем полеты на уровне полей класса Flight
        //5. убираем полеты с числом сегментов, не равным одному
        result = rules.removeFlightIfSegment(notOne).filter(flights);
        System.out.println("\nflights with the number of segments one and less: " + result);

        //6. применение нескольких фильтров одновременно
        result = rules.removeFlightIfDate(departureInPast)
                .andThen(rules.removeFlightIfDate(departureAfterArrival))
                .andThen(rules.removeFlightIfHoursOnGroundMore(t -> t >= 2))
                .andThen(rules.removeFlightIfSegment(notOne))
                .filter(flights);
        System.out.println("\nflights after working the several filters: " + result);
    }
}