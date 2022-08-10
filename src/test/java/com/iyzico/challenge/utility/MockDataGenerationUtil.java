package com.iyzico.challenge.utility;

import com.iyzico.challenge.entity.Payment;
import com.iyzico.challenge.entity.Product;

import java.math.BigDecimal;

public class MockDataGenerationUtil {

    public static Product createProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Name");
        product.setPrice(BigDecimal.valueOf(10.0));
        product.setStock(3L);

        return product;
    }

}
