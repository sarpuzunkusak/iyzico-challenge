package com.iyzico.challenge.controller;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PlaceOrderRequest {

    private @NotNull(message = "Id field should not be empty.") Long id;
    private @NotNull(message = "Quantity field should not be empty.") Long quantity;
}
