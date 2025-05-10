package com.sklassics.cars.services;

import com.sklassics.cars.entities.Booking;
import com.sklassics.cars.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BookingAlertService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private JavaMailSender mailSender;

    private final String adminEmail = "admin@example.com"; 

    @Scheduled(fixedRate = 15 * 60 * 1000) // every 15 minutes
    public void checkExact3HourDropAlerts() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAll();

        for (Booking booking : bookings) {
            if (booking.getToDate() != null && booking.getDropTime() != null) {
                try {
                    LocalDateTime dropDateTime = LocalDateTime.of(
                            booking.getToDate(),
                            LocalTime.parse(booking.getDropTime(), DateTimeFormatter.ofPattern("HH:mm"))
                    );

                    LocalDateTime expectedAlertTime = dropDateTime.minusHours(3);
                    long minutesDiff = Math.abs(java.time.Duration.between(expectedAlertTime, now).toMinutes());

                    if (minutesDiff <= 5) { // Allow ±5 minutes margin
                        sendDropAlert(booking);
                      
                    }

                } catch (Exception e) {
                    System.out.println("Error processing booking ID: " + booking.getId() + " - " + e.getMessage());
                }
            }
        }
    }

    private void sendDropAlert(Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("Upcoming Drop Alert - Booking ID: " + booking.getId());
        message.setText("Booking ID " + booking.getId() + " will end at " +
                booking.getToDate() + " " + booking.getDropTime() + ".\n" +
                "Car ID: " + booking.getCarId() + "\nUser ID: " + booking.getUserId());
        message.setFrom("noreply@sklassics.com");

        try {
            mailSender.send(message);
            System.out.println("Alert sent for booking ID: " + booking.getId());
        } catch (Exception e) {
            System.out.println("Failed to send alert email: " + e.getMessage());
        }
    }
}
