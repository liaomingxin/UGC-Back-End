from pydantic import BaseModel, HttpUrl
from typing import List, Optional

class ProductDTO(BaseModel):
    """
    数据传输对象，表示商品信息。
    属性：
        title (str): 商品标题。
        price (str): 商品价格。
        image_url (Optional[HttpUrl]): 商品图片的URL，可选。
        product_url (HttpUrl): 商品页面的URL。
    """
    title: str
    price: str
    image_url: Optional[HttpUrl]
    product_url: HttpUrl

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
    """
    通用API响应对象。
    属性：
        code (int): 响应状态码。
        message (str): 响应消息。
        data (Optional[dict]): 返回的数据内容，可选。
        details (Optional[str]): 错误详情信息，可选。
    """
    code: int
    message: str
    data: Optional[dict] = None
    details: Optional[str] = None

    @classmethod
    def success(cls, data):
        """
        创建一个成功的API响应。
        参数：
            data: 响应的主要数据。
        返回：
            ApiResponse: 成功响应对象。
        """
        return cls(code=200, message="success", data=data)

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
        return cls(code=code, message=message, details=details)