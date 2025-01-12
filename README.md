# UGC Content Generator Backend

这是一个基于Spring Boot的电商内容生成后端服务，支持从多个电商平台抓取商品信息并利用AI生成营销文案。

## 功能特点

- 支持多平台商品信息抓取（目前支持淘宝/天猫、京东）
- 基于配置的爬虫选择器系统，易于扩展新平台
- 使用Selenium进行动态页面抓取
- 集成OpenAI API生成营销文案
- RESTful API接口设计
- 支持跨域请求

## 技术栈

- Spring Boot 2.7.0
- Selenium 4.16.1
- OpenAI GPT-3.5
- Lombok
- Chrome WebDriver

## 项目结构
src/main/java/com/ugc/backend/
├── config/ # 配置类
│ ├── RestTemplateConfig.java
│ └── SelectorConfig.java
├── controller/ # 控制器
│ └── ContentController.java
├── dto/ # 数据传输对象
│ ├── ContentGenerationRequest.java
│ ├── ContentGenerationResponse.java
│ ├── ProductCrawler.java
│ └── ProductDTO.java
├── service/ # 服务层
│ ├── AIService.java
│ └── impl/
│ └── AIServiceImpl.java
└── UGCBackEndApplication.java


## 配置文件

### application.properties
配置端口、OpenAI API-key、API-base-URL

### selectors.yml
用于配置不同电商网站的selector样式

## API接口

### 生成营销内容

```http
POST /api/content/generate
```

请求体：
```json
{
    "productUrl": "商品链接",
    "style": "营销风格",
    "length": "文案长度",
    "language": "生成语言"
}
```

响应体：
```json
{
    "content": "生成的营销文案"
}
```

## 使用说明

1. 确保系统已安装Chrome浏览器
2. 下载与Chrome版本匹配的ChromeDriver
3. 在application.properties中配置OpenAI API密钥
4. 在selectors.yml中配置电商平台的选择器
5. 运行UGCBackEndApplication.java启动服务

## 扩展支持新平台

要添加新的电商平台支持，只需在selectors.yml中添加相应配置：

```yaml
selectors:
  new-platform:
    domain: ["example.com"]
    title: "标题选择器"
    price:
      className: "价格类名" // 模糊，选取固定类名
      pattern: "价格匹配模式" // 可选
    image: "图片选择器" // 模糊，选取固定类名
```

## 注意事项

- 确保ChromeDriver版本与Chrome浏览器版本匹配
- 配置文件中的选择器需要根据目标网站的实际DOM结构调整
- 建议在生产环境中使用无头模式运行Chrome
- 注意遵守目标网站的爬虫政策和使用规范

## License

This project is licensed under the Apache License 2.0