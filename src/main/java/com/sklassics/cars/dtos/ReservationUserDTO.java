package com.sklassics.cars.dtos;

import java.time.LocalDate;

import com.sklassics.cars.entities.Reservation;
import com.sklassics.cars.entities.User;

public class ReservationUserDTO {

    // Reservation fields
    private Long reservationId;
    private Long carId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String pickupTime;
    private String dropTime;
    private String status;
    private Double totalAmount;
    private Double dueAmount;

    // User fields
    private Long userId;
    private String fullName;
    private String email;
    private String mobile;

    // Constructor
    public ReservationUserDTO() {}

    public ReservationUserDTO(Reservation reservation, User user) {
        this.reservationId = reservation.getId();
        this.carId = reservation.getCarId();
        this.fromDate = reservation.getFromDate();
        this.toDate = reservation.getToDate();
        this.pickupTime = reservation.getPickupTime();
        this.dropTime = reservation.getDropTime();
        this.status = reservation.getStatus();
        this.totalAmount = reservation.getTotalAmount();
        this.dueAmount = reservation.getDueAmount();

        this.userId = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.mobile = user.getMobile();
    }

	public Long getReservationId() {
		return reservationId;
	}

	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
	}

	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public String getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(String pickupTime) {
		this.pickupTime = pickupTime;
	}

	public String getDropTime() {
		return dropTime;
	}

	public void setDropTime(String dropTime) {
		this.dropTime = dropTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Double getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(Double dueAmount) {
		this.dueAmount = dueAmount;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

    
}
