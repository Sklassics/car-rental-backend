package com.sklassics.cars.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sklassics.cars.dtos.CarRequestDTO;
import com.sklassics.cars.entities.CarEntity;
import com.sklassics.cars.services.CarService;
import com.sklassics.cars.services.JwtService;
import com.sklassics.cars.services.utility.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class VehicleManagement {

    @Autowired
    private CarService carService;
    @Autowired
    private JwtService jwtService;

    @PostMapping(value = "/vehicles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createCar(
    		
            @RequestPart("car") String carJson,
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestPart(value ="agreement") MultipartFile agreement,
            @RequestHeader(value = "Authorization",required = false) String authorizationHeader) {
    	
  
        try {
        	 if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                 System.out.println("Authorization header missing or invalid format");
                 return ResponseEntity
                         .status(HttpStatus.UNAUTHORIZED)
                         .body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
             }

             // Extract token from Authorization header
             String token = authorizationHeader.substring(7);
             System.out.println("Extracted token: " + token);

             // Validate token expiry
             if (jwtService.isTokenExpired(token)) {
                 System.out.println("Token expired: " + token);
                 return ResponseEntity
                         .status(HttpStatus.UNAUTHORIZED)
                         .body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
             }

             // Extract and validate role
             String role = jwtService.extractRole(token);
             System.out.println("Extracted role from token: " + role);
             if (role == null || !role.equalsIgnoreCase("admin")) {
                 System.out.println("Role mismatch or role is null");
                 return ResponseEntity
                         .status(HttpStatus.UNAUTHORIZED)
                         .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
             }

             // Extract user ID (optional use)
             Long userId = jwtService.extractUserId(token);
             System.out.println("Extracted user ID from token: " + userId);
            if (carJson == null || carJson.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ResponseUtil.validationError("Car JSON must not be empty"));
            }

            ObjectMapper objectMapper = new ObjectMapper();
            CarRequestDTO carRequest = objectMapper.readValue(carJson, CarRequestDTO.class);

            CarEntity savedCar = carService.saveCar(carRequest, images,agreement);
            if (savedCar == null) {
                return ResponseEntity.internalServerError()
                        .body(ResponseUtil.internalError("Car could not be saved"));
            }

            return ResponseEntity.ok(ResponseUtil.successMessage("Car saved successfully"));

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest()
                    .body(ResponseUtil.validationError("Invalid JSON format: " + e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseUtil.internalError("Error processing request: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseUtil.internalError("Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping("/vehicles")
    public ResponseEntity<Map<String, Object>> getAllCars() {
        try {
            List<Map<String, Object>> cars = carService.getAllCars();
            if (cars == null || cars.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ResponseUtil.notFound("No vehicles found"));
            }
            return ResponseEntity.ok(ResponseUtil.successWithData("Cars retrieved successfully", cars));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseUtil.internalError("Error fetching car data: " + e.getMessage()));
        }
    }

//    @GetMapping("/vehicles/{id}")
//    public ResponseEntity<Map<String, Object>> getCarById(@PathVariable Long id) {
//        try {
//            if (id == null || id <= 0) {
//                return ResponseEntity.badRequest()
//                        .body(ResponseUtil.validationError("Invalid vehicle ID"));
//            }
//
//            CarRequestDTO car = carService.getCarById(id);
//            if (car == null) {
//                return ResponseEntity.status(404)
//                        .body(ResponseUtil.notFound(ResponseUtil.ErrorMessages.notFoundWithId("Vehicle", id)));
//            }
//
//            return ResponseEntity.ok(ResponseUtil.successWithData("Vehicle retrieved successfully", car));
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError()
//                    .body(ResponseUtil.internalError("Error fetching vehicle: " + e.getMessage()));
//        }
//    }
//    
    @GetMapping("/vehicles/{id}")
    public ResponseEntity<Map<String, Object>> getCarById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(ResponseUtil.validationError("Invalid vehicle ID"));
            }

            CarRequestDTO car = carService.getCarById(id);
            if (car == null) {
                return ResponseEntity.status(404)
                        .body(ResponseUtil.notFound(ResponseUtil.ErrorMessages.notFoundWithId("Vehicle", id)));
            }

            // Use ObjectMapper to exclude null fields
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            // Convert CarRequestDTO to a Map with nulls trimmed
            Map<String, Object> cleanData = mapper.convertValue(car, new TypeReference<>() {});

            return ResponseEntity.ok(ResponseUtil.successWithData("Vehicle retrieved successfully", cleanData));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseUtil.internalError("Error fetching vehicle: " + e.getMessage()));
        }
    }


    @PutMapping("/vehicles/{id}")
    public ResponseEntity<Map<String, Object>> updateCar(@PathVariable Long id, @RequestBody CarEntity car) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(ResponseUtil.validationError("Invalid vehicle ID"));
            }

            if (car == null) {
                return ResponseEntity.badRequest()
                        .body(ResponseUtil.validationError("Car details must not be null"));
            }

            CarEntity updatedCar = carService.updateCar(id, car);
            if (updatedCar == null) {
                return ResponseEntity.status(404)
                        .body(ResponseUtil.notFound("Update failed. Vehicle not found with ID: " + id));
            }

            return ResponseEntity.ok(ResponseUtil.successWithData("Vehicle updated successfully", updatedCar));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseUtil.internalError("Error updating vehicle: " + e.getMessage()));
        }
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Map<String, Object>> deleteCar(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(ResponseUtil.validationError("Invalid vehicle ID"));
            }

            boolean deleted = carService.deleteCar(id);
            if (!deleted) {
                return ResponseEntity.status(404)
                        .body(ResponseUtil.notFound("Vehicle not found or could not be deleted"));
            }

            return ResponseEntity.ok(ResponseUtil.successMessage("Vehicle deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseUtil.internalError("Error deleting vehicle: " + e.getMessage()));
        }
    }

    @GetMapping("/test")
    public String test() {
        return "HELLO!";
    }
}
