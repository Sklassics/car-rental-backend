package com.sklassics.cars.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class CarRequestDTO {
	private String firstName;
	private String lastName;
	private String mobile;
	private String email;
	private String carName;
	private String carModel;
	private int year;
	private String vehicleType;
	private String fuelType;
	private String transmission;
	private double mileage;
	private int seatingCapacity;
	private String color;
	private List<String> imageUrls;
	private LocalDateTime createdAt;
	private Long id;
	private String location;
	private boolean available;

	private String chassisNumber;

	private String engineNumber;

	private String vehicleRegistrationNumber;

	private Double cost;

	public CarRequestDTO() {
	};

	// Constructor matching the one you're trying to call
	public CarRequestDTO(Long id, String carName, String carModel, int year, String vehicleType, String fuelType,
			String transmission, double mileage, int seatingCapacity, String color, List<String> imageUrls, Double cost,
			boolean available, String location,String chassisNumber,String engineNumber,String vehicleRegistrationNumber) {
		this.id = id;
		this.carName = carName;
		this.carModel = carModel;
		this.year = year;
		this.vehicleType = vehicleType;
		this.fuelType = fuelType;
		this.transmission = transmission;
		this.mileage = mileage;
		this.seatingCapacity = seatingCapacity;
		this.color = color;
		this.imageUrls = imageUrls;
		this.cost = cost;
		this.available = available;
		this.location = location;
		this.chassisNumber=chassisNumber;
		this.engineNumber=engineNumber;
		this.vehicleRegistrationNumber=vehicleRegistrationNumber;
		
	}
	
	public CarRequestDTO(Long id, String carName, String carModel, int year, String vehicleType, String fuelType,
			String transmission, double mileage, int seatingCapacity, String color, List<String> imageUrls, Double cost,
			boolean available, String location) {
		this.id = id;
		this.carName = carName;
		this.carModel = carModel;
		this.year = year;
		this.vehicleType = vehicleType;
		this.fuelType = fuelType;
		this.transmission = transmission;
		this.mileage = mileage;
		this.seatingCapacity = seatingCapacity;
		this.color = color;
		this.imageUrls = imageUrls;
		this.cost = cost;
		this.available = available;
		this.location = location;
		
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getChassisNumber() {
		return chassisNumber;
	}

	public void setChassisNumber(String chasisNumber) {
		this.chassisNumber = chasisNumber;
	}

	public String getEngineNumber() {
		return engineNumber;
	}

	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
	}

	public String getVehicleRegistrationNumber() {
		return vehicleRegistrationNumber;
	}

	public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
		this.vehicleRegistrationNumber = vehicleRegistrationNumber;
	}

	
}
