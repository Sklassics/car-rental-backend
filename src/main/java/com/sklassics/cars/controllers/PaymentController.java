package com.sklassics.cars.controllers;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sklassics.cars.dtos.PaymentRequest;
import com.sklassics.cars.dtos.PaymentResponse;
import com.sklassics.cars.dtos.RefundResponse;
import com.sklassics.cars.dtos.TransactionResponse;
import com.sklassics.cars.services.PaymentService;
import com.sklassics.cars.services.utility.ResponseUtil;
import com.sklassics.cars.services.utility.ResponseUtil.ErrorMessages;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/calculate")
    public Map<String, Object> calculateRentalCost(@RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentResponse response = paymentService.calculateCost(paymentRequest);
            return ResponseUtil.successWithData("Rental cost calculated successfully", response);
        } catch (Exception e) {
            return ResponseUtil.internalError("Error calculating rental cost: " + e.getMessage());
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
            return ResponseUtil.successWithData("Refund processed", response);
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



