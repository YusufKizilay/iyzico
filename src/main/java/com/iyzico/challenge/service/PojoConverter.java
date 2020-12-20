package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.model.ApiConfiguration;
import com.iyzico.challenge.model.request.*;
import com.iyzico.challenge.model.response.BuyProductResponse;
import com.iyzico.challenge.model.response.ProductResponse;
import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PojoConverter {

    private static final String DEFAULT_LOCALE = Locale.TR.getValue();
    private static final String DEFAULT_CURRENCY = Currency.TRY.name();
    private static final String DEFAULT_PAYMENT_CHANNEL = PaymentChannel.WEB.name();
    private static final String DEFAULT_PAYMENT_GROUP = PaymentGroup.PRODUCT.name();
    private static final String DEFAULT_CATEGORY = "default";

    public Product toEntity(ProductRequest request) {
        return new Product(request.getName(), request.getDescription(), request.getPrice(), request.getStock());
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getName(), product.getDescription(), product.getPrice(), product.getStock(), product.getId());
    }

    /**
     * In order to keep it simple and save time, method makes some assumptions that would not be available in real life scenarios.
     *
     * 1- There is no such table as an "order" table for this job. That's why i didn't send the an orderId as conversationId.
     * Instead of this i send a dummy id.
     *
     * 2- I sent the same address as shipping address and billingAddress address. I did not distinguish between the two.
     *
     * 3- I assumed that there can only be one product in the basket.
     *
     * 4- Generally some fields iyzico payment api needed, i didn't get it from request and set it as dummy.
     *
     * 5- Before reaching this point, that is, before converting the fields from the request to the fields requested by the payment api,
     * the necessary validations must be made, but I did not do it because it would take time to add the validations required by the api and
     * I thought that this was not what is wanted to be measured in the case study.
     *
     */
    public CreatePaymentRequest toCreatePaymentRequest(BuyProductRequest buyProductRequest, Product product) {
        CreatePaymentRequest request = createPaymentRequest(product);

        PaymentCard paymentCard = createPaymentCard(buyProductRequest.getPaymentCard());
        request.setPaymentCard(paymentCard);

        Buyer buyer = createBuyer(buyProductRequest.getBuyer());
        request.setBuyer(buyer);

        Address shippingAddress = createAddress(buyProductRequest.getAddress());
        request.setShippingAddress(shippingAddress);

        Address billingAddress = createAddress(buyProductRequest.getAddress());
        request.setBillingAddress(billingAddress);

        List<BasketItem> basketItems = createBasketItems(product);
        request.setBasketItems(basketItems);

        return request;
    }

    public Options toOptions(ApiConfiguration configuration) {
        Options options = new Options();
        
        options.setBaseUrl(configuration.getBaseUrl());
        options.setApiKey(configuration.getApiKey());
        options.setSecretKey(configuration.getSecretKey());
        return options;
    }

    public BuyProductResponse toResponse(Long id, String conversationId) {
        return new BuyProductResponse(id, conversationId);
    }

    private CreatePaymentRequest createPaymentRequest(Product product) {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLocale(DEFAULT_LOCALE);
        request.setConversationId(UUID.randomUUID().toString());
        request.setCurrency(DEFAULT_CURRENCY);
        request.setInstallment(1);
        request.setBasketId(UUID.randomUUID().toString());
        request.setPaymentChannel(DEFAULT_PAYMENT_CHANNEL);
        request.setPaymentGroup(DEFAULT_PAYMENT_GROUP);
        request.setPrice(product.getPrice());
        request.setPaidPrice(product.getPrice());


        return request;
    }

    private PaymentCard createPaymentCard(PaymentCardRequest cardRequest) {
        PaymentCard paymentCard = new PaymentCard();

        paymentCard.setCardHolderName(cardRequest.getOwnerName());
        paymentCard.setCardNumber(cardRequest.getCardNumber());
        paymentCard.setExpireMonth(cardRequest.getExpireMonth());
        paymentCard.setExpireYear(cardRequest.getExpireYear());
        paymentCard.setCvc(cardRequest.getCvc());
        paymentCard.setRegisterCard(0);

        return paymentCard;
    }

    private Address createAddress(AddressRequest addressRequest) {
        Address address = new Address();
        address.setContactName(addressRequest.getContactName());
        address.setCity(addressRequest.getCity());
        address.setCountry(addressRequest.getCountry());
        address.setAddress(addressRequest.getAddress());
        address.setZipCode(addressRequest.getZipCode());

        return address;
    }

    private List<BasketItem> createBasketItems(Product product) {
        List<BasketItem> basketItems = new ArrayList<BasketItem>();
        BasketItem firstBasketItem = new BasketItem();
        firstBasketItem.setId(product.getId().toString());
        firstBasketItem.setName(product.getName());
        firstBasketItem.setPrice(product.getPrice());
        firstBasketItem.setItemType(BasketItemType.PHYSICAL.name());
        firstBasketItem.setCategory1(DEFAULT_CATEGORY);
        basketItems.add(firstBasketItem);

        return basketItems;
    }

    private Buyer createBuyer(BuyerRequest request) {
        Buyer buyer = new Buyer();
        buyer.setId(request.getId());
        buyer.setName(request.getName());
        buyer.setSurname(request.getSurname());
        buyer.setGsmNumber(request.getGsmNumber());
        buyer.setEmail(request.getEmail());
        buyer.setIdentityNumber(request.getIdentityNumber());
        buyer.setLastLoginDate(request.getLastLoginDate());
        buyer.setRegistrationDate(request.getRegistrationDate());
        buyer.setRegistrationAddress(request.getRegistrationAddress());
        buyer.setIp(request.getIp());
        buyer.setCity(request.getCity());
        buyer.setCountry(request.getCountry());
        buyer.setZipCode(request.getZipCode());

        return buyer;
    }


}
