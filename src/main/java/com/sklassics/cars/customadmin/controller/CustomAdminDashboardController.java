package com.sklassics.cars.customadmin.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD
import org.springframework.web.multipart.MultipartFile;

import com.sklassics.cars.customadmin.repositories.CustomUsersRepository;
import com.sklassics.cars.dtos.UserDTO;
import com.sklassics.cars.entities.User;
import com.sklassics.cars.exceptions.CustomExceptions.UserNotFoundException;
import com.sklassics.cars.repositories.UserRepository;
import com.sklassics.cars.services.JwtService;
import com.sklassics.cars.services.OneDriveService;
import com.sklassics.cars.services.OtpService;
import com.sklassics.cars.services.utility.ResponseUtil;

@RestController
@RequestMapping("/api/custom-admin")
public class CustomAdminDashboardController {

    @Autowired
    private CustomUsersRepository customUsersRepository;
=======

import com.sklassics.cars.dtos.UserDTO;
import com.sklassics.cars.entities.User;
import com.sklassics.cars.repositories.UserRepository;
import com.sklassics.cars.services.JwtService;
import com.sklassics.cars.services.OneDriveService;
import com.sklassics.cars.services.utility.ResponseUtil;

@RestController
@RequestMapping("/custom-admin/dashboard")
public class CustomAdminDashboardController {

    @Autowired
    private UserRepository userRepository;
>>>>>>> acc4b4f35693779f375ab3e1dd9d69f1580529d2
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private OneDriveService oneDriveService;
<<<<<<< HEAD
    
    @Autowired
    private OtpService otpService;

    
    @PostMapping("/register")
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
=======

    @GetMapping("/pending-documents")
    public ResponseEntity<Map<String, Object>> getUsersWithPendingDocuments(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                System.out.println("Authorization header missing or invalid format");
>>>>>>> acc4b4f35693779f375ab3e1dd9d69f1580529d2
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Missing or invalid token. Token must be in Bearer format."));
            }

<<<<<<< HEAD
            String token = authorizationHeader.substring(7);
            if (jwtService.isTokenExpired(token)) {
=======
            // Extract token from Authorization header
            String token = authorizationHeader.substring(7);
            System.out.println("Extracted token: " + token);

            // Validate token expiry
            if (jwtService.isTokenExpired(token)) {
                System.out.println("Token expired: " + token);
>>>>>>> acc4b4f35693779f375ab3e1dd9d69f1580529d2
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Token has expired. JWT token is not valid."));
            }

<<<<<<< HEAD
            String role = jwtService.extractRole(token);
            if (role == null || !role.equalsIgnoreCase("customAdmin")) {
=======
            // Extract and validate role
            String role = jwtService.extractRole(token);
            System.out.println("Extracted role from token: " + role);
            if (role == null || !role.equalsIgnoreCase("admin")) {
                System.out.println("Role mismatch or role is null");
>>>>>>> acc4b4f35693779f375ab3e1dd9d69f1580529d2
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
            }
<<<<<<< HEAD
            
            Long userId = jwtService.extractUserId(token);
            System.out.println("Extracted user ID from token: " + userId);

            // Registration process
            otpService.customRegisterUser(name, address, aadharNumber, aadharFile, drivingLicenseFile,userId, selfieFile);
            return ResponseEntity.ok(ResponseUtil.successMessage("Profile submitted !"));

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
=======

            // Extract user ID (optional use)
            Long userId = jwtService.extractUserId(token);
            System.out.println("Extracted user ID from token: " + userId);
            
            // Fetch users with pending documents
            List<User> pendingUsers = userRepository.findByIsAdminVerifiedDocuments("PENDING");

            if (pendingUsers.isEmpty()) {
                return ResponseEntity.ok(ResponseUtil.notFound("No users found with pending documents"));
            }

            List<UserDTO> userDTOs = pendingUsers.stream().map(user -> {
                UserDTO dto = new UserDTO();
                dto.setEmail(user.getEmail());
                dto.setMobile(user.getMobile());
                dto.setFullName(user.getFullName());
                dto.setAadhaarNumber(user.getAadhaarNumber());
                // Convert Aadhaar and License files to Base64
                dto.setAadhaarFilePath(oneDriveService.convertFileToBase64(user.getAadhaarFilePath()));
                dto.setLicenseFilePath(oneDriveService.convertFileToBase64(user.getLicenseFilePath()));

                dto.setLocation(user.getAddress());
                dto.setSubmittedAt(user.getSubmittedAt());
                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(ResponseUtil.successWithData("Pending users retrieved successfully", userDTOs));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ResponseUtil.internalError(e.getMessage()));
        }
    }


    // 2. Approve or Reject documents
    @PostMapping("/verify-document")
    public ResponseEntity<Map<String, Object>> verifyUserDocuments(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam String mobile,
            @RequestParam String action) {

        try {
            // Check for authorization token
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
            if (role == null || !role.equalsIgnoreCase("admin")) {
                System.out.println("Role mismatch or role is null");
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
            }

            // Find the user by mobile number
            User user = userRepository.findByMobile(mobile).orElse(null);

            if (user == null) {
                return ResponseEntity.ok(ResponseUtil.notFound(
                        ResponseUtil.ErrorMessages.notFoundWithId("User with mobile", mobile)));
            }

            // Validate action (APPROVED or REJECTED)
            if (!action.equalsIgnoreCase("APPROVED") && !action.equalsIgnoreCase("REJECTED")) {
                return ResponseEntity.badRequest().body(ResponseUtil.validationError("Invalid action. Use APPROVED or REJECTED."));
            }

            // Update user's verification status
            user.setIsAdminVerifiedDocuments(action.toUpperCase());
            userRepository.save(user);

            return ResponseEntity.ok(ResponseUtil.successMessage("User verification status updated to: " + action.toUpperCase()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ResponseUtil.internalError(e.getMessage()));
        }
    }

>>>>>>> acc4b4f35693779f375ab3e1dd9d69f1580529d2
}
