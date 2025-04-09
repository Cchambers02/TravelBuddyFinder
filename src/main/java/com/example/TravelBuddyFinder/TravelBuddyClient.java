package com.example.TravelBuddyFinder;

import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.awt.*;

public class TravelBuddyClient {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api";

    public TravelBuddyClient() {
        this.restTemplate = new RestTemplate();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                TravelBuddyClient window = new TravelBuddyClient();
                window.initialize();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initialize() {
        JFrame frame = new JFrame();
        frame.setBounds(100, 100, 600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblTitle = new JLabel("Travel Buddy");
        lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblTitle.setBounds(200, 10, 250, 30);
        frame.getContentPane().add(lblTitle);

        JTextArea resultArea = new JTextArea();
        resultArea.setBounds(50, 250, 500, 200);
        frame.getContentPane().add(resultArea);

        JButton btnGenerateId = new JButton("Generate User ID");
        btnGenerateId.setBounds(50, 60, 180, 25);
        frame.getContentPane().add(btnGenerateId);

        JButton btnQueryTrips = new JButton("Query Trips");
        btnQueryTrips.setBounds(250, 60, 180, 25);
        frame.getContentPane().add(btnQueryTrips);

        JLabel lblLocation = new JLabel("Location:");
        lblLocation.setBounds(50, 100, 80, 25);
        frame.getContentPane().add(lblLocation);

        JTextField locationField = new JTextField();
        locationField.setBounds(140, 100, 150, 25);
        frame.getContentPane().add(locationField);
        locationField.setColumns(10);

        JLabel lblTripId = new JLabel("Trip ID:");
        lblTripId.setBounds(250, 140, 80, 25);
        frame.getContentPane().add(lblTripId);

        JButton btnProposeTrip = new JButton("Propose Trip");
        btnProposeTrip.setBounds(50, 140, 180, 25);
        frame.getContentPane().add(btnProposeTrip);

        JLabel lblUserId = new JLabel("User ID:");
        lblUserId.setBounds(50, 180, 80, 25);
        frame.getContentPane().add(lblUserId);

        JTextField userIdField = new JTextField();
        userIdField.setBounds(150, 180, 150, 25);
        frame.getContentPane().add(userIdField);
        userIdField.setColumns(10);

        JLabel DateLabel = new JLabel("Date:");
        DateLabel.setBounds(50, 220, 80, 25);
        frame.getContentPane().add(DateLabel);

        JTextField tripIdField = new JTextField();
        tripIdField.setBounds(350, 140, 150, 25);
        frame.getContentPane().add(tripIdField);
        tripIdField.setColumns(10);
        JTextField dateField = new JTextField();

        dateField.setBounds(150, 220, 150, 25);
        frame.getContentPane().add(dateField);
        dateField.setColumns(10);

        JButton btnCheckInterests = new JButton("Check Interests");
        btnCheckInterests.setBounds(340, 180, 180, 25);
        frame.getContentPane().add(btnCheckInterests);

        JButton btnExpressInterest = new JButton("Express Interest");
        btnExpressInterest.setBounds(340, 100, 180, 25);
        frame.getContentPane().add(btnExpressInterest);


        // Add action listeners for buttons
        btnGenerateId.addActionListener(e -> {
            String result = generateUserId();
            resultArea.setText(result);
        });

        btnQueryTrips.addActionListener(e -> {
            String location = locationField.getText();
            String result = queryTrips(location);
            resultArea.setText(result);
        });

        btnProposeTrip.addActionListener(e -> {
            String userId = userIdField.getText();
            String location = locationField.getText();
            String date = dateField.getText();
            String result = proposeTrip(userId, location, date);
            resultArea.setText(result);
        });

        btnExpressInterest.addActionListener(e -> {
            String tripId = tripIdField.getText();
            String userId = userIdField.getText();
            String result = expressInterest(tripId, userId);
            resultArea.setText(result);
        });

        btnCheckInterests.addActionListener(e -> {
            String tripId = tripIdField.getText();
            String result = checkInterests(tripId);
            resultArea.setText(result);
        });

        frame.setVisible(true);
    }

    private String generateUserId() {
        String url = baseUrl + "/generate-id";
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String queryTrips(String location) {
        String url = baseUrl + "/trips?location=" + location;
        try {
            Trip[] trips = restTemplate.getForObject(url, Trip[].class);
            if (trips != null && trips.length > 0) {
                StringBuilder result = new StringBuilder();
                for (Trip trip : trips) {
                    result.append("Trip ID: ").append(trip.getId()).append(", Location: ").append(trip.getLocation()).append(", Date: ").append(trip.getDate()).append(", Weather: ").append(trip.getWeather()).append("\n");
                }
                return result.toString();
            } else {
                return "No trips found for location: " + location;
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String proposeTrip(String userId, String location, String date) {
        String url = baseUrl + "/trips";
        Trip trip = new Trip(userId, location, date);
        try {
            Trip proposedTrip = restTemplate.postForObject(url, trip, Trip.class);
            return "Proposed Trip ID: " + proposedTrip.getId() + ", Location: " + proposedTrip.getLocation() + ", Weather: " + proposedTrip.getWeather();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String expressInterest(String tripId, String userId) {
        String url = baseUrl + "/trips/" + tripId + "/interest?userId=" + userId;
        try {
            return restTemplate.postForObject(url, null, String.class);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String checkInterests(String tripId) {
        String url = baseUrl + "/trips/" + tripId + "/interests";
        try {
            String[] interests = restTemplate.getForObject(url, String[].class);
            if (interests != null && interests.length > 0) {
                StringBuilder result = new StringBuilder("Users interested in Trip ID ").append(tripId).append(":\n");
                for (String interest : interests) {
                    result.append(interest).append("\n");
                }
                return result.toString();
            } else {
                return "No users have expressed interest in Trip ID " + tripId;
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
