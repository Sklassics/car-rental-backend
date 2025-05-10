package com.sklassics.cars.customadmin.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private OneDriveService oneDriveService;
    
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
            if (role == null || !role.equalsIgnoreCase("customAdmin")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseUtil.unauthorized("Unauthorized access. Role mismatch."));
            }
            
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
}
