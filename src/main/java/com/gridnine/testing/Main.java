package com.gridnine.testing;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.util.FlightBuilder;
import com.gridnine.testing.util.FlightBuilderEnlarged;

import java.util.List;

import static com.gridnine.testing.rules.Rules.*;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilderEnlarged.createFlights();
        System.out.println("flights before filter: " + flights);
        List<Flight> result;

        //раздел фильтров №1: фильтруем полеты на уровне полей класса Segment
        //1. убираем вылеты до текущего момента времени
        result = removeFlightIfDate(departureInPast).filter(flights);
//        System.out.println("\nflights without departures in the past: " + result);

        //2. убираем сегменты с датой вылета позже даты вылета
        result = removeFlightIfDate(departureAfterArrival).filter(flights);
//        System.out.println("\nflights without departures after arrival: " + result);

        //3. убираем полеты с общим временем на земле свыше определенного значения (два часа)
        result = removeFlightIfHoursOnGroundMore(t -> t >= 2).filter(flights);
//        System.out.println("\nwithout flights with a total time on ground 2 hours and more: " + result);


        //4. применение нескольких фильтров одновременно
        result = removeFlightIfDate(departureInPast)
                .andThen(removeFlightIfDate(departureAfterArrival))
                .andThen(removeFlightIfHoursOnGroundMore(t -> t >= 2))
                .filter(flights);
//        System.out.println("\nflights after working the several filters: " + result);

        //раздел фильтров №2: фильтруем полеты на уровне полей класса Flight
        //5. убираем полеты с числом сегментов, не равным одному
        result = removeFlightIfSegment(notOne).filter(flights);
//        System.out.println("\nflights with the number of segments one and less: " + result);

        //6. применение нескольких фильтров одновременно
        result = removeFlightIfDate(departureInPast)
                .andThen(removeFlightIfDate(departureAfterArrival))
                .andThen(removeFlightIfHoursOnGroundMore(t -> t >= 2))
                .andThen(removeFlightIfSegment(notOne))
                .andThen(removeFlightIfSegment(moreThanOne))
                .filter(flights);
//        System.out.println("\nflights after working the several filters: " + result);

        //7. применение нескольких фильтров одновременно
        result = slipFlightIfDate(departureAfterArrival)
                .filter(flights);
//        System.out.println("\nflights after working the several filters: " + result);
    }
}