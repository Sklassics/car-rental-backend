
package com.sklassics.cars.services;

import com.razorpay.*;
import com.sklassics.cars.dtos.PaymentRequest;
import com.sklassics.cars.dtos.PaymentResponse;
import com.sklassics.cars.dtos.RefundResponse;
import com.sklassics.cars.dtos.TransactionResponse;
import com.sklassics.cars.entites.CarEntity;
import com.sklassics.cars.entites.Transaction;
import com.sklassics.cars.repositories.CarRepository;
import com.sklassics.cars.repositories.TransactionRepository;
import com.sklassics.cars.exceptions.CustomExceptions.CarNotFoundException;

import jakarta.annotation.PostConstruct;

import java.time.*;
import java.time.temporal.ChronoUnit;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private RazorpayClient razorpayClient;

    @Value("${razorpay.keyId}")
    private String razorpayKey;

    @Value("${razorpay.keySecret}")
    private String razorpaySecret;

    @PostConstruct
    public void init() throws RazorpayException {
        this.razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);
    }
    
    
    public PaymentResponse calculateCost(PaymentRequest paymentRequest) {

        System.out.println("Received PaymentRequest: " + paymentRequest);

        long carId = paymentRequest.getCarId();
        System.out.println("Extracted carId: " + carId);

        CarEntity car = carRepository.findById(carId)
            .orElseThrow(() -> {
                System.out.println("Car not found with ID: " + carId);
                return new CarNotFoundException("Car not found");
            });

        System.out.println("Fetched CarEntity: " + car);

        Double costPerDay = car.getCost();
        System.out.println("Cost per day: " + costPerDay);

        LocalDate fromDate = paymentRequest.getFromDate();
        LocalDate toDate = paymentRequest.getToDate();
        System.out.println("From Date: " + fromDate + ", To Date: " + toDate);

        LocalTime pickupTime = paymentRequest.getPickupTime();
        LocalTime returnTime = paymentRequest.getReturnTime();
        System.out.println("Pickup Time: " + pickupTime + ", Return Time: " + returnTime);

        long duration = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
        System.out.println("Calculated duration: " + duration);

        // Handle same day rentals
        if (duration == 0) {
            duration = 1;
            System.out.println("Same-day rental. Setting duration to 1 day.");
        }

        System.out.println("Rental duration in days: " + duration);

        double carPrice = roundToTwoDecimals(duration * costPerDay); 
        double discount = 0.0;

        if (duration >= 10) {
            discount = roundToTwoDecimals(carPrice * 0.20);
            System.out.println("Applied 20% discount: -" + discount);
        } else if (duration >= 5) {
            discount = roundToTwoDecimals(carPrice * 0.15);
            System.out.println("Applied 15% discount: -" + discount);
        }

        double insuranceCost = 0.0;
        double cleaningFee = 199.0;

        double extraCost = 0.0;
        if (pickupTime != null && returnTime != null) {
            long extraHours = returnTime.getHour() - pickupTime.getHour();
            double hourlyRate = costPerDay / 24.0;
            extraCost = roundToTwoDecimals(extraHours * hourlyRate);
            System.out.println("Extra hours: " + extraHours + ", Extra cost: " + extraCost);
        }

        double totalCost = roundToTwoDecimals(carPrice - discount + insuranceCost + cleaningFee + extraCost);
        System.out.println("Total rental cost after all additions: " + totalCost);

        PaymentResponse response = new PaymentResponse();
        response.setRentalCost(totalCost);
        response.setDuration(duration);
        response.setDurationText(duration == 1 ? "1 day" : duration + " days");

        response.setCarPrice(carPrice);
        response.setDiscount(discount);
        response.setInsuranceCost(insuranceCost);
        response.setCleaningFee(cleaningFee);

        return response;
    }


  private double roundToTwoDecimals(double value) {
      return Math.round(value * 100.0) / 100.0;
  }

  public TransactionResponse getByRazorpayPaymentId(String razorpayOrderId) {
      Transaction transaction = transactionRepository.findByRazorpayPaymentId(razorpayOrderId).orElse(null);

      if (transaction == null) {
          return null;
      }

      return new TransactionResponse(transaction);
  }


    public RefundResponse refundPayment(String razorpayOrderId) {
        Transaction transaction = transactionRepository.findByRazorpayPaymentId(razorpayOrderId).orElse(null);

        if (transaction != null && transaction.getRazorpayPaymentId() != null) {
            try {
                JSONObject refundRequest = new JSONObject();
                refundRequest.put("payment_id", transaction.getRazorpayPaymentId());

                Refund refund = razorpayClient.payments.refund(refundRequest);

                transaction.setRefundStatus("REFUNDED");
                transaction.setRefundId(refund.get("id"));
                transactionRepository.save(transaction);

                return new RefundResponse(
                    transaction.getId(),
                    "Refund Successful",
                    "Refund ID: " + refund.get("id")
                );
            } catch (RazorpayException e) {
                e.printStackTrace();
                return new RefundResponse(
                    transaction.getId(),
                    "Refund Failed",
                    "Error: " + e.getMessage()
                );
            }
        }

        return null;
    }
    
    public String getRefundStatus(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);

        if (transaction != null && transaction.getRefundId() != null) {
            try {
                Refund refund = razorpayClient.refunds.fetch(transaction.getRefundId());
                return (String) refund.get("status"); // e.g., "processed", "pending", "failed"
            } catch (RazorpayException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


   
}





//package com.sklassics.cars.services;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.razorpay.RazorpayException;
//import com.razorpay.Refund;
//import com.sklassics.cars.dtos.PaymentRequest;
//import com.sklassics.cars.dtos.PaymentResponse;
//import com.sklassics.cars.dtos.RefundResponse;
//import com.sklassics.cars.dtos.TransactionResponse;
//import com.sklassics.cars.entites.CarEntity;
//import com.sklassics.cars.entites.Transaction;
//import com.sklassics.cars.exceptions.CustomExceptions.CarNotFoundException;
//import com.sklassics.cars.repositories.CarRepository;
//import com.sklassics.cars.repositories.TransactionRepository;
//
//import net.minidev.json.JSONObject;
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import com.razorpay.RazorpayClient;
//@Service
//public class PaymentService {
//
//    @Autowired
//    private CarRepository carRepository;
//    
//    @Autowired
//    private TransactionRepository transactionRepository;
//
//    public PaymentResponse calculateCost(PaymentRequest paymentRequest) {
//
//        System.out.println("Received PaymentRequest: " + paymentRequest);
//
//        long carId = paymentRequest.getCarId();
//        System.out.println("Extracted carId: " + carId);
//
//        CarEntity car = carRepository.findById(carId)
//            .orElseThrow(() -> {
//                System.out.println("Car not found with ID: " + carId);
//                return new CarNotFoundException("Car not found");
//            });
//
//        System.out.println("Fetched CarEntity: " + car);
//
//        Double costPerDay = car.getCost();
//        System.out.println("Cost per day: " + costPerDay);
//
//        LocalDate fromDate = paymentRequest.getFromDate();
//        LocalDate toDate = paymentRequest.getToDate();
//
//        System.out.println("From Date: " + fromDate + ", To Date: " + toDate);
//
//        LocalTime pickupTime = paymentRequest.getPickupTime();
//        LocalTime returnTime = paymentRequest.getReturnTime();
//        System.out.println("Pickup Time: " + pickupTime + ", Return Time: " + returnTime);
//
//        long duration = Duration.between(fromDate.atStartOfDay(), toDate.atStartOfDay()).toDays();
//        System.out.println("Rental duration in days: " + duration);
//
//        double carPrice = roundToTwoDecimals(duration * costPerDay); 
//        double discount = 0.0;
//
//        if (duration >= 10) {
//            discount = roundToTwoDecimals(carPrice * 0.20);
//            System.out.println("Applied 20% discount: -" + discount);
//        } else if (duration >= 5 || duration >= 3) {
//            discount = roundToTwoDecimals(carPrice * 0.15);
//            System.out.println("Applied 15% discount: -" + discount);
//        }
//
//        double insuranceCost = 0.0;
//        double cleaningFee = 199.0;
//
//        double extraCost = 0.0;
//        if (pickupTime != null && returnTime != null) {
//            long extraHours = returnTime.getHour() - pickupTime.getHour();
//            double hourlyRate = costPerDay / 24.0;
//            extraCost = roundToTwoDecimals(extraHours * hourlyRate);
//            System.out.println("Extra hours: " + extraHours + ", Extra cost: " + extraCost);
//        }
//
//        double totalCost = roundToTwoDecimals(carPrice - discount + insuranceCost + cleaningFee + extraCost);
//        System.out.println("Total rental cost after all additions: " + totalCost);
//
//        PaymentResponse response = new PaymentResponse();
//        response.setRentalCost(totalCost);
//        response.setDuration(duration);
//        response.setDurationText(duration == 1 ? "1 day" : duration + " days");
//
//        response.setCarPrice(carPrice);
//        response.setDiscount(discount);
//        response.setInsuranceCost(insuranceCost);
//        response.setCleaningFee(cleaningFee);
//
//        return response;
//    }
//
//    private double roundToTwoDecimals(double value) {
//        return Math.round(value * 100.0) / 100.0;
//    }
//
//    public TransactionResponse getByRazorpayPaymentId(String razorpayOrderId) {
//        Transaction transaction = transactionRepository.findByRazorpayPaymentId(razorpayOrderId).orElse(null);
//
//        if (transaction == null) {
//            return null;
//        }
//
//        return new TransactionResponse(transaction);
//    }
//
//
//    public RefundResponse refundPayment(Long id) {
//        Transaction transaction = transactionRepository.findById(id).orElse(null);
//
//        if (transaction != null && transaction.getRazorpayPaymentId() != null) {
//            try {
//                // Refund through Razorpay
//                JSONObject refundRequest = new JSONObject();
//                refundRequest.put("payment_id", transaction.getRazorpayPaymentId()); 
//
//                Refund refund = razorpayClient.payments.refund(refundRequest);
//
//                // Update refund status
//                transaction.setRefundStatus("REFUNDED");
//                transactionRepository.save(transaction);
//
//                return new RefundResponse(
//                    transaction.getId(),
//                    "Refund Successful",
//                    "Refund ID: " + refund.get("id")
//                );
//            } catch (RazorpayException e) {
//                e.printStackTrace();
//                return new RefundResponse(
//                    transaction.getId(),
//                    "Refund Failed",
//                    "Error: " + e.getMessage()
//                );
//            }
//        }
//
//        return null;
//    }
//}
//    
//  


