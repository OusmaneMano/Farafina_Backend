package com.mano.Farafina_Backend.services;

import com.mano.Farafina_Backend.entity.Product;
import com.mano.Farafina_Backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByUserId(Long userId) {
        return productRepository.findByUserId(userId);
    }

    public List<Product> searchProducts(String category, String country,
                                        String city, String condition, String search) {
        return productRepository.findProductsWithFilters(
                category, country, city, condition, search);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setProductName(productDetails.getProductName());
        product.setDescription(productDetails.getDescription());
        product.setCategory(productDetails.getCategory());
        product.setCondition(productDetails.getCondition());
        product.setPrice(productDetails.getPrice());
        product.setCurrency(productDetails.getCurrency());
        product.setCountry(productDetails.getCountry());
        product.setCity(productDetails.getCity());
        product.setShopName(productDetails.getShopName());
        product.setContactPhone(productDetails.getContactPhone());
        product.setQuantity(productDetails.getQuantity());
        product.setShippingAvailable(productDetails.getShippingAvailable());
        product.setLocalPickup(productDetails.getLocalPickup());
        product.setImages(productDetails.getImages());
        product.setVideoUrl(productDetails.getVideoUrl());

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}