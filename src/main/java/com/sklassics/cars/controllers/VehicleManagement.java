package com.sklassics.cars.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sklassics.cars.dtos.CarRequestDTO;
import com.sklassics.cars.entites.CarEntity;
import com.sklassics.cars.services.CarService;
import com.sklassics.cars.services.utility.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VehicleManagement {

    @Autowired
    private CarService carService;

    @PostMapping(value = "/vehicles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createCar(
            @RequestPart("car") String carJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        try {
            if (carJson == null || carJson.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ResponseUtil.validationError("Car JSON must not be empty"));
            }

            ObjectMapper objectMapper = new ObjectMapper();
            CarRequestDTO carRequest = objectMapper.readValue(carJson, CarRequestDTO.class);

            CarEntity savedCar = carService.saveCar(carRequest, images);
            if (savedCar == null) {
                return ResponseEntity.internalServerError()
                        .body(ResponseUtil.internalError("Car could not be saved"));
            }

            return ResponseEntity.ok(ResponseUtil.successWithData("Car saved successfully", savedCar));

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
            List<CarRequestDTO> cars = carService.getAllCars();
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

            return ResponseEntity.ok(ResponseUtil.successWithData("Vehicle retrieved successfully", car));
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
