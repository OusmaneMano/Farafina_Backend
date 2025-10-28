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
            System.out.println("Price: " + product.getPrice());
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
            Product updatedProduct = productService.updateProduct(id, productDetails);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product updated successfully");
            response.put("product", updatedProduct);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
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
}
