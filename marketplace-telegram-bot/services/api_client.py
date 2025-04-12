import logging
from typing import Optional
import aiohttp
from aiohttp import ClientError
from loguru import logger

from config.config import settings
from models.schemas import (
    TelegramVerificationRequest,
    TelegramLinkRequest,
    ProductCreate,
    Product
)

class APIClient:
    def __init__(self):
        self.base_url = settings.BACKEND_URL
        self.session = aiohttp.ClientSession()
        
    async def close(self):
        await self.session.close()
        
    async def _make_request(self, method: str, endpoint: str, **kwargs):
        headers = kwargs.pop('headers', {})
        headers.update({
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        })
        
        url = f"{self.base_url}{endpoint}"
        try:
            async with self.session.request(
                method, 
                url, 
                headers=headers,
                **kwargs
            ) as response:
                if response.status != 200:
                    error = await response.text()
                    logger.error(f"API request to {url} failed with status {response.status}: {error}")
                    return None
                return await response.json()
        except ClientError as e:
            logger.error(f"API connection error to {url}: {e}")
            return None
    
    async def verify_telegram(self, telegram: str, chat_id: int) -> Optional[dict]:
        data = {
            "telegram": telegram,
            "chat_id": chat_id
        }
        return await self._make_request(
            "POST",
            "/api/telegram/verify",
            json=data
        )
    async def get_user_products(self, chat_id: int) -> Optional[list[Product]]:
        return await self._make_request(
            "GET",
            f"/api/telegram/user-products?chat_id={chat_id}"
        )
        
    async def create_product(self, product: ProductCreate, chat_id: int) -> Optional[Product]:
        return await self._make_request(
            "POST",
            f"/api/telegram/products?chat_id={chat_id}",
            json=product.dict()
        )