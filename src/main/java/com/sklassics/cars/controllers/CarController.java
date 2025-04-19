package com.sklassics.cars.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sklassics.cars.dtos.CarRequestDTO;
import com.sklassics.cars.entites.CarEntity;
import com.sklassics.cars.services.CarService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {

    @Autowired
    private CarService carService;


    @PostMapping(value = "/vehicles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCar(
            @RequestPart("car") String carJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        try {
        	System.out.println("Images received: " + (images == null ? "null" : images.size()));

            // Convert JSON string to CarRequestDTO object
            ObjectMapper objectMapper = new ObjectMapper();
            CarRequestDTO carRequest = objectMapper.readValue(carJson, CarRequestDTO.class);

            // Save car entity
            CarEntity savedCar = carService.saveCar(carRequest, images);
            return ResponseEntity.ok(savedCar);

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format: " + e.getMessage());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request: " + e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }



    @GetMapping("/vehicles")
    public ResponseEntity<List<CarRequestDTO>> getAllCars() {
        try {
            List<CarRequestDTO> cars = carService.getAllCars();
            return ResponseEntity.ok(cars);
        } catch (Exception e) {
            System.err.println("Error fetching car data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/vehicles/{id}")
    public CarRequestDTO getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }
    
    

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCar(@PathVariable Long id, @RequestBody CarEntity car) {
      
    	try{
    		
    		 CarEntity carEntity = carService.updateCar(id, car);
    		 return ResponseEntity.ok(carEntity);
    		
    	}
    	catch(Exception e) {
    		System.err.println("Error fetching car data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    		
    	}
    }
    
    

    @DeleteMapping("/vehicles/{id}")
    public String deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return "Car deleted successfully!";
    }
    
    
//    
//    @PutMapping("/{id}/add-images")
//    public CarEntity addImages(@PathVariable Long id, @RequestPart("images") List<MultipartFile> images) throws Exception {
//        return carService.addImagesToCar(id, images);
//    }
//    
//    @DeleteMapping("/{id}/remove-image")
//    public CarEntity removeImage(@PathVariable Long id, @RequestParam String imageUrl) {
//        return carService.removeImageFromCar(id, imageUrl);
//    }
    
    @GetMapping("/test")
    public String test()
    {
    	return "HELLO !";
    }
}
