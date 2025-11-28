package com.hotel.management.enums;

/**
 * Enum representing user roles for authentication and authorization
 */
public enum UserRole {
    ROLE_ADMIN("Administrator"),
    ROLE_MANAGER("Manager"),
    ROLE_RECEPTIONIST("Receptionist"),
    ROLE_STAFF("Staff"),
    ROLE_USER("User");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAuthority() {
        return this.name();
    }

    @Override
    public String toString() {
        return displayName;
    }
}

