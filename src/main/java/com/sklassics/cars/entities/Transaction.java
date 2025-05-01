package com.sklassics.cars.entities;


import jakarta.persistence.*;


@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Payment Fields
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String status;
    private String email;
    private String contact;
    private Long amount; // in paise
    private Long fee;
    private Long tax;
    private String paymentMethod;
    private Long createdAt;

    // Card Details
    private String cardId;
    private String cardLast4;
    private String cardNetwork;
    private String cardType;
    private String cardIssuer;
    private boolean isCardInternational;
    private boolean isCardEMI;

    // Order Details
    private String orderId;
    private Long orderAmount;
    private Long amountPaid;
    private Long amountDue;
    private String orderReceipt;
    private String orderStatus;


    // Acquirer Data
    private String acquirerAuthCode;

    // Full Payload (for debugging)
    @Column(columnDefinition = "TEXT")
    private String fullPayload;
    
    private String refundStatus;
    
    private String refundId;
    
    public Transaction() {};
    
    
    public Transaction(String razorpayOrderId, String razorpayPaymentId, String status, String email, String contact, Long amount, Long fee, Long tax, String paymentMethod, Long createdAt, String cardId, String cardLast4, String cardNetwork, String cardType, String cardIssuer, boolean isCardInternational, boolean isCardEMI, String orderId, Long orderAmount, Long amountPaid, Long amountDue, String orderReceipt, String orderStatus, String acquirerAuthCode, String fullPayload) {
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.status = status;
        this.email = email;
        this.contact = contact;
        this.amount = amount;
        this.fee = fee;
        this.tax = tax;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.cardId = cardId;
        this.cardLast4 = cardLast4;
        this.cardNetwork = cardNetwork;
        this.cardType = cardType;
        this.cardIssuer = cardIssuer;
        this.isCardInternational = isCardInternational;
        this.isCardEMI = isCardEMI;
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.amountPaid = amountPaid;
        this.amountDue = amountDue;
        this.orderReceipt = orderReceipt;
        this.orderStatus = orderStatus;

        this.acquirerAuthCode = acquirerAuthCode;
        this.fullPayload = fullPayload;
        
    }
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public String getRazorpayPaymentId() {
		return razorpayPaymentId;
	}

	public void setRazorpayPaymentId(String razorpayPaymentId) {
		this.razorpayPaymentId = razorpayPaymentId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getFee() {
		return fee;
	}

	public void setFee(Long fee) {
		this.fee = fee;
	}

	public Long getTax() {
		return tax;
	}

	public void setTax(Long tax) {
		this.tax = tax;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getCardLast4() {
		return cardLast4;
	}

	public void setCardLast4(String cardLast4) {
		this.cardLast4 = cardLast4;
	}

	public String getCardNetwork() {
		return cardNetwork;
	}

	public void setCardNetwork(String cardNetwork) {
		this.cardNetwork = cardNetwork;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCardIssuer() {
		return cardIssuer;
	}

	public void setCardIssuer(String cardIssuer) {
		this.cardIssuer = cardIssuer;
	}

	public boolean isCardInternational() {
		return isCardInternational;
	}

	public void setCardInternational(boolean isCardInternational) {
		this.isCardInternational = isCardInternational;
	}

	public boolean isCardEMI() {
		return isCardEMI;
	}

	public void setCardEMI(boolean isCardEMI) {
		this.isCardEMI = isCardEMI;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Long getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(Long orderAmount) {
		this.orderAmount = orderAmount;
	}

	public Long getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Long amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Long getAmountDue() {
		return amountDue;
	}

	public void setAmountDue(Long amountDue) {
		this.amountDue = amountDue;
	}

	public String getOrderReceipt() {
		return orderReceipt;
	}

	public void setOrderReceipt(String orderReceipt) {
		this.orderReceipt = orderReceipt;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getAcquirerAuthCode() {
		return acquirerAuthCode;
	}

	public void setAcquirerAuthCode(String acquirerAuthCode) {
		this.acquirerAuthCode = acquirerAuthCode;
	}

	public String getFullPayload() {
		return fullPayload;
	}

	public void setFullPayload(String fullPayload) {
		this.fullPayload = fullPayload;
	}


	public String getRefundStatus() {
		return refundStatus;
	}


	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}


	public String getRefundId() {
		return refundId;
	}


	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}
    
    

}
