package com.hotel.management.enums;

/**
 * Enum representing the status of a reservation
 */


public enum ReservationStatus {
    PENDING("Waiting for confirmation"),
    CONFIRMED("Confirmed"),
    SEATED("Customer seated"),
    IN_SERVICE("In service"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}