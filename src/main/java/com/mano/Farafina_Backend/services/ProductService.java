package com.mano.Farafina_Backend.services;

import com.mano.Farafina_Backend.entity.Product;
import com.mano.Farafina_Backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        // Ensure images list is initialized
        if (product.getImages() == null) {
            product.setImages(new ArrayList<>());
        }

        System.out.println("ProductService: Saving product with " + product.getImages().size() + " images");

        // Save the product
        Product savedProduct = productRepository.save(product);

        // Force flush to ensure images are persisted immediately
        productRepository.flush();

        System.out.println("ProductService: Product saved with ID " + savedProduct.getId());
        System.out.println("ProductService: Images count after save: " + savedProduct.getImages().size());

        return savedProduct;
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

        // Update images - clear old ones and add new ones
        if (productDetails.getImages() != null) {
            product.getImages().clear();
            product.getImages().addAll(productDetails.getImages());
        }

        product.setVideoUrl(productDetails.getVideoUrl());

        Product updated = productRepository.save(product);
        productRepository.flush();

        return updated;
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}