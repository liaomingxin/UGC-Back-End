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

@Component
public class ProductCrawler {
    private static final Logger logger = LoggerFactory.getLogger(ProductCrawler.class);
    private static final int MAX_RETRIES = 2;  // 最大重试次数
    private static final int TIMEOUT_SECONDS = 10;  // 总超时时间
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
        int retryCount = 0;
        while (retryCount <= MAX_RETRIES) {
            WebDriver driver = null;
            try {
                ProductDTO product = new ProductDTO();
                SiteConfig siteConfig = getSiteConfig(url);

                // 设置Chrome浏览器驱动
                String os = System.getProperty("os.name").toLowerCase();
                String driverPath = os.contains("win") ? 
                    "src/main/resources/chromedriver.exe" : 
                    "/usr/local/bin/chromedriver";
                System.setProperty("webdriver.chrome.driver", driverPath);

                ChromeOptions options = new ChromeOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("--headless");
                options.addArguments("--disable-gpu");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-extensions");

                driver = new ChromeDriver(options);
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TIMEOUT_SECONDS));
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5)); // 减少单次等待时间

                driver.get(url);

                // 使用CompletableFuture实现超时控制
                product.setProductUrl(url);
                
                // 爬取标题
                crawlTitle(driver, wait, siteConfig, product);
                
                // 爬取价格
                crawlPrice(driver, wait, siteConfig, product);
                
                // 爬取图片
                crawlImage(driver, wait, siteConfig, product);

                // 检查是否获取到了基本数据
                if (isProductDataValid(product)) {
                    return product;
                }
                
                // 如果数据不完整，抛出异常进行重试
                throw new RuntimeException("Incomplete product data");

            } catch (Exception e) {
                logger.warn("Attempt {} failed: {}", retryCount + 1, e.getMessage());
                retryCount++;
                
                if (retryCount > MAX_RETRIES) {
                    logger.error("All retry attempts failed for URL: {}", url);
                    // 返回空的ProductDTO而不是抛出异常
                    return new ProductDTO();
                }
                
                // 在重试之间添加短暂延迟
                try {
                    Thread.sleep(1000 * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } finally {
                if (driver != null) {
                    try {
                        driver.quit();
                    } catch (Exception e) {
                        logger.warn("Error closing driver: {}", e.getMessage());
                    }
                }
            }
        }
        
        return new ProductDTO(); // 如果所有重试都失败，返回空对象
    }

    private void crawlTitle(WebDriver driver, WebDriverWait wait, SiteConfig siteConfig, ProductDTO product) {
        try {
            WebElement titleElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(siteConfig.getTitle())));
            product.setTitle(titleElement.getText().trim());
        } catch (Exception e) {
            logger.warn("Failed to find title");
        }
    }

    private void crawlPrice(WebDriver driver, WebDriverWait wait, SiteConfig siteConfig, ProductDTO product) {
        try {
            List<WebElement> elements;
            try {
                elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.className(siteConfig.getPrice().getClassName())));
            } catch (Exception e) {
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
            logger.warn("Failed to find price");
        }
    }

    private void crawlImage(WebDriver driver, WebDriverWait wait, SiteConfig siteConfig, ProductDTO product) {
        try {
            WebElement imgElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(siteConfig.getImage())));
            
            String imageUrl = imgElement.getAttribute("src");
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = imgElement.getAttribute("data-src");
            }
            product.setImageUrl(imageUrl);
        } catch (Exception e) {
            logger.warn("Failed to find image");
        }
    }

    private boolean isProductDataValid(ProductDTO product) {
        // 检查是否至少获取到了标题或价格
        return (product.getTitle() != null && !product.getTitle().isEmpty()) ||
               (product.getPrice() != null && !product.getPrice().isEmpty());
    }
} 