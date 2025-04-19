package com.sklassics.cars.entites;


import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.sklassics.cars.services.utility.StringListConverter;

import jakarta.persistence.*;


@Entity
@Table(name = "cars")

public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;
    private String lastName;
    private String contactInfo;
    private String carName;
    private String carModel;
    private int year;
    private String vehicleType;
    private String fuelType;
    private String transmission;
    private double mileage;
    private int seatingCapacity;
    private String color;
    
    private String location;
    private Double cost;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> imageUrls;
    
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private boolean isReserved = false;
    
    private String reservedFrom;
    
    private String reservedTo;
    
    private String reservationPickUpTime;
    
    private String reservationDropTime;
    
   
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getContactInfo() {
		return contactInfo;
	}
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}
	public String getCarName() {
		return carName;
	}
	public void setCarName(String carName) {
		this.carName = carName;
	}
	public String getCarModel() {
		return carModel;
	}
	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public String getFuelType() {
		return fuelType;
	}
	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}
	public String getTransmission() {
		return transmission;
	}
	public void setTransmission(String transmission) {
		this.transmission = transmission;
	}
	public double getMileage() {
		return mileage;
	}
	public void setMileage(double mileage) {
		this.mileage = mileage;
	}
	public int getSeatingCapacity() {
		return seatingCapacity;
	}
	public void setSeatingCapacity(int seatingCapacity) {
		this.seatingCapacity = seatingCapacity;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public List<String> getImageUrls() {
		return imageUrls;
	}
	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public boolean isReserved() {
		return isReserved;
	}
	public void setReserved(boolean isReserved) {
		this.isReserved = isReserved;
	}
	public String getReservedFrom() {
		return reservedFrom;
	}
	public void setReservedFrom(String reservedFrom) {
		this.reservedFrom = reservedFrom;
	}
	public String getReservedTo() {
		return reservedTo;
	}
	public void setReservedTo(String reservedTo) {
		this.reservedTo = reservedTo;
	}
	public String getReservationPickUpTime() {
		return reservationPickUpTime;
	}
	public void setReservationPickUpTime(String reservationPickUpTime) {
		this.reservationPickUpTime = reservationPickUpTime;
	}
	public String getReservationDropTime() {
		return reservationDropTime;
	}
	public void setReservationDropTime(String reservationDropTime) {
		this.reservationDropTime = reservationDropTime;
	}
	
	
	
	
	
    
}
