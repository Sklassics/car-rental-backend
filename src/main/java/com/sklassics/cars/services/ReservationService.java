package com.sklassics.cars.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sklassics.cars.entities.CarEntity;
import com.sklassics.cars.entities.Reservation;
import com.sklassics.cars.entities.Transaction;
import com.sklassics.cars.exceptions.CustomExceptions.CarNotFoundException;
import com.sklassics.cars.repositories.CarRepository;
import com.sklassics.cars.repositories.ReservationRepository;
import com.sklassics.cars.repositories.TransactionRepository;

@Service
public class ReservationService {
	
	@Autowired
    private ReservationRepository reservationRepository;
	
	@Autowired
    private CarRepository carRepository;

	@Autowired
    private TransactionRepository transactionRepository;
	
	@Autowired
    private OneDriveService oneDriveService;

	
	public Reservation saveReservation(Reservation reservation, Long userId) {

	    
	    carRepository.findById(reservation.getCarId())
	        .orElseThrow(() -> new CarNotFoundException("Car not found with ID: " + reservation.getCarId()));

	    
	    reservation.setCarId(reservation.getCarId());
	    reservation.setUserId(userId);
	    reservation.setFromDate(reservation.getFromDate());
	    reservation.setToDate(reservation.getToDate());
	    reservation.setPickupTime(reservation.getPickupTime());
	    reservation.setDropTime(reservation.getDropTime());
	    reservation.setPaymentId(reservation.getPaymentId());
	    // Set booking status based on transaction data
        Optional<Transaction> transaction = transactionRepository.findByRazorpayPaymentId(reservation.getPaymentId());
        if (transaction.isPresent()) {
            reservation.setStatus(transaction.get().getOrderStatus());
            System.out.println("Booking status set to: " + transaction.get().getOrderStatus());
        }

	    return reservationRepository.save(reservation);
	}

	public List<Map<String, Object>> getReservationsByUserId(Long userId) {
	    // Fetch the reservations based on the userId
	    List<Reservation> reservations = reservationRepository.findByUserId(userId);
	    
	    List<Map<String, Object>> totalReservations = new ArrayList<>();
	    
	    // Iterate over each reservation to fetch car details and create the result
	    for (Reservation reservation : reservations) {
	        // Create a map to hold the reservation data and associated car details
	        Map<String, Object> reservationMap = new HashMap<>();
	        
	        // Add reservation details
	        reservationMap.put("reservationId", reservation.getId());
	        reservationMap.put("userId", reservation.getUserId());
	        reservationMap.put("fromDate", reservation.getFromDate());
	        reservationMap.put("toDate", reservation.getToDate());
	        reservationMap.put("pickupTime", reservation.getPickupTime());
	        reservationMap.put("dropTime", reservation.getDropTime());
	        reservationMap.put("paymentId", reservation.getPaymentId());
	        reservationMap.put("status", reservation.getStatus());
	        reservationMap.put("createdAt", reservation.getCreatedAt());

	        // Fetch car details based on carId
	        CarEntity car = carRepository.findById(reservation.getCarId()).orElse(null);
	        
	        if (car != null) {
	            // Add car details to the map
	            Map<String, Object> carDetails = new HashMap<>();
	            carDetails.put("carId", car.getId());
	            carDetails.put("carName", car.getCarName());
	            carDetails.put("carModel", car.getCarModel());
//	            carDetails.put("year", car.getYear());
//	            carDetails.put("fuelType", car.getFuelType());
//	            carDetails.put("transmission", car.getTransmission());
//	            carDetails.put("mileage", car.getMileage());
//	            carDetails.put("seatingCapacity", car.getSeatingCapacity());
//	            carDetails.put("color", car.getColor());
//	            carDetails.put("cost", car.getCost());
	            carDetails.put("location", car.getLocation());
	            List<String> secureImageUrls = car.getImageUrls().stream()
		                .map((String url) -> {  
		                    try {
		                        String path = url.substring(url.indexOf("/root:/") + 7);
		                        return oneDriveService.generateDirectDownloadLink(path);
		                    } catch (Exception e) {
		                        System.err.println("Error generating secure URL for " + url + ": " + e.getMessage());
		                        return null;
		                    }
		                })
		                .filter(Objects::nonNull)
		                .collect(Collectors.toList());

		            carDetails.put("imageUrls", secureImageUrls);
	            
	            // Add car details to the reservation map
	            reservationMap.put("carDetails", carDetails);
	        }
	        
	        // Add the combined data to the list
	        totalReservations.add(reservationMap);
	    }
	    
	    return totalReservations;
	}


	public Optional<Reservation> getReservationById(Long id) {
	    return reservationRepository.findById(id);
	}

	

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }




    public boolean deleteReservation(Long id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
    
    
    public Reservation updateReservation(Long id, Reservation updatedReservation) {
        Optional<Reservation> existingOpt = reservationRepository.findById(id);

        if (existingOpt.isPresent()) {
            Reservation existing = existingOpt.get();

            // Example fields to update — update as per your model
            existing.setFromDate(updatedReservation.getFromDate());
            existing.setToDate(updatedReservation.getToDate());
            existing.setPickupTime(updatedReservation.getPickupTime());
            existing.setDropTime(updatedReservation.getDropTime());



            return reservationRepository.save(existing);
        } else {
            return null;
        }
    }


}
