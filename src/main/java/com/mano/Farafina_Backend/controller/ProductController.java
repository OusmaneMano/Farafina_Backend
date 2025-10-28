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

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            // DEBUGGING: Log what we received
            System.out.println("=== PRODUCT CREATION DEBUG ===");
            System.out.println("Product Name: " + product.getProductName());
            System.out.println("User ID: " + product.getUserId());
            System.out.println("Category: " + product.getCategory());
            System.out.println("Price: " + product.getPrice());
            System.out.println("Images received: " + (product.getImages() != null ? product.getImages().size() : "NULL"));

            if (product.getImages() != null && !product.getImages().isEmpty()) {
                System.out.println("Image URLs:");
                for (int i = 0; i < product.getImages().size(); i++) {
                    System.out.println("  [" + i + "]: " + product.getImages().get(i));
                }
            } else {
                System.out.println("WARNING: No images in product object!");
            }

            System.out.println("Video URL: " + product.getVideoUrl());

            // Initialize images list if null to prevent issues
            if (product.getImages() == null) {
                System.out.println("Initializing empty images list");
                product.setImages(new ArrayList<>());
            }

            Product savedProduct = productService.createProduct(product);

            // DEBUGGING: Log what was saved
            System.out.println("--- After Save ---");
            System.out.println("Product saved with ID: " + savedProduct.getId());
            System.out.println("Images in saved product: " +
                    (savedProduct.getImages() != null ? savedProduct.getImages().size() : "NULL"));

            if (savedProduct.getImages() != null && !savedProduct.getImages().isEmpty()) {
                System.out.println("Saved image URLs:");
                for (int i = 0; i < savedProduct.getImages().size(); i++) {
                    System.out.println("  [" + i + "]: " + savedProduct.getImages().get(i));
                }
            }
            System.out.println("=== END DEBUG ===");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product created successfully");
            response.put("product", savedProduct);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("ERROR creating product: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to create product",
                            "details", e.getMessage()));
        }
    }

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

            if (category != null || country != null || city != null ||
                    condition != null || search != null) {
                products = productService.searchProducts(
                        category, country, city, condition, search);
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody Product productDetails
    ) {
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