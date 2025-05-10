package com.sklassics.cars.dtos;

import java.time.LocalDate;

public class UserDTO {
    private String email;
    private String mobile;
    private String fullName;
    private String aadhaarNumber;
    private String aadhaarFilePath;
    private String licenseFilePath;
    private String location;
    private LocalDate submittedAt;
    
    private String selfieFilePath;
    
    
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
	public String getLicenseFilePath() {
		return licenseFilePath;
	}
	public void setLicenseFilePath(String licenseFilePath) {
		this.licenseFilePath = licenseFilePath;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public LocalDate getSubmittedAt() {
		return submittedAt;
	}
	public void setSubmittedAt(LocalDate submittedAt) {
		this.submittedAt = submittedAt;
	}
	public String getSelfieFilePath() {
		return selfieFilePath;
	}
	public void setSelfieFilePath(String selfieFilePath) {
		this.selfieFilePath = selfieFilePath;
	}

    // Getters and setters
    
    
}
