package com.iyzico.challenge.controller;

import com.iyzico.challenge.model.request.BuyProductRequest;
import com.iyzico.challenge.model.request.ProductRequest;
import com.iyzico.challenge.model.request.UpdateProductRequest;
import com.iyzico.challenge.model.response.BuyProductResponse;
import com.iyzico.challenge.model.response.ProductResponse;
import com.iyzico.challenge.service.BuyProductService;
import com.iyzico.challenge.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iyzico/product")
public class ProductController {

    private final ProductService productService;
    private final BuyProductService buyProductService;

    public ProductController(ProductService productService, BuyProductService buyProductService) {
        this.productService = productService;
        this.buyProductService = buyProductService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse addProduct(@RequestBody ProductRequest request) {
        return productService.saveProduct(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse updateProduct(@RequestBody UpdateProductRequest request) {
        return productService.updateProduct(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PutMapping("/buy")
    @ResponseStatus(HttpStatus.OK)
    public BuyProductResponse buyProduct(@RequestBody BuyProductRequest request) throws Exception {
        return buyProductService.buyProduct(request);
    }
}
