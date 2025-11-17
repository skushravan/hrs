package com.hotel.management.enums;

public enum RatingStatus {
    PENDING("Pending Review"),
    APPROVED("Approved"),
    REJECTED("Rejected");
    
    private final String displayName;
    
    RatingStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}