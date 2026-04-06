package com.bloodhub.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Optional<double[]> getCoordinates(String address) {
        try {
            // OpenStreetMap Nominatim API
            String url = "https://nominatim.openstreetmap.org/search?q=" +
                    URLEncoder.encode(address, StandardCharsets.UTF_8) +
                    "&format=json&limit=1";

            // Must utilize a User-Agent relative to the application to avoid being blocked
            // by OSM
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "RedLinkBloodHub-Project-CDAC/1.0 (contact: admin@bloodhub.com)");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET,
                    entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                if (root.isArray() && root.size() > 0) {
                    JsonNode location = root.get(0);
                    double lat = location.get("lat").asDouble();
                    double lon = location.get("lon").asDouble();
                    return Optional.of(new double[] { lat, lon });
                }
            }
        } catch (Exception e) {
            System.err.println("Geocoding failed for address '" + address + "': " + e.getMessage());
        }
        return Optional.empty();
    }

    public java.util.List<com.bloodhub.entity.Hospital> searchHospitalsInCity(String cityName,
            com.bloodhub.entity.City cityEnum) {
        java.util.List<com.bloodhub.entity.Hospital> hospitals = new java.util.ArrayList<>();
        try {
            // Nominatim search for hospitals in a specific city
            String url = "https://nominatim.openstreetmap.org/search?amenity=hospital&city=" +
                    URLEncoder.encode(cityName, StandardCharsets.UTF_8) +
                    "&format=json&limit=25";

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "RedLinkBloodHub-Project-CDAC/1.0 (contact: admin@bloodhub.com)");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET,
                    entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        com.bloodhub.entity.Hospital h = new com.bloodhub.entity.Hospital();
                        String rawName = node.get("display_name").asText().split(",")[0];
                        h.setName(cleanHospitalName(rawName)); // Clean Marathi characters
                        h.setAddress(node.get("display_name").asText());
                        h.setLatitude(node.get("lat").asDouble());
                        h.setLongitude(node.get("lon").asDouble());
                        h.setCity(cityEnum);
                        h.setContact(generateRandomContact());
                        h.setActive(true);
                        hospitals.add(h);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Hospital search failed for city '" + cityName + "': " + e.getMessage());
        }
        return hospitals;
    }

    private String cleanHospitalName(String name) {
        // Remove Marathi/Devanagari characters and keep only English text
        // Devanagari Unicode range: \u0900-\u097F
        String cleaned = name.replaceAll("[\\u0900-\\u097F]", "").trim();
        // Remove extra spaces and parentheses artifacts
        cleaned = cleaned.replaceAll("\\s+", " ").replaceAll("^[\\s()]+|[\\s()]+$", "");
        // If nothing remains, use a generic name
        return cleaned.isEmpty() ? "Hospital" : cleaned;
    }

    private String generateRandomContact() {
        // Generate random 10-digit Indian phone number starting with 7, 8, or 9
        int[] firstDigits = { 7, 8, 9 };
        int firstDigit = firstDigits[new java.util.Random().nextInt(firstDigits.length)];
        long number = firstDigit * 100000000L + (long) (Math.random() * 100000000);
        return String.valueOf(number);
    }
}
