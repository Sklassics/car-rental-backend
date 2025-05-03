
package com.sklassics.cars.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
    public class EmailService {

        private final JavaMailSender javaMailSender;

        public EmailService(JavaMailSender javaMailSender) {
            this.javaMailSender = javaMailSender;
        }

        public void sendOtpEmail(String toEmail, String otp) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your OTP for Registration");
            message.setText("Your OTP for mobile verification is: " + otp);
            
            try {
                javaMailSender.send(message);
            } catch (Exception e) {
                // Log the exception to help with debugging
                e.printStackTrace();
                throw new RuntimeException("Failed to send OTP email. Please try again.", e);
            }
        }
        

        
    }




