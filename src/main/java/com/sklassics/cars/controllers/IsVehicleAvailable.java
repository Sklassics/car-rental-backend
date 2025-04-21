package com.sklassics.cars.controllers;


import com.sklassics.cars.dtos.BookedRange;
import com.sklassics.cars.services.CarAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class IsVehicleAvailable {

    @Autowired
    private CarAvailabilityService availabilityService;

    @GetMapping("/{carId}")
    public List<BookedRange> getBookedDates(@PathVariable Long carId) {
        return availabilityService.getUnavailableDates(carId);
    }
}
