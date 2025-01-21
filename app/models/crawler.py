from pydantic import BaseModel
from typing import List, Optional, Dict

class PriceSelector(BaseModel):
    class_name: str
    pattern: Optional[str] = None

class SiteConfig(BaseModel):
    domain: List[str]
    title: str
    price: PriceSelector
    image: str

class SelectorsConfig(BaseModel):
    selectors: Dict[str, SiteConfig] 