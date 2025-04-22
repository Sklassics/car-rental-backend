package com.sklassics.cars.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sklassics.cars.entites.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByCarId(Long carId);
    
    @Query("SELECT b FROM Booking b WHERE b.carId = :carId AND b.status = 'paid'")
    List<Booking> findPaidBookingsByCarId(Long carId);

}
