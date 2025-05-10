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
    
    private Long actionId;
    
    private Double payableCarCost;
    
   private String mobileNumber;
    
   private Double dueAmount;
   
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	
	
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public Double getPayableCarCost() {
		return payableCarCost;
	}
	public void setPayableCarCost(Double payableCarCost) {
		this.payableCarCost = payableCarCost;
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

	
	
	
	public Long getActionId() {
		return actionId;
	}
	public void setActionId(Long actionId) {
		this.actionId = actionId;
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
	public Double getDueAmount() {
		return dueAmount;
	}
	public void setDueAmount(Double dueAmount) {
		this.dueAmount = dueAmount;
	}

    
    
}
