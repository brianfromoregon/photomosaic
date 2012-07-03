package com.brianfromoregon.tiles;

import java.util.Arrays;

public final class Tuple {
    private final Object[] vals;

    private Tuple(Object[] vals) {
        this.vals = vals;
    }

    public static Tuple tuple(Object... vals) {
        return new Tuple(vals);
    }

    public <T> T get(int index) {
        return (T) vals[index];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return Arrays.equals(vals, ((Tuple) o).vals);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vals);
    }
}
