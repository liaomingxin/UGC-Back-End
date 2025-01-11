package com.ugc.backend.crawler;

import com.ugc.backend.model.Product;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ProductCrawler extends WebCrawler {
    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");
    private Product product;
    private String targetUrl;

    public ProductCrawler(String url) {
        this.targetUrl = url;
        this.product = new Product();
        product.setProductUrl(url);
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        // 只访问目标URL和图片URL
        return href.equals(targetUrl.toLowerCase()) || IMAGE_EXTENSIONS.matcher(href).matches();
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        
        if (url.equals(targetUrl)) {
            if (page.getParseData() instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                String html = htmlParseData.getHtml();
                Document doc = Jsoup.parse(html);

                // 提取产品信息
                product.setTitle(extractTitle(doc));
                product.setPrice(extractPrice(doc));
                product.setDescription(extractDescription(doc));
                product.setImageUrl(extractImageUrl(doc));
            }
        }
    }

    private String extractTitle(Document doc) {
        return doc.select("h1").first().text();
    }

    private BigDecimal extractPrice(Document doc) {
        String priceText = doc.select(".price").first().text()
                .replaceAll("[^0-9.]", "");
        return new BigDecimal(priceText);
    }

    private String extractDescription(Document doc) {
        return doc.select(".product-description").first().text();
    }

    private String extractImageUrl(Document doc) {
        return doc.select(".product-image img").first().attr("src");
    }

    public Product getProduct() {
        return product;
    }
} 