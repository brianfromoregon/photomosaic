package com.brianfromoregon.tiles;

public enum ColorSpace {

    SRGB,
    CIELAB;

    public static ColorSpace fromString(String colorSpace) {
        return ColorSpace.valueOf(colorSpace.toUpperCase());
    }
}
