package com.sklassics.cars.services.utility;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) {
                return "[]"; // Return empty JSON array string for null or empty list
            }
            return objectMapper.writeValueAsString(attribute); // Convert List to JSON String
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert list to JSON", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return Collections.emptyList(); // Return empty list for null or empty JSON string
            }
            return Arrays.asList(objectMapper.readValue(dbData, String[].class)); // Convert JSON to List
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to list", e);
        }
    }
}
