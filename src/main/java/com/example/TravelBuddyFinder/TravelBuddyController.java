package com.example.TravelBuddyFinder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class TravelBuddyController {

    private final RestTemplate restTemplate;
    private final ConcurrentHashMap<String, Trip> trips = new ConcurrentHashMap<>();
    private int tripIdPoints = 1;
    private int userIdCounter = 1;


    public TravelBuddyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/generate-id")
    public String generateId() {
        String url = "https://www.random.org/integers/?num=1&min=1&max=100000&col=1&base=10&format=plain&rnd=new";
        String randomId = restTemplate.getForObject(url, String.class).trim();
        return "User-" + randomId;
    }

    @GetMapping("/trips")
    public List<Trip> getTrips(@RequestParam String location) {
        List<Trip> tripList = new ArrayList<>();
        for (Trip trip : trips.values()) {
            if (trip.getLocation().equalsIgnoreCase(location)) {
                tripList.add(trip);
            }
        }
        return tripList;
    }

    @PostMapping("/trips")
    public Trip proposeTrip(@RequestBody Trip trip) {
        trip.setId("Trip-" + tripIdPoints++);
        trip.setWeather(fetchWeather(trip.getLocation()));
        trips.put(trip.getId(), trip);
        return trip;
    }

    @PostMapping("/trips/{tripId}/interest")
    public String expressInterest(@PathVariable String tripId, @RequestParam String userId) {
        Trip trip = trips.get(tripId);
        if (trip != null) {
            trip.getInterestedUsers().add(userId);
            return "User " + userId + " expressed interest in Trip " + tripId;
        } else {
            return "Trip is not found!";
        }
    }

    @GetMapping("/trips/{tripId}/interests")
    public List<String> getInterests(@PathVariable String tripId) {
        Trip trip = trips.get(tripId);
        return trip != null ? trip.getInterestedUsers() : new ArrayList<>();
    }

    private String fetchWeather(String location) {
        String apiKey = "b07db316c823917567b216280f7a6dea"; // Replace with your actual API key from OpenWeatherMap
        String weatherApiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey + "&units=metric";

        try {
            // Fetching API weather data from OpenWeatherMap API
            String weatherData = restTemplate.getForObject(weatherApiUrl, String.class);

            // This will parse JSON response to get specific information
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(weatherData);

            // Extract essential information
            String weatherDescription = root.path("weather").get(0).path("description").asText();
            double temperature = root.path("main").path("temp").asDouble();
            int humidity = root.path("main").path("humidity").asInt();
            double windSpeed = root.path("wind").path("speed").asDouble();
            String cityName = root.path("name").asText();

            // Return a formatted string with the extracted information
            return String.format("Weather in %s: %s\nTemperature: %.2f°C\nHumidity: %d%%\nWind Speed: %.2f m/s",
                    cityName, weatherDescription, temperature, humidity, windSpeed);
        } catch (HttpClientErrorException e) {
            // Handle client-side HTTP errors remember 400 is classed as a Bad Request)
            System.err.println("Error fetching weather data: " + e.getMessage());
            return "Error fetching weather data for " + location;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return "Unexpected error fetching weather data for " + location;
        }
    }

}
