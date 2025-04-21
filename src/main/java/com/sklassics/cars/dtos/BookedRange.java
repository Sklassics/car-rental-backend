package com.sklassics.cars.dtos;

import java.time.LocalDate;

public class BookedRange {
    private LocalDate fromDate;
    private LocalDate toDate;

    public BookedRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }
}
