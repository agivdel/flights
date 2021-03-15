package com.gridnine.testing.rules;

import java.util.Objects;

@FunctionalInterface
public interface Rule<IN, OUT> {
    default <OUT2> Rule<IN, OUT2> andThen(Rule<OUT, OUT2> next) {
        Objects.requireNonNull(next);
        return in -> {
            OUT out1 = Rule.this.fromSource(in);
            return next.fromSource(out1);
        };
    }

    OUT fromSource(IN in);
}