package com.sklassics.cars.entites;


import jakarta.persistence.*;

@Entity
public class CarPayment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String mobile;
    private String email;
    private boolean proceedToPay;
    private boolean paymentGateway;
    private boolean termsAccepted;
    private String panNumber;
    private String aadhaarNumber;
    private String panImageUrl;
    private String aadhaarImageUrl;
    private String drivingLicenseNumber;
    private String drivingLicenseImageUrl;
    private String address;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isProceedToPay() { return proceedToPay; }
    public void setProceedToPay(boolean proceedToPay) { this.proceedToPay = proceedToPay; }

    public boolean isPaymentGateway() { return paymentGateway; }
    public void setPaymentGateway(boolean paymentGateway) { this.paymentGateway = paymentGateway; }

    public boolean isTermsAccepted() { return termsAccepted; }
    public void setTermsAccepted(boolean termsAccepted) { this.termsAccepted = termsAccepted; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }

    public String getPanImageUrl() { return panImageUrl; }
    public void setPanImageUrl(String panImageUrl) { this.panImageUrl = panImageUrl; }

    public String getAadhaarImageUrl() { return aadhaarImageUrl; }
    public void setAadhaarImageUrl(String aadhaarImageUrl) { this.aadhaarImageUrl = aadhaarImageUrl; }

    public String getDrivingLicenseNumber() { return drivingLicenseNumber; }
    public void setDrivingLicenseNumber(String drivingLicenseNumber) { this.drivingLicenseNumber = drivingLicenseNumber; }

    public String getDrivingLicenseImageUrl() { return drivingLicenseImageUrl; }
    public void setDrivingLicenseImageUrl(String drivingLicenseImageUrl) { this.drivingLicenseImageUrl = drivingLicenseImageUrl; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
