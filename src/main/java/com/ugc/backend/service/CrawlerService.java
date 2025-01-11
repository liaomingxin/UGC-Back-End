package com.ugc.backend.service;

import com.ugc.backend.model.Product;

public interface CrawlerService {
    Product crawlProductData(String productUrl);
    void updateProductData(String productUrl);
} 