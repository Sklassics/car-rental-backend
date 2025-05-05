package com.sklassics.cars.services;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sklassics.cars.entities.Booking;
import com.sklassics.cars.entities.Reservation;
import com.sklassics.cars.entities.TransactionUnderVerification;
import com.sklassics.cars.repositories.BookingRepository;
import com.sklassics.cars.repositories.ReservationRepository;
import com.sklassics.cars.repositories.TransactionUnderVerificationRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionUnderVerificationService {

	@Autowired
	private TransactionUnderVerificationRepository transactionUnderVerificationRepository;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	
	
	@Autowired
	private OneDriveService oneDriveService;

	public TransactionUnderVerification saveTransaction(Long userId, String mobile, String email, String transactionId,
			Double amount, MultipartFile screenshot,String Action) throws IOException {

		
		String imageUrl = saveFileToOneDrive(screenshot, "transactions_screenshots");
		System.out.println("Transaction file uploaded to OneDrive at: " + imageUrl);

		
		
		TransactionUnderVerification transaction = new TransactionUnderVerification();
		transaction.setUserId(userId);
		transaction.setMobile(mobile);
		transaction.setEmail(email);
		transaction.setTransactionId(transactionId);
		transaction.setAmount(amount);
		transaction.setTransactionScreenshotUrl(imageUrl);
		transaction.setAction(Action);
		transaction.setAdminVerified(false);

		return transactionUnderVerificationRepository.save(transaction);
	}
	
	 public List<TransactionUnderVerification> getUnverifiedTransactions() {
	        // Fetch transactions where 'isAdminVerified' is false
	        List<TransactionUnderVerification> transactions = transactionUnderVerificationRepository.findByIsAdminVerifiedFalse();
	        
	        // Manually set each field in the response
	        return transactions.stream().map(transaction -> {
	            // Manually setting fields if needed (e.g., converting image URLs)
	            transaction.setTransactionScreenshotUrl(oneDriveService.convertFileToBase64(transaction.getTransactionScreenshotUrl()));
	            
	            // Set other fields manually if necessary
	            return transaction;
	        }).collect(Collectors.toList());
	    }
	
	

	@Transactional
	public TransactionUnderVerification updateAdminVerification(String transactionId, boolean isVerified) {
	    TransactionUnderVerification transaction = transactionUnderVerificationRepository.findByTransactionId(transactionId)
	            .orElseThrow(() -> new RuntimeException("Transaction not found"));

	    transaction.setAdminVerified(isVerified);
	    transactionUnderVerificationRepository.save(transaction);

	    String action = transaction.getAction();

	    if (isVerified && "book".equals(action)) {
	        Booking booking = new Booking();
	        booking.setUserId(transaction.getUserId());
	        booking.setPaymentId(transaction.getTransactionId());
	        booking.setStatus("paid");

	        bookingRepository.save(booking);
	    } else if (isVerified && "reserve".equals(action)) {
	        Reservation reservation = new Reservation();
	        reservation.setUserId(transaction.getUserId());
	        reservation.setPaymentId(transaction.getTransactionId());
	        reservation.setStatus("paid");

	        reservationRepository.save(reservation);
	    }

	    return transaction;
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
}
