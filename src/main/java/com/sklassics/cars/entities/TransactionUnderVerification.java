package com.sklassics.cars.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "transaction_under_verification")
public class TransactionUnderVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;

    private String transactionId;
    private Double amount;
    private String transactionScreenshotUrl;
    private boolean isAdminVerified;
    
    private String action;
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	
	
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getTransactionScreenshotUrl() {
		return transactionScreenshotUrl;
	}
	public void setTransactionScreenshotUrl(String transactionScreenshotUrl) {
		this.transactionScreenshotUrl = transactionScreenshotUrl;
	}
	public boolean isAdminVerified() {
		return isAdminVerified;
	}
	public void setAdminVerified(boolean isAdminVerified) {
		this.isAdminVerified = isAdminVerified;
	}

    
    
}
