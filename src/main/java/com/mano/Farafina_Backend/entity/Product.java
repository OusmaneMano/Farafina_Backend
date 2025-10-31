package com.mano.Farafina_Backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_country", columnList = "country"),
        @Index(name = "idx_condition_col", columnList = "product_condition")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    @JsonProperty("userId")
    private Long userId;

    @Column(name = "product_name", nullable = false, length = 255)
    @JsonProperty("productName")
    private String productName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "product_condition", nullable = false, length = 50)
    @JsonProperty("condition")
    private String condition;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "shop_name", length = 255)
    @JsonProperty("shopName")
    private String shopName;

    @Column(name = "contact_phone", length = 50)
    @JsonProperty("contactPhone")
    private String contactPhone;

    @Column(name = "quantity")
    private Integer quantity = 1;

    @Column(name = "shipping_available")
    @JsonProperty("shippingAvailable")
    private Boolean shippingAvailable = false;

    @Column(name = "local_pickup")
    @JsonProperty("localPickup")
    private Boolean localPickup = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "product_images",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "image_url", length = 500)
    @OrderColumn(name = "image_order")
    private List<String> images = new ArrayList<>();

    @Column(name = "video_url", length = 500)
    @JsonProperty("videoUrl")
    private String videoUrl;

    // ============ LIKES & COMMENTS COUNT FIELDS ============
    @Column(name = "likes_count")
    @JsonProperty("likesCount")
    private Integer likesCount = 0;

    @Column(name = "comments_count")
    @JsonProperty("commentsCount")
    private Integer commentsCount = 0;
    // ============ END LIKES & COMMENTS FIELDS ============

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    // Constructors
    public Product() {
        this.images = new ArrayList<>();
        this.likesCount = 0;
        this.commentsCount = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getShippingAvailable() {
        return shippingAvailable;
    }

    public void setShippingAvailable(Boolean shippingAvailable) {
        this.shippingAvailable = shippingAvailable;
    }

    public Boolean getLocalPickup() {
        return localPickup;
    }

    public void setLocalPickup(Boolean localPickup) {
        this.localPickup = localPickup;
    }

    public List<String> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    public void setImages(List<String> images) {
        if (images == null) {
            this.images = new ArrayList<>();
        } else {
            this.images = images;
        }
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    // ============ LIKES & COMMENTS GETTERS/SETTERS ============
    public Integer getLikesCount() {
        return likesCount != null ? likesCount : 0;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount != null ? likesCount : 0;
    }

    public Integer getCommentsCount() {
        return commentsCount != null ? commentsCount : 0;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount != null ? commentsCount : 0;
    }
    // ============ END LIKES & COMMENTS GETTERS/SETTERS ============

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}