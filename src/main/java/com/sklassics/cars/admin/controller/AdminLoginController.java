package com.sklassics.cars.admin.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sklassics.cars.admin.entites.Admin;
import com.sklassics.cars.admin.repositories.AdminRepository;
import com.sklassics.cars.dtos.MobileNoDTO;
import com.sklassics.cars.services.JwtService;
import com.sklassics.cars.services.OtpService;
import com.sklassics.cars.services.utility.ResponseUtil;

@RestController
@RequestMapping("/admin")
public class AdminLoginController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/login/send-otp")
    public ResponseEntity<Map<String, Object>> sendOtpMobile(@RequestBody MobileNoDTO otpRequest) {
        String mobile = otpRequest.getPhoneNumber();

        if (mobile == null || mobile.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseUtil.validationError("Mobile number is required."));
        }

        Optional<Admin> admin = adminRepository.findByMobileNumber(mobile);
        if (admin.isEmpty()) {
            return ResponseEntity.ok(ResponseUtil.notFound("Mobile number is not registered in the admin system."));
        }

        try {
            return otpService.sendOtp(mobile); // Assumes OtpService already returns ResponseUtil-style response
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseUtil.internalError("Failed to send OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/login/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtpMobile(@RequestBody Map<String, String> request) {
        String mobileNumber = request.get("phoneNumber");
        String otp = request.get("otp");

        if (mobileNumber == null || mobileNumber.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseUtil.validationError("Mobile number is required."));
        }

        if (otp == null || otp.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseUtil.validationError("OTP is required."));
        }

        try {
            ResponseEntity<Map<String, Object>> validationResponse = otpService.validateOtp(mobileNumber, otp);

            if (!validationResponse.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = validationResponse.getBody();
                String message = responseBody != null && responseBody.containsKey("message")
                        ? responseBody.get("message").toString()
                        : "OTP verification failed.";
                return ResponseEntity.status(validationResponse.getStatusCode())
                        .body(ResponseUtil.unauthorized(message));
            }
            
            Long userId = otpService.getAdminByMobile(mobileNumber); 

            String token = jwtService.generateToken(mobileNumber, "admin", userId);

            return ResponseEntity.ok(
                    ResponseUtil.successWithData("OTP Verified Successfully", Map.of("token", token))
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseUtil.internalError("Error processing response: " + e.getMessage()));
        }
    }
}
