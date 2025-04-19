package com.sklassics.cars.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sklassics.cars.entites.CarPayment;

@Repository
public interface CarPaymentRepository extends JpaRepository<CarPayment, Long> {
}
