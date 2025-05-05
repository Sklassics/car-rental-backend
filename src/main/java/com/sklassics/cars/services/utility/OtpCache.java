//package com.sklassics.cars.services.utility;
//
//public class OtpCache {
//    private String email;
//    private String otp;
//
//    public OtpCache(String email, String otp) {
//        this.email = email;
//        this.otp = otp;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String getOtp() {
//        return otp;
//    }
//}


package com.sklassics.cars.services.utility;

public class OtpCache {
    private String email;
    private String mobile;  // Add mobile field
    private String otp;

    // Constructor with mobile
    public OtpCache(String mobile, String email, String otp) {
        this.mobile = mobile;  // Store the mobile number
        this.email = email;
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }

    public String getMobile() {
        return mobile;  // Getter for mobile
    }
}
