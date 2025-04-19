package com.sklassics.cars.dtos;



import com.sklassics.cars.entites.Transaction;

public class TransactionResponse {

    private Long id;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String status;
    private String email;
    private String contact;
    private Long amount;
    private Long fee;
    private Long tax;
    private String paymentMethod;
    private Long createdAt;

    private String cardId;
    private String cardLast4;
    private String cardNetwork;
    private String cardType;
    private String cardIssuer;
    private boolean isCardInternational;
    private boolean isCardEMI;

    private String orderId;
    private Long orderAmount;
    private Long amountPaid;
    private Long amountDue;
    private String orderReceipt;
    private String orderStatus;

    private String notesEmail;
    private String notesMobile;

    private String acquirerAuthCode;
    private String refundStatus;

    private String fullPayload;

    public TransactionResponse(Transaction t) {
        this.id = t.getId();
        this.razorpayOrderId = t.getRazorpayOrderId();
        this.razorpayPaymentId = t.getRazorpayPaymentId();
        this.status = t.getStatus();
        this.email = t.getEmail();
        this.contact = t.getContact();
        this.amount = t.getAmount();
        this.fee = t.getFee();
        this.tax = t.getTax();
        this.paymentMethod = t.getPaymentMethod();
        this.createdAt = t.getCreatedAt();

        this.cardId = t.getCardId();
        this.cardLast4 = t.getCardLast4();
        this.cardNetwork = t.getCardNetwork();
        this.cardType = t.getCardType();
        this.cardIssuer = t.getCardIssuer();
        this.isCardInternational = t.isCardInternational();
        this.isCardEMI = t.isCardEMI();

        this.orderId = t.getOrderId();
        this.orderAmount = t.getOrderAmount();
        this.amountPaid = t.getAmountPaid();
        this.amountDue = t.getAmountDue();
        this.orderReceipt = t.getOrderReceipt();
        this.orderStatus = t.getOrderStatus();

        this.notesEmail = t.getNotesEmail();
        this.notesMobile = t.getNotesMobile();

        this.acquirerAuthCode = t.getAcquirerAuthCode();
        this.refundStatus = t.getRefundStatus();

        
        this.fullPayload = null;
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

	public String getNotesEmail() {
		return notesEmail;
	}

	public void setNotesEmail(String notesEmail) {
		this.notesEmail = notesEmail;
	}

	public String getNotesMobile() {
		return notesMobile;
	}

	public void setNotesMobile(String notesMobile) {
		this.notesMobile = notesMobile;
	}

	public String getAcquirerAuthCode() {
		return acquirerAuthCode;
	}

	public void setAcquirerAuthCode(String acquirerAuthCode) {
		this.acquirerAuthCode = acquirerAuthCode;
	}

	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}

	public String getFullPayload() {
		return fullPayload;
	}

	public void setFullPayload(String fullPayload) {
		this.fullPayload = fullPayload;
	}

    // Add getters if needed for serialization
}
