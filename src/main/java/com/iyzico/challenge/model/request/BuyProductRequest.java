package com.iyzico.challenge.model.request;

import com.iyzico.challenge.model.ApiConfiguration;

public class BuyProductRequest {

    private Long id;

    private Long amount;

    private PaymentCardRequest paymentCard;

    private AddressRequest address;

    private ApiConfiguration configuration;

    private BuyerRequest buyer;

    public BuyProductRequest() {
    }

    public BuyProductRequest(Long id, Long amount, PaymentCardRequest paymentCard,
                             AddressRequest address, ApiConfiguration configuration, BuyerRequest buyer) {
        this.id = id;
        this.amount = amount;
        this.paymentCard = paymentCard;
        this.address = address;
        this.configuration = configuration;
        this.buyer=buyer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public PaymentCardRequest getPaymentCard() {
        return paymentCard;
    }

    public void setPaymentCard(PaymentCardRequest paymentCard) {
        this.paymentCard = paymentCard;
    }

    public AddressRequest getAddress() {
        return address;
    }

    public void setAddress(AddressRequest address) {
        this.address = address;
    }

    public ApiConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ApiConfiguration configuration) {
        this.configuration = configuration;
    }

    public BuyerRequest getBuyer() {
        return buyer;
    }

    public void setBuyer(BuyerRequest buyer) {
        this.buyer = buyer;
    }
}
