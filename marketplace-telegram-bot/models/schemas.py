from pydantic import BaseModel, Field, validator
from typing import Optional

class TelegramVerificationRequest(BaseModel):
    telegram: str
    chat_id: int

class TelegramLinkRequest(BaseModel):
    verification_token: str
    chat_id: int

class ProductBase(BaseModel):
    name: str = Field(..., min_length=1, max_length=100)
    price: float = Field(..., gt=0)

class ProductCreate(ProductBase):
    pass

class Product(ProductBase):
    id: str