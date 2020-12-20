package com.iyzico.challenge.model.request;

import java.math.BigDecimal;

public class UpdateProductRequest extends ProductRequest {
    private Long id;

    public UpdateProductRequest() {
    }

    public UpdateProductRequest(String name, String description, BigDecimal price, Long stock, Long id) {
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
