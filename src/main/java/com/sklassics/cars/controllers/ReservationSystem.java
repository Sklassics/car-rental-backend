package com.sklassics.cars.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sklassics.cars.entites.Reservation;
import com.sklassics.cars.exceptions.CustomExceptions.CarNotFoundException;
import com.sklassics.cars.services.ReservationService;
import com.sklassics.cars.services.utility.ResponseUtil;
import com.sklassics.cars.services.utility.ResponseUtil.ErrorMessages;

@RestController
@RequestMapping("/api/reservations")
public class ReservationSystem {

    private final ReservationService reservationService;

    public ReservationSystem(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Reservation reservation) {
        try {
            
            Reservation savedReservation = reservationService.saveReservation(reservation);

            
            if (savedReservation != null && savedReservation.getId() != null) {
                return ResponseEntity.ok(ResponseUtil.successMessage("Reservation Created"));
            } else {
                return ResponseEntity
                        .status(500)
                        .body(ResponseUtil.internalError("Failed to create reservation"));
            }
        } catch (CarNotFoundException e) {
            
            return ResponseEntity
                    .status(404) 
                    .body(ResponseUtil.notFound(ErrorMessages.VEHICLE_NOT_FOUND)); 
        } catch (Exception e) {
            
            return ResponseEntity
                    .status(500)  
                    .body(ResponseUtil.internalError(ResponseUtil.ErrorMessages.INTERNAL_SERVER_ERROR));
        }
    }



    @GetMapping
    public ResponseEntity<?> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        if (reservations.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body(ResponseUtil.notFound(ResponseUtil.ErrorMessages.VEHICLE_NOT_FOUND));
        }
        return ResponseEntity.ok(ResponseUtil.successWithData("Reservation Fetched Successfully !",reservations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(reservation -> ResponseEntity.ok(
                        ResponseUtil.successWithData(
                            String.format("Reservation Fetched Successfully for the id %d", id), 
                            reservation)))
                .orElse(ResponseEntity
                        .status(404)
                        .body(ResponseUtil.notFound(
                                ResponseUtil.ErrorMessages.notFoundWithId("Reservation", id))));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        boolean deleted = reservationService.deleteReservation(id);
        if (deleted) {
            return ResponseEntity.ok(ResponseUtil.successMessage("Reservation deleted successfully"));
        } else {
            return ResponseEntity
                    .status(404)
                    .body(ResponseUtil.notFound(
                            ResponseUtil.ErrorMessages.notFoundWithId("Reservation", id)));
        }
    }
    
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable Long id, @RequestBody Reservation updatedReservation) {
        Reservation reservation = reservationService.updateReservation(id, updatedReservation);
        
        if (reservation != null) {
            return ResponseEntity.ok(ResponseUtil.successMessage("Reservation Updated successfully"));
        } else {
            return ResponseEntity
                    .status(404)
                    .body(ResponseUtil.notFound(
                            ResponseUtil.ErrorMessages.notFoundWithId("Reservation", id)));
        }
    }

    @GetMapping("/user/{mobile}")
    public ResponseEntity<?> getReservationsByMobile(@PathVariable String mobile) {
        List<Reservation> reservations = reservationService.getReservationsByMobile(mobile);
        
        if (reservations.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body(ResponseUtil.notFound(
                            ResponseUtil.ErrorMessages.notFoundWithId("Reservation(s) for mobile", mobile)));
        }

        return ResponseEntity.ok(
                ResponseUtil.successWithData(
                        String.format("Reservations Fetched Successfully for mobile %s", mobile),
                        reservations));
    }

}
