package com.sklassics.cars.dtos;

import org.springframework.web.multipart.MultipartFile;

public class UserRequest {
    private String name;
    private String mobile;
    private String location;
    private String aadharOtp;

    // Use MultipartFile for file uploads
    private MultipartFile aadharFile;
    private MultipartFile drivingLicenseFile;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getAadharOtp() {
		return aadharOtp;
	}
	public void setAadharOtp(String aadharOtp) {
		this.aadharOtp = aadharOtp;
	}
	public MultipartFile getAadharFile() {
		return aadharFile;
	}
	public void setAadharFile(MultipartFile aadharFile) {
		this.aadharFile = aadharFile;
	}
	public MultipartFile getDrivingLicenseFile() {
		return drivingLicenseFile;
	}
	public void setDrivingLicenseFile(MultipartFile drivingLicenseFile) {
		this.drivingLicenseFile = drivingLicenseFile;
	}


    
}
