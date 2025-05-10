package com.sklassics.cars.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sklassics.cars.dtos.PaymentRequest;
import com.sklassics.cars.dtos.PaymentResponse;
import com.sklassics.cars.dtos.RefundResponse;
import com.sklassics.cars.dtos.TransactionResponse;
import com.sklassics.cars.entities.Booking;
import com.sklassics.cars.entities.TransactionUnderVerification;
import com.sklassics.cars.services.JwtService;
import com.sklassics.cars.services.PaymentService;
import com.sklassics.cars.services.TransactionUnderVerificationService;
import com.sklassics.cars.services.utility.ResponseUtil;
import com.sklassics.cars.services.utility.ResponseUtil.ErrorMessages;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

<<<<<<< HEAD
	@Autowired
	private JwtService jwtService;
=======
    @PostMapping("/calculate")
    public Map<String, Object> calculateRentalCost(@RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentResponse response = paymentService.calculateCost(paymentRequest);
            return ResponseUtil.successWithData("Rental cost calculated successfully", response);
        } catch (Exception e) {
            return ResponseUtil.internalError("Error calculating rental cost: " + e.getMessage());
        }
    }
    
    @PostMapping("/transaction-details")
    public ResponseEntity<?> saveTransaction(
                                             @RequestParam("transactionId") String transactionId,
                                             @RequestParam("amount") Double amount,
                                             @RequestParam("screenshot") MultipartFile screenshot,
                                             @RequestParam("type") String action,
                                             @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Token validation
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
            }
>>>>>>> acc4b4f35693779f375ab3e1dd9d69f1580529d2

	@Autowired
	private TransactionUnderVerificationService transactionUnderVerificationService;

	@PostMapping("/calculate")
	public Map<String, Object> calculateRentalCost(@RequestBody PaymentRequest paymentRequest) {
		try {
			PaymentResponse response = paymentService.calculateCost(paymentRequest);
			return ResponseUtil.successWithData("Rental cost calculated successfully", response);
		} catch (Exception e) {
			return ResponseUtil.internalError("Error calculating rental cost: " + e.getMessage());
		}
	}

