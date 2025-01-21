from pydantic import BaseModel, HttpUrl
from typing import List, Optional

class ProductDTO(BaseModel):
    title: str
    price: str
    image_url: Optional[HttpUrl]
    product_url: HttpUrl

class ContentGenerationRequest(BaseModel):
    product: ProductDTO
    style: str
    length: str
    language: str

class ContentGenerationResponse(BaseModel):
    content: str
    product: ProductDTO

class GenerateMimicRequest(BaseModel):
    template: str
    scene: str
    length: str
    product_info: Optional[ProductDTO]

class GenerateMimicResponse(BaseModel):
    content: str
    word_count: int
    sentiment: str
    keywords: List[str]

class ApiResponse(BaseModel):
    code: int
    message: str
    data: Optional[dict] = None
    details: Optional[str] = None

    @classmethod
    def success(cls, data):
        return cls(code=200, message="success", data=data)

    @classmethod
    def error(cls, code: int, message: str, details: str = None):
        return cls(code=code, message=message, details=details) 