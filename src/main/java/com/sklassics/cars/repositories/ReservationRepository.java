package com.sklassics.cars.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sklassics.cars.entites.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	
	List<Reservation> findByCarIdAndStatus(Long carId, String status);
	
	List<Reservation> findByMobile(String mobile);


   
}


