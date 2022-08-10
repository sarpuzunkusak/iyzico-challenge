package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.ProductNotFoundException;
import com.iyzico.challenge.exception.ProductOutOfStockException;
import com.iyzico.challenge.repository.ProductRepository;
import com.iyzico.challenge.utility.MockDataGenerationUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceITest {

    public static final long SOME_ID = 1L;
    public static final long SOME_QUANTITY = 2L;
    public static final String NEW_DESCRIPTION = "new description";

    @Autowired private ProductRepository productRepository;

    @Autowired private ProductService productService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void shutdown() {
        productRepository.deleteAll();
    }

    @Test
    public void shouldListAllWhenAnyProductExists() {
        Product product = MockDataGenerationUtil.createProduct();
        Product existing = productRepository.save(product);

        List<Product> products = productService.listProducts();

        Assert.assertFalse(products.isEmpty());
        Assert.assertEquals(existing.getId(), products.get(0).getId());
    }

    @Test
    public void shouldListEmptyWhenNoProductExists() {
        List<Product> products = productService.listProducts();

        Assert.assertNotNull(products);
        Assert.assertTrue(products.isEmpty());
    }

    @Test
    public void shouldReturnSpecificProductWhenIdIsProvided() throws ProductNotFoundException {
        Product product = MockDataGenerationUtil.createProduct();
        Product existing = productRepository.save(product);

        Product result = productService.getProductById(existing.getId());

        Assert.assertNotNull(result);
        Assert.assertEquals(existing.getId(), result.getId());
    }

    @Test(expected = ProductNotFoundException.class)
    public void shouldThrowNotFoundExceptionWhenNoProductForIdIsFound()
            throws ProductNotFoundException {
        Product result = productService.getProductById(SOME_ID);

        Assert.assertNull(result);
    }

    @Test
    public void shouldInsertNewRecordWhenCalled() {
        Product product = MockDataGenerationUtil.createProduct();

        Product result = productService.addProduct(product);

        Assert.assertNotNull(result);
        Assert.assertEquals(product.getName(), result.getName());
        Assert.assertEquals(product.getDescription(), result.getDescription());
        Assert.assertEquals(product.getStock(), result.getStock());
        Assert.assertEquals(0, product.getPrice().compareTo(result.getPrice()));
    }

    @Test
    public void shouldRemoveRecordWhenProductWithIdExists() throws ProductNotFoundException {
        Product product = MockDataGenerationUtil.createProduct();
        Product existing = productRepository.save(product);

        productService.deleteProduct(existing.getId());

        Assert.assertFalse(productRepository.findById(existing.getId()).isPresent());
        Assert.assertTrue(productRepository.findAll().isEmpty());
    }

    @Test(expected = ProductNotFoundException.class)
    public void shouldThrowNotFoundExceptionWhenNoProductForIdIsFoundForDeletion()
            throws ProductNotFoundException {
        productService.deleteProduct(SOME_ID);

        Assert.assertTrue(productRepository.findAll().isEmpty());
    }

    @Test
    public void shouldUpdateRecordWhenCalled() throws ProductNotFoundException {
        Product product = MockDataGenerationUtil.createProduct();
        Product existing = productRepository.save(product);
        product.setDescription(NEW_DESCRIPTION);

        Product result = productService.updateProduct(existing.getId(), product);

        Assert.assertEquals(product.getName(), result.getName());
        Assert.assertEquals(product.getDescription(), result.getDescription());
        Assert.assertEquals(product.getStock(), result.getStock());
        Assert.assertEquals(0, product.getPrice().compareTo(result.getPrice()));
    }

    @Test(expected = ProductNotFoundException.class)
    public void shouldThrowNotFoundExceptionWhenNoProductForIdIsFoundForUpdate()
            throws ProductNotFoundException {
        Product product = MockDataGenerationUtil.createProduct();
        product.setDescription(NEW_DESCRIPTION);

        productService.updateProduct(SOME_ID, product);

        Assert.assertTrue(productRepository.findAll().isEmpty());
    }

    @Test
    public void shouldIncreaseStockWhenCalled() throws ProductNotFoundException {
        Product product = MockDataGenerationUtil.createProduct();
        Product existing = productRepository.save(product);

        productService.increaseStock(existing.getId(), SOME_QUANTITY);

        Assert.assertTrue(productRepository.findById(existing.getId()).isPresent());
        Assert.assertEquals(
                existing.getStock() + SOME_QUANTITY,
                productRepository.findById(existing.getId()).get().getStock().longValue());
    }

    @Test(expected = ProductNotFoundException.class)
    public void shouldThrowNotFoundExceptionWhenNoProductForIdIsFoundToIncrease()
            throws ProductNotFoundException {
        productService.increaseStock(SOME_ID, SOME_QUANTITY);

        Assert.assertTrue(productRepository.findAll().isEmpty());
    }

    @Test
    public void shouldDecreaseStockWhenCalled()
            throws ProductNotFoundException, ProductOutOfStockException {
        Product product = MockDataGenerationUtil.createProduct();
        Product existing = productRepository.save(product);

        productService.reduceStock(existing.getId(), SOME_QUANTITY);

        Assert.assertTrue(productRepository.findById(existing.getId()).isPresent());
        Assert.assertEquals(
                existing.getStock() - SOME_QUANTITY,
                productRepository.findById(existing.getId()).get().getStock().longValue());
    }

    @Test(expected = ProductNotFoundException.class)
    public void shouldThrowNotFoundExceptionWhenNoProductForIdIsFoundToDecrease()
            throws ProductNotFoundException, ProductOutOfStockException {
        productService.reduceStock(SOME_ID, SOME_QUANTITY);

        Assert.assertTrue(productRepository.findAll().isEmpty());
    }

    @Test(expected = ProductOutOfStockException.class)
    public void shouldThrowOutOfStockExceptionWhenThereIsNotEnoughStock()
            throws ProductNotFoundException, ProductOutOfStockException {
        Product product = MockDataGenerationUtil.createProduct();
        product.setStock(1L);
        Product existing = productRepository.save(product);

        productService.reduceStock(existing.getId(), SOME_QUANTITY);

        Assert.assertTrue(productRepository.findById(existing.getId()).isPresent());
        Assert.assertEquals(
                existing.getStock(), productRepository.findById(existing.getId()).get().getStock());
    }
}
