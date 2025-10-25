package com.mano.Farafina_Backend.repository;

import com.mano.Farafina_Backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByUserId(Long userId);

    List<Product> findByCategory(String category);

    List<Product> findByCountry(String country);

    List<Product> findByCondition(String condition);

    @Query("SELECT p FROM Product p WHERE " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:country IS NULL OR p.country = :country) AND " +
            "(:city IS NULL OR p.city = :city) AND " +
            "(:condition IS NULL OR p.condition = :condition) AND " +
            "(:search IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY p.createdAt DESC")
    List<Product> findProductsWithFilters(
            @Param("category") String category,
            @Param("country") String country,
            @Param("city") String city,
            @Param("condition") String condition,
            @Param("search") String search
    );
}