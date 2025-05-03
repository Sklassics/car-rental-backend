package com.sklassics.cars.services;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class WhatsAppOtpService {

    private static final String ACCOUNT_SID = "ACb0a1795ab3c78f1c705201316114b892";
    private static final String AUTH_TOKEN = "c53594e3e87ecde1f43487a2ee575bd0";
    private static final String FROM_WHATSAPP_NUMBER = "whatsapp:+14155238886"; // Twilio sandbox number

    public WhatsAppOtpService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendOtp(String mobile, String otp) {
        String to = "whatsapp:" + mobile; 
        String message = "Your OTP code is: " + otp;
        
        try {
            Message.creator(new PhoneNumber(to), new PhoneNumber(FROM_WHATSAPP_NUMBER), message).create();
            System.out.println("OTP sent successfully to: " + mobile);
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
        }
    }

}
