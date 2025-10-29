package com.mano.Farafina_Backend.repository;

import com.mano.Farafina_Backend.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ---------------- FIND BY USER ----------------
    List<Product> findByUserId(Long userId);

    // ---------------- FIND BY CATEGORY ----------------
    List<Product> findByCategory(String category);

    // ---------------- FIND BY COUNTRY ----------------
    List<Product> findByCountry(String country);

    // ---------------- FIND BY CONDITION ----------------
    List<Product> findByCondition(String condition);

    // ---------------- COMBINED FILTERS ----------------
    List<Product> findByCategoryAndCountry(String category, String country);

    List<Product> findByCountryAndCity(String country, String city);

    List<Product> findByCategoryAndCountryAndCity(String category, String country, String city);

    // ---------------- SEARCH BY KEYWORD ----------------
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.shopName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    // ---------------- COUNT METHODS FOR STATISTICS ----------------

    // Count by condition
    long countByCondition(String condition);

    // Count by category
    long countByCategory(String category);

    // Count by user
    long countByUserId(Long userId);

    // Count by country
    long countByCountry(String country);

    // ---------------- GET LATEST PRODUCTS ----------------
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findLatestProducts(Pageable pageable);

    // ---------------- GET RECENTLY UPDATED PRODUCTS ----------------
    @Query("SELECT p FROM Product p ORDER BY p.updatedAt DESC")
    List<Product> findRecentlyUpdated(Pageable pageable);

    // ---------------- GET PRODUCTS WITH VIDEOS ----------------
    @Query("SELECT p FROM Product p WHERE p.videoUrl IS NOT NULL")
    List<Product> findProductsWithVideo();

    // ---------------- GET PRODUCTS BY PRICE RANGE ----------------
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
}