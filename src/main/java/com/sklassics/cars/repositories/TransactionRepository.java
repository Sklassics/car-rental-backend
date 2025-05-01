package com.sklassics.cars.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sklassics.cars.entities.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	
	Optional<Transaction> findByRazorpayPaymentId(String razorpayPaymentId);


}
