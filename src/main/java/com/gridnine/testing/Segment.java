package com.gridnine.testing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Bean that represents a flight segment.
 */
public class Segment {
    private final LocalDateTime departureDate;
    private final LocalDateTime arrivalDate;

    Segment(LocalDateTime departure, LocalDateTime arrivalDate) {
        this.departureDate = Objects.requireNonNull(departure);
        this.arrivalDate = Objects.requireNonNull(arrivalDate);
    }

    LocalDateTime getDepartureDate() {
        return departureDate;
    }

    LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return '[' + departureDate.format(fmt) + '|' + arrivalDate.format(fmt) + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Segment)) return false;
        Segment segment = (Segment) o;
        return departureDate.equals(segment.departureDate) && arrivalDate.equals(segment.arrivalDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departureDate, arrivalDate);
    }
}