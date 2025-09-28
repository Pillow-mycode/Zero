package com.software.zero.enums;

public enum ScrollViewLocation {
    TOP(1),
    MIDDLE(2),
    BOTTOM(3);

    private final int value;

    ScrollViewLocation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
