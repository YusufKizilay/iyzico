package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.PaymentException;
import com.iyzico.challenge.model.request.BuyProductRequest;
import com.iyzipay.model.Payment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BuyProductServiceTest {

    @Autowired
    private BuyProductService buyProductService;
    @MockBean
    private ProductService productService;
    @MockBean
    private PojoConverter pojoConverter;
    @MockBean
    private IyzicoIntegratedPaymentServiceClients iyzicoIntegratedPaymentServiceClients;
    @Mock
    private Product product;
    @Mock
    private BuyProductRequest buyProductRequest;
    @Mock
    private Payment payment;


    @Test
    public void buyProduct_shouldNotRollback_whenPaymentServiceReturnSuccess() throws Exception {
        when(productService.buyProduct(buyProductRequest)).thenReturn(product);
        when(iyzicoIntegratedPaymentServiceClients.call(buyProductRequest, product)).thenReturn(CompletableFuture.completedFuture(payment));
        when(payment.getStatus()).thenReturn("success");
        when(payment.getConversationId()).thenReturn("conversationId");
        when(product.getId()).thenReturn(1L);

        buyProductService.buyProduct(buyProductRequest);

        verify(productService, times(0)).updateProduct(any(Product.class));
        verify(pojoConverter, times(1)).toResponse(1L, "conversationId");
    }

    @Test
    public void buyProduct_shouldRollback_whenPaymentServiceReturnFailure() throws Exception {
        when(productService.buyProduct(buyProductRequest)).thenReturn(product);
        when(iyzicoIntegratedPaymentServiceClients.call(buyProductRequest, product)).thenReturn(CompletableFuture.completedFuture(payment));
        when(payment.getStatus()).thenReturn("failure");
        when(payment.getErrorMessage()).thenReturn("error retrieved");

        try {
            buyProductService.buyProduct(buyProductRequest);
        } catch (PaymentException ex) {
            assertEquals("error retrieved", ex.getMessage());
        }

        verify(productService, times(1)).updateProduct(product);
        verify(pojoConverter, times(0)).toResponse(anyLong(), anyString());
    }

    @Test
    public void buyProduct_shouldRollback_whenPaymentServiceThrowsException() throws Exception {
        when(productService.buyProduct(buyProductRequest)).thenReturn(product);
        when(iyzicoIntegratedPaymentServiceClients.call(buyProductRequest, product)).thenThrow(new RuntimeException("error msg"));

        try {
            buyProductService.buyProduct(buyProductRequest);
        } catch (Exception ex) {
            assertEquals("error msg", ex.getMessage());
        }

        verify(productService, times(1)).updateProduct(product);
        verify(pojoConverter, times(0)).toResponse(anyLong(), anyString());
    }
}
