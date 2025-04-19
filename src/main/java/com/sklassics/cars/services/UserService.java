package com.sklassics.cars.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sklassics.cars.entites.User;
import com.sklassics.cars.exceptions.CustomExceptions.UserNotFoundException;
import com.sklassics.cars.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    
    
    @Autowired
    private OneDriveService oneDriveService;
    
    

    private static final String DUMMY_OTP = "123456";

    public ResponseEntity<?> register(Map<String, String> request) {
        String mobile = request.get("mobile");
        String email = request.get("email");
        String name = request.get("name"); // Add other fields if needed
        String password = request.get("password");

        // Check if mobile is already registered
        if (userRepository.existsByMobile(mobile)) {
            return ResponseEntity.badRequest().body("Mobile already registered");
        }

        // Create the user and save it
        User user = new User();
        user.setMobile(mobile);
        user.setEmail(email);
        

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
    public ResponseEntity<?> sendOtp(String mobile, String email) {
        // No OTP in request, generate/send the dummy one
        System.out.println("Sending dummy OTP " + DUMMY_OTP + " to " + mobile + " / " + email);
        return ResponseEntity.ok("OTP sent to your mobile/email.");
    }

    // ========== VERIFY OTP ==========
    public ResponseEntity<?> verifyOtp(String mobile, String otp) {
        if (!DUMMY_OTP.equals(otp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }

        return ResponseEntity.ok("OTP verified successfully.");
    }
    

    public ResponseEntity<?> login(String mobile, String otp) {
        if (!DUMMY_OTP.equals(otp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }

        return userRepository.findByMobile(mobile)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException("User not found with mobile number :  " + mobile));
    }
    



    public ResponseEntity<?> getUser(Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEmail(updatedUser.getEmail());
                    user.setFullName(updatedUser.getFullName());
                    user.setMobile(updatedUser.getMobile());
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public String uploadLicenseFile(Long userId, MultipartFile file) throws Exception {
        // Optionally, associate the file with the user and save file path in database
        String uploadedFileUrl = oneDriveService.uploadLicenseFile("UserLicenses", file);
        return uploadedFileUrl; // Returns the URL of the uploaded license file
    }
//    public ResponseEntity<?> uploadLicense(Long id, MultipartFile file) {
//        return userRepository.findById(id)
//                .map(user -> {
//                    try {
//                        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//                        Path uploadPath = Paths.get("uploads/licenses");
//                        Files.createDirectories(uploadPath);
//                        Path filePath = uploadPath.resolve(fileName);
//                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//                        user.setLicenseFilePath(filePath.toString());
//                        userRepository.save(user);
//                        return ResponseEntity.ok("License uploaded successfully");
//                    } catch (IOException e) {
//                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload error");
//                    }
//                })
//                .orElse(ResponseEntity.notFound().build());
//    }
}

