package com.sklassics.cars.dtos;

public class PaymentResponse {

    private double rentalCost;
    private long duration;
    private String durationText;
    
    private double carPrice;           
    private double discount;          
    private double insuranceCost;     
    private double cleaningFee;

    public double getRentalCost() {
        return rentalCost;
    }

    public void setRentalCost(double rentalCost) {
        this.rentalCost = rentalCost;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

	public double getCarPrice() {
		return carPrice;
	}

	public void setCarPrice(double carPrice) {
		this.carPrice = carPrice;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getInsuranceCost() {
		return insuranceCost;
	}

	public void setInsuranceCost(double insuranceCost) {
		this.insuranceCost = insuranceCost;
	}

	public double getCleaningFee() {
		return cleaningFee;
	}

	public void setCleaningFee(double cleaningFee) {
		this.cleaningFee = cleaningFee;
	}
    
    
}
