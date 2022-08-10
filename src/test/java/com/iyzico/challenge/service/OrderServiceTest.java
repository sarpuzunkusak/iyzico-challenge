package com.iyzico.challenge.service;

import com.iyzico.challenge.controller.PlaceOrderRequest;
import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.PaymentFailedException;
import com.iyzico.challenge.exception.ProductNotFoundException;
import com.iyzico.challenge.exception.ProductOutOfStockException;
import com.iyzico.challenge.utility.MockDataGenerationUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class OrderServiceTest {

    @Mock private ProductService productService;

    @Mock private IyzicoSandboxPaymentService paymentService;

    @InjectMocks private OrderService underTest;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPlaceOrder()
            throws ProductOutOfStockException, ProductNotFoundException, PaymentFailedException {
        Product product = MockDataGenerationUtil.createProduct();

        Mockito.when(productService.getProductById(1L)).thenReturn(product);
        Mockito.doNothing().when(productService).reduceStock(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(paymentService).pay(Mockito.any());

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setId(1L);
        request.setQuantity(1L);

        underTest.placeOrder(request);

        Mockito.verify(productService, Mockito.atMostOnce())
                .reduceStock(request.getId(), request.getQuantity());
        Mockito.verify(paymentService, Mockito.atMostOnce()).pay(Mockito.any());
    }

    @Test(expected = PaymentFailedException.class)
    public void testPlaceOrderWhenPaymentFails()
            throws ProductOutOfStockException, ProductNotFoundException, PaymentFailedException {
        Product product = MockDataGenerationUtil.createProduct();

        Mockito.when(productService.getProductById(1L)).thenReturn(product);
        Mockito.doNothing().when(productService).reduceStock(Mockito.any(), Mockito.any());
        Mockito.doThrow(PaymentFailedException.class).when(paymentService).pay(Mockito.any());
        Mockito.doNothing().when(productService).increaseStock(Mockito.any(), Mockito.any());

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setId(1L);
        request.setQuantity(1L);

        underTest.placeOrder(request);

        Mockito.verify(productService, Mockito.atMostOnce())
                .reduceStock(request.getId(), request.getQuantity());
        Mockito.verify(paymentService, Mockito.atMostOnce()).pay(Mockito.any());
        Mockito.verify(productService, Mockito.atMostOnce())
                .increaseStock(request.getId(), request.getQuantity());
    }

    @Test(expected = ProductNotFoundException.class)
    public void testPlaceOrderWhenProductNotFound()
            throws ProductOutOfStockException, ProductNotFoundException, PaymentFailedException {
        Mockito.when(productService.getProductById(1L)).thenThrow(ProductNotFoundException.class);

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setId(1L);
        request.setQuantity(1L);

        underTest.placeOrder(request);

        Mockito.verify(productService, Mockito.never())
                .reduceStock(request.getId(), request.getQuantity());
        Mockito.verify(paymentService, Mockito.never()).pay(Mockito.any());
    }

    @Test(expected = ProductOutOfStockException.class)
    public void testPlaceOrderWhenProductHasNotEnoughStock()
            throws ProductOutOfStockException, ProductNotFoundException, PaymentFailedException {
        Product product = MockDataGenerationUtil.createProduct();

        Mockito.when(productService.getProductById(1L)).thenReturn(product);
        Mockito.doThrow(ProductOutOfStockException.class)
                .when(productService)
                .reduceStock(Mockito.any(), Mockito.any());

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setId(1L);
        request.setQuantity(1L);

        underTest.placeOrder(request);

        Mockito.verify(productService, Mockito.atMostOnce())
                .reduceStock(request.getId(), request.getQuantity());
        Mockito.verify(paymentService, Mockito.never()).pay(Mockito.any());
        Mockito.verify(productService, Mockito.never())
                .increaseStock(request.getId(), request.getQuantity());
    }
}
