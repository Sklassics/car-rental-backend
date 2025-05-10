package com.sklassics.cars.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sklassics.cars.dtos.ReservationUserDTO;
import com.sklassics.cars.dtos.ReservationWithDueAmountDTO;
import com.sklassics.cars.entities.Booking;
import com.sklassics.cars.entities.Reservation;
import com.sklassics.cars.entities.TransactionUnderVerification;
import com.sklassics.cars.entities.User;
import com.sklassics.cars.exceptions.CustomExceptions.CarNotFoundException;
import com.sklassics.cars.repositories.UserRepository;
import com.sklassics.cars.services.JwtService;
import com.sklassics.cars.services.ReservationService;
import com.sklassics.cars.services.TransactionUnderVerificationService;
import com.sklassics.cars.services.utility.ResponseUtil;
import com.sklassics.cars.services.utility.ResponseUtil.ErrorMessages;

@RestController
@RequestMapping("/api/reservations")
public class ReservationSystem {
	
	

	 @Autowired
	    private TransactionUnderVerificationService transactionUnderVerificationService;
	 
	 @Autowired
		private UserRepository userRepository;
	 
	private final ReservationService reservationService;
	private final JwtService jwtService;

	public ReservationSystem(ReservationService reservationService, JwtService jwtService) {
		this.reservationService = reservationService;
		this.jwtService = jwtService;
	}

//    @PostMapping
//    public ResponseEntity<?> createReservation(@RequestHeader (value ="Authorization", required = false) String authorizationHeader, @RequestBody Reservation reservation) {
//        try {
//        	  // Check for missing or invalid token format
//            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//                System.out.println("Authorization header missing or invalid format");
//                return ResponseEntity
//                        .status(HttpStatus.UNAUTHORIZED)
//                        .body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
//            }
//
//            // Extract token from Authorization header
//            String token = authorizationHeader.substring(7);
//            System.out.println("Extracted token: " + token);
//
//            // Validate token expiry
//            if (jwtService.isTokenExpired(token)) {
//                System.out.println("Token expired: " + token);
//                return ResponseEntity
//                        .status(HttpStatus.UNAUTHORIZED)
//                        .body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
//            }
//
//            // Extract and validate role
//            String role = jwtService.extractRole(token);
//            System.out.println("Extracted role from token: " + role);
//            if (role == null || !role.equalsIgnoreCase("customer")) {
//                System.out.println("Role mismatch or role is null");
//                return ResponseEntity
//                        .status(HttpStatus.UNAUTHORIZED)
//                        .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
//            }
//
//            // Extract user ID (optional use)
//            Long userId = jwtService.extractUserId(token);
//            System.out.println("Extracted user ID from token: " + userId);
//            
//            Reservation savedReservation = reservationService.saveReservation(reservation,userId);
//
//            
//            if (savedReservation != null && savedReservation.getId() != null) {
//                return ResponseEntity.ok(ResponseUtil.successMessage("Reservation Created"));
//            } else {
//                return ResponseEntity
//                        .status(500)
//                        .body(ResponseUtil.internalError("Failed to create reservation"));
//            }
//        } catch (CarNotFoundException e) {
//            
//            return ResponseEntity
//                    .status(404) 
//                    .body(ResponseUtil.notFound(ErrorMessages.VEHICLE_NOT_FOUND)); 
//        } catch (Exception e) {
//            
//            return ResponseEntity
//                    .status(500)  
//                    .body(ResponseUtil.internalError(ResponseUtil.ErrorMessages.INTERNAL_SERVER_ERROR));
//        }
//    }
	@PostMapping("/full-transaction")
	public ResponseEntity<?> submitFullTransaction(
	        @RequestParam("transactionId") String transactionId,
	        @RequestParam("amount") Double amount,
	        @RequestParam("screenshot") MultipartFile screenshot,
	        @RequestParam("type") String type,
	        @RequestParam("reserve") String reserveJson,
	        @RequestParam("payableCarCost") Double payableCarCost,
	        @RequestHeader("Authorization") String authorizationHeader) {

	    try {
	        // Validate Authorization header
	        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
	        }

	        String token = authorizationHeader.substring(7);

	        // Validate token expiry
	        if (jwtService.isTokenExpired(token)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
	        }

	        // Validate role
	        String role = jwtService.extractRole(token);
	        if (role == null || !role.equalsIgnoreCase("customer")) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
	        }

	        // Extract user ID
	        Long userId = jwtService.extractUserId(token);

	        // Parse booking JSON
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.registerModule(new JavaTimeModule());
	        Reservation reservation = mapper.readValue(reserveJson, Reservation.class);

