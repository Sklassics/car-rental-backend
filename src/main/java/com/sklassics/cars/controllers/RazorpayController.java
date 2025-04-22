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
        try {
            String mobile = (String) data.get("mobile");
            String email = (String) data.get("email");
            Double amount = Double.valueOf(data.get("amount").toString());

            System.out.println("Received mobile: " + mobile + ", email: " + email + ", amount: " + amount);

            if (mobile == null || email == null || amount == null) {
                return ResponseEntity.badRequest().body("Invalid data received");
            }

            Map<String, Object> order = razorpayService.createOrder(mobile, email, amount);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            System.err.println("Error creating Razorpay order: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error creating Razorpay order: " + e.getMessage());
        }
    }

 
}
