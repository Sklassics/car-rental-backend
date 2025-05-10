package com.sklassics.cars.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sklassics.cars.entities.User;
import com.sklassics.cars.repositories.UserRepository;
import com.sklassics.cars.services.EmailService;
import com.sklassics.cars.services.JwtService;
import com.sklassics.cars.services.OneDriveService;
import com.sklassics.cars.services.utility.ResponseUtil;

@RestController
@RequestMapping("/api")
public class WaitingController {
		
	
	@Autowired
	private JwtService jwtService;
	
	 @Autowired
	    private UserRepository userRepository;
	 
	 @Autowired
	 private EmailService emailService;
	 
<<<<<<< HEAD
		@Autowired
	    private OneDriveService oneDriveService;
	 
=======
>>>>>>> acc4b4f35693779f375ab3e1dd9d69f1580529d2
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
	
	@PostMapping("/subscribe")
    public ResponseEntity<?> subscribeToNewsletter(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }

        try {
            emailService.sendNewsletterSubscriptionEmail(email);
<<<<<<< HEAD
            return ResponseEntity.ok("Subscribed to our newsletter!!");
=======
            return ResponseEntity.ok("Subscription successful. Confirmation email sent to " + email);
>>>>>>> acc4b4f35693779f375ab3e1dd9d69f1580529d2
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to send email: " + e.getMessage());
        }
    }
<<<<<<< HEAD
	
	 @GetMapping("/car-homepage")
	    public ResponseEntity<Map<String, Object>> getCarHomePageImages() {
	        try {
	            List<String> imageUrls = oneDriveService.getImageUrlsFromCarHomePageFolder();
	            return ResponseEntity.ok(ResponseUtil.successWithData("Image URLs fetched successfully", imageUrls));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(ResponseUtil.internalError("Failed to fetch image URLs"));
	        }
	    }
=======
>>>>>>> acc4b4f35693779f375ab3e1dd9d69f1580529d2
}
