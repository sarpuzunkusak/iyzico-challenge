package com.iyzico.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyzico.challenge.exception.PaymentFailedException;
import com.iyzico.challenge.exception.ProductOutOfStockException;
import com.iyzico.challenge.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc(addFilters = false)
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderControllerITest {

    @MockBean private OrderService orderService;

    @Autowired private MockMvc mvc;

    @Autowired private ObjectMapper objectMapper;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnOkWhenOrderIsSuccessful() throws Exception {
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setId(1L);
        request.setQuantity(1L);

        Mockito.doNothing().when(orderService).placeOrder(request);

        mvc.perform(
                        MockMvcRequestBuilders.post("/order")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturnBadRequestWhenIdIsEmpty() throws Exception {
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setQuantity(1L);

        mvc.perform(
                        MockMvcRequestBuilders.post("/order")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(orderService, Mockito.never()).placeOrder(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenQuantityIsEmpty() throws Exception {
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setId(1L);

        mvc.perform(
                        MockMvcRequestBuilders.post("/order")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(orderService, Mockito.never()).placeOrder(Mockito.any());
    }

    @Test
    public void shouldReturnServerErrorWhenPaymentFails() throws Exception {
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setId(1L);
        request.setQuantity(1L);

        Mockito.doThrow(PaymentFailedException.class).when(orderService).placeOrder(request);

        mvc.perform(
                        MockMvcRequestBuilders.post("/order")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void shouldReturnServerErrorWhenStockIsNotEnough() throws Exception {
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setId(1L);
        request.setQuantity(1L);

        Mockito.doThrow(ProductOutOfStockException.class).when(orderService).placeOrder(request);

        mvc.perform(
                        MockMvcRequestBuilders.post("/order")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
