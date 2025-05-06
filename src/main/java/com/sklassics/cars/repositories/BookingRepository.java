package com.sklassics.cars.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sklassics.cars.entities.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByCarId(Long carId);
    
    Optional<Booking> findTopByUserId(Long userId);
    
    Optional<Booking> findTopByCarId(Long carId);
    
    @Query("SELECT b FROM Booking b WHERE b.carId = :carId AND b.status = 'paid'")
    List<Booking> findPaidBookingsByCarId(Long carId);

}