//    @PostMapping("/transaction-details")
//    public ResponseEntity<?> saveTransaction(
//                                             @RequestParam("transactionId") String transactionId,
//                                             @RequestParam("amount") Double amount,
//                                             @RequestParam("screenshot") MultipartFile screenshot,
//                                             @RequestParam("type") String action,
//                                             @RequestHeader("Authorization") String authorizationHeader) {
//        try {
//            // Token validation
//            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//                return ResponseEntity
//                        .status(HttpStatus.UNAUTHORIZED)
//                        .body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
//            }
//
//            String token = authorizationHeader.substring(7);
//            if (jwtService.isTokenExpired(token)) {
//                return ResponseEntity
//                        .status(HttpStatus.UNAUTHORIZED)
//                        .body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
//            }
//
//            String role = jwtService.extractRole(token);
//            if (role == null || !role.equalsIgnoreCase("customer")) {
//                return ResponseEntity
//                        .status(HttpStatus.UNAUTHORIZED)
//                        .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
//            }
//
//            Long userId = jwtService.extractUserId(token);
//            System.out.println("Extracted user ID from token: " + userId);
//
//            // Save the transaction via service
//            TransactionUnderVerification savedTransaction = transactionUnderVerificationService.saveTransaction(
//                    userId, transactionId, amount, screenshot,action);
//
//            return ResponseEntity.ok(savedTransaction);
//
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ResponseUtil.internalError("Something went wrong: " + e.getMessage()));
//        }
//    }
//    
	@GetMapping("/unverified")
	public ResponseEntity<?> getUnverifiedTransactions(@RequestHeader("Authorization") String authorizationHeader) {
		try {
			// Token validation
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
			}

<<<<<<< HEAD
			String token = authorizationHeader.substring(7);
			if (jwtService.isTokenExpired(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
			}
=======
            // Save the transaction via service
            TransactionUnderVerification savedTransaction = transactionUnderVerificationService.saveTransaction(
                    userId, transactionId, amount, screenshot,action);
>>>>>>> acc4b4f35693779f375ab3e1dd9d69f1580529d2

			String role = jwtService.extractRole(token);
			if (role == null || !role.equalsIgnoreCase("admin")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
			}

			// Get all unverified transactions
			List<TransactionUnderVerification> unverifiedList = transactionUnderVerificationService
					.getUnverifiedTransactions();

			return ResponseEntity
					.ok(ResponseUtil.successWithData("Unverified transactions fetched successfully", unverifiedList));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ResponseUtil.internalError("Something went wrong while fetching unverified transactions."));
		}
	}

	@PutMapping("/verify")
	public ResponseEntity<?> verifyTransaction(@RequestBody Map<String, Object> payload,
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

		try {
			// ✅ Extract and validate fields from body
			String transactionId = (String) payload.get("transactionId");
			Object bookingIdObj = payload.get("bookingId");
			Boolean isVerified = (Boolean) payload.get("isVerified");
			Double dueAmount = ((Number) payload.get("dueAmount")).doubleValue();
			Double totalPayableAmount = ((Number) payload.get("totalPayableAmount")).doubleValue();

			if (transactionId == null || transactionId.trim().isEmpty()) {
				return ResponseEntity.badRequest().body(ResponseUtil.validationError("Transaction ID is required."));
			}

			if (bookingIdObj == null) {
				return ResponseEntity.badRequest().body(ResponseUtil.validationError("Booking ID is required."));
			}

<<<<<<< HEAD
			Long actionId;
			try {
				actionId = ((Number) bookingIdObj).longValue();
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(ResponseUtil.validationError("Invalid booking ID format."));
			}

			if (isVerified == null) {
				return ResponseEntity.badRequest()
						.body(ResponseUtil.validationError("isVerified must be true or false."));
			}

			// ✅ Token checks
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

			// ✅ Business logic
			TransactionUnderVerification updatedTransaction = transactionUnderVerificationService
					.updateAdminVerification(transactionId, isVerified, actionId, dueAmount, totalPayableAmount);
=======
    @PutMapping("/verify")
    public ResponseEntity<?> verifyTransaction(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        try {
            // ✅ Extract and validate fields from body
            String transactionId = (String) payload.get("transactionId");
            Object bookingIdObj = payload.get("bookingId");
            Boolean isVerified = (Boolean) payload.get("isVerified");
            

            if (transactionId == null || transactionId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseUtil.validationError("Transaction ID is required."));
            }

            if (bookingIdObj == null) {
                return ResponseEntity.badRequest().body(ResponseUtil.validationError("Booking ID is required."));
            }

            Long bookingId;
            try {
                bookingId = ((Number) bookingIdObj).longValue();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(ResponseUtil.validationError("Invalid booking ID format."));
            }

            if (isVerified == null) {
                return ResponseEntity.badRequest().body(ResponseUtil.validationError("isVerified must be true or false."));
            }

            // ✅ Token checks
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

            // ✅ Business logic
            TransactionUnderVerification updatedTransaction =
                    transactionUnderVerificationService.updateAdminVerification(transactionId, isVerified, bookingId);

            return ResponseEntity.ok(ResponseUtil.successMessage("Transaction verification successful."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.internalError("Something went wrong: " + e.getMessage()));
        }
    }
>>>>>>> acc4b4f35693779f375ab3e1dd9d69f1580529d2

			return ResponseEntity.ok(ResponseUtil.successMessage("Transaction verification successful."));

<<<<<<< HEAD
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ResponseUtil.internalError("Something went wrong: " + e.getMessage()));
		}
	}
=======


    @GetMapping("/{id}")
    public Map<String, Object> getTransactionByOrderId(@PathVariable String id) {
        TransactionResponse transaction = paymentService.getByRazorpayPaymentId(id);
        if (transaction != null) {
            return ResponseUtil.successWithData("Transaction found", transaction);
        } else {
            return ResponseUtil.notFound(ErrorMessages.notFoundWithId("Transaction", id));
        }
    }
>>>>>>> acc4b4f35693779f375ab3e1dd9d69f1580529d2

	@GetMapping("/{id}")
	public Map<String, Object> getTransactionByOrderId(@PathVariable String id) {
		TransactionResponse transaction = paymentService.getByRazorpayPaymentId(id);
		if (transaction != null) {
			return ResponseUtil.successWithData("Transaction found", transaction);
		} else {
			return ResponseUtil.notFound(ErrorMessages.notFoundWithId("Transaction", id));
		}
	}

	@PostMapping("/{id}/refund")
	public Map<String, Object> refundPayment(@PathVariable String id) {
		RefundResponse response = paymentService.refundPayment(id);
		if (response != null) {
			return ResponseUtil.successMessage("Refund Initiated !!");
		} else {
			return ResponseUtil.notFound(ErrorMessages.notFoundWithId("Payment", id));
		}
	}

	@GetMapping("/refund-status/{id}")
	public Map<String, Object> getRefundStatus(@PathVariable Long id) {
		String status = paymentService.getRefundStatus(id);

		if (status != null) {
			return ResponseUtil.successWithData("Refund status fetched", Map.of("status", status));
		} else {
			return ResponseUtil.notFound("Refund not found or not yet processed for transaction ID: " + id);
		}
	}

}
