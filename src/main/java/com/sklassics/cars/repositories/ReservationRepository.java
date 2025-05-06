package com.sklassics.cars.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sklassics.cars.entities.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	
	List<Reservation> findByCarIdAndStatus(Long carId, String status);
	
	List<Reservation> findByUserId(Long userId);
	
	
	Optional<Reservation> findTopByUserId(Long userId);



	@Query("SELECT r FROM Reservation r WHERE r.carId = :carId AND r.status NOT IN ('cancelled', 'refunded')")
	List<Reservation> findActiveReservationsByCarId(Long carId);

}


