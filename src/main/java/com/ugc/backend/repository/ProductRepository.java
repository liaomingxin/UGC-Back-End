package com.ugc.backend.repository;

import com.ugc.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductUrl(String productUrl);
    boolean existsByProductUrl(String productUrl);
} 