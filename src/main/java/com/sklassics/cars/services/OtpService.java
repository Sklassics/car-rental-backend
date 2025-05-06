package com.sklassics.cars.services;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sklassics.cars.admin.entities.Admin;
import com.sklassics.cars.admin.repositories.AdminRepository;
import com.sklassics.cars.customadmin.entities.CustomAdmin;
import com.sklassics.cars.customadmin.repositories.CustomAdminRepository;
import com.sklassics.cars.entities.User;
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

	@Autowired
	private JavaMailSender javaMailSender;
	
	  @Autowired
	    private CustomAdminRepository customAdminRepository;

	@Autowired
	private EmailService emailService;

	private static final String DUMMY_OTP = "123456";
	private final Map<String, OtpCache> otpCacheMap = new ConcurrentHashMap<>();
	private final Map<String, String> otpCache = new ConcurrentHashMap<>();
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	
	
	
	public ResponseEntity<?> sendMobileEmailOtp(String mobile, String email) {
		// Check if the mobile number already exists in the database
		if (userRepository.existsByMobile(mobile)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ResponseUtil.alreadyExist("Mobile number " + mobile + " already exists. Please Login !!"));
		}
	
		if (userRepository.existsByEmail(email)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ResponseUtil.alreadyExistWithEmail("Email: " + email + " already exists !!"));
		}
	
		// Generate 6-digit OTP
		String otp = String.format("%06d", new Random().nextInt(999999));
	
		// Save OTP in cache
		otpCacheMap.put(email, new OtpCache(mobile, email, otp));
		System.out.println("Generated OTP " + otp + " for mobile: " + mobile + ", email: " + email);
	
		// Send OTP via Email using EmailService
		try {
			emailService.sendOtpEmail(email, otp);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ResponseUtil.internalError("Failed to send OTP email. Please try again."));
		}
	
		return ResponseEntity.ok(ResponseUtil.successMessage("OTP has been sent to email: " + email));
	}
	

	public ResponseEntity<?> validateMobileEmailOtp(String email, String otp) {
	    OtpCache cached = otpCacheMap.get(email);

	    if (cached == null) {
	        return ResponseEntity.badRequest()
	                .body(ResponseUtil.notFound("No OTP generated for this email."));
	    }

	    if (!cached.getOtp().equals(otp)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(ResponseUtil.unauthorized("Invalid OTP."));
	    }

	    // Create and save user
	    User user = new User();
	    user.setMobile(cached.getMobile());
	    user.setEmail(cached.getEmail());
	    userRepository.save(user);

	    // Clean up cache after successful validation
	    otpCacheMap.remove(email); 

	    // Generate token
	    String token = jwtService.generateToken(cached.getMobile(), "customer", user.getId());  // Use cached.getMobile() to fetch the correct mobile number

	    Map<String, Object> data = new HashMap<>();
	    data.put("token", token);

	    return ResponseEntity.ok(ResponseUtil.successWithData("OTP Verified Successfully!", data));
	}




	public ResponseEntity<?> sendLoginOtp(String email) {
		// Generate 6-digit OTP
				String otp = String.format("%06d", new Random().nextInt(999999));
			
				// Save OTP in cache
				otpCache.put(email,otp);				
				try {
					emailService.sendOtpEmail(email, otp);
				} catch (Exception e) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body(ResponseUtil.internalError("Failed to send OTP email. Please try again."));
				}
		return ResponseEntity.ok(ResponseUtil.successMessage("OTP has been sent to email: " + email));
	}
	
	
	

	public ResponseEntity<?> validateLoginOtp(String email, String otp) {
	    // Basic null or empty check
	    if (email == null || email.trim().isEmpty()) {
	        return ResponseEntity.badRequest()
	                .body(ResponseUtil.conflict("Email must not be empty."));
	    }

	    if (otp == null || otp.trim().isEmpty()) {
	        return ResponseEntity.badRequest()
	                .body(ResponseUtil.conflict("OTP must not be empty."));
	    }


	    // OTP format validation (assuming 6-digit numeric)
	    if (!otp.matches("^\\d{6}$")) {
	        return ResponseEntity.badRequest()
	                .body(ResponseUtil.conflict("Invalid OTP format. OTP must be 6 digits."));
	    }

	    String cachedOtp = otpCache.get(email);
	    if (cachedOtp == null) {
	        return ResponseEntity.badRequest()
	                .body(ResponseUtil.notFound("No OTP generated for this email."));
	    }

	    if (!cachedOtp.equals(otp)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(ResponseUtil.unauthorized("Invalid OTP."));
	    }

	    otpCache.remove(email);
	    return ResponseEntity.ok(ResponseUtil.successMessage("OTP verified successfully."));
	}



	public Long getAdminByMobile(String mobile) {
		return adminRepository.findByMobileNumber(mobile).map(Admin::getId)
				.orElseThrow(() -> new UserNotFoundException("User not found with mobile: " + mobile));
	}
	
	public Long getCustomAdminByMobile(String mobile) {
		return customAdminRepository.findByMobileNumber(mobile).map(CustomAdmin::getId)
				.orElseThrow(() -> new UserNotFoundException("User not found with mobile: " + mobile));
	}
	
