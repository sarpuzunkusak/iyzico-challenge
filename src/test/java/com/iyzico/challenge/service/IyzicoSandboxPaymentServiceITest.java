package com.iyzico.challenge.service;

import com.iyzico.challenge.exception.PaymentFailedException;
import com.iyzico.challenge.repository.PaymentRepository;
import com.iyzipay.model.Payment;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IyzicoSandboxPaymentServiceITest {

    @Autowired private PaymentRepository paymentRepository;

    @Autowired private IyzicoSandboxPaymentService paymentService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void shutdown() {
        paymentRepository.deleteAll();
    }

    @Test
    public void shouldInsertPaymentWhenPaymentSucceeds() throws PaymentFailedException {

        try (MockedStatic<Payment> utilities =
                Mockito.mockStatic(com.iyzipay.model.Payment.class)) {
            com.iyzipay.model.Payment response = new Payment();
            response.setStatus("success");

            utilities
                    .when(() -> com.iyzipay.model.Payment.create(Mockito.any(), Mockito.any()))
                    .thenReturn(response);

            paymentService.pay(BigDecimal.valueOf(10.0));

            com.iyzico.challenge.entity.Payment payment = paymentRepository.findAll().get(0);

            Assert.assertEquals(0, BigDecimal.valueOf(10.0).compareTo(payment.getPrice()));
        }
    }

    @Test(expected = PaymentFailedException.class)
    public void shouldInsertPaymentWhenPaymentFails() throws PaymentFailedException {

        try (MockedStatic<Payment> utilities =
                Mockito.mockStatic(com.iyzipay.model.Payment.class)) {
            com.iyzipay.model.Payment response = new Payment();
            response.setStatus("failure");
            response.setErrorCode("1000");

            utilities
                    .when(() -> com.iyzipay.model.Payment.create(Mockito.any(), Mockito.any()))
                    .thenReturn(response);

            paymentService.pay(BigDecimal.valueOf(10.0));

            com.iyzico.challenge.entity.Payment payment = paymentRepository.findAll().get(0);

            Assert.assertEquals(0, BigDecimal.valueOf(10.0).compareTo(payment.getPrice()));
            Assert.assertEquals(response.getErrorCode(), payment.getBankResponse());
        }
    }
}
