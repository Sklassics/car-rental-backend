package com.sklassics.cars.services;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sklassics.cars.admin.entites.Admin;
import com.sklassics.cars.admin.repositories.AdminRepository;
import com.sklassics.cars.entites.User;
import com.sklassics.cars.exceptions.CustomExceptions.UserNotFoundException;
import com.sklassics.cars.repositories.UserRepository;
import com.sklassics.cars.services.utility.OtpCache;
import com.sklassics.cars.services.utility.ResponseUtil;

@Service
public class OtpService {

	@Autowired
	private UserRepository userRepository;
	
	
	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private OneDriveService oneDriveService;

	@Autowired
	private JwtService jwtService;

	private static final String DUMMY_OTP = "123456";
	private final Map<String, OtpCache> otpCacheMap = new ConcurrentHashMap<>();
	private final Map<String, String> otpCache = new ConcurrentHashMap<>();
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public ResponseEntity<?> sendMobileEmailOtp(String mobile, String email) {
		// Check if the mobile number already exists in the database
		boolean isMobileExists = userRepository.existsByMobile(mobile); // Assuming userRepository exists and has this
																		// method

		if (isMobileExists) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ResponseUtil.alreadyExist("Mobile number already exist. Please Login !!: " + mobile));
		}

		// If the mobile number doesn't exist in the database, proceed with OTP sending
		otpCacheMap.put(mobile, new OtpCache(email, DUMMY_OTP));
		System.out.println("Storing OTP " + DUMMY_OTP + " for mobile: " + mobile + ", email: " + email);

		return ResponseEntity.ok(ResponseUtil.successMessage("OTP has been sent to mobile number: " + mobile));
	}

	public ResponseEntity<?> validateMobileEmailOtp(String mobile, String otp) {
		OtpCache cached = otpCacheMap.get(mobile);
		if (cached == null) {
			return ResponseEntity.badRequest().body(ResponseUtil.notFound("No OTP generated for this mobile number."));
		}

		if (cached.getOtp().equals(otp)) {
			User user = new User();
			user.setMobile(mobile);
			user.setEmail(cached.getEmail());

			userRepository.save(user);
			otpCacheMap.remove(mobile);

			// Use saved user ID in the token
			Long userId = user.getId(); // Assuming ID is generated and set after save
			String token = jwtService.generateToken(mobile, "customer", userId);

			Map<String, Object> data = new HashMap<>();
			data.put("token", token);
			return ResponseEntity.ok(ResponseUtil.successWithData("OTP Verified Successfully !", data));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorized("Invalid OTP."));
		}
	}

	public ResponseEntity<?> sendLoginOtp(String mobile) {
		otpCache.put(mobile, DUMMY_OTP);
		scheduleOtpExpiration(mobile, 5);
		System.out.println("OTP sent to mobile: " + mobile);
		return ResponseEntity.ok(ResponseUtil.successMessage("OTP has been sent to mobile number: " + mobile));
	}

	public ResponseEntity<?> validateLoginOtp(String mobile, String otp) {
		String cachedOtp = otpCache.get(mobile);
		if (cachedOtp == null) {
			return ResponseEntity.badRequest().body(ResponseUtil.notFound("No OTP generated for this mobile number."));
		}
		if (cachedOtp.equals(otp)) {
			otpCache.remove(mobile);
			return ResponseEntity.ok(ResponseUtil.successMessage("OTP verified successfully."));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorized("Invalid OTP."));
		}
	}

	public Long getAdminByMobile(String mobile) {
		return adminRepository.findByMobileNumber(mobile).map(Admin::getId)
				.orElseThrow(() -> new UserNotFoundException("User not found with mobile: " + mobile));
	}
	
	

	public ResponseEntity<?> sendAadhaarOtp(String aadhaar) {
		otpCache.put(aadhaar, DUMMY_OTP);
		scheduleOtpExpiration(aadhaar, 5);
		return ResponseEntity.ok(ResponseUtil.successMessage("OTP sent successfully to Aadhaar number: " + aadhaar));
	}

	public ResponseEntity<?> validateAadhaarOtp(String aadhaar, String otp) {
		if (!otpCache.containsKey(aadhaar)) {
			return ResponseEntity.badRequest().body(ResponseUtil.notFound("No OTP generated for this Aadhaar number."));
		}
		if (!otpCache.get(aadhaar).equals(otp)) {
			return ResponseEntity.status(401).body(ResponseUtil.unauthorized("Invalid OTP."));
		}
		otpCache.remove(aadhaar);
		
		
		return ResponseEntity.ok(ResponseUtil.successMessage("OTP validated successfully for Aadhaar."));
	}

	private String saveFileToOneDrive(MultipartFile file, String folder) {
		try {
			String fileName = file.getOriginalFilename();
			System.out.println("Uploading file to OneDrive - Folder: " + folder + ", File: " + fileName);

			// Replace with your actual OneDrive service logic
			String oneDriveFileUrl = oneDriveService.uploadFile(file, folder, fileName); 
																							

			System.out.println("File uploaded to OneDrive. Accessible at: " + oneDriveFileUrl);
			return oneDriveFileUrl;
		} catch (Exception e) {
			System.out.println("Error uploading to OneDrive: " + e.getMessage());
			throw new RuntimeException("OneDrive upload failed", e);
		}
	}

	public void registerUser(String name, String address, String aadharNumber, MultipartFile aadharFile,
			MultipartFile drivingLicenseFile, Long userId) {

		System.out.println("Starting user registration...");
		System.out.println(
				"Received details - Name: " + name + ", Location: " + address + ", Aadhaar Number: " + aadharNumber);
		System.out.println("Aadhaar File Original Name: " + aadharFile.getOriginalFilename());
		System.out.println("Driving License File Original Name: " + drivingLicenseFile.getOriginalFilename());


		String aadharUrl = saveFileToOneDrive(aadharFile, "aadhaar");
		System.out.println("Aadhaar file uploaded to OneDrive at: " + aadharUrl);

		String licenseUrl = saveFileToOneDrive(drivingLicenseFile, "license");
		System.out.println("License file uploaded to OneDrive at: " + licenseUrl);

		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isEmpty()) {
			throw new UserNotFoundException("User not found with ID: " + userId);
		}

		User user = optionalUser.get();
		user.setFullName(name);
		user.setAddress(address);
		user.setAadhaarNumber(aadharNumber);
		user.setAadhaarFilePath(aadharUrl);
		user.setLicenseFilePath(licenseUrl);
		user.setIsAdminVerifiedDocuments("PENDING");
		user.setSubmittedAt(LocalDate.now());

		System.out.println("Updating user in database: " + user);
		userRepository.save(user);
		System.out.println("User registration completed successfully.");
	}

	public ResponseEntity<Map<String, Object>> sendOtp(String phoneNumber) {
		Map<String, Object> response = new HashMap<>();
		if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
			return ResponseEntity.badRequest()
					.body(ResponseUtil.validationError("Invalid phone number format. Must be 10 digits."));
		}

		otpCache.put(phoneNumber, DUMMY_OTP);
		scheduleOtpExpiration(phoneNumber, 5);
		response.put("otp", DUMMY_OTP);
		return ResponseEntity.ok(ResponseUtil.successMessage("OTP sent successfully."));
	}

	public ResponseEntity<Map<String, Object>> validateOtp(String phoneNumber, String otp) {
		Map<String, Object> response = new HashMap<>();

		if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
			return ResponseEntity.badRequest().body(ResponseUtil.validationError("Phone number is required."));
		}

		if (otp == null || otp.trim().isEmpty()) {
			return ResponseEntity.badRequest().body(ResponseUtil.validationError("OTP is required."));
		}

		if (!otp.matches("\\d{6}")) {
			return ResponseEntity.badRequest()
					.body(ResponseUtil.validationError("Invalid OTP format. Must be 6 digits."));
		}

		String storedOtp = otpCache.get(phoneNumber);
		if (storedOtp == null) {
			return ResponseEntity.badRequest()
					.body(ResponseUtil.notFound("OTP not requested or expired for this number."));
		}

		if (!storedOtp.equals(otp)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(ResponseUtil.unauthorized("Invalid OTP. Please check and try again."));
		}

		otpCache.remove(phoneNumber);
		response.put("message", "OTP verified successfully.");
		return ResponseEntity.ok(ResponseUtil.successWithData("OTP verified successfully.", response));
	}
	
	public Long getUserIdByMobile(String mobile) {
		return userRepository.findByMobile(mobile).map(User::getId)
				.orElseThrow(() -> new UserNotFoundException("User not found with mobile: " + mobile));
	}

	private void scheduleOtpExpiration(String key, int minutes) {
		scheduler.schedule(() -> {
			otpCache.remove(key);
			System.out.println("OTP expired for: " + key);
		}, minutes, TimeUnit.MINUTES);
	}
}
