package com.gridnine.testing;

import com.gridnine.testing.entities.Flight;
import com.gridnine.testing.rules.TimeUnit;
import com.gridnine.testing.util.FlightBuilder;

import java.util.List;

import static com.gridnine.testing.rules.Rules.*;

public class Main {
    //TODO java doc;
    //TODO поиск полетов с хотя бы одним временем пересадки менее определенного значения
    // (сейчас идет поиск накопленного значения);
    //TODO убрать жесткую зависимость от реализации класа правил

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
        result = removeFlightIfTotalGroundTime(t -> t >= 2, TimeUnit.HOURS).fromSource(flights);
        System.out.println("\nwithout flights with a total time on ground 2 hours and more:");
        result.forEach(System.out::println);

        //4. убираем полеты с общим временем на земле менее определенного значения
        result = removeFlightIfTotalGroundTime(t -> t < 60, TimeUnit.MINUTES).fromSource(flights);
        System.out.println("\nwithout flights with a total time on ground less 1 hour");
        System.out.println("(in fact, it is only multi segment flights):");
        result.forEach(System.out::println);

        //5. применение нескольких фильтров одновременно
        result = removeFlightIfDate(departureInPast)
                .andThen(removeFlightIfDate(departureAfterArrival))
                .andThen(removeFlightIfTotalGroundTime(t -> t >= 2, TimeUnit.HOURS))
                .fromSource(flights);
        System.out.println("\nthe normal flights:");
        result.forEach(System.out::println);



        //раздел фильтров №2: фильтруем полеты на уровне полей класса Flight
        //6. убираем полеты с числом сегментов более одного
        result = removeFlightIfSegment(moreOne).fromSource(flights);
        System.out.println("\none segment flights:");
        result.forEach(System.out::println);

        //7. применение нескольких фильтров одновременно
        result = removeFlightIfSegment(moreOne)
                .andThen(removeFlightIfDate(departureInPast))
                .andThen(removeFlightIfDate(departureAfterArrival))
                .fromSource(flights);
        System.out.println("\none segment normal flights:");
        result.forEach(System.out::println);
    }
}