
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
        
        public void sendNewsletterSubscriptionEmail(String toEmail) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Thank you for subscribing to our newsletter!");
            message.setText("Hi there,\n\nThank you for subscribing! You'll now receive the latest updates.\n\n- Team");

            javaMailSender.send(message);
        }
        
        public void sendEmail(String to, String subject, String text) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(text);
                message.setFrom("your@email.com"); // Replace with a valid sender
                javaMailSender.send(message);
                System.out.println("Email sent to: " + to);
            } catch (Exception e) {
                System.out.println("Failed to send email: " + e.getMessage());
            }
        }

        
    }




