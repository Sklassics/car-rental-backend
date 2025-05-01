package com.sklassics.cars.services;

import com.sklassics.cars.dtos.BookedRange;
import com.sklassics.cars.entities.Booking;
import com.sklassics.cars.entities.Reservation;
import com.sklassics.cars.repositories.BookingRepository;
import com.sklassics.cars.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarAvailabilityService {

    @Autowired
    private ReservationRepository reservationRepo;

    @Autowired
    private BookingRepository bookingRepo;

    public List<BookedRange> getUnavailableDates(Long carId) {
        List<BookedRange> blockedDates = new ArrayList<>();

        List<Reservation> reservations = reservationRepo.findActiveReservationsByCarId(carId);
        for (Reservation r : reservations) {
            blockedDates.add(new BookedRange(r.getFromDate(), r.getToDate()));
        }

        List<Booking> bookings = bookingRepo.findPaidBookingsByCarId(carId);
        for (Booking b : bookings) {
            blockedDates.add(new BookedRange(b.getFromDate(), b.getToDate()));
        }

        return blockedDates;
    }
}
