package com.gridnine.testing;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();
        System.out.println("flights before filter: " + flights);
        Rules rules = new Rules();

        //1. вылет до текущего момента времени
        List<Flight> filteredFlights1_1 = rules.filterFlight(flights);
        System.out.println("flights after filter: " + filteredFlights1_1);

        List<Flight> filteredFlights1_2 = rules.departureInPast2.filter(flights);
        System.out.println("flights after filter: " + filteredFlights1_2);

        //2. имеются сегменты с датой прилёта раньше даты вылета
        List<Flight> filteredFlights2_1 = rules.removeDepartureBeforeArrival.filter(flights);
        System.out.println("flights after filter: " + filteredFlights2_1);

        //3. общее время, проведённое на земле превышает два часа
        // (время на земле — это интервал между прилётом одного сегмента
        // и вылетом следующего за ним)

    }
}