//	public ResponseEntity<?> sendMobileEmailOtp(String mobile, String email) {
//	// Check if the mobile number already exists in the database
//	boolean isMobileExists = userRepository.existsByMobile(mobile); 
//
//	if (isMobileExists) {
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//				.body(ResponseUtil.alreadyExist("Mobile number " + mobile +"already exist. Please Login !! "));
//	}
//	
//	boolean isEmailExists =userRepository.existsByEmail(email);
//	
//	if (isEmailExists)
//	{
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//				.body(ResponseUtil.alreadyExistWithEmail("Email :" + email + " already exist !!"));
//		
//	}
//
//	// If the mobile number doesn't exist in the database, proceed with OTP sending
//	otpCacheMap.put(mobile, new OtpCache(email, DUMMY_OTP));
//	System.out.println("Storing OTP " + DUMMY_OTP + " for mobile: " + mobile + ", email: " + email);
//
//	return ResponseEntity.ok(ResponseUtil.successMessage("OTP has been sent to mobile number: " + mobile));
//}
//
//
//public ResponseEntity<?> validateMobileEmailOtp(String mobile, String otp) {
//	OtpCache cached = otpCacheMap.get(mobile);
//	if (cached == null) {
//		return ResponseEntity.badRequest().body(ResponseUtil.notFound("No OTP generated for this mobile number."));
//	}
//
//	if (cached.getOtp().equals(otp)) {
//		User user = new User();
//		user.setMobile(mobile);
//		user.setEmail(cached.getEmail());
//
//		userRepository.save(user);
//		otpCacheMap.remove(mobile);
//
//		// Use saved user ID in the token
//		Long userId = user.getId();
//		String token = jwtService.generateToken(mobile, "customer", userId);
//		
//		
//
//		Map<String, Object> data = new HashMap<>();
//		data.put("token", token);
//		return ResponseEntity.ok(ResponseUtil.successWithData("OTP Verified Successfully !", data));
//	} else {
//		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.unauthorized("Invalid OTP."));
//	}
//}

//	public ResponseEntity<?> sendAadhaarOtp(String aadhaar) {
//		otpCache.put(aadhaar, DUMMY_OTP);
//		scheduleOtpExpiration(aadhaar, 5);
//		return ResponseEntity.ok(ResponseUtil.successMessage("OTP sent successfully to Aadhaar number: " + aadhaar));
//	}
//
//	public ResponseEntity<?> validateAadhaarOtp(String aadhaar, String otp) {
//		if (!otpCache.containsKey(aadhaar)) {
//			return ResponseEntity.badRequest().body(ResponseUtil.notFound("No OTP generated for this Aadhaar number."));
//		}
//		if (!otpCache.get(aadhaar).equals(otp)) {
//			return ResponseEntity.status(401).body(ResponseUtil.unauthorized("Invalid OTP."));
//		}
//		otpCache.remove(aadhaar);
//		
//		
//		return ResponseEntity.ok(ResponseUtil.successMessage("OTP validated successfully for Aadhaar."));
//	}

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
	
	public Long getUserIdByEmail(String email) {
		return userRepository.findByEmail(email).map(User::getId)
				.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
	}

	private void scheduleOtpExpiration(String key, int minutes) {
		scheduler.schedule(() -> {
			otpCache.remove(key);
			System.out.println("OTP expired for: " + key);
		}, minutes, TimeUnit.MINUTES);
	}
}
