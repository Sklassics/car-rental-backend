package com.sklassics.cars.services;

import com.sklassics.cars.entities.Booking;
import com.sklassics.cars.entities.Transaction;
import com.sklassics.cars.entities.TransactionUnderVerification;
import com.sklassics.cars.repositories.BookingRepository;
import com.sklassics.cars.repositories.CarRepository;
import com.sklassics.cars.repositories.TransactionRepository;
import com.sklassics.cars.repositories.TransactionUnderVerificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {
	
	@Autowired
    private BookingRepository bookingRepository;
	
	
	@Autowired
    private TransactionRepository transactionRepository;
	
	@Autowired
	private CarRepository carRepository;
	
	@Autowired
    private OneDriveService oneDriveService;
	
	
	@Autowired
    private TransactionUnderVerificationRepository transactionUnderVerificationRepository;

    
    

	public Booking createBooking(Booking booking, Long userId) {
	    try {
	        Booking newBooking = new Booking();
	        newBooking.setCarId(booking.getCarId());
	        newBooking.setFromDate(booking.getFromDate());
	        newBooking.setToDate(booking.getToDate());
	        newBooking.setPickupTime(booking.getPickupTime());
	        newBooking.setDropTime(booking.getDropTime());
	        newBooking.setPaymentId(booking.getPaymentId());
	        newBooking.setAgreedToTerms(booking.isAgreedToTerms());
	        newBooking.setUserId(userId);

	        // Set booking status based on transaction data
	        Optional<Transaction> transaction = transactionRepository.findByRazorpayPaymentId(booking.getPaymentId());
	        if (transaction.isPresent()) {
	            newBooking.setStatus(transaction.get().getOrderStatus());
	            System.out.println("Booking status set to: " + transaction.get().getOrderStatus());
	        }

	        // Save newBooking instead of booking
	        return bookingRepository.save(newBooking);
	    } catch (Exception e) {
	        System.out.println("Error occurred while creating the booking: " + e.getMessage());
	        throw e;
	    }
	}

//	public List<Map<String, Object>> getBookingsByUserId(Long userId) {
//	    List<Booking> bookings = bookingRepository.findByUserId(userId);
//	    List<Map<String, Object>> enrichedBookings = new ArrayList<>();
//
//	    for (Booking booking : bookings) {
//	        Map<String, Object> bookingMap = new HashMap<>();
//	        bookingMap.put("id", booking.getId());
//	        bookingMap.put("carId", booking.getCarId());
//	        bookingMap.put("userId", booking.getUserId());
//	        bookingMap.put("fromDate", booking.getFromDate());
//	        bookingMap.put("toDate", booking.getToDate());
//	        bookingMap.put("pickupTime", booking.getPickupTime());
//	        bookingMap.put("dropTime", booking.getDropTime());
//	        bookingMap.put("paymentId", booking.getPaymentId());
//	        bookingMap.put("status", booking.getStatus());
//	        bookingMap.put("agreedToTerms", booking.isAgreedToTerms());
//	        bookingMap.put("createdAt", booking.getCreatedAt());
//
//	        carRepository.findById(booking.getCarId()).ifPresent(car -> {
//	            Map<String, Object> carDetails = new HashMap<>();
//	            carDetails.put("carName", car.getCarName());
//	            carDetails.put("carModel", car.getCarModel());
//	            carDetails.put("location", car.getLocation());
//
//	            // Ensure imageUrls is List<String>
//	            List<String> secureImageUrls = car.getImageUrls().stream()
//	                .map((String url) -> {  
//	                    try {
//	                        String path = url.substring(url.indexOf("/root:/") + 7);
//	                        return oneDriveService.generateDirectDownloadLink(path);
//	                    } catch (Exception e) {
//	                        System.err.println("Error generating secure URL for " + url + ": " + e.getMessage());
//	                        return null;
//	                    }
//	                })
//	                .filter(Objects::nonNull)
//	                .collect(Collectors.toList());
//
//	            carDetails.put("imageUrls", secureImageUrls);
//
//	            bookingMap.put("car", carDetails);
//	        });
//
//	        enrichedBookings.add(bookingMap);
//	    }
//
//	    return enrichedBookings;
//	}
	
	public List<Map<String, Object>> getBookingsByUserId(Long userId) {
	    List<Booking> bookings = bookingRepository.findByUserId(userId);
	    List<Map<String, Object>> enrichedBookings = new ArrayList<>();

	    for (Booking booking : bookings) {
	        Map<String, Object> bookingMap = new HashMap<>();
	        bookingMap.put("id", booking.getId());
	        bookingMap.put("carId", booking.getCarId());
	        bookingMap.put("userId", booking.getUserId());
	        bookingMap.put("fromDate", booking.getFromDate());
	        bookingMap.put("toDate", booking.getToDate());
	        bookingMap.put("pickupTime", booking.getPickupTime());
	        bookingMap.put("dropTime", booking.getDropTime());
	        bookingMap.put("paymentId", booking.getPaymentId());
	        bookingMap.put("status", booking.getStatus());
	        bookingMap.put("agreedToTerms", booking.isAgreedToTerms());
	        bookingMap.put("createdAt", booking.getCreatedAt());

	        // Set isAdminVerified based on transactionId
	        String transactionId = booking.getPaymentId();
	        boolean isAdminVerified = transactionUnderVerificationRepository
	            .findByTransactionId(transactionId)
	            .map(TransactionUnderVerification::isAdminVerified)
	            .orElse(false);
	        bookingMap.put("isAdminVerified", isAdminVerified);

	        // If admin is not verified, return minimal data and a verification message
	        if (!isAdminVerified) {
	            bookingMap.put("message", "Your document is under admin verification. Please wait.");
	            enrichedBookings.add(bookingMap);
	            continue;
	        }

	        // Add car details only if admin is verified
	        carRepository.findById(booking.getCarId()).ifPresent(car -> {
	            Map<String, Object> carDetails = new HashMap<>();
	            carDetails.put("carName", car.getCarName());
	            carDetails.put("carModel", car.getCarModel());
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
	            bookingMap.put("car", carDetails);
	        });

	        enrichedBookings.add(bookingMap);
	    }

	    return enrichedBookings;
	}




	
    public Booking getBookingsById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }


    public Booking updateBooking(Long id, Booking updatedBooking) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isEmpty()) {
            return null;
        }

        Booking existingBooking = optionalBooking.get();

        existingBooking.setCarId(updatedBooking.getCarId());
        existingBooking.setUserId(updatedBooking.getUserId());
        existingBooking.setFromDate(updatedBooking.getFromDate());
        existingBooking.setToDate(updatedBooking.getToDate());

        return bookingRepository.save(existingBooking);
    }


    public boolean cancelBooking(Long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
            return true;
        }
        return false;
    }

}
