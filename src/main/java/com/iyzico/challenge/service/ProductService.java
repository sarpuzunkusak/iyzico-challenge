package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.ProductNotFoundException;
import com.iyzico.challenge.exception.ProductOutOfStockException;
import com.iyzico.challenge.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) throws ProductNotFoundException {
        Optional<Product> optional = productRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new ProductNotFoundException(String.format("Product cannot be found. id=%s", id));
        }
    }

    public Product addProduct(Product product) {
        Product saved = productRepository.save(product);
        logger.info("Product created. id={}", saved.getId());
        return saved;
    }

    public void deleteProduct(Long id) throws ProductNotFoundException {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            logger.info("Product deleted. id={}", id);
        } else {
            throw new ProductNotFoundException(String.format("Product cannot be found. id=%s", id));
        }
    }

    public Product updateProduct(Long id, Product product) throws ProductNotFoundException {
        Optional<Product> optional = productRepository.findById(id);

        if (optional.isPresent()) {
            Product existing = optional.get();
            existing.setName(product.getName());
            existing.setDescription(product.getDescription());
            existing.setPrice(product.getPrice());
            existing.setStock(product.getStock());

            Product saved = productRepository.save(existing);
            logger.info("Product updated. id={}", id);
            return saved;
        } else {
            throw new ProductNotFoundException(String.format("Product cannot be found. id=%s", id));
        }
    }

    public void increaseStock(Long id, Long amount) throws ProductNotFoundException {
        Optional<Product> optional = productRepository.findById(id);

        if (optional.isPresent()) {
            Product existing = optional.get();
            existing.setStock(existing.getStock() + amount);
            productRepository.save(existing);
        } else {
            throw new ProductNotFoundException(String.format("Product cannot be found. id=%s", id));
        }
    }

    public void reduceStock(Long id, Long amount)
            throws ProductNotFoundException, ProductOutOfStockException {
        Optional<Product> optional = productRepository.findById(id);

        if (optional.isPresent()) {
            Product existing = optional.get();
            if (existing.getStock() < amount) {
                throw new ProductOutOfStockException(
                        String.format(
                                "Product has not enough stock. id=%s, stock=%s",
                                id, existing.getStock()));
            }

            existing.setStock(existing.getStock() - amount);
            productRepository.save(existing);
        } else {
            throw new ProductNotFoundException(String.format("Product cannot be found. id=%s", id));
        }
    }
}
