package com.sklassics.cars.services.utility;

import java.time.LocalDate;
import java.time.LocalTime;

public class DateOverlap{

		public boolean isDateTimeOverlap(
		        LocalDate newFromDate, LocalDate newToDate, LocalTime newPickupTime, LocalTime newDropTime,
		        LocalDate existingFromDate, LocalDate existingToDate, LocalTime existingPickupTime, LocalTime existingDropTime) {
		
		    boolean datesOverlap = !(newToDate.isBefore(existingFromDate) || newFromDate.isAfter(existingToDate));
		
		    if (!datesOverlap) return false;
		
		    if (newFromDate.equals(existingFromDate) && newToDate.equals(existingToDate)) {
		        return !(newDropTime.isBefore(existingPickupTime) || newPickupTime.isAfter(existingDropTime));
		    }
		
		    return true;
		}
}