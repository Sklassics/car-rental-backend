package com.sklassics.cars.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sklassics.cars.dtos.CarRequestDTO;
import com.sklassics.cars.entites.CarEntity;
import com.sklassics.cars.exceptions.CustomExceptions.CarNotFoundException;
import com.sklassics.cars.repositories.CarRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private OneDriveService oneDriveService;

    
    public CarEntity saveCar(CarRequestDTO carRequest, List<MultipartFile> images) throws Exception {
        try {
        	
        	if (images != null && !images.isEmpty()) {
        	    // Proceed
        	} else {
        	    System.out.println("No images received for upload.");
        	}

            // Create CarEntity from request DTO
            CarEntity car = new CarEntity();
            car.setFirstName(carRequest.getFirstName());
            car.setLastName(carRequest.getLastName());
            car.setContactInfo(carRequest.getContactInfo());
            car.setCarName(carRequest.getCarName());
            car.setCarModel(carRequest.getCarModel());
            car.setYear(carRequest.getYear());
            car.setVehicleType(carRequest.getVehicleType());
            car.setFuelType(carRequest.getFuelType());
            car.setTransmission(carRequest.getTransmission());
            car.setMileage(carRequest.getMileage());
            car.setCost(carRequest.getCost());
            car.setSeatingCapacity(carRequest.getSeatingCapacity());
            car.setColor(carRequest.getColor());
            car.setLocation(carRequest.getLocation());
            car.setAvailable(true);
            
            car = carRepository.save(car);

            List<String> imageUrls = new ArrayList<>();

            // Upload images if provided
            if (images != null && !images.isEmpty()) {
            	String folderName = car.getFirstName() + "_" + car.getLastName() + "_" + car.getId();
            	imageUrls = oneDriveService.uploadCarImages(folderName, images);

            }
            
            System.out.println("Image urls buddy==========>"+imageUrls);

            // Update car entity with image URLs
            car.setImageUrls(imageUrls);

            // Save again with images
            return carRepository.save(car);

        } catch (IllegalArgumentException e) {
            throw new Exception("Validation error: " + e.getMessage());

        } catch (IOException e) {
            throw new Exception("Failed to upload images: " + e.getMessage());

        } catch (Exception e) {
            throw new Exception("An unexpected error occurred: " + e.getMessage());
        }
    }

    public List<CarRequestDTO> getAllCars() {
        List<CarEntity> cars = carRepository.findAll();
        System.out.println("Total cars fetched from DB: " + cars.size());

        return cars.stream().map(car -> {
            try {
                System.out.println("Processing car ID: " + car.getId());
                System.out.println("Image URLs (Graph API paths): " + car.getImageUrls());

                List<String> secureLinks = car.getImageUrls().stream()
                    .map(url -> {
                        try {
                            // Extract path from Graph API URL
                            String path = url.substring(url.indexOf("/root:/") + 7);
                            return oneDriveService.generateDirectDownloadLink(path);
                        } catch (Exception e) {
                            System.err.println("Error generating view link for " + url + ": " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

                System.out.println("Secure viewable image links count: " + secureLinks.size());

                return new CarRequestDTO(
                    car.getId(),
                    car.getFirstName(),
                    car.getLastName(),
                    car.getContactInfo(),
                    car.getCarName(),
                    car.getCarModel(),
                    car.getYear(),
                    car.getVehicleType(),
                    car.getFuelType(),
                    car.getTransmission(),
                    car.getMileage(),
                    car.getSeatingCapacity(),
                    car.getColor(),
                    secureLinks,
                    car.getCreatedAt(),
                    car.getCost(),
                    car.getLocation(),
                    car.isAvailable()
                );
            } catch (Exception e) {
                System.err.println("Error handling car ID " + car.getId() + ": " + e.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public CarRequestDTO getCarById(Long id) {
        CarEntity car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with ID: " + id));

        try {
            System.out.println("Fetching car by ID: " + car.getId());
            System.out.println("Image URLs (Graph API paths): " + car.getImageUrls());

            List<String> secureLinks = car.getImageUrls().stream()
                    .map(url -> {
                        try {
                            // Extract path from Graph API URL
                            String path = url.substring(url.indexOf("/root:/") + 7);
                            return oneDriveService.generateDirectDownloadLink(path);
                        } catch (Exception e) {
                            System.err.println("Error generating view link for " + url + ": " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            System.out.println("Secure viewable image links count: " + secureLinks.size());

            return new CarRequestDTO(
                    car.getId(),
                    car.getFirstName(),
                    car.getLastName(),
                    car.getContactInfo(),
                    car.getCarName(),
                    car.getCarModel(),
                    car.getYear(),
                    car.getVehicleType(),
                    car.getFuelType(),
                    car.getTransmission(),
                    car.getMileage(),
                    car.getSeatingCapacity(),
                    car.getColor(),
                    secureLinks,
                    car.getCreatedAt(),
                    car.getCost(),
                    car.getLocation(),
                    car.isAvailable()
            );

        } catch (Exception e) {
            System.err.println("Error processing car ID " + car.getId() + ": " + e.getMessage());
            throw new RuntimeException("Error processing car data");
        }
    }



//    public CarEntity addImagesToCar(Long id, List<MultipartFile> images) {
//    	CarEntity car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));
//        List<String> newUrls = oneDriveService.uploadImagesToOneDrive(images);
//        car.getImageUrls().addAll(newUrls);
//        return carRepository.save(car);
//    }

    public CarEntity removeImageFromCar(Long id, String imageUrl) {
        CarEntity car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));
        car.getImageUrls().remove(imageUrl);
        return carRepository.save(car);
    }

   


    public CarEntity updateCar(Long id, CarEntity updatedCarData) {
        CarEntity existingCar = carRepository.findById(id)
            .orElseThrow(() -> new CarNotFoundException("Car not found with id: " + id));

        existingCar.setFirstName(updatedCarData.getFirstName());
        existingCar.setLastName(updatedCarData.getLastName());
        existingCar.setContactInfo(updatedCarData.getContactInfo());
        existingCar.setCarName(updatedCarData.getCarName());
        existingCar.setCarModel(updatedCarData.getCarModel());
        existingCar.setYear(updatedCarData.getYear());
        existingCar.setVehicleType(updatedCarData.getVehicleType());
        existingCar.setFuelType(updatedCarData.getFuelType());
        existingCar.setTransmission(updatedCarData.getTransmission());
        existingCar.setMileage(updatedCarData.getMileage());
        existingCar.setSeatingCapacity(updatedCarData.getSeatingCapacity());
        existingCar.setColor(updatedCarData.getColor());
        existingCar.setLocation(updatedCarData.getLocation());
        existingCar.setCost(updatedCarData.getCost());
        existingCar.setImageUrls(updatedCarData.getImageUrls());

        return carRepository.save(existingCar);
    }


    public boolean deleteCar(Long id) {
        Optional<CarEntity> existingCarOptional = carRepository.findById(id);

        if (existingCarOptional.isPresent()) {
            carRepository.deleteById(existingCarOptional.get().getId());
            return true; // deletion successful
        }

        return false; // car not found
    }


    


}
