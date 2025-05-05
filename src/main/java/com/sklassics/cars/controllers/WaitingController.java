package com.sklassics.cars.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sklassics.cars.entities.User;
import com.sklassics.cars.repositories.UserRepository;
import com.sklassics.cars.services.JwtService;
import com.sklassics.cars.services.utility.ResponseUtil;

@RestController
@RequestMapping("/api")
public class WaitingController {
		
	
	@Autowired
	private JwtService jwtService;
	
	 @Autowired
	    private UserRepository userRepository;
	 
	@GetMapping("/adminVerified")
    public ResponseEntity<?> getWaitingProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmailFromToken(token);
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.get().getAddress() == null) {
                return ResponseEntity.ok(ResponseUtil.notFound(
                    ResponseUtil.ErrorMessages.notFoundWithId("User", email)
                ));
            }

            User user = optionalUser.get();

            if ("PENDING".equalsIgnoreCase(user.getIsAdminVerifiedDocuments())) {
                return ResponseEntity.ok(ResponseUtil.underVerification("Documents are under verification"));
            }

            
            if ("REJECTED".equalsIgnoreCase(user.getIsAdminVerifiedDocuments())) {
                return ResponseEntity.ok(ResponseUtil.rejected("Application rejected !! Re-apply with valid documents"));
            }
            
            return ResponseEntity.ok(ResponseUtil.successMessage("Documents Verified Successfully !!"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(ResponseUtil.internalError("Error while fetching user profile"));
        }
    }
}
