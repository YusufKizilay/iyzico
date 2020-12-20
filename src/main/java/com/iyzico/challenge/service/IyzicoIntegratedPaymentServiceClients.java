package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.model.request.BuyProductRequest;
import com.iyzipay.Options;
import com.iyzipay.model.Payment;
import com.iyzipay.request.CreatePaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class IyzicoIntegratedPaymentServiceClients {

    private final IyzicoPaymentService iyzicoPaymentService;
    private final PojoConverter pojoConverter;

    public IyzicoIntegratedPaymentServiceClients(IyzicoPaymentService iyzicoPaymentService, PojoConverter pojoConverter) {
        this.iyzicoPaymentService = iyzicoPaymentService;
        this.pojoConverter = pojoConverter;
    }

    @Async
    public CompletableFuture<Payment> call(BuyProductRequest buyProductRequest, Product product) {
        CreatePaymentRequest request = pojoConverter.toCreatePaymentRequest(buyProductRequest, product);
        Options options = pojoConverter.toOptions(buyProductRequest.getConfiguration());

        Payment payment = iyzicoPaymentService.payWithIyzico(request, options);

        return CompletableFuture.completedFuture(payment);
    }
}
