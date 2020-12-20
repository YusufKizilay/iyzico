package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.PaymentException;
import com.iyzico.challenge.model.request.BuyProductRequest;
import com.iyzico.challenge.model.response.BuyProductResponse;
import com.iyzipay.model.Payment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BuyProductService {
    private final ProductService productService;
    private final PojoConverter pojoConverter;
    private final IyzicoIntegratedPaymentServiceClients iyzicoIntegratedPaymentServiceClients;

    private static final String FAILURE = "failure";

    public BuyProductService(ProductService productService, PojoConverter pojoConverter,
                             IyzicoIntegratedPaymentServiceClients iyzicoIntegratedPaymentServiceClients) {
        this.productService = productService;
        this.pojoConverter = pojoConverter;
        this.iyzicoIntegratedPaymentServiceClients = iyzicoIntegratedPaymentServiceClients;
    }

    /**
     * There are some points need to pay attention to avoid inconsistency in the system
     * 1- Calling the remote iyzico payment service should be last process, because if a validation fails or an exception
     * occurs before the remote payment call, remote payment call should not be executed.
     * 2- If remote iyzico payment call gets exception or returns failure, the database update should be rolled back.
     *
     * To ensure the points mentioned above,the following could be applied:
     * The entire "buyProduct" method could be @Transactional and the remote call could be last step of the transaction.
     * In this case, if the remote call received an error or threw exception, the previous update operation would not be committed.
     * And there would be no need to use a logic that rolls back the transaction.
     *
     * But the solution mentioned above includes the remote call into a transactional method ({@link BuyProductService#buyProduct(BuyProductRequest)}).
     * If remote call takes too long, we would be faced the problem which is described and implemented in {@link IyzicoPaymentService#pay(BigDecimal)}
     * So we would get "Connection is not available, request timed out after 30005ms" error.
     *
     * So to avoid this error, {@link BuyProductService#buyProduct(BuyProductRequest)} is not annotated with @Transactional
     * Instead of this, {@link ProductService#buyProduct(BuyProductRequest)} is annotated with @Transactional which is required
     * to apply the row lock mechanism.({@link com.iyzico.challenge.repository.ProductRepository#findById(Long)})
     *
     * In this case, the remote iyzico payment call is not part of the transaction and we need to rollback previous product update process
     * if remote call gets failure or throws exception
     *
     */
    public BuyProductResponse buyProduct(BuyProductRequest request) throws Exception {
        Product existedProduct = productService.buyProduct(request);

        Payment payment = callWithIyzico(request, existedProduct);

        return pojoConverter.toResponse(existedProduct.getId(), payment.getConversationId());
    }


    private Payment callWithIyzico(BuyProductRequest request, Product product) throws Exception {
        Payment payment;

        try {
            payment = iyzicoIntegratedPaymentServiceClients.call(request, product).get();
        } catch (Exception e) {
            rollbackBuyProduct(product);
            throw e;
        }

        if (payment.getStatus().equals(FAILURE)) {
            rollbackBuyProduct(product);

            throw new PaymentException(payment.getErrorMessage());
        }

        return payment;
    }


    private void rollbackBuyProduct(Product product) {
        productService.updateProduct(product);
    }

}
