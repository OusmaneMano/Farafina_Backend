package com.mano.Farafina_Backend.services;

import com.mano.Farafina_Backend.entity.Product;
import com.mano.Farafina_Backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    // ============ LIKES & COMMENTS METHODS ============

    // Get all likes for a product
    public List<Map<String, Object>> getProductLikes(Long productId) {
        String sql = "SELECT pl.id, pl.product_id, pl.user_id, pl.liked_at, " +
                "u.username, u.email " +
                "FROM product_likes pl " +
                "JOIN users u ON pl.user_id = u.id " +
                "WHERE pl.product_id = ? " +
                "ORDER BY pl.liked_at DESC";

        return jdbcTemplate.queryForList(sql, productId);
    }

    // Get all comments for a product
    public List<Map<String, Object>> getProductComments(Long productId) {
        String sql = "SELECT pc.id, pc.product_id, pc.user_id, pc.comment, " +
                "pc.commented_at, pc.updated_at, u.username, u.email " +
                "FROM product_comments pc " +
                "JOIN users u ON pc.user_id = u.id " +
                "WHERE pc.product_id = ? " +
                "ORDER BY pc.commented_at DESC";

        return jdbcTemplate.queryForList(sql, productId);
    }

    // Add a like to a product
    public boolean likeProduct(Long productId, Long userId) {
        try {
            // Check if like already exists
            String checkSql = "SELECT COUNT(*) FROM product_likes WHERE product_id = ? AND user_id = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, productId, userId);

            if (count != null && count > 0) {
                return false; // Already liked
            }

            // Add like
            String sql = "INSERT INTO product_likes (product_id, user_id, liked_at) VALUES (?, ?, NOW())";
            jdbcTemplate.update(sql, productId, userId);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Remove a like from a product
    public boolean unlikeProduct(Long productId, Long userId) {
        try {
            String sql = "DELETE FROM product_likes WHERE product_id = ? AND user_id = ?";
            int rows = jdbcTemplate.update(sql, productId, userId);
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add a comment to a product
    public Map<String, Object> addComment(Long productId, Long userId, String comment) {
        try {
            String sql = "INSERT INTO product_comments (product_id, user_id, comment, commented_at) " +
                    "VALUES (?, ?, ?, NOW())";
            jdbcTemplate.update(sql, productId, userId, comment);

            // Get the inserted comment
            String selectSql = "SELECT pc.id, pc.product_id, pc.user_id, pc.comment, " +
                    "pc.commented_at, pc.updated_at, u.username, u.email " +
                    "FROM product_comments pc " +
                    "JOIN users u ON pc.user_id = u.id " +
                    "WHERE pc.product_id = ? AND pc.user_id = ? " +
                    "ORDER BY pc.commented_at DESC LIMIT 1";

            return jdbcTemplate.queryForMap(selectSql, productId, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // Delete a comment
    public boolean deleteComment(Long commentId) {
        try {
            String sql = "DELETE FROM product_comments WHERE id = ?";
            int rows = jdbcTemplate.update(sql, commentId);
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get interaction statistics for a product
    public Map<String, Object> getProductInteractionStats(Long productId) {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Count likes
            String likesSql = "SELECT COUNT(*) FROM product_likes WHERE product_id = ?";
            Integer likesCount = jdbcTemplate.queryForObject(likesSql, Integer.class, productId);
            stats.put("likesCount", likesCount != null ? likesCount : 0);

            // Count comments
            String commentsSql = "SELECT COUNT(*) FROM product_comments WHERE product_id = ?";
            Integer commentsCount = jdbcTemplate.queryForObject(commentsSql, Integer.class, productId);
            stats.put("commentsCount", commentsCount != null ? commentsCount : 0);

            // Get recent interactions
            String recentSql = "SELECT 'like' as type, liked_at as timestamp FROM product_likes WHERE product_id = ? " +
                    "UNION ALL " +
                    "SELECT 'comment' as type, commented_at as timestamp FROM product_comments WHERE product_id = ? " +
                    "ORDER BY timestamp DESC LIMIT 5";
            List<Map<String, Object>> recentActivity = jdbcTemplate.queryForList(recentSql, productId, productId);
            stats.put("recentActivity", recentActivity);
        } catch (Exception e) {
            e.printStackTrace();
            stats.put("likesCount", 0);
            stats.put("commentsCount", 0);
            stats.put("recentActivity", new ArrayList<>());
        }

        return stats;
    }
}