package com.mano.Farafina_Backend.controller;

import com.mano.Farafina_Backend.entity.Product;
import com.mano.Farafina_Backend.services.ProductService;
import com.mano.Farafina_Backend.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;
    private final S3Service s3Service;

    @Autowired
    public ProductController(ProductService productService, S3Service s3Service) {
        this.productService = productService;
        this.s3Service = s3Service;
    }

    // CREATE PRODUCT WITH MEDIA UPLOAD
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createProduct(
            @RequestParam("userId") Long userId,
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("condition") String condition,
            @RequestParam("price") String price,
            @RequestParam("currency") String currency,
            @RequestParam("country") String country,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "shopName", required = false) String shopName,
            @RequestParam(value = "contactPhone", required = false) String contactPhone,
            @RequestParam(value = "quantity", required = false, defaultValue = "1") Integer quantity,
            @RequestParam(value = "shippingAvailable", required = false, defaultValue = "false") Boolean shippingAvailable,
            @RequestParam(value = "localPickup", required = false, defaultValue = "true") Boolean localPickup,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "video", required = false) MultipartFile video
    ) {
        try {
            Product product = new Product();
            product.setUserId(userId);
            product.setProductName(productName);
            product.setDescription(description);
            product.setCategory(category);
            product.setCondition(condition);
            product.setPrice(new BigDecimal(price));
            product.setCurrency(currency);
            product.setCountry(country);
            product.setCity(city);
            product.setShopName(shopName);
            product.setContactPhone(contactPhone);
            product.setQuantity(quantity);
            product.setShippingAvailable(shippingAvailable);
            product.setLocalPickup(localPickup);

            // Upload images to S3 and set URLs
            List<String> imageUrls = new ArrayList<>();
            if (images != null) {
                for (MultipartFile img : images) {
                    String url = s3Service.uploadImage(img);
                    imageUrls.add(url);
                }
            }
            product.setImages(imageUrls);

            // Upload video to S3 and set URL
            if (video != null) {
                String videoUrl = s3Service.uploadVideo(video);
                product.setVideoUrl(videoUrl);
            }

            Product savedProduct = productService.createProduct(product);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product created successfully");
            response.put("product", savedProduct);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "error", "Failed to create product", "details", e.getMessage())
            );
        }
    }

    // GET ALL PRODUCTS
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

    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            return productService.getProductById(id)
                    .map(product -> ResponseEntity.ok(Map.of("success", true, "product", product)))
                    .orElse(ResponseEntity.status(404).body(Map.of("success", false, "error", "Product not found")));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to fetch product"));
        }
    }

    // GET PRODUCTS BY USER ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getProductsByUserId(@PathVariable Long userId) {
        try {
            List<Product> products = productService.getProductsByUserId(userId);
            return ResponseEntity.ok(Map.of("success", true, "products", products));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to fetch products"));
        }
    }

    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody Product productDetails
    ) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(Map.of("success", true, "message", "Product updated successfully", "product", updatedProduct));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to update product"));
        }
    }

    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to delete product"));
        }
    }
}
