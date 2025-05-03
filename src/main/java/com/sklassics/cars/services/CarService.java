package com.sklassics.cars.services;

import org.hibernate.query.sqm.sql.ConversionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sklassics.cars.dtos.CarRequestDTO;
import com.sklassics.cars.entities.CarEntity;
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

    
    public CarEntity saveCar(CarRequestDTO carRequest, List<MultipartFile> images, MultipartFile agreement) throws Exception {
        try {
            // Log message for image upload
            if (images != null && !images.isEmpty()) {
                // Proceed to upload images
            } else {
                System.out.println("No images received for upload.");
            }

            // Create CarEntity from request DTO
            CarEntity car = new CarEntity();
            car.setFirstName(carRequest.getFirstName());
            car.setLastName(carRequest.getLastName());
            car.setMobile(carRequest.getMobile());
            car.setEmail(carRequest.getEmail());
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
            car.setChassisNumber(carRequest.getChassisNumber());
            car.setEngineNumber(carRequest.getEngineNumber());
            car.setVehicleRegistrationNumber(carRequest.getVehicleRegistrationNumber());
            car.setAvailable(true);

            // Save the car entity (before uploading images and agreement)
            car = carRepository.save(car);

            // List to hold image URLs
            List<String> imageUrls = new ArrayList<>();

            // Upload images if provided
            if (images != null && !images.isEmpty()) {
                String folderName = car.getFirstName() + "_" + car.getLastName() + "_" + car.getId();
                imageUrls = oneDriveService.uploadCarImages(folderName, images);
            }

            // Save the agreement if provided
            String agreementUrl = null;
            if (agreement != null) {
                // Assuming you have a method in your oneDriveService to upload a file
            	String folderName = car.getFirstName() + "_" + car.getLastName() + "_" + car.getId();
                agreementUrl = oneDriveService.uploadAgreement(folderName, agreement);
            }

            // Log image URLs for debugging
            System.out.println("Image URLs: " + imageUrls);
            System.out.println("Agreement URL: " + agreementUrl);

            // Update car entity with image URLs and agreement URL
            car.setImageUrls(imageUrls);
            car.setAgreementPdfLink(agreementUrl); // Assuming 'setAgreementUrl' is a method in CarEntity

            // Save the updated car entity with images and agreement
            return carRepository.save(car);

        } catch (IllegalArgumentException e) {
            throw new Exception("Validation error: " + e.getMessage());

        } catch (IOException e) {
            throw new Exception("Failed to upload files: " + e.getMessage());

        } catch (Exception e) {
            throw new Exception("An unexpected error occurred: " + e.getMessage());
        }
    }


//    public List<CarRequestDTO> getAllCars() {
//        List<CarEntity> cars = carRepository.findAll();
//        System.out.println("Total cars fetched from DB: " + cars.size());
//
//        return cars.stream().map(car -> {
//            try {
//                System.out.println("Processing car ID: " + car.getId());
//                System.out.println("Image URLs (Graph API paths): " + car.getImageUrls());
//
//                List<String> secureLinks = car.getImageUrls().stream()
//                    .map(url -> {
//                        try {
//                            // Extract path from Graph API URL
//                            String path = url.substring(url.indexOf("/root:/") + 7);
//                            return oneDriveService.generateDirectDownloadLink(path);
//                        } catch (Exception e) {
//                            System.err.println("Error generating view link for " + url + ": " + e.getMessage());
//                            return null;
//                        }
//                    })
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList());
//
//                System.out.println("Secure viewable image links count: " + secureLinks.size());
//
//                return new CarRequestDTO(
//                    car.getId(),
//
//                    car.getCarName(),
//                    car.getCarModel(),
//                    car.getYear(),
//                    car.getVehicleType(),
//                    car.getFuelType(),
//                    car.getTransmission(),
//                    car.getMileage(),
//                    car.getSeatingCapacity(),
//                    car.getColor(),
//                    secureLinks,
//                    car.getCost(),
//                    car.isAvailable()
//                );
//            } catch (Exception e) {
//                System.err.println("Error handling car ID " + car.getId() + ": " + e.getMessage());
//                return null;
//            }
//        }).filter(Objects::nonNull).collect(Collectors.toList());
//    }

    
    public List<CarRequestDTO> getAllCars() {
        System.out.println("Fetching all cars from the database...");
        List<CarEntity> cars = carRepository.findAll();
        System.out.println("Total cars fetched from DB: " + cars.size());

        return cars.stream().map(car -> {
            try {
                System.out.println("Processing car ID: " + car.getId());

                // Debugging image URLs
                System.out.println("Original Image URLs (Graph API paths): " + car.getImageUrls());

                List<String> secureLinks = car.getImageUrls().stream()
                    .map(url -> {
                        try {
                            // Extract path from Graph API URL
                            String path = url.substring(url.indexOf("/root:/") + 7);
                            System.out.println("Extracted path for car ID " + car.getId() + ": " + path);

                            // Generate direct download link
                            String secureLink = oneDriveService.generateDirectDownloadLink(path);
                            System.out.println("Generated secure link for car ID " + car.getId() + ": " + secureLink);

                            return secureLink;
                        } catch (Exception e) {
                            System.err.println("Error generating view link for URL: " + url + " (Car ID: " + car.getId() + ")");
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

                System.out.println("Secure viewable image links count for car ID " + car.getId() + ": " + secureLinks.size());

                return new CarRequestDTO(
                    car.getId(),
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
                    car.getCost(),
                    car.isAvailable(),
                    car.getLocation()
                );
            }
            catch (ConversionException e) {
                System.err.println("Conversion error for car ID " + car.getId() + ": " + e.getMessage());
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                System.err.println("Error handling car ID " + car.getId() + ": " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull)
          .collect(Collectors.toList());
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
                      car.getCost(),
                      car.isAvailable(),
                      car.getLocation()
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
        existingCar.setMobile(updatedCarData.getMobile());
        existingCar.setEmail(updatedCarData.getEmail());
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
