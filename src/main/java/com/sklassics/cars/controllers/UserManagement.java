package com.sklassics.cars.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sklassics.cars.entites.User;
import com.sklassics.cars.exceptions.CustomExceptions.UserNotFoundException;
import com.sklassics.cars.repositories.UserRepository;
import com.sklassics.cars.services.JwtService;
import com.sklassics.cars.services.OneDriveService;
import com.sklassics.cars.services.OtpService;
import com.sklassics.cars.services.utility.ResponseUtil;

@RestController
@RequestMapping("/api")
public class UserManagement {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;
    
    @Autowired
    private JwtService jwtService;
    

    @Autowired
    private OneDriveService oneDriveService;
    
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
            return ResponseEntity.status(404).body(ResponseUtil.notFound("Mobile number not registered. Please register first."));
        }

        return otpService.sendLoginOtp(mobile);
    }

    @PostMapping("/login/verify-otp")
    public ResponseEntity<?> loginVerifyOtp(@RequestBody Map<String, String> request) {
        String mobile = request.get("mobile");
        String otp = request.get("otp");

        ResponseEntity<?> otpValidationResponse = otpService.validateLoginOtp(mobile, otp);

        if (otpValidationResponse.getStatusCode().is2xxSuccessful()) {
            
            Long userId = otpService.getUserIdByMobile(mobile); 
            String role = "customer";
            String token = jwtService.generateToken(mobile, role, userId);

            Optional<User> optionalUser = userRepository.findByMobile(mobile);
            User user = optionalUser.get();

            // Create a response map with token and pending status
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("pending", "PENDING".equalsIgnoreCase(user.getIsAdminVerifiedDocuments()));

            return ResponseEntity.ok(ResponseUtil.successWithData("Login successful.", data));
        } else {
            return otpValidationResponse;
        }
    }



    @PostMapping("/aadhaar/send-otp")
    public ResponseEntity<?> sendAadhaarOtp(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                            @RequestBody Map<String, String> request) {
        try {
            // Check for missing or invalid token format
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                System.out.println("Authorization header missing or invalid format");
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
            }

            // Extract token from Authorization header
            String token = authorizationHeader.substring(7);
            System.out.println("Extracted token: " + token);

            // Validate token expiry
            if (jwtService.isTokenExpired(token)) {
                System.out.println("Token expired: " + token);
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
            }

            // Extract and validate role
            String role = jwtService.extractRole(token);
            System.out.println("Extracted role from token: " + role);
            if (role == null || !role.equalsIgnoreCase("customer")) {
                System.out.println("Role mismatch or role is null");
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
            }

            // Extract user ID (optional use)
            Long userId = jwtService.extractUserId(token);
            System.out.println("Extracted user ID from token: " + userId);

            // Validate aadhaar input
            String aadhaar = request.get("aadhaar");
            if (aadhaar == null || aadhaar.trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(ResponseUtil.validationError("Aadhaar number is required."));
            }

            // Proceed to send OTP
            return otpService.sendAadhaarOtp(aadhaar);

        } catch (Exception e) {
            System.err.println("Error while sending Aadhaar OTP: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.internalError("An error occurred while processing the request."));
        }
    }

    @PostMapping("/aadhaar/verify-otp")
    public ResponseEntity<?> validateAadhaarOtp(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody Map<String, String> request) {

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

            // Request body validation
            String aadhaar = request.get("aadhaar");
            String otp = request.get("otp");

            if (aadhaar == null || otp == null) {
                return ResponseEntity.badRequest().body(ResponseUtil.validationError("Aadhaar and OTP are required."));
            }

            return otpService.validateAadhaarOtp(aadhaar, otp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.internalError("Error during Aadhaar OTP verification: " + e.getMessage()));
        }
    }


    @PostMapping("/auth/register")
    public ResponseEntity<?> register(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("aadhaarNumber") String aadharNumber,
            @RequestParam("aadhaarFile") MultipartFile aadharFile,
            @RequestParam("drivingLicenseFile") MultipartFile drivingLicenseFile
    ) {
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

            // Registration process
            otpService.registerUser(name, address, aadharNumber, aadharFile, drivingLicenseFile,userId);
            return ResponseEntity.ok(ResponseUtil.successMessage("Profile submitted for Verification!"));

        }
        catch (UserNotFoundException e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseUtil.internalError("User not found with id : " + e.getMessage()));
        }
        
        catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseUtil.internalError("Error during registration: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String mobileNumber = jwtService.extractMobileNumber(token);
            Optional<User> optionalUser = userRepository.findByMobile(mobileNumber);
            if (optionalUser.get().getAddress() == null) {
                return ResponseEntity.ok(ResponseUtil.notFound(
                    ResponseUtil.ErrorMessages.notFoundWithId("User", mobileNumber)
                ));
            }

            User user = optionalUser.get();

            if ("PENDING".equalsIgnoreCase(user.getIsAdminVerifiedDocuments())) {
                return ResponseEntity.ok(ResponseUtil.underVerification("Documents are under verification"));
            }

            Map<String, Object> userData = Map.of(
                "fullName", user.getFullName(),
                "address", user.getAddress(),
                "aadhaarNumber", user.getAadhaarNumber(),
                "aadhaarFilePath", oneDriveService.convertFileToBase64(user.getAadhaarFilePath()),
                "licenseFilePath", oneDriveService.convertFileToBase64(user.getLicenseFilePath()),
                "submittedAt", user.getSubmittedAt(),
                "status", user.getIsAdminVerifiedDocuments()
                

            );

            return ResponseEntity.ok(ResponseUtil.successWithData("User profile fetched successfully", userData));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(ResponseUtil.internalError("Error while fetching user profile"));
        }
    }



//    // PUT update user profile by ID
//    @PutMapping("/users/{id}")
//    public ResponseEntity<?> updateUserProfile(
//            @PathVariable Long id,
//            @RequestBody Map<String, String> updates) {
//
//        Optional<User> userOptional = userRepository.findById(id);
//
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.status(404).body(ResponseUtil.notFound("User not found with ID: " + id));
//        }
//
//        User user = userOptional.get();
//
//        if (updates.containsKey("email")) {
//            user.setEmail(updates.get("email"));
//        }
//        if (updates.containsKey("mobile")) {
//            user.setMobile(updates.get("mobile"));
//        }
//        if (updates.containsKey("fullName")) {
//            user.setFullName(updates.get("fullName"));
//        }
//        if (updates.containsKey("location")) {
//            user.setLocation(updates.get("location"));
//        }
//
//        if (updates.containsKey("submittedAt")) {
//            try {
//                user.setSubmittedAt(LocalDate.parse(updates.get("submittedAt")));
//            } catch (Exception e) {
//                return ResponseEntity.badRequest().body(ResponseUtil.validationError("Invalid date format for submittedAt. Expected format: yyyy-MM-dd"));
//            }
//        }
//
//        userRepository.save(user);
//        return ResponseEntity.ok(ResponseUtil.successMessage("User profile updated successfully."));
//    }
}
