package com.sklassics.cars.entites;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;
	private String mobile;
	private String fullName;
	private String aadhaarNumber;
	private String aadhaarFilePath;
	private String licenseFilePath;
	private String Location;

	private String isAdminVerifiedDocuments;

	private LocalDate submittedAt;

	private LocalDate updatedAt;

	private LocalDate accountCreatedAt;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Reservation> reservations;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getLicenseFilePath() {
		return licenseFilePath;
	}

	public void setLicenseFilePath(String licenseFilePath) {
		this.licenseFilePath = licenseFilePath;
	}

	public String getAadhaarNumber() {
		return aadhaarNumber;
	}

	public void setAadhaarNumber(String aadhaarNumber) {
		this.aadhaarNumber = aadhaarNumber;
	}

	public String getAadhaarFilePath() {
		return aadhaarFilePath;
	}

	public void setAadhaarFilePath(String aadhaarFilePath) {
		this.aadhaarFilePath = aadhaarFilePath;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	public String getIsAdminVerifiedDocuments() {
		return isAdminVerifiedDocuments;
	}

	public void setIsAdminVerifiedDocuments(String isAdminVerifiedDocuments) {
		this.isAdminVerifiedDocuments = isAdminVerifiedDocuments;
	}

	public LocalDate getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(LocalDate submittedAt) {
		this.submittedAt = submittedAt;
	}

	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDate updatedAt) {
		this.updatedAt = updatedAt;
	}

	public LocalDate getAccountCreatedAt() {
		return accountCreatedAt;
	}

	public void setAccountCreatedAt(LocalDate accountCreatedAt) {
		this.accountCreatedAt = accountCreatedAt;
	}

	public List<Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}

	
//	public List<Booking> getBookings() {
//		return bookings;
//	}
//
//	public void setBookings(List<Booking> bookings) {
//		this.bookings = bookings;
//	}
	
	
}
