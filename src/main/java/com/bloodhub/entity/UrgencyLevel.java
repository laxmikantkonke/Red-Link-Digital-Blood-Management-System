package com.bloodhub.entity;

public enum UrgencyLevel {
    LOW("Low"),
    NORMAL("Normal"),
    HIGH("High"),
    CRITICAL("Critical");
    
    private final String displayName;
    
    UrgencyLevel(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
