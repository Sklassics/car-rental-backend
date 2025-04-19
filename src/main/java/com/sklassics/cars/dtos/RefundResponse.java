package com.sklassics.cars.dtos;
public class RefundResponse {
    private Long transactionId;
    private String refundStatus;
    private String refundDetails;

    // Constructor
    public RefundResponse(Long transactionId, String refundStatus, String refundDetails) {
        this.transactionId = transactionId;
        this.refundStatus = refundStatus;
        this.refundDetails = refundDetails;
    }

    // Getters and setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getRefundDetails() {
        return refundDetails;
    }

    public void setRefundDetails(String refundDetails) {
        this.refundDetails = refundDetails;
    }
}
