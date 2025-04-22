package com.sklassics.cars.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sklassics.cars.entites.CarEntity;
import com.sklassics.cars.entites.Reservation;
import com.sklassics.cars.exceptions.CustomExceptions.CarNotFoundException;
import com.sklassics.cars.repositories.CarRepository;
import com.sklassics.cars.repositories.ReservationRepository;

@Service
public class ReservationService {
	
	@Autowired
    private ReservationRepository reservationRepository;
	
	@Autowired
    private CarRepository carRepository;


	
	public Reservation saveReservation(Reservation reservation) {

	    
	    carRepository.findById(reservation.getCarId())
	        .orElseThrow(() -> new CarNotFoundException("Car not found with ID: " + reservation.getCarId()));

	    
	    reservation.setCarId(reservation.getCarId());
	    reservation.setMobile(reservation.getMobile());
	    reservation.setEmail(reservation.getEmail());
	    reservation.setFromDate(reservation.getFromDate());
	    reservation.setToDate(reservation.getToDate());
	    reservation.setPickupTime(reservation.getPickupTime());
	    reservation.setDropTime(reservation.getDropTime());
	    reservation.setPaymentId(reservation.getPaymentId());
	    reservation.setStatus("CONFIRMED");

	    return reservationRepository.save(reservation);
	}





    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public boolean deleteReservation(Long id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
    
    
    public Reservation updateReservation(Long id, Reservation updatedReservation) {
        Optional<Reservation> existingOpt = reservationRepository.findById(id);

        if (existingOpt.isPresent()) {
            Reservation existing = existingOpt.get();

            // Example fields to update — update as per your model
            existing.setFromDate(updatedReservation.getFromDate());
            existing.setToDate(updatedReservation.getToDate());
            existing.setPickupTime(updatedReservation.getPickupTime());
            existing.setDropTime(updatedReservation.getDropTime());



            return reservationRepository.save(existing);
        } else {
            return null;
        }
    }

    
    public List<Reservation> getReservationsByMobile(String mobile) {
        return reservationRepository.findByMobile(mobile);
    }

}
