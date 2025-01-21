from fastapi import APIRouter, HTTPException
from typing import Optional
from app.models.schemas import (
    ProductDTO,
    ContentGenerationRequest,
    ContentGenerationResponse,
    GenerateMimicRequest,
    GenerateMimicResponse,
    ApiResponse
)
from app.core.crawler import ProductCrawler
from app.core.ai_service import AIService
from app.utils.logger import setup_logger

logger = setup_logger()
router = APIRouter()

# 初始化服务
crawler = ProductCrawler()
ai_service = AIService()

@router.post("/crawl", response_model=ProductDTO)
async def crawl_product(product: ProductDTO):
    """爬取商品信息"""
    try:
        logger.info(f"Crawling product data from URL: {product.product_url}")
        product_data = await crawler.crawl_product_data(str(product.product_url))
        return product_data
    except Exception as e:
        logger.error(f"Error crawling product: {str(e)}")
        raise HTTPException(status_code=400, detail=str(e))

@router.post("/generate", response_model=ContentGenerationResponse)
async def generate_content(request: ContentGenerationRequest):
    """生成营销文案"""
    try:
        logger.info(f"Generating content for product: {request.product.title}")
        generated_content = await ai_service.generate_content(
            product=request.product,
            style=request.style,
            length=request.length,
            language=request.language
        )
        
        return ContentGenerationResponse(
            content=generated_content,
            product=request.product
        )
    except Exception as e:
        logger.error(f"Error generating content: {str(e)}")
        raise HTTPException(status_code=400, detail=str(e))

@router.post("/generate-mimic", response_model=ApiResponse)
async def generate_mimic_content(request: GenerateMimicRequest):
    """生成模仿文案"""
    try:
        # 输入验证
        if not request.template or not request.template.strip():
            return ApiResponse.error(
                code=400,
                message="Invalid template",
                details="Template cannot be empty"
            )
        
        if len(request.template) < 10:
            return ApiResponse.error(
                code=400,
                message="Template too short",
                details="Template must be at least 10 characters"
            )
        
        logger.info("Generating mimic content")
        response = await ai_service.generate_mimic_content(request)
        
        return ApiResponse.success(data=response.dict())
        
    except Exception as e:
        logger.error(f"Error generating mimic content: {str(e)}")
        return ApiResponse.error(
            code=500,
            message="Internal server error",
            details=str(e)
        )

# 健康检查接口
@router.get("/health")
async def health_check():
    """健康检查接口"""
    return {"status": "healthy"} 