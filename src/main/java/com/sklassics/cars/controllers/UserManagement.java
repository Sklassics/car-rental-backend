package com.sklassics.cars.controllers;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sklassics.cars.entites.User;
import com.sklassics.cars.repositories.UserRepository;
import com.sklassics.cars.services.OtpService;
import com.sklassics.cars.services.utility.ResponseUtil;

@RestController
@RequestMapping("/api")
public class UserManagement {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @PostMapping("/auth/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String mobile = request.get("mobile");
        String email = request.get("email");

        if (mobile == null || email == null) {
            return ResponseEntity.badRequest().body(ResponseUtil.validationError("Mobile and Email are required."));
        }

        return otpService.sendMobileEmailOtp(mobile, email);
    }

    @PostMapping("/auth/verify-otp")
    public ResponseEntity<?> validateOtp(@RequestBody Map<String, String> request) {
        String mobile = request.get("mobile");
        String otp = request.get("otp");

        if (mobile == null || otp == null) {
            return ResponseEntity.badRequest().body(ResponseUtil.validationError("Mobile and OTP are required."));
        }

        return otpService.validateMobileEmailOtp(mobile, otp);
    }

    @PostMapping("/login/send-otp")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String mobile = request.get("mobile");
        
        Optional<User> user = userRepository.findByMobile(mobile);

        if (user.isEmpty()) {
            return ResponseEntity.ok(ResponseUtil.notFound("Mobile number not registered. Please register first."));
        }

        return otpService.sendLoginOtp(mobile);
    }


    @PostMapping("/login/verify-otp")
    public ResponseEntity<?> loginVerifyOtp(@RequestBody Map<String, String> request) {
        String mobile = request.get("mobile");
        String otp = request.get("otp");

        ResponseEntity<?> otpValidationResponse = otpService.validateLoginOtp(mobile, otp);

        if (otpValidationResponse.getStatusCode().is2xxSuccessful()) {
            // String jwtToken = userService.generateJwtToken(mobile);
            return ResponseEntity.ok(ResponseUtil.successMessage("Login successful."));
        } else {
            return otpValidationResponse;
        }
    }


    @PostMapping("/aadhaar/send-otp")
    public ResponseEntity<?> sendAadhaarOtp(@RequestBody Map<String, String> request) {
        String aadhaar = request.get("aadhaar");

        if (aadhaar == null || aadhaar.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseUtil.validationError("Aadhaar number is required."));
        }

        return otpService.sendAadhaarOtp(aadhaar);
    }

    @PostMapping("/aadhaar/verify-otp")
    public ResponseEntity<?> validateAadhaarOtp(@RequestBody Map<String, String> request) {
        String aadhaar = request.get("aadhaar");
        String otp = request.get("otp");

        if (aadhaar == null || otp == null) {
            return ResponseEntity.badRequest().body(ResponseUtil.validationError("Aadhaar and OTP are required."));
        }

        return otpService.validateAadhaarOtp(aadhaar, otp);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(
            @RequestParam("name") String name,
            @RequestParam("location") String location,
            @RequestParam("aadhaarNumber") String aadharNumber,
            @RequestParam("aadhaarFile") MultipartFile aadharFile,
            @RequestParam("drivingLicenseFile") MultipartFile drivingLicenseFile
    ) {
        try {
            otpService.registerUser(name, location, aadharNumber, aadharFile, drivingLicenseFile);
            return ResponseEntity.ok(ResponseUtil.successMessage("User registered successfully."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseUtil.internalError("Error during registration: " + e.getMessage()));
        }
    }
    
 // GET user profile by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(ResponseUtil.notFound(ResponseUtil.ErrorMessages.notFoundWithId("User", id)));
        }

        return ResponseEntity.ok(
                ResponseUtil.successWithData("User profile fetched successfully", userOptional.get()));
    }


    // PUT update user profile by ID
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates) {

        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(ResponseUtil.notFound("User not found with ID: " + id));
        }

        User user = userOptional.get();

        if (updates.containsKey("email")) {
            user.setEmail(updates.get("email"));
        }
        if (updates.containsKey("mobile")) {
            user.setMobile(updates.get("mobile"));
        }
        if (updates.containsKey("fullName")) {
            user.setFullName(updates.get("fullName"));
        }
        
        if (updates.containsKey("location")) {
            user.setLocation(updates.get("location"));
        }
       
        {
            try {
                user.setSubmittedAt(LocalDate.parse(updates.get("submittedAt")));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(ResponseUtil.validationError("Invalid date format for submittedAt. Expected format: yyyy-MM-dd"));
            }
        }

        userRepository.save(user);
        return ResponseEntity.ok(ResponseUtil.successMessage("User profile updated successfully."));
    }


}
