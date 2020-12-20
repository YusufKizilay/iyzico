package com.iyzico.challenge.service;

import com.iyzico.challenge.constant.Error;
import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.ProductException;
import com.iyzico.challenge.model.request.BuyProductRequest;
import com.iyzico.challenge.model.request.ProductRequest;
import com.iyzico.challenge.model.request.UpdateProductRequest;
import com.iyzico.challenge.model.response.ProductResponse;
import com.iyzico.challenge.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final PojoConverter pojoConverter;

    public ProductService(ProductRepository productRepository, PojoConverter pojoConverter) {
        this.productRepository = productRepository;
        this.pojoConverter = pojoConverter;
    }

    public ProductResponse saveProduct(ProductRequest request) {
        Product savedProduct = productRepository.save(pojoConverter.toEntity(request));

        return pojoConverter.toResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(UpdateProductRequest request) {
        Product existedProduct = findProduct(request.getId());

        existedProduct.setDescription(request.getDescription());
        existedProduct.setName(request.getName());
        existedProduct.setPrice(request.getPrice());
        existedProduct.setStock(request.getStock());

        return pojoConverter.toResponse(existedProduct);
    }

    public ProductResponse updateProduct(Product existedProduct) {
        Product persistedProduct = productRepository.save(existedProduct);

        return pojoConverter.toResponse(persistedProduct);
    }

    public void deleteProduct(Long id) {
        Product foundProduct = findProduct(id);

        productRepository.delete(foundProduct);
    }

    public ProductResponse getProduct(Long id) {
        Product foundProduct = findProduct(id);

        return pojoConverter.toResponse(foundProduct);
    }

    private Product findProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);

        if (!product.isPresent()) {
            throw new ProductException(Error.PRODUCT_NOT_FOUND);
        }

        return product.get();
    }

    @Transactional
    public Product buyProduct(BuyProductRequest request) throws Exception {
        Product productToBeUpdated = findProduct(request.getId());

        if (request.getAmount().compareTo(productToBeUpdated.getStock()) == 1) {
            throw new ProductException(Error.STOCK_DEPLETED);
        }

        Long updatedStock = productToBeUpdated.getStock() - request.getAmount();

        /**
         * The method returns existed and not updated product to the caller ({@link BuyProductService#buyProduct(BuyProductRequest)})
         * for the possible rollback case.
         * Used to copy constructor to accomplish this.
         */
        Product existedProduct = new Product(productToBeUpdated);

        productToBeUpdated.setStock(updatedStock);

        return existedProduct;

    }
}
