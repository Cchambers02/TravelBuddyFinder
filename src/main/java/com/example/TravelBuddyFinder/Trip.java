// File: com/example/TravelBuddyFinder/Trip.java
package com.example.TravelBuddyFinder;

import java.util.ArrayList;
import java.util.List;

public class Trip {
    private String id;
    private String userId;
    private String location;
    private String date;
    private String weather;
    private final List<String> interestedUsers = new ArrayList<>();

    // Constructor
    public Trip() { }

    public Trip(String userId, String location, String date) {
        this.userId = userId;
        this.location = location;
        this.date = date;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }
    public List<String> getInterestedUsers() { return interestedUsers; }

    @Override
    public String toString() {
        return "Trip{id='" + id + "', userId='" + userId + "', location='" + location + "', date='" + date + "', weather='" + weather + "'}";
    }
}
