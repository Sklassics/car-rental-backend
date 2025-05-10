package com.sklassics.cars.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sklassics.cars.dtos.BookingUserDTO;
import com.sklassics.cars.dtos.UserWithDueAmountDTO;
import com.sklassics.cars.entities.Booking;
import com.sklassics.cars.entities.TransactionUnderVerification;
import com.sklassics.cars.entities.User;
import com.sklassics.cars.repositories.UserRepository;
import com.sklassics.cars.services.BookingService;
import com.sklassics.cars.services.JwtService;
import com.sklassics.cars.services.TransactionUnderVerificationService;
import com.sklassics.cars.services.utility.ResponseUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

	
	 @Autowired
	    private TransactionUnderVerificationService transactionUnderVerificationService;
	 
		@Autowired
		private UserRepository userRepository;
		
	 
	 
    private final BookingService bookingService;
    private final JwtService jwtService;

    public BookingController(BookingService bookingService, JwtService jwtService) {
        this.bookingService = bookingService;
        this.jwtService = jwtService;
    }


//    @PostMapping
//    public Map<String, Object> createBooking(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Booking booking) {
//        try {
//            // Validate Authorization Header
//            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//                return ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format.");
//            }
//
//            String token = authorizationHeader.substring(7);
//
//            if (jwtService.isTokenExpired(token)) {
//                return ResponseUtil.unauthorized("Token has expired. JWT token is not valid.");
//            }
//
//            String role = jwtService.extractRole(token);
//
//            if (role == null || !role.equalsIgnoreCase("customer")) {
//                return ResponseUtil.unauthorized("Unauthorized access. Role mismatch.");
//            }
//
//            Long userId = jwtService.extractUserId(token);
//            
//            System.out.println("token ===>>......." + token);
//            System.out.println("userID ===>>......." + userId);
//            
//            // Proceed to create the booking
//            Booking createdBooking = bookingService.createBooking(booking, userId);
//            
//            if(createdBooking == null)
//            {
//            	 return ResponseUtil.internalError("Error while creating the booking");
//            }
//            return ResponseUtil.successMessage("Booking created successfully");
//
//        } catch (Exception e) {
//            return ResponseUtil.internalError("An unexpected error occurred: " + e.getMessage());
//        }
//    }
    
    
    @PostMapping("/full-transaction")
    public ResponseEntity<?> submitFullTransaction(
            @RequestParam("transactionId") String transactionId,
            @RequestParam("amount") Double amount,
            @RequestParam("screenshot") MultipartFile screenshot,
            @RequestParam("type") String type,
            @RequestParam("payableCarCost") Double payableCarCost,
            @RequestParam("booking") String bookingJson,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Validate Authorization header
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
            }

            String token = authorizationHeader.substring(7);

            // Check token expiration
            if (jwtService.isTokenExpired(token)) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
            }

            // Role validation
            String role = jwtService.extractRole(token);
            if (role == null || !role.equalsIgnoreCase("customer")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
            }

            // Extract userId
            Long userId = jwtService.extractUserId(token);

            // Parse booking JSON
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            Booking booking = mapper.readValue(bookingJson, Booking.class);


            // Save booking first
            Booking createdBooking = bookingService.createBooking(booking, userId);
            if (createdBooking == null || createdBooking.getId() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ResponseUtil.internalError("Booking creation failed."));
            }
            
            
            Long bookingId = createdBooking.getId();
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            String mobileNumber = user.getMobile();


            // Save transaction and associate with booking ID
            TransactionUnderVerification savedTransaction = transactionUnderVerificationService.saveTransaction(
                    userId, transactionId, amount, screenshot, type, bookingId,payableCarCost,mobileNumber);

            return ResponseEntity.ok(ResponseUtil.successMessage("Transaction and Booking submitted successfully."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.internalError("Error occurred: " + e.getMessage()));
        }
    }



    
    @GetMapping("/user/bookings")
    public ResponseEntity<Map<String, Object>> getBookingsByUserId(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Check for missing or improperly formatted token
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                System.out.println("Authorization header missing or invalid format");
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
            }

            String token = authorizationHeader.substring(7);
            System.out.println("Extracted token: " + token);

            // Check token expiry
            if (jwtService.isTokenExpired(token)) {
                System.out.println("Token expired: " + token);
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
            }

            // Validate role
            String role = jwtService.extractRole(token);
            System.out.println("Extracted role from token: " + role);
            if (role == null || !role.equalsIgnoreCase("customer")) {
                System.out.println("Role mismatch or role is null");
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
            }

            // Extract user ID and fetch bookings
            Long userId = jwtService.extractUserId(token);
            System.out.println("Extracted user ID from token: " + userId);

            List<Map<String, Object>> bookings = bookingService.getBookingsByUserId(userId);
           

            System.out.println("Bookings fetched for user ID: " + userId);

            return ResponseEntity.ok(ResponseUtil.successWithData("Bookings fetched successfully", bookings));

        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.internalError("An unexpected error occurred: " + e.getMessage()));
        }
    }





    @GetMapping("/{id}")
    public Map<String, Object> getBooking(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id) {
        try {
            // Validate Authorization Header
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format.");
            }

            String token = authorizationHeader.substring(7);

            if (jwtService.isTokenExpired(token)) {
                return ResponseUtil.unauthorized("Token has expired. JWT token is not valid.");
            }

            String role = jwtService.extractRole(token);

            if (role == null || !role.equalsIgnoreCase("admin")) {
                return ResponseUtil.unauthorized("Unauthorized access. Role mismatch.");
            }

            Long userId = jwtService.extractUserId(token);
            Booking booking = bookingService.getBookingsById(id);

            if (booking == null) {
                return ResponseUtil.notFound(ResponseUtil.ErrorMessages.notFoundWithId("Booking", id));
            }

            return ResponseUtil.successWithData("Booking retrieved successfully", booking);
        } catch (Exception e) {
            return ResponseUtil.internalError("An error occurred while retrieving the booking.");
        }
    }

    	 
    	 

    @GetMapping
    public Map<String, Object> getAllBookings(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate Authorization Header
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format.");
            }

            String token = authorizationHeader.substring(7);

            if (jwtService.isTokenExpired(token)) {
                return ResponseUtil.unauthorized("Token has expired. JWT token is not valid.");
            }

            String role = jwtService.extractRole(token);

            if (role == null || !role.equalsIgnoreCase("admin")) {
                return ResponseUtil.unauthorized("Unauthorized access. Role mismatch.");
            }

            Long userId = jwtService.extractUserId(token);
            List<BookingUserDTO> bookings = bookingService.getAllBookings();
            return ResponseUtil.successWithData("Bookings retrieved successfully", bookings);
        } catch (Exception e) {
            return ResponseUtil.internalError("An error occurred while retrieving the bookings.");
        }
    }


    @PutMapping("/update/{id}")
    public Map<String, Object> updateBooking(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id, @RequestBody Booking booking) {

        try {

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format.");
            }

            String token = authorizationHeader.substring(7);

            if (jwtService.isTokenExpired(token)) {
                return ResponseUtil.unauthorized("Token has expired. JWT token is not valid.");
            }

            String role = jwtService.extractRole(token);

            if (role == null || !role.equalsIgnoreCase("admin")) {
                return ResponseUtil.unauthorized("Unauthorized access. Role mismatch.");
            }
            
            Booking updatedBooking = bookingService.updateBooking(id, booking);
            if (updatedBooking == null) {
                return ResponseUtil.notFound(ResponseUtil.ErrorMessages.notFoundWithId("Booking", id));
            }
            return ResponseUtil.successMessage("Booking updated successfully");
        } catch (Exception e) {
            return ResponseUtil.internalError("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteBooking(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id) {
       

        try {
        	
        	 if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                 return ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format.");
             }

             String token = authorizationHeader.substring(7);

             if (jwtService.isTokenExpired(token)) {
                 return ResponseUtil.unauthorized("Token has expired. JWT token is not valid.");
             }

             String role = jwtService.extractRole(token);

             if (role == null || !role.equalsIgnoreCase("admin")) {
                 return ResponseUtil.unauthorized("Unauthorized access. Role mismatch.");
             }
             
           boolean isDeleted = bookingService.cancelBooking(id);
            if (!isDeleted) {
                return ResponseUtil.notFound(ResponseUtil.ErrorMessages.notFoundWithId("Booking", id));
            }
            return ResponseUtil.successMessage("Booking deleted successfully");
        } catch (Exception e) {
            return ResponseUtil.internalError("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/users-with-due-amount")
    public ResponseEntity<?> getUsersWithDueAmount(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Token validation
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
            }

            String token = authorizationHeader.substring(7);
            if (jwtService.isTokenExpired(token)) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
            }

            String role = jwtService.extractRole(token);
            if (role == null || !role.equalsIgnoreCase("admin")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
            }

            // Get users with due amount
            List<UserWithDueAmountDTO> usersWithDueAmount = bookingService.getUsersWithDueAmount();

            return ResponseEntity.ok(
                    ResponseUtil.successWithData("Users with due amount fetched successfully", usersWithDueAmount)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.internalError("Something went wrong while fetching users with due amount."));
        }
    }



}
