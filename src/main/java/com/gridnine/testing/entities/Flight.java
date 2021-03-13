package com.gridnine.testing.entities;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Bean that represents a flight.
 */
public class Flight {
    private final List<Segment> segments;

    public Flight(List<Segment> segments) {
        this.segments = segments;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    @Override
    public String toString() {
        return segments.stream().map(Objects::toString).collect(Collectors.joining(" "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flight)) return false;
        Flight flight = (Flight) o;
        return segments.equals(flight.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments);
    }
}