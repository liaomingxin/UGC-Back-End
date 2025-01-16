package com.ugc.backend.dto;

import com.ugc.backend.config.SelectorConfig;
import com.ugc.backend.config.SelectorConfig.SiteConfig;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.io.File;

@Component
public class ProductCrawler {
    private static final Logger logger = LoggerFactory.getLogger(ProductCrawler.class);
    private final SelectorConfig selectorConfig;

    public ProductCrawler(SelectorConfig selectorConfig) {
        this.selectorConfig = selectorConfig;
    }

    private SiteConfig getSiteConfig(String url) {
        String domain = URI.create(url).getHost().toLowerCase();
        return selectorConfig.getSelectors().entrySet().stream()
            .filter(entry -> entry.getValue().getDomain().stream()
                .anyMatch(domain::contains))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unsupported e-commerce platform: " + domain));
    }

    public ProductDTO crawlProductData(String url) {
        ProductDTO product = new ProductDTO();
        WebDriver driver = null;
        try {
            SiteConfig siteConfig = getSiteConfig(url);
            
            // 根据操作系统选择正确的驱动文件
            String driverFileName = System.getProperty("os.name").toLowerCase().contains("windows") 
                ? "chromedriver.exe" 
                : "chromedriver";
            
            String driverPath = getClass().getClassLoader()
                .getResource(driverFileName)
                .getPath();
            
            // 处理 URL 编码的路径（处理空格等特殊字符）
            driverPath = java.net.URLDecoder.decode(driverPath, "UTF-8");
            
            // 设置可执行权限（Linux系统需要）
            if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
                new File(driverPath).setExecutable(true);
            }
            
            System.setProperty("webdriver.chrome.driver", driverPath);

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            
            driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            driver.get(url);
            wait.until(webDriver -> ((ChromeDriver) webDriver)
                .executeScript("return document.readyState")
                .equals("complete"));

            // 获取标题
            try {
                WebElement titleElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(siteConfig.getTitle())));
                product.setTitle(titleElement.getText().trim());
            } catch (Exception e) {
                logger.warn("Failed to find title: {}", e.getMessage());
            }

            // 获取价格
            try {
                List<WebElement> elements;
                try {
                    // 首先尝试使用className
                    elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.className(siteConfig.getPrice().getClassName())));
                } catch (Exception e) {
                    // 如果失败，尝试使用cssSelector
                    elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector(siteConfig.getPrice().getClassName())));
                }

                for (WebElement element : elements) {
                    String text = element.getText().trim();
                    if (text.matches(siteConfig.getPrice().getPattern())) {
                        product.setPrice(text);
                        break;
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to find price: {}", e.getMessage());
            }

            // 获取图片
            try {
                WebElement imgElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(siteConfig.getImage())));
                
                String imageUrl = imgElement.getAttribute("src");
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = imgElement.getAttribute("data-src");
                }
                product.setImageUrl(imageUrl);
            } catch (Exception e) {
                logger.warn("Failed to find image: {}", e.getMessage());
            }

            product.setProductUrl(url);
            return product;

        } catch (Exception e) {
            logger.error("Error crawling product data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to crawl product data: " + e.getMessage(), e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
} 