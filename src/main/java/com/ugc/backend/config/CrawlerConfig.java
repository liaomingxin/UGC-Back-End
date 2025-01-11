package com.ugc.backend.config;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CrawlerConfig {
    
    @Bean
    public CrawlController crawlController() throws Exception {
        String crawlStorageFolder = "./crawler";
        int numberOfCrawlers = 1;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(1);  // 只爬取目标页面
        config.setMaxPagesToFetch(1);     // 只爬取一个页面
        config.setIncludeBinaryContentInCrawling(true); // 允许爬取图片

        // 初始化爬虫控制器
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        
        return new CrawlController(config, pageFetcher, robotstxtServer);
    }
} 