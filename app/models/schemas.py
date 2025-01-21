from pydantic import BaseModel, HttpUrl
from typing import List, Optional

class ProductDTO(BaseModel):
    """商品数据传输对象"""
    title: str
    price: str
    image_url: Optional[str] = None
    product_url: str

class ProductRequest(BaseModel):
    """商品请求模型"""
    productUrl: str

class ProductResponse(BaseModel):
    """商品响应模型"""
    title: str
    price: str
    imageUrl: Optional[str] = None
    productUrl: str

    @classmethod
    def from_dto(cls, dto: ProductDTO, product_url: str):
        """从DTO创建响应对象"""
        return cls(
            title=dto.title,
            price=dto.price,
            imageUrl=dto.image_url,
            productUrl=product_url
        )

class ContentGenerationRequest(BaseModel):
    """
    文案生成请求对象。
    属性：
        product (ProductDTO): 商品信息。
        style (str): 文案风格。
        length (str): 文案长度。
        language (str): 文案语言。
    """
    product: ProductDTO
    style: str
    length: str
    language: str

class ContentGenerationResponse(BaseModel):
    """
    文案生成响应对象。
    属性：
        content (str): 生成的文案内容。
        product (ProductDTO): 对应的商品信息。
    """
    content: str
    product: ProductDTO

class GenerateMimicRequest(BaseModel):
    """
    模仿文案生成请求对象。
    属性：
        template (str): 模仿的模板。
        scene (str): 应用场景。
        length (str): 文案长度。
        product_info (Optional[ProductDTO]): 商品信息，可选。
    """
    template: str
    scene: str
    length: str
    product_info: Optional[ProductDTO]

class GenerateMimicResponse(BaseModel):
    """
    模仿文案生成响应对象。
    属性：
        content (str): 生成的模仿文案。
        word_count (int): 文案字数。
        sentiment (str): 文案情感。
        keywords (List[str]): 提取的关键词列表。
    """
    content: str
    word_count: int
    sentiment: str
    keywords: List[str]

class ApiResponse(BaseModel):
    """通用API响应模型"""
    success: bool
    data: Optional[dict] = None
    error: Optional[str] = None
    code: int = 200

    @classmethod
    def success(cls, data):
        """
        创建一个成功的API响应。
        参数：
            data: 响应的主要数据。
        返回：
            ApiResponse: 成功响应对象。
        """
        return cls(success=True, data=data)

    @classmethod
    def error(cls, code: int, message: str, details: str = None):
        """
        创建一个错误的API响应。
        参数：
            code (int): 错误状态码。
            message (str): 错误信息。
            details (str): 错误详情，可选。
        返回：
            ApiResponse: 错误响应对象。
        """
        return cls(success=False, code=code, error=message)