package com.sklassics.cars.services;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RazorpayService {

    @Value("${razorpay.keyId}")
    private String keyId;

    @Value("${razorpay.keySecret}")
    private String keySecret;

    public Map<String, Object> createOrder(double amount) throws RazorpayException {
        System.out.println("Starting to create Razorpay order...");

        try {
            System.out.println("Initializing Razorpay client with keyId: " + keyId);
            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            String shortReceipt = "rct_" + UUID.randomUUID().toString().replace("-", "").substring(0, 30);
            System.out.println("Generated short receipt ID: " + shortReceipt);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int)(amount * 100)); // Convert rupees to paise
            System.out.println("Order amount (in paise): " + (int)(amount * 100));

            orderRequest.put("currency", "INR");
            System.out.println("Currency set to: INR");

            orderRequest.put("receipt", shortReceipt);
            System.out.println("Receipt ID set to: " + shortReceipt);

            orderRequest.put("payment_capture", 1);
            System.out.println("Payment capture set to 1 (automatic capture)");

            System.out.println("Creating order with Razorpay client...");
            Order order = client.orders.create(orderRequest);

            System.out.println("Order created successfully with ID: " + order.get("id"));
            System.out.println("Order details: " + order.toString());

            Map<String, Object> response = new HashMap<>();
            response.put("id", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));
            response.put("status", order.get("status"));

            System.out.println("Returning response: " + response);

            return response;
        } catch (RazorpayException e) {
            System.err.println("Error creating Razorpay order: " + e.getMessage());
            e.printStackTrace();
            throw e;  // Re-throw the exception for further handling
        }
    }
}
