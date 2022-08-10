package com.iyzico.challenge.service;

import com.iyzico.challenge.controller.PlaceOrderRequest;
import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.PaymentFailedException;
import com.iyzico.challenge.exception.ProductNotFoundException;
import com.iyzico.challenge.exception.ProductOutOfStockException;
import com.iyzico.challenge.repository.PaymentRepository;
import com.iyzico.challenge.repository.ProductRepository;
import com.iyzico.challenge.utility.MockDataGenerationUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceITest {

    @MockBean private IyzicoSandboxPaymentService paymentService;

    @Autowired private ProductRepository productRepository;

    @Autowired private PaymentRepository paymentRepository;

    @Autowired private OrderService orderService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void shutdown() {
        productRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    public void shouldDecreaseStockWhenPaymentSuccessful()
            throws ProductOutOfStockException, ProductNotFoundException, PaymentFailedException {
        Product given = MockDataGenerationUtil.createProduct();
        Product existing = productRepository.save(given);

        Mockito.doNothing().when(paymentService).pay(given.getPrice());

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setId(existing.getId());
        request.setQuantity(1L);

        orderService.placeOrder(request);

        Product current = productRepository.findById(existing.getId()).get();

        Assert.assertEquals(
                given.getStock() - request.getQuantity(), current.getStock().longValue());
    }

    @Test(expected = PaymentFailedException.class)
    public void shouldNotDecreaseStockWhenPaymentFails()
            throws ProductOutOfStockException, ProductNotFoundException, PaymentFailedException {
        Product given = MockDataGenerationUtil.createProduct();
        Product existing = productRepository.save(given);

        Mockito.doThrow(PaymentFailedException.class).when(paymentService).pay(Mockito.any());

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setId(existing.getId());
        request.setQuantity(1L);

        orderService.placeOrder(request);

        Product current = productRepository.findById(existing.getId()).get();

        Assert.assertEquals(given.getStock().longValue(), current.getStock().longValue());
    }

    @Test(expected = ProductOutOfStockException.class)
    public void shouldNotDecreaseStockWhenStockNotEnough()
            throws ProductOutOfStockException, ProductNotFoundException, PaymentFailedException {
        Product given = MockDataGenerationUtil.createProduct();
        Product existing = productRepository.save(given);

        Mockito.doNothing().when(paymentService).pay(given.getPrice());

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setId(existing.getId());
        request.setQuantity(4L);

        orderService.placeOrder(request);

        Product current = productRepository.findById(existing.getId()).get();

        Assert.assertEquals(
                given.getStock() - request.getQuantity(), current.getStock().longValue());
    }
}
