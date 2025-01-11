package com.ugc.backend.repository;

import com.ugc.backend.model.GeneratedContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GeneratedContentRepository extends JpaRepository<GeneratedContent, Long> {
    List<GeneratedContent> findByProductId(Long productId);
    List<GeneratedContent> findByProductIdAndStyle(Long productId, String style);
} 