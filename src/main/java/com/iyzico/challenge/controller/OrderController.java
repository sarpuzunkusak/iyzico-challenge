package com.iyzico.challenge.controller;

import com.iyzico.challenge.exception.PaymentFailedException;
import com.iyzico.challenge.exception.ProductNotFoundException;
import com.iyzico.challenge.exception.ProductOutOfStockException;
import com.iyzico.challenge.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public ResponseEntity<?> placeOrder(@RequestBody @Valid PlaceOrderRequest body)
            throws ProductOutOfStockException, ProductNotFoundException, PaymentFailedException {
        orderService.placeOrder(body);
        return ResponseEntity.ok().build();
    }
}
