//package com.sklassics.cars.services;
//
//import com.razorpay.Order;
//import com.razorpay.RazorpayClient;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class RazorpayService {
//
//    @Value("${razorpay.keyId}")
//    private String keyId;
//
//    @Value("${razorpay.keySecret}")
//    private String keySecret;
//
//    public String createOrder(double amount) throws Exception {
//        RazorpayClient client = new RazorpayClient(keyId, keySecret);
//
//        JSONObject orderRequest = new JSONObject();
//        orderRequest.put("amount", (int)(amount * 100));
//        orderRequest.put("currency", "INR");
//        orderRequest.put("receipt", "receipt_" + System.currentTimeMillis());
//
//        Order order = client.orders.create(orderRequest); 
//        return order.toString();
//    }
//}


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

    public Map<String, Object> createOrder(String mobile, String email, double amount) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        String shortReceipt = "rct_" + UUID.randomUUID().toString().replace("-", "").substring(0, 30);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(amount)); // amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", shortReceipt);
        orderRequest.put("payment_capture", 1);

        JSONObject notes = new JSONObject();
        notes.put("mobile", mobile);
        notes.put("email", email);
        orderRequest.put("notes", notes); // ✅ include in the order

        Order order = client.orders.create(orderRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("id", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("currency", order.get("currency"));
        response.put("status", order.get("status"));

        return response;
    }
    
   

}

