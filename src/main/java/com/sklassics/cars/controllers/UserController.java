package com.sklassics.cars.controllers;

import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sklassics.cars.entites.User;
import com.sklassics.cars.services.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;
    
    @PostMapping("/auth/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String mobile = request.get("mobile");
        String email = request.get("email");

        if (mobile == null || email == null) {
            return ResponseEntity.badRequest().body("Mobile and email are required.");
        }

        return userService.sendOtp(mobile, email);
    }

    @PostMapping("/auth/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String mobile = request.get("mobile");
        String otp = request.get("otp");

        if (mobile == null || otp == null) {
            return ResponseEntity.badRequest().body("Mobile and OTP are required.");
        }

        return userService.verifyOtp(mobile, otp);
    }
    
    
    
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        return userService.register(request);
    }
    



    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestParam String mobile, @RequestParam String otp) {
        return userService.login(mobile, otp);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }
    @PostMapping("/users/{id}/license")
    public ResponseEntity<?> uploadLicenseFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String uploadedFileUrl = userService.uploadLicenseFile(id, file);
            return ResponseEntity.ok("File uploaded successfully: " + uploadedFileUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading file: " + e.getMessage());
        }
    }


//    @PostMapping("/users/{id}/license")
//    public ResponseEntity<?> uploadLicense(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
//        return userService.uploadLicense(id, file);
//    }
}

