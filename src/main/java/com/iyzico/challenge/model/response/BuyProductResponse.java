package com.iyzico.challenge.model.response;

public class BuyProductResponse {
    private Long productId;
    private String conversationId;

    public BuyProductResponse() {
    }

    public BuyProductResponse(Long productId, String conversationId) {
        this.productId = productId;
        this.conversationId = conversationId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
