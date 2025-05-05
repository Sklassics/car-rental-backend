package com.sklassics.cars.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sklassics.cars.entities.TransactionUnderVerification;

public interface TransactionUnderVerificationRepository extends JpaRepository<TransactionUnderVerification, Long> {
	
    List<TransactionUnderVerification> findByIsAdminVerifiedFalse();
    
    Optional<TransactionUnderVerification> findByTransactionId(String transactionId);


}
