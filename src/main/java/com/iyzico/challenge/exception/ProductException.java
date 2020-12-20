package com.iyzico.challenge.exception;

import com.iyzico.challenge.constant.Error;

public class ProductException extends RuntimeException {

    public ProductException(Error errorCode) {
        super(errorCode.getMsg());
    }
}
