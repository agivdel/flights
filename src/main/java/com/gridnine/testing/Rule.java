package com.gridnine.testing;

import java.util.Objects;

@FunctionalInterface
public interface Rule<IN, OUT> {
    default <OUT2> Rule<IN, OUT2> andThen(Rule<OUT, OUT2> next) {
        Objects.requireNonNull(next);
        return in -> {
            OUT out1 = Rule.this.filter(in);
            return next.filter(out1);
        };
    }

    OUT filter(IN in);
}