	        // Save reservation
	        Reservation savedReservation = reservationService.saveReservation(reservation, userId);
	        if (savedReservation == null || savedReservation.getId() == null) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(ResponseUtil.internalError("Failed to create reservation"));
	        }

	        // Save transaction and associate with reservation ID
	        Long reservationId = savedReservation.getId();
	        
	        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            String mobileNumber = user.getMobile();
            
            
	        TransactionUnderVerification savedTransaction = transactionUnderVerificationService.saveTransaction(
	                userId, transactionId, amount, screenshot, type, reservationId,payableCarCost,mobileNumber);

	        return ResponseEntity.ok(ResponseUtil.successMessage("Transaction and Reservation submitted successfully"));

	    } catch (CarNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(ResponseUtil.notFound(ErrorMessages.VEHICLE_NOT_FOUND));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(ResponseUtil.internalError("Error occurred: " + e.getMessage()));
	    }
	}

	@GetMapping("/user/reservations")
	public ResponseEntity<?> getUserReservation(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		try {
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
			}

			String token = authorizationHeader.substring(7);

			if (jwtService.isTokenExpired(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Token has expired."));
			}

			String role = jwtService.extractRole(token);
			if (role == null || !role.equalsIgnoreCase("customer")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
			}

			Long userId = jwtService.extractUserId(token);
			List<Map<String, Object>> reservations = reservationService.getReservationsByUserId(userId);

			if (reservations != null && !reservations.isEmpty()) {
				return ResponseEntity
						.ok(ResponseUtil.successWithData("Reservations fetched successfully!", reservations));
			} else {
				return ResponseEntity.status(HttpStatus.OK)
						.body(ResponseUtil.notFound("No reservations found for this user."));
			}
		} catch (CarNotFoundException e) {
			return ResponseEntity.status(404).body(ResponseUtil.notFound(ErrorMessages.VEHICLE_NOT_FOUND));
		} catch (Exception e) {
			e.printStackTrace(); // helpful for debugging
			return ResponseEntity.status(500)
					.body(ResponseUtil.internalError("Internal Server Error while fetching reservations."));
		}
	}

	@GetMapping
	public ResponseEntity<?> getAllReservations(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		try {
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
			}

			String token = authorizationHeader.substring(7);

			if (jwtService.isTokenExpired(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Token has expired."));
			}

			String role = jwtService.extractRole(token);
			if (role == null || !role.equalsIgnoreCase("admin")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
			}

			Long userId = jwtService.extractUserId(token);

			List<ReservationUserDTO> reservations = reservationService.getAllReservations();
			if (reservations.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ResponseUtil.notFound(ResponseUtil.ErrorMessages.VEHICLE_NOT_FOUND));
			}

			return ResponseEntity.ok(ResponseUtil.successWithData("Reservation Fetched Successfully!", reservations));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ResponseUtil.internalError("An error occurred while fetching reservations."));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getReservationById(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Long id) {
		try {
			// Validate Authorization Header
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
			}

			String token = authorizationHeader.substring(7);

			if (jwtService.isTokenExpired(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
			}

			String role = jwtService.extractRole(token);
			if (role == null || !role.equalsIgnoreCase("admin")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
			}

			Long userId = jwtService.extractUserId(token);

			return reservationService.getReservationById(id)
					.map(reservation -> ResponseEntity.ok(ResponseUtil.successWithData(
							String.format("Reservation Fetched Successfully for the id %d", id), reservation)))
					.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
							.body(ResponseUtil.notFound(ResponseUtil.ErrorMessages.notFoundWithId("Reservation", id))));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ResponseUtil.internalError("An unexpected error occurred while fetching the reservation."));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteReservation(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Long id) {
		try {
			// Validate Authorization Header
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
			}

			String token = authorizationHeader.substring(7);

			if (jwtService.isTokenExpired(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
			}

			String role = jwtService.extractRole(token);
			if (role == null || !role.equalsIgnoreCase("customer")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
			}

			Long userId = jwtService.extractUserId(token);

			// Call the service to delete the reservation
			boolean deleted = reservationService.deleteReservation(id);
			if (deleted) {
				return ResponseEntity.ok(ResponseUtil.successMessage("Reservation deleted successfully"));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ResponseUtil.notFound(ResponseUtil.ErrorMessages.notFoundWithId("Reservation", id)));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ResponseUtil.internalError("An unexpected error occurred while deleting the reservation."));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateReservation(@PathVariable Long id, @RequestBody Reservation updatedReservation,
			@RequestHeader("Authorization") String authorizationHeader) {
		try {
			// Validate Authorization Header
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
			}

			String token = authorizationHeader.substring(7);

			if (jwtService.isTokenExpired(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
			}

			String role = jwtService.extractRole(token);
			if (role == null || !role.equalsIgnoreCase("customer")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
			}

			Long userId = jwtService.extractUserId(token);

			// Call the service to update the reservation
			Reservation reservation = reservationService.updateReservation(id, updatedReservation);

			if (reservation != null) {
				return ResponseEntity.ok(ResponseUtil.successMessage("Reservation updated successfully"));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ResponseUtil.notFound(ResponseUtil.ErrorMessages.notFoundWithId("Reservation", id)));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ResponseUtil.internalError("An unexpected error occurred while updating the reservation."));
		}
	}

	
	@GetMapping("/reservations-with-due-amount")
	public ResponseEntity<?> getReservationsWithDueAmount(@RequestHeader("Authorization") String authorizationHeader) {
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

	        // Get reservations with due amount
	        List<ReservationWithDueAmountDTO> reservationsWithDueAmount = reservationService.getReservationsWithDueAmount();

	        return ResponseEntity.ok(
	                ResponseUtil.successWithData("Reservations with due amount fetched successfully", reservationsWithDueAmount)
	        );

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(ResponseUtil.internalError("Something went wrong while fetching reservations with due amount."));
	    }
	}

}
