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
    
    @Autowired
    private JwtService jwtService;
    
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
    
    @PostMapping("/transaction-details")
    public ResponseEntity<?> saveTransaction(@RequestParam("mobile") String mobile,
                                             @RequestParam("email") String email,
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

            String token = authorizationHeader.substring(7);
            if (jwtService.isTokenExpired(token)) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
            }

            String role = jwtService.extractRole(token);
            if (role == null || !role.equalsIgnoreCase("customer")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
            }

            Long userId = jwtService.extractUserId(token);
            System.out.println("Extracted user ID from token: " + userId);

            // Save the transaction via service
            TransactionUnderVerification savedTransaction = transactionUnderVerificationService.saveTransaction(
                    userId, mobile, email, transactionId, amount, screenshot,action);

            return ResponseEntity.ok(savedTransaction);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.internalError("Something went wrong: " + e.getMessage()));
        }
    }
    
    @GetMapping("/unverified")
    public ResponseEntity<?> getUnverifiedTransactions(@RequestHeader("Authorization") String authorizationHeader) {
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

            // Get all unverified transactions
            List<TransactionUnderVerification> unverifiedList = transactionUnderVerificationService.getUnverifiedTransactions();

            return ResponseEntity.ok(
                    ResponseUtil.successWithData("Unverified transactions fetched successfully", unverifiedList)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.internalError("Something went wrong while fetching unverified transactions."));
        }
    }

    
    

    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verifyTransaction(
            @PathVariable String id,
            @RequestParam boolean isVerified,
            @RequestHeader("Authorization") String authorizationHeader) {
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

            // Perform transaction update verification
            TransactionUnderVerification updatedTransaction = 
                transactionUnderVerificationService.updateAdminVerification(id, isVerified);

            return ResponseEntity.ok(
                ResponseUtil.successMessage("Transaction verification successful.")
            );

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.internalError("Something went wrong: " + e.getMessage()));
        }
    }


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



