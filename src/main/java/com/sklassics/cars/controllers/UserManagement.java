package com.sklassics.cars.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sklassics.cars.entities.User;
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
        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body(ResponseUtil.validationError("Email and OTP are required."));
        }

        return otpService.validateMobileEmailOtp(email, otp);
    }

    @PostMapping("/login/send-otp")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return ResponseEntity.status(404).body(ResponseUtil.notFound("Email not registered. Please register first."));
        }

        return otpService.sendLoginOtp(email);
    }

    @PostMapping("/login/verify-otp")
    public ResponseEntity<?> loginVerifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        System.out.println("Received login OTP verification request for email: " + email + ", otp: " + otp);

        ResponseEntity<?> otpValidationResponse = otpService.validateLoginOtp(email, otp);

        if (otpValidationResponse.getStatusCode().is2xxSuccessful()) {
            System.out.println("OTP validated successfully for email: " + email);

            Long userId = otpService.getUserIdByEmail(email); 
            String role = "customer";
            String token = jwtService.generateToken(email, role, userId);
            System.out.println("Generated JWT token for userId: " + userId);

            Optional<User> optionalUser = userRepository.findByEmail(email);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                System.out.println("Fetched user details for email: " + email);

                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("isRegistered", user.getAadhaarNumber() != null && !user.getAadhaarNumber().isEmpty());
                data.put("adminApproval", user.getIsAdminVerifiedDocuments());

                System.out.println("Login successful. Returning response with token and user info.");
                return ResponseEntity.ok(ResponseUtil.successWithData("Login successful.", data));
            } else {
                System.out.println("User not found for email: " + email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseUtil.notFound("User not found."));
            }
        } else {
            System.out.println("OTP validation failed for email: " + email);
            return otpValidationResponse;
        }
    }



    @PostMapping("/auth/register")
    public ResponseEntity<?> register(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("aadhaarNumber") String aadharNumber,
            @RequestParam("aadhaarFile") MultipartFile aadharFile,
            @RequestParam("selfieImage") MultipartFile selfieFile,
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
            otpService.registerUser(name, address, aadharNumber, aadharFile, drivingLicenseFile,userId, selfieFile);
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
    public ResponseEntity<?> getUserProfile(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            // Token validation
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
            }

            String token = authorizationHeader.substring(7);  // Extract token from Bearer
            if (jwtService.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
            }

            String role = jwtService.extractRole(token);
            if (role == null || !role.equalsIgnoreCase("customer")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
            }

            Long userId = jwtService.extractUserId(token);
            System.out.println("Extracted user ID from token: " + userId);  // Debugging statement (can be removed later)

            // Extract the user's email from the token
            String email = jwtService.extractEmailFromToken(token);

            // Fetch user data from repository based on email
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(ResponseUtil.notFound(ResponseUtil.ErrorMessages.notFoundWithId("User", email)));
            }

            User user = optionalUser.get();

            // If the user's address is null, return a not found response
            if (user.getAddress() == null) {
                return ResponseEntity.ok(ResponseUtil.notFound(
                    ResponseUtil.ErrorMessages.notFoundWithId("User Address", email)
                ));
            }

            // Check if documents are pending verification
            if ("PENDING".equalsIgnoreCase(user.getIsAdminVerifiedDocuments())) {
                return ResponseEntity.ok(ResponseUtil.underVerification("Documents are under verification"));
            }

            // Prepare the user data map
            Map<String, Object> userData = Map.of(
                "fullName", user.getFullName(),
                "mobile",user.getMobile(),
                "email",user.getEmail(),
                "address", user.getAddress(),
                "aadhaarNumber", user.getAadhaarNumber(),
                "aadhaarFilePath", oneDriveService.convertFileToBase64(user.getAadhaarFilePath()),
                "licenseFilePath", oneDriveService.convertFileToBase64(user.getLicenseFilePath()),
                "selfiePath",oneDriveService.convertFileToBase64(user.getSelfieImage()),
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


 // PUT update user profile by ID
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            // Token validation
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
            }

            String token = authorizationHeader.substring(7);  // Extract token from Bearer
            if (jwtService.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
            }

            String role = jwtService.extractRole(token);
            if (role == null || !role.equalsIgnoreCase("customer")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
            }

            Long userId = jwtService.extractUserId(token);
            System.out.println("Extracted user ID from token: " + userId);  // Debugging

            Optional<User> userOptional = userRepository.findById(userId);
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
            	try {
                user.setFullName(updates.get("fullName"));
            
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(ResponseUtil.validationError("something went wrong !"));
                }
            }

            userRepository.save(user);
            return ResponseEntity.ok(ResponseUtil.successMessage("User profile updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(ResponseUtil.internalError("An unexpected error occurred."));
        }
    }

}
