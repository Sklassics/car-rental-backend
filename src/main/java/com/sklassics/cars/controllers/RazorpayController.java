package com.sklassics.cars.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sklassics.cars.services.RazorpayService;

import java.util.Map;

@RestController
@RequestMapping("/api/razorpay")
public class RazorpayController {

    @Autowired
    private RazorpayService razorpayService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        System.out.println("Received request to create Razorpay order");

        try {
            System.out.println("Extracting amount from request data...");
            Double amount = Double.valueOf(data.get("amount").toString());
            System.out.println("Amount extracted: " + amount);

            System.out.println("Calling Razorpay service to create order...");
            Map<String, Object> order = razorpayService.createOrder(amount);

            System.out.println("Razorpay order created successfully: " + order);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            System.err.println("Error creating Razorpay order: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error creating Razorpay order: " + e.getMessage());
        }
    }
}
