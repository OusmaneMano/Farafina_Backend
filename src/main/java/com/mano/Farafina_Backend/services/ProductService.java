package com.mano.Farafina_Backend.services;

import com.mano.Farafina_Backend.entity.Product;
import com.mano.Farafina_Backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ---------------- CREATE PRODUCT ----------------
    public Product createProduct(Product product) {
        // Set timestamps for new product
        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        return productRepository.save(product);
    }

    // ---------------- GET ALL PRODUCTS ----------------
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ---------------- GET PRODUCT BY ID ----------------
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // ---------------- GET PRODUCTS BY USER ----------------
    public List<Product> getProductsByUserId(Long userId) {
        return productRepository.findByUserId(userId);
    }

    // ---------------- GET PRODUCTS BY CATEGORY ----------------
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    // ---------------- GET PRODUCTS BY CONDITION ----------------
    public List<Product> getProductsByCondition(String condition) {
        return productRepository.findByCondition(condition);
    }

    // ---------------- SEARCH PRODUCTS ----------------
    public List<Product> searchProducts(String category, String country, String city,
                                        String condition, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return productRepository.searchByKeyword(search);
        }

        if (category != null && country != null && city != null) {
            return productRepository.findByCategoryAndCountryAndCity(category, country, city);
        }

        if (category != null && country != null) {
            return productRepository.findByCategoryAndCountry(category, country);
        }

        if (category != null) {
            return productRepository.findByCategory(category);
        }

        if (country != null && city != null) {
            return productRepository.findByCountryAndCity(country, city);
        }

        if (country != null) {
            return productRepository.findByCountry(country);
        }

        if (condition != null) {
            return productRepository.findByCondition(condition);
        }

        return productRepository.findAll();
    }

    // ---------------- UPDATE PRODUCT ----------------
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Update only non-null fields
        if (productDetails.getProductName() != null) {
            product.setProductName(productDetails.getProductName());
        }
        if (productDetails.getDescription() != null) {
            product.setDescription(productDetails.getDescription());
        }
        if (productDetails.getCategory() != null) {
            product.setCategory(productDetails.getCategory());
        }
        if (productDetails.getCondition() != null) {
            product.setCondition(productDetails.getCondition());
        }
        if (productDetails.getPrice() != null) {
            product.setPrice(productDetails.getPrice());
        }
        if (productDetails.getCurrency() != null) {
            product.setCurrency(productDetails.getCurrency());
        }
        if (productDetails.getCountry() != null) {
            product.setCountry(productDetails.getCountry());
        }
        if (productDetails.getCity() != null) {
            product.setCity(productDetails.getCity());
        }
        if (productDetails.getShopName() != null) {
            product.setShopName(productDetails.getShopName());
        }
        if (productDetails.getContactPhone() != null) {
            product.setContactPhone(productDetails.getContactPhone());
        }
        if (productDetails.getQuantity() != null) {
            product.setQuantity(productDetails.getQuantity());
        }
        if (productDetails.getImages() != null) {
            product.setImages(productDetails.getImages());
        }
        if (productDetails.getVideoUrl() != null) {
            product.setVideoUrl(productDetails.getVideoUrl());
        }

        // ✅ CRITICAL: Always update the updatedAt timestamp
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    // ---------------- DELETE PRODUCT ----------------
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // ---------------- GET STATISTICS ----------------
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total products
        long totalProducts = productRepository.count();
        stats.put("totalProducts", totalProducts);

        // Count by condition
        long newProducts = productRepository.countByCondition("New");
        long usedProducts = productRepository.countByCondition("Second Hand");
        stats.put("newProducts", newProducts);
        stats.put("usedProducts", usedProducts);

        // Count by category
        Map<String, Long> categoryStats = new HashMap<>();
        List<String> categories = Arrays.asList(
                "Electronics", "Clothing", "Shoes", "Agriculture/Elevage/Peche",
                "Sports", "Books", "Toys", "Automobile/Accessoires",
                "Moto/Accessoire", "Home & Garden", "Foods", "Beverages",
                "Quincaillerie", "House/Flats/Lands", "School Fournitures", "Jewelleries"
        );

        for (String category : categories) {
            long count = productRepository.countByCategory(category);
            if (count > 0) {
                categoryStats.put(category, count);
            }
        }
        stats.put("byCategory", categoryStats);

        // Top countries
        // Note: This would require a custom query, simplified version
        stats.put("topCountries", new HashMap<>());

        return stats;
    }

    // ---------------- GET LATEST PRODUCTS ----------------
    public List<Product> getLatestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findLatestProducts(pageable);
    }

    // ---------------- GET RECENTLY UPDATED PRODUCTS ----------------
    public List<Product> getRecentlyUpdatedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findRecentlyUpdated(pageable);
    }
}