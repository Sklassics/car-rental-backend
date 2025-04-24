package com.sklassics.cars.controllers;

import com.sklassics.cars.entites.Booking;
import com.sklassics.cars.services.BookingService;
import com.sklassics.cars.services.JwtService;
import com.sklassics.cars.services.utility.ResponseUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final JwtService jwtService;

    public BookingController(BookingService bookingService, JwtService jwtService) {
        this.bookingService = bookingService;
        this.jwtService = jwtService;
    }


    @PostMapping
    public Map<String, Object> createBooking(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Booking booking) {
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

            if (role == null || !role.equalsIgnoreCase("customer")) {
                return ResponseUtil.unauthorized("Unauthorized access. Role mismatch.");
            }

            Long userId = jwtService.extractUserId(token);
            
            System.out.println("token ===>>......." + token);
            System.out.println("userID ===>>......." + userId);
            
            // Proceed to create the booking
            Booking createdBooking = bookingService.createBooking(booking, userId);
            
            if(createdBooking == null)
            {
            	 return ResponseUtil.internalError("Error while creating the booking");
            }
            return ResponseUtil.successMessage("Booking created successfully");

        } catch (Exception e) {
            return ResponseUtil.internalError("An unexpected error occurred: " + e.getMessage());
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




//
//    @GetMapping("/{id}")
//    public Map<String, Object> getBooking(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id) {
//    	 try {
//             // Validate Authorization Header
//             if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//                 return ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format.");
//             }
//
//             String token = authorizationHeader.substring(7);
//
//             if (jwtService.isTokenExpired(token)) {
//                 return ResponseUtil.unauthorized("Token has expired. JWT token is not valid.");
//             }
//
//             String role = jwtService.extractRole(token);
//
//             if (role == null || !role.equalsIgnoreCase("student")) {
//                 return ResponseUtil.unauthorized("Unauthorized access. Role mismatch.");
//             }
//
//             Long userId = jwtService.extractUserId(token);
//             Booking booking = bookingService.getBooking(id);
//        if (booking == null) {
//            return ResponseUtil.notFound(ResponseUtil.ErrorMessages.notFoundWithId("Booking", id));
//        }
//        return ResponseUtil.successWithData("Booking retrieved successfully", booking);
//    }

//    @GetMapping
//    public List<Booking> getAllBookings(@RequestHeader("Authorization") String authorizationHeader) {
//    	 try {
//             // Validate Authorization Header
//             if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//                 return ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format.");
//             }
//
//             String token = authorizationHeader.substring(7);
//
//             if (jwtService.isTokenExpired(token)) {
//                 return ResponseUtil.unauthorized("Token has expired. JWT token is not valid.");
//             }
//
//             String role = jwtService.extractRole(token);
//
//             if (role == null || !role.equalsIgnoreCase("student")) {
//                 return ResponseUtil.unauthorized("Unauthorized access. Role mismatch.");
//             }
//
//             Long userId = jwtService.extractUserId(token);
//        return bookingService.getAllBookings();
//    }






//    @PutMapping("/update/{id}")
//    public Map<String, Object> updateBooking(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id, @RequestBody Booking booking) {
//        // Validate token
//        Map<String, Object> tokenValidationResponse = validateToken(authorizationHeader);
//        if (tokenValidationResponse != null) {
//            return tokenValidationResponse;
//        }
//
//        try {
//            Booking updatedBooking = bookingService.updateBooking(id, booking);
//            if (updatedBooking == null) {
//                return ResponseUtil.notFound(ResponseUtil.ErrorMessages.notFoundWithId("Booking", id));
//            }
//            return ResponseUtil.successWithData("Booking updated successfully", updatedBooking);
//        } catch (Exception e) {
//            return ResponseUtil.internalError("An unexpected error occurred: " + e.getMessage());
//        }
//    }

//    @DeleteMapping("/{id}")
//    public Map<String, Object> deleteBooking(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id) {
//        // Validate token
//        Map<String, Object> tokenValidationResponse = validateToken(authorizationHeader);
//        if (tokenValidationResponse != null) {
//            return tokenValidationResponse;
//        }
//
//        try {
//           boolean isDeleted = bookingService.cancelBooking(id);
//            if (!isDeleted) {
//                return ResponseUtil.notFound(ResponseUtil.ErrorMessages.notFoundWithId("Booking", id));
//            }
//            return ResponseUtil.successMessage("Booking deleted successfully");
//        } catch (Exception e) {
//            return ResponseUtil.internalError("An unexpected error occurred: " + e.getMessage());
//        }
//    }
}
