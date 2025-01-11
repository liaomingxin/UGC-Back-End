package com.ugc.backend.service.impl;

import com.ugc.backend.crawler.ProductCrawler;
import com.ugc.backend.model.Product;
import com.ugc.backend.repository.ProductRepository;
import com.ugc.backend.service.CrawlerService;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CrawlerServiceImpl implements CrawlerService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CrawlController crawlController;
    
    @Override
    public Product crawlProductData(String productUrl) {
        try {
            // 设置爬虫种子URL
            crawlController.addSeed(productUrl);
            
            // 创建爬虫工厂
            CrawlController.WebCrawlerFactory<ProductCrawler> factory = () -> new ProductCrawler(productUrl);
            
            // 启动爬虫
            crawlController.start(factory, 1);
            
            // 等待爬虫完成
            crawlController.waitUntilFinish();
            
            // 获取爬取结果
            ProductCrawler crawler = factory.newInstance();
            Product product = crawler.getProduct();
            
            // 保存到数据库
            return productRepository.save(product);
        } catch (Exception e) {
            log.error("Error crawling data from URL: " + productUrl, e);
            throw new RuntimeException("Failed to crawl product data", e);
        } finally {
            crawlController.shutdown();
        }
    }
    
    @Override
    public void updateProductData(String productUrl) {
        Product product = crawlProductData(productUrl);
        productRepository.save(product);
    }
} 