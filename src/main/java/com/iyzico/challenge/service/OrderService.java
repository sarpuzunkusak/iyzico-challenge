package com.iyzico.challenge.service;

import com.iyzico.challenge.controller.PlaceOrderRequest;
import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.PaymentFailedException;
import com.iyzico.challenge.exception.ProductNotFoundException;
import com.iyzico.challenge.exception.ProductOutOfStockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final ProductService productService;

    private final IyzicoSandboxPaymentService paymentService;

    public OrderService(ProductService productService, IyzicoSandboxPaymentService paymentService) {
        this.productService = productService;
        this.paymentService = paymentService;
    }

    @Transactional(noRollbackFor = PaymentFailedException.class)
    public void placeOrder(PlaceOrderRequest body)
            throws ProductOutOfStockException, ProductNotFoundException, PaymentFailedException {
        Product product = productService.getProductById(body.getId());

        productService.reduceStock(body.getId(), body.getQuantity());

        try {
            paymentService.pay(product.getPrice().multiply(BigDecimal.valueOf(body.getQuantity())));
        } catch (PaymentFailedException e) {
            productService.increaseStock(body.getId(), body.getQuantity());
            throw e;
        }
    }
}
