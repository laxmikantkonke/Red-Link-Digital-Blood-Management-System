package com.bloodhub.entity;

public enum City {
    MUMBAI("Mumbai"),
    PUNE("Pune"),
    NAGPUR("Nagpur"),
    THANE("Thane"),
    NASHIK("Nashik"),
    SOLAPUR("Solapur"),
    AMRAVATI("Amravati"),
    KOLHAPUR("Kolhapur"),
    SANGLI("Sangli"),
    JALGAON("Jalgaon"),
    AKOLA("Akola"),
    NANDED("Nanded"),
    LATUR("Latur"),
    SATARA("Satara"),
    HYDERABAD("Hyderabad"),
    WARANGAL("Warangal");

    private final String displayName;

    City(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name();
    }
}
