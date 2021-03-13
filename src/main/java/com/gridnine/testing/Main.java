package com.gridnine.testing;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();
        System.out.println("flights before filter: " + flights);
        Rules rules = new Rules();
        List<Flight> result;

        //1. вылет до текущего момента времени
        result = rules.departureInPastStream.filter(flights);
        System.out.println("\nflights without departures in the past: " + result);

        //2. имеются сегменты с датой вылета позже даты вылета
        result = rules.departureAfterArrivalStream.filter(flights);
        System.out.println("\nflights without departures after arrival: " + result);

        //3. общее время, проведённое на земле превышает два часа
        result = rules.stayOnGroundOverIterator(2, flights);
        System.out.println("\nwithout flights with a total time on ground 2 hours and more: " + result);


        //4. применение нескольких фильтров одновременно
        result = rules.departureInPastStream
                .andThen(rules.departureAfterArrivalStream)
                .andThen(rules.stayOnGroundOver2HoursIterator)
                .filter(flights);
        System.out.println("\nflights after working the several filters: " + result);

        //5. имеются полеты с общем временем на земле более определенного значения

        System.out.println("\nwithout flights with a total time on the ground 1 hour and less: " + result);
    }
}