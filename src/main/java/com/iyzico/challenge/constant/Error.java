package com.iyzico.challenge.constant;

public enum Error {

    PRODUCT_NOT_FOUND("There is no product with given id."),
    STOCK_DEPLETED("Stock is depleted for the given product.");

    private String msg;

     Error(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
