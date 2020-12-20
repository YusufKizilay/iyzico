package com.iyzico.challenge.model.response;

import com.iyzico.challenge.model.request.ProductRequest;

import java.math.BigDecimal;

public class ProductResponse extends ProductRequest {
    private Long id;

    public ProductResponse() {
    }

    public ProductResponse(String name, String description, BigDecimal price, Long stock, Long id) {
        super(name, description, price, stock);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
