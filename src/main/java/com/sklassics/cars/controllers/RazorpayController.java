package com.sklassics.cars.controllers;

import com.sklassics.cars.services.RazorpayService;
import com.sklassics.cars.services.utility.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/razorpay")
public class RazorpayController {

    @Autowired
    private RazorpayService razorpayService;

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> data) {
        System.out.println("Received request to create Razorpay order");

        try {
            System.out.println("Extracting amount from request data...");
            if (!data.containsKey("amount")) {
                return ResponseEntity.badRequest().body(ResponseUtil.validationError("Amount is required"));
            }

            Double amount = Double.valueOf(data.get("amount").toString());
            System.out.println("Amount extracted: " + amount);

            System.out.println("Calling Razorpay service to create order...");
            Map<String, Object> order = razorpayService.createOrder(amount);

            System.out.println("Razorpay order created successfully: " + order);
            return ResponseEntity.ok(ResponseUtil.successWithData("Order created successfully", order));

        } catch (NumberFormatException e) {
            System.err.println("Invalid amount format: " + e.getMessage());
            return ResponseEntity.badRequest().body(ResponseUtil.validationError("Invalid amount format"));
        } catch (Exception e) {
            System.err.println("Error creating Razorpay order: " + e.getMessage());
            return ResponseEntity.status(500).body(ResponseUtil.internalError("Error creating Razorpay order: " + e.getMessage()));
        }
    }
}
