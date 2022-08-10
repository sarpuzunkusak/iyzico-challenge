package com.iyzico.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.ProductNotFoundException;
import com.iyzico.challenge.service.ProductService;
import com.iyzico.challenge.utility.MockDataGenerationUtil;
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

import java.math.BigDecimal;
import java.util.Collections;

@AutoConfigureMockMvc(addFilters = false)
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductControllerITest {

    @MockBean private ProductService productService;

    @Autowired private MockMvc mvc;

    @Autowired private ObjectMapper objectMapper;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnOkWhenEverythingIsOkOnListing() throws Exception {
        Mockito.when(productService.listProducts()).thenReturn(Collections.emptyList());

        mvc.perform(
                        MockMvcRequestBuilders.get("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturnSpecificProductWhenThatProductExists() throws Exception {
        Product given = MockDataGenerationUtil.createProduct();

        Mockito.when(productService.getProductById(1L)).thenReturn(given);

        mvc.perform(
                        MockMvcRequestBuilders.get("/product/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturnNotFoundWhenProductIdProvided() throws Exception {
        Mockito.when(productService.getProductById(1L)).thenThrow(ProductNotFoundException.class);

        mvc.perform(
                        MockMvcRequestBuilders.get("/product/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturnOkWhenProductCreationIsSuccessful() throws Exception {
        Product given = MockDataGenerationUtil.createProduct();

        Mockito.when(productService.addProduct(given)).thenReturn(given);

        mvc.perform(
                        MockMvcRequestBuilders.post("/product")
                                .content(objectMapper.writeValueAsString(given))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void shouldReturnBadRequestWhenNameIsEmptyOnCreation() throws Exception {
        Product given = new Product();
        given.setPrice(BigDecimal.valueOf(10.0));
        given.setStock(3L);

        mvc.perform(
                        MockMvcRequestBuilders.post("/product")
                                .content(objectMapper.writeValueAsString(given))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(productService, Mockito.never()).addProduct(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenPriceIsEmptyOnCreation() throws Exception {
        Product given = new Product();
        given.setName("Name");
        given.setStock(3L);

        mvc.perform(
                        MockMvcRequestBuilders.post("/product")
                                .content(objectMapper.writeValueAsString(given))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(productService, Mockito.never()).addProduct(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenStockIsEmptyOnCreation() throws Exception {
        Product given = new Product();
        given.setName("Name");
        given.setPrice(BigDecimal.valueOf(10.0));

        mvc.perform(
                        MockMvcRequestBuilders.post("/product")
                                .content(objectMapper.writeValueAsString(given))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(productService, Mockito.never()).addProduct(Mockito.any());
    }

    @Test
    public void shouldReturnOkWhenProductUpdateIsSuccessful() throws Exception {
        Product given = MockDataGenerationUtil.createProduct();

        Mockito.when(productService.updateProduct(1L, given)).thenReturn(given);

        mvc.perform(
                        MockMvcRequestBuilders.post("/product/1")
                                .content(objectMapper.writeValueAsString(given))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturnNotFoundWhenIdNotFoundOnUpdate() throws Exception {
        Product given = MockDataGenerationUtil.createProduct();

        Mockito.when(productService.updateProduct(Mockito.any(), Mockito.any()))
                .thenThrow(ProductNotFoundException.class);

        mvc.perform(
                        MockMvcRequestBuilders.post("/product/1")
                                .content(objectMapper.writeValueAsString(given))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundWhenIdNotFoundOnDelete() throws Exception {
        Mockito.doThrow(ProductNotFoundException.class).when(productService).deleteProduct(1L);

        mvc.perform(
                        MockMvcRequestBuilders.delete("/product/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
