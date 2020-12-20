package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Payment;
import com.iyzico.challenge.repository.PaymentRepository;
import com.iyzipay.Options;
import com.iyzipay.request.CreatePaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class IyzicoPaymentService {

    private Logger logger = LoggerFactory.getLogger(IyzicoPaymentService.class);

    private BankService bankService;
    private PaymentRepository paymentRepository;

    public IyzicoPaymentService(BankService bankService, PaymentRepository paymentRepository) {
        this.bankService = bankService;
        this.paymentRepository = paymentRepository;
    }

    /**
     * @Transactional annotation was top of the class at first.
     * This made entire "pay" method was transactional.
     * In this situation, db connection is getting before the bankService remote  call and
     * releasing after the payment db insert. Since bankService remote call takes too long,
     * the method is unable to release the connection to the pool in time, this causes that
     * other threads have no chance to get db connection.
     * I removed the @Transactional annotation  from the top of class , every method in the paymentRepository is transactional by default,
     * and it is correct transaction boundary for this case
     */
    public void pay(BigDecimal price) {
        //pay with bank
        BankPaymentRequest request = new BankPaymentRequest();
        request.setPrice(price);
        BankPaymentResponse response = bankService.pay(request);

        //insert records
        Payment payment = new Payment();
        payment.setBankResponse(response.getResultCode());
        payment.setPrice(price);
        paymentRepository.save(payment);
        logger.info("Payment saved successfully!");
    }

    public com.iyzipay.model.Payment payWithIyzico(CreatePaymentRequest request, Options options) {
        return com.iyzipay.model.Payment.create(request, options);
    }

}
