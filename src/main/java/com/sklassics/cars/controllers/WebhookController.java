package com.sklassics.cars.controllers;

import org.springframework.web.bind.annotation.*;

import com.sklassics.cars.entities.Transaction;
import com.sklassics.cars.repositories.TransactionRepository;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

	@Autowired
	private TransactionRepository transactionRepository;

    @Value("${razorpay.webhook-secret}") 
    private String webhookSecret;

    @PostMapping("/razorpay")
    public String handleRazorpayWebhook(@RequestHeader("X-Razorpay-Signature") String signature,
                                        @RequestBody String payload) {
    	System.out.println("🔁 Webhook triggered. Printing stack trace:");
        Arrays.stream(Thread.currentThread().getStackTrace()).forEach(System.out::println);
    	
    	
        System.out.println("🔹 Webhook received!");
        System.out.println("➡️ Signature: " + signature);
        System.out.println("➡️ Payload: " + payload);

        // Validate webhook signature
        if (!isValidSignature(payload, signature, webhookSecret)) {
            System.out.println("❌ Invalid signature. Possible fake request!");
            return "Invalid signature. Ignoring webhook.";
        }

        System.out.println("✅ Webhook Signature Verified Successfully!");

//        // Parse webhook payload
//        JSONObject event = new JSONObject(payload);
//
//        // Extract payment details
//        JSONObject payment = event.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
//        String razorpayOrderId = payment.getString("order_id");
//        String razorpayPaymentId = payment.getString("id");
//        String status = payment.getString("status");
//        String email = payment.optString("email");
//        String contact = payment.optString("contact");
//        Long amount = payment.optLong("amount", 0L);
//        Long fee = payment.optLong("fee", 0L);
//        Long tax = payment.optLong("tax", 0L);
//        String paymentMethod = payment.optString("method");
//        Long createdAt = payment.optLong("created_at", 0L);
//
//        // Extract card details
//        JSONObject card = payment.optJSONObject("card");
//        String cardId = card != null ? card.optString("id") : null;
//        String cardLast4 = card != null ? card.optString("last4") : null;
//        String cardNetwork = card != null ? card.optString("network") : null;
//        String cardType = card != null ? card.optString("type") : null;
//        String cardIssuer = card != null ? card.optString("issuer") : null;
//        boolean isCardInternational = card != null && card.optBoolean("international", false);
//        boolean isCardEMI = card != null && card.optBoolean("emi", false);
//
//        // Extract order details
//        JSONObject order = event.getJSONObject("payload").getJSONObject("order").getJSONObject("entity");
//        String orderId = order.getString("id");
//        Long orderAmount = order.getLong("amount");
//        Long amountPaid = order.getLong("amount_paid");
//        Long amountDue = order.getLong("amount_due");
//        String orderReceipt = order.optString("receipt");
//        String orderStatus = order.getString("status");
//
//        // Extract notes
//        JSONObject notes = order.optJSONObject("notes");
//        String notesEmail = notes != null ? notes.optString("email") : null;
//        String notesMobile = notes != null ? notes.optString("mobile") : null;
//
//        // Extract acquirer data
//        JSONObject acquirerData = payment.optJSONObject("acquirer_data");
//        String acquirerAuthCode = acquirerData != null ? acquirerData.optString("auth_code") : null;
//
//        // Save the transaction to the database
//        Transaction txn = new Transaction(
//        	    razorpayOrderId, 
//        	    razorpayPaymentId, 
//        	    status, 
//        	    email, 
//        	    contact, 
//        	    amount, 
//        	    fee, 
//        	    tax, 
//        	    paymentMethod, 
//        	    createdAt, 
//        	    cardId, 
//        	    cardLast4, 
//        	    cardNetwork, 
//        	    cardType, 
//        	    cardIssuer, 
//        	    isCardInternational, 
//        	    isCardEMI, 
//        	    orderId, 
//        	    orderAmount, 
//        	    amountPaid, 
//        	    amountDue, 
//        	    orderReceipt, 
//        	    orderStatus, 
//        	    notesEmail, 
//        	    notesMobile, 
//        	    acquirerAuthCode, 
//        	    payload
//        	);
//
//        	// Save transaction to the database
//        	transactionRepository.save(txn);
//        	System.out.println("💾 Transaction saved to database.");
//
//        	return "Webhook received and processed successfully";
        
        
     // Parse webhook payload
        JSONObject event = new JSONObject(payload);
        JSONObject payloadObject = event.getJSONObject("payload");

        // Extract payment details
        JSONObject payment = payloadObject.getJSONObject("payment").getJSONObject("entity");
        String razorpayOrderId = payment.getString("order_id"); // Get order_id
        String razorpayPaymentId = payment.getString("id");
        String status = payment.getString("status");
        String email = payment.optString("email");
        String contact = payment.optString("contact");
        Long amount = payment.optLong("amount", 0L);
        Long fee = payment.optLong("fee", 0L);
        Long tax = payment.optLong("tax", 0L);
        String paymentMethod = payment.optString("method");
        Long createdAt = payment.optLong("created_at", 0L);

        // Extract card details
        JSONObject card = payment.optJSONObject("card");
        String cardId = card != null ? card.optString("id") : null;
        String cardLast4 = card != null ? card.optString("last4") : null;
        String cardNetwork = card != null ? card.optString("network") : null;
        String cardType = card != null ? card.optString("type") : null;
        String cardIssuer = card != null ? card.optString("issuer") : null;
        boolean isCardInternational = card != null && card.optBoolean("international", false);
        boolean isCardEMI = card != null && card.optBoolean("emi", false);

        // Order details might be missing in payment.authorized, handle gracefully
        String orderId = null, orderReceipt = null, orderStatus = null;
        Long orderAmount = null, amountPaid = null, amountDue = null;
        String notesEmail = null, notesMobile = null;
        if (payloadObject.has("order")) {
            // Handle order details if present
            JSONObject order = payloadObject.getJSONObject("order").getJSONObject("entity");
            orderId = order.getString("id");
            orderAmount = order.getLong("amount");
            amountPaid = order.getLong("amount_paid");
            amountDue = order.getLong("amount_due");
            orderReceipt = order.optString("receipt");
            orderStatus = order.getString("status");
        }

        // Extract acquirer data
        JSONObject acquirerData = payment.optJSONObject("acquirer_data");
        String acquirerAuthCode = acquirerData != null ? acquirerData.optString("auth_code") : null;

        // Save the transaction to the database
        Transaction txn = new Transaction(
                razorpayOrderId, 
                razorpayPaymentId, 
                status, 
                email, 
                contact, 
                amount, 
                fee, 
                tax, 
                paymentMethod, 
                createdAt, 
                cardId, 
                cardLast4, 
                cardNetwork, 
                cardType, 
                cardIssuer, 
                isCardInternational, 
                isCardEMI, 
                orderId, // Only orderId from the payment details
                orderAmount, 
                amountPaid, 
                amountDue, 
                orderReceipt, 
                orderStatus, 
                acquirerAuthCode, 
                payload
        );

        // Save transaction to the database
        transactionRepository.save(txn);
        System.out.println("💾 Transaction saved to database.");

        return "Webhook received and processed successfully";

    }


    // ✅ Validate Razorpay Webhook Signature
    private boolean isValidSignature(String payload, String signature, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
            String expectedSignature = Hex.encodeHexString(mac.doFinal(payload.getBytes()));

            return expectedSignature.equals(signature);
        } catch (Exception e) {
            System.out.println("❌ Error in Signature Validation: " + e.getMessage());
            return false;
        }
    }
}