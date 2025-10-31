package com.mano.Farafina_Backend.controller;

import com.mano.Farafina_Backend.entity.Product;
import com.mano.Farafina_Backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ---------------- CREATE PRODUCT ----------------
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            System.out.println("=== PRODUCT CREATION DEBUG ===");
            System.out.println("Product Name: " + product.getProductName());
            System.out.println("User ID: " + product.getUserId());
            System.out.println("Category: " + product.getCategory());
            System.out.println("Condition: " + product.getCondition());
            System.out.println("Price: " + product.getPrice());
            System.out.println("Country: " + product.getCountry());
            System.out.println("Images received: " + (product.getImages() != null ? product.getImages().size() : "NULL"));
            System.out.println("Video URL: " + product.getVideoUrl());

            if (product.getImages() == null) {
                product.setImages(new ArrayList<>());
            }

            Product savedProduct = productService.createProduct(product);

            System.out.println("Product saved with ID: " + savedProduct.getId());
            System.out.println("Images in saved product: " +
                    (savedProduct.getImages() != null ? savedProduct.getImages().size() : "NULL"));
            System.out.println("=== END DEBUG ===");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product created successfully");
            response.put("product", savedProduct);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to create product", "details", e.getMessage()));
        }
    }

    // ---------------- GET ALL PRODUCTS / SEARCH ----------------
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String search
    ) {
        try {
            List<Product> products;
            if (category != null || country != null || city != null || condition != null || search != null) {
                products = productService.searchProducts(category, country, city, condition, search);
            } else {
                products = productService.getAllProducts();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("products", products);
            response.put("count", products.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to fetch products"));
        }
    }

    // ---------------- GET PRODUCT BY ID ----------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            return productService.getProductById(id)
                    .map(product -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("product", product);
                        return ResponseEntity.ok(response);
                    })
                    .orElse(ResponseEntity.status(404)
                            .body(Map.of("success", false, "error", "Product not found")));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to fetch product"));
        }
    }

    // ---------------- GET PRODUCTS BY USER ----------------
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getProductsByUserId(@PathVariable Long userId) {
        try {
            List<Product> products = productService.getProductsByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("products", products);
            response.put("count", products.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to fetch products"));
        }
    }

    // ---------------- UPDATE PRODUCT ----------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        try {
            System.out.println("=== PRODUCT UPDATE DEBUG ===");
            System.out.println("Updating product ID: " + id);
            System.out.println("New data received: " + productDetails.getProductName());

            Product updatedProduct = productService.updateProduct(id, productDetails);

            System.out.println("Product updated successfully");
            System.out.println("Updated at: " + updatedProduct.getUpdatedAt());
            System.out.println("=== END UPDATE DEBUG ===");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product updated successfully");
            response.put("product", updatedProduct);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to update product"));
        }
    }

    // ---------------- DELETE PRODUCT ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to delete product"));
        }
    }

    // ---------------- GET STATISTICS ----------------
    @GetMapping("/stats")
    public ResponseEntity<?> getStatistics() {
        try {
            Map<String, Object> stats = productService.getStatistics();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to fetch statistics"));
        }
    }

    // ---------------- GET PRODUCTS BY CATEGORY ----------------
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable String category) {
        try {
            List<Product> products = productService.getProductsByCategory(category);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("products", products);
            response.put("count", products.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to fetch products"));
        }
    }

    // ---------------- GET PRODUCTS BY CONDITION ----------------
    @GetMapping("/condition/{condition}")
    public ResponseEntity<?> getProductsByCondition(@PathVariable String condition) {
        try {
            List<Product> products = productService.getProductsByCondition(condition);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("products", products);
            response.put("count", products.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to fetch products"));
        }
    }

    // ---------------- GET LATEST PRODUCTS ----------------
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestProducts(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Product> products = productService.getLatestProducts(limit);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("products", products);
            response.put("count", products.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to fetch products"));
        }
    }

    // ---------------- GET RECENTLY UPDATED PRODUCTS ----------------
    @GetMapping("/recently-updated")
    public ResponseEntity<?> getRecentlyUpdatedProducts(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Product> products = productService.getRecentlyUpdatedProducts(limit);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("products", products);
            response.put("count", products.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to fetch products"));
        }
    }

    // ============ LIKES & COMMENTS ENDPOINTS ============

    // Get all likes for a product
    @GetMapping("/{productId}/likes")
    public ResponseEntity<?> getProductLikes(@PathVariable Long productId) {
        try {
            List<Map<String, Object>> likes = productService.getProductLikes(productId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("likes", likes);
            response.put("count", likes.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Get all comments for a product
    @GetMapping("/{productId}/comments")
    public ResponseEntity<?> getProductComments(@PathVariable Long productId) {
        try {
            List<Map<String, Object>> comments = productService.getProductComments(productId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("comments", comments);
            response.put("count", comments.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Add a like to a product
    @PostMapping("/{productId}/like")
    public ResponseEntity<?> likeProduct(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            boolean result = productService.likeProduct(productId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", result ? "Product liked successfully" : "Like already exists");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Remove a like from a product
    @DeleteMapping("/{productId}/like/{userId}")
    public ResponseEntity<?> unlikeProduct(
            @PathVariable Long productId,
            @PathVariable Long userId) {
        try {
            boolean result = productService.unlikeProduct(productId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", "Like removed successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Add a comment to a product
    @PostMapping("/{productId}/comment")
    public ResponseEntity<?> commentOnProduct(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String comment = request.get("comment").toString();

            Map<String, Object> result = productService.addComment(productId, userId, comment);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Comment added successfully");
            response.put("comment", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Delete a comment
    @DeleteMapping("/{productId}/comment/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long productId,
            @PathVariable Long commentId) {
        try {
            boolean result = productService.deleteComment(commentId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", "Comment deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Get interaction statistics for a product
    @GetMapping("/{productId}/stats")
    public ResponseEntity<?> getProductInteractionStats(@PathVariable Long productId) {
        try {
            Map<String, Object> stats = productService.getProductInteractionStats(productId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}