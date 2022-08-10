package com.iyzico.challenge.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Product {

    @Id @GeneratedValue private Long id;

    private @NotBlank(message = "Name field should not be blank.") String name;
    private String description;
    private @NotNull(message = "Price field should not be empty.") BigDecimal price;
    private @NotNull(message = "Stock field should not be empty.") Long stock;

    @Version @JsonIgnore private Long version;
}
