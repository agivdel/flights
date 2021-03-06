package com.gridnine.testing;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.util.FlightBuilder;

import java.util.List;

import static com.gridnine.testing.rules.Rules.*;
import static com.gridnine.testing.rules.Predicates.*;
import static com.gridnine.testing.rules.TimeMeasure.*;

public class Main {

    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();
        System.out.println("flights before filter:");
        flights.forEach(System.out::println);
        List<Flight> result;

        //раздел фильтров №1: фильтруем полеты на уровне полей класса Segment
        //1. убираем полеты до текущего момента времени
        result = removeFlightIfDate(departureInPast).fromSource(flights);
        System.out.println("\nflights without departures in the past:");
        result.forEach(System.out::println);

        //2. убираем полеты с датой вылета позже даты вылета
        result = removeFlightIfDate(departureAfterArrival).fromSource(flights);
        System.out.println("\nflights without departures after arrival:");
        result.forEach(System.out::println);

        //3. убираем полеты с общим временем на земле свыше определенного значения
        result = removeFlightIfTotalGroundTime(notLessThen(ofHours(2))).fromSource(flights);
        System.out.println("\nwithout flights with a total time on ground 2 hours and more:");
        result.forEach(System.out::println);

        //4. убираем полеты с общим временем на земле менее определенного значения
        result = removeFlightIfTotalGroundTime(lessThen(ofMinutes(60))).fromSource(flights);
        System.out.println("\nwithout flights with a total time on ground less 1 hour");
        System.out.println("(in fact, it is only multi segment flights):");
        result.forEach(System.out::println);

        //5. убираем полеты с хотя бы одним временем на земле менее определенного значения
        result = removeFlightIfAnyGroundTime(lessThen(ofMinutes(60))).fromSource(flights);
        System.out.println("\nwithout flights with any time on ground less 1 hour");
        System.out.println("(in fact, it is only multi segment flights) - by Interval:");
        result.forEach(System.out::println);

        //6. применение нескольких фильтров одновременно
        result = removeFlightIfDate(departureInPast)
                .andThen(removeFlightIfDate(departureAfterArrival))
                .andThen(removeFlightIfTotalGroundTime(notLessThen(ofHours(2))))
                .fromSource(flights);
        System.out.println("\nthe normal flights with interval:");
        result.forEach(System.out::println);

        //раздел фильтров №2: фильтруем полеты на уровне полей класса Flight
        //7. убираем полеты с числом сегментов более одного
        result = removeFlightIfSegment(moreOne).fromSource(flights);
        System.out.println("\none segment flights:");
        result.forEach(System.out::println);

        //8. применение нескольких фильтров одновременно
        result = removeFlightIfSegment(moreOne)
                .andThen(removeFlightIfDate(departureInPast))
                .andThen(removeFlightIfDate(departureAfterArrival))
                .fromSource(flights);
        System.out.println("\none segment normal flights:");
        result.forEach(System.out::println);
    }
}