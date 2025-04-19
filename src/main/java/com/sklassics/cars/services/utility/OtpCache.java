package com.sklassics.cars.services.utility;

public class OtpCache {
    private String email;
    private String otp;

    public OtpCache(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }
}
