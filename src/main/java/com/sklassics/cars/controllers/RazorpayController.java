//package com.sklassics.cars.controllers;
//
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import com.sklassics.cars.services.RazorpayService;
//
//@RestController
//@RequestMapping("/api/razorpay")
//public class RazorpayController {
//
//    @Autowired
//    private RazorpayService razorpayService;
//
//    @PostMapping("/create-order")
//    public ResponseEntity<String> createOrder(@RequestParam double amount) {
//        try {
//            String order = razorpayService.createOrder(amount);
//            return ResponseEntity.ok(order);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error creating Razorpay order: " + e.getMessage());
//        }
//    }
//}
//
//
//

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

            Map<String, Object> order = razorpayService.createOrder(mobile, email, amount);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating Razorpay order: " + e.getMessage());
        }
    }
}
