package com.hotel.management.enums;

/**
 * Enum representing the role of a staff member
 */
public enum StaffRole {
    MANAGER("Manager"),
    RECEPTIONIST("Receptionist"),
    WAITER("Waiter"),
    CHEF("Chef"),
    CLEANER("Cleaner"),
    SECURITY("Security"),
    MAINTENANCE("Maintenance"),
    ADMIN("Administrator");

    private final String displayName;

    StaffRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
