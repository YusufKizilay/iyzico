package com.iyzico.challenge;

import com.iyzico.challenge.constant.Error;
import com.iyzico.challenge.model.ApiConfiguration;
import com.iyzico.challenge.model.request.*;
import com.iyzico.challenge.model.response.BuyProductResponse;
import com.iyzico.challenge.model.response.ErrorResponse;
import com.iyzico.challenge.model.response.ProductResponse;
import com.iyzico.challenge.service.IyzicoPaymentService;
import com.iyzipay.Options;
import com.iyzipay.request.CreatePaymentRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ProductApiIT.TestConfig.class)
@EnableAsync
public class ProductApiIT {

    private static final String HOST = "http://localhost:";
    private static final String API_URL = "/iyzico/product";

    @LocalServerPort
    private int port;
    TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private ApplicationContext applicationContext;

    @TestConfiguration
    public static class TestConfig {
        @SpyBean
        IyzicoPaymentService iyzicoPaymentService;
    }


    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/delete-all.sql")
    public void addProduct_shouldReturn201() {
        ProductRequest request = new ProductRequest("name", "desc", BigDecimal.TEN, 3L);

        ResponseEntity<ProductResponse> response = restTemplate.postForEntity(createURI(""), createHttpEntity(request), ProductResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("name", response.getBody().getName());
        assertEquals("desc", response.getBody().getDescription());
        assertEquals(BigDecimal.TEN, response.getBody().getPrice());
        assertTrue(3L == response.getBody().getStock());
    }

    @Test
    @Sql("/insert-product.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/delete-all.sql")
    public void getProduct_shouldReturn200() {
        ResponseEntity<ProductResponse> response = restTemplate.getForEntity(createURI("/1"), ProductResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("name", response.getBody().getName());
        assertEquals("desc", response.getBody().getDescription());
        assertEquals(new BigDecimal("10.00"), response.getBody().getPrice());
        assertTrue(3L == response.getBody().getStock());
        assertTrue(1L == response.getBody().getId());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/delete-all.sql")
    public void getProduct_shouldReturn400_whenProductNotExist() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(createURI("/1"), ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Error.PRODUCT_NOT_FOUND.getMsg(), response.getBody().getMessage());
    }

    @Test
    @Sql("/insert-product.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/delete-all.sql")
    public void updateProduct_shouldReturn200() {
        UpdateProductRequest request = new UpdateProductRequest("updatedName", "updatedDesc", new BigDecimal(5), 0L, 1L);

        ResponseEntity<ProductResponse> response = restTemplate.exchange(createURI(""), HttpMethod.PUT, createHttpEntity(request), ProductResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("updatedName", response.getBody().getName());
        assertEquals("updatedDesc", response.getBody().getDescription());
        assertEquals(new BigDecimal("5"), response.getBody().getPrice());
        assertTrue(0L == response.getBody().getStock());
        assertTrue(1L == response.getBody().getId());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/delete-all.sql")
    public void updateProduct_shouldReturn400_whenProductNotExist() {
        UpdateProductRequest request = new UpdateProductRequest("updatedName", "updatedDesc", new BigDecimal(5), 0L, 1L);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(createURI(""), HttpMethod.PUT, createHttpEntity(request), ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Error.PRODUCT_NOT_FOUND.getMsg(), response.getBody().getMessage());
    }

    @Test
    @Sql("/insert-product.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/delete-all.sql")
    public void deleteProduct_shouldReturn204() {
        ResponseEntity response = restTemplate.exchange(createURI("/1"), HttpMethod.DELETE, null, ResponseEntity.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/delete-all.sql")
    public void deleteProduct_shouldReturn400_whenProductNotExist() {
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(createURI("/1"), HttpMethod.DELETE, null, ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Error.PRODUCT_NOT_FOUND.getMsg(), response.getBody().getMessage());
    }


    @Test
    @Sql("/insert-product.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/delete-all.sql")
    public void buyProduct_shouldReturn200_andUpdateStock_whenIyzicoPaymentApiReturnsSuccess() {
        // insert-product.sql insert a product with id :1, stock:3
        // The request wants 2 of the product with id 1.
        // The request is prepared to get success response from iyzico payment api
        BuyProductRequest request = createBuyProductRequest(1L, 2L, RequestType.SUCCESS);

        ResponseEntity<BuyProductResponse> response = restTemplate.exchange(createURI("/buy"), HttpMethod.PUT, createHttpEntity(request), BuyProductResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(1L == response.getBody().getProductId());

        //Product stock should be 3-2=1
        //Get product and check product'stock is 1 or not
        assertTrue(1L == findProduct().getStock());
    }


    @Test
    @Sql("/insert-product.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/delete-all.sql")
    public void buyProduct_shouldReturn500_andDoNotUpdateStock_whenIyzicoPaymentApiReturnsFailure() {
        // insert-product.sql insert a product with id :1, stock:3
        // The request wants 2 of the product with id 1.
        // The request is prepared to get failure response from iyzico payment api
        BuyProductRequest request = createBuyProductRequest(1L, 2L, RequestType.FAIL);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(createURI("/buy"), HttpMethod.PUT, createHttpEntity(request), ErrorResponse.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Kart numarası geçersizdir", response.getBody().getMessage());

        //Iyzico payment api returns failure, the product's stock should be same(3)
        //Get product and check product'stock is 3 or not
        assertTrue(3L == findProduct().getStock());
    }


    @Test
    @Sql("/insert-product.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/delete-all.sql")
    public void buyProduct_shouldReturn500_andDoNotUpdateStock_whenIyzicoPaymentApiThrowsException() {
        // insert-product.sql insert a product with id :1, stock:3
        // The request wants 2 of the product with id 1.
        // The request is prepared to get exception from iyzico payment api
        BuyProductRequest request = createBuyProductRequest(1L, 2L, RequestType.EXCEPTION);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(createURI("/buy"), HttpMethod.PUT, createHttpEntity(request), ErrorResponse.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        //Iyzico payment api throws exception, the product's stock should be same(3)
        //Get product and check product'stock is 3 or not
        assertTrue(3L == findProduct().getStock());

    }

    @Test
    @Sql("/insert-product.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/delete-all.sql")
    public void buyProduct_shouldReturn400_whenStockDepleted() {
        // insert-product.sql insert a product with id :1, stock:3
        // The request wants 4 of the product with id 1.
        BuyProductRequest request = createBuyProductRequest(1L, 4L, RequestType.SUCCESS);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(createURI("/buy"), HttpMethod.PUT, createHttpEntity(request), ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Error.STOCK_DEPLETED.getMsg(), response.getBody().getMessage());

        //Stock validation is failed, the product's stock should be same(3)
        //Get product and check product'stock is 3 or not
        assertTrue(3L == findProduct().getStock());
    }

    @Test
    @Sql("/insert-product.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/delete-all.sql")
    public void buySameProductConcurrently_shouldCallPaymentServiceOnlyOnce() {
        // insert-product.sql insert a product with id :1, stock:3
        // The request wants 2 of the product with id 1. 100 of the request sends concurrently to the server
        // Only one request should buy the product and call iyzico payment service and update product stock
        // The other requests will fail
        BuyProductRequest request = createBuyProductRequest(1L, 2L, RequestType.SUCCESS);

        ExecutorService executor = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                restTemplate.exchange(createURI("/buy"), HttpMethod.PUT, createHttpEntity(request), BuyProductResponse.class);
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {

        }

        verify(getIyzicoPaymentService(), times(1)).payWithIyzico(any(CreatePaymentRequest.class), any(Options.class));
        assertTrue(1L == findProduct().getStock());
    }

    private IyzicoPaymentService getIyzicoPaymentService() {
        return (IyzicoPaymentService) applicationContext.getBean("iyzicoPaymentService");
    }

    private ProductResponse findProduct() {
        ResponseEntity<ProductResponse> response = restTemplate.getForEntity(createURI("/1"), ProductResponse.class);
        return response.getBody();
    }

    private BuyProductRequest createBuyProductRequest(Long productId, Long amount, RequestType type) {
        BuyProductRequest request = new BuyProductRequest();

        request.setId(productId);
        request.setAmount(amount);

        request.setAddress(new AddressRequest("contactName", "city", "country", "address", "zipcode"));

        String baseUrl = type.equals(RequestType.EXCEPTION) ? "asd" : "https://sandbox-api.iyzipay.com";
        String cardNumber = type.equals(RequestType.FAIL) ? "asd" : "5528790000000008";

        request.setPaymentCard(new PaymentCardRequest("ownerName", cardNumber, "12", "2030", "123"));
        request.setConfiguration(new ApiConfiguration(baseUrl, "sandbox-kpV4WdR8M5QP0SqxGa6cV0MyfhcMDoEa", "sandbox-oO4Hco4c2KchXnGHxKTy9wg6KK9i9Pys"));


        request.setBuyer(new BuyerRequest("BY789", "John", "Doe", "905350000000",
                "email@email.com", "74300864791", "2015-10-05 12:43:35",
                "2013-04-21 15:12:09", "Nidakule Göztepe, Merdivenköy Mah. Bora Sok. No:1\"",
                "85.34.78.112", "Istanbul", "Turkey", "34732"));


        return request;
    }

    private String createURI(String str) {
        StringBuilder builder = new StringBuilder(HOST);
        return builder.append(port).append(API_URL).append(str).toString();
    }

    private <T> HttpEntity<T> createHttpEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        return new HttpEntity<>(body, headers);
    }

    enum RequestType {
        SUCCESS, FAIL, EXCEPTION
    }
}
