package com.sklassics.cars.services;

import com.sklassics.cars.entites.Booking;
import com.sklassics.cars.entites.Transaction;
import com.sklassics.cars.repositories.BookingRepository;
import com.sklassics.cars.repositories.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
	
	@Autowired
    private BookingRepository bookingRepository;
	
	
	@Autowired
    private TransactionRepository transactionRepository;

    
    

    public Booking createBooking(Booking booking,Long userId) {
    	
    	Optional<Transaction> transaction = transactionRepository.findByRazorpayPaymentId(booking.getPaymentId());

    	if (transaction.isPresent()) {
    	    
    	    booking.setStatus(transaction.get().getOrderStatus());
    	}

    	Booking newBooking = new Booking();
        newBooking.setCarId(booking.getCarId());
        newBooking.setMobile(booking.getMobile());
        newBooking.setEmail(booking.getEmail());
        newBooking.setFromDate(booking.getFromDate());
        newBooking.setToDate(booking.getToDate());
        newBooking.setPickupTime(booking.getPickupTime());
        newBooking.setDropTime(booking.getDropTime());
        newBooking.setPaymentId(booking.getPaymentId());
        newBooking.setAgreedToTerms(booking.isAgreedToTerms());
        newBooking.setUserId(userId);
        
        
        return bookingRepository.save(booking);
    }

    public Booking getBooking(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public boolean cancelBooking(Long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
            return true;
        }
        return false;
    }

}
