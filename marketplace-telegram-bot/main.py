import logging
import asyncio
from aiogram import Bot, Dispatcher
from loguru import logger

from config.config import settings
from services.api_client import APIClient
from handlers import auth, products, common

async def main():
    # Configure logging
    logging.basicConfig(level=settings.LOG_LEVEL)
    logger.add(
        "bot.log",
        level=settings.LOG_LEVEL,
        rotation="10 MB",
        retention="10 days",
        format="{time} {level} {message}"
    )
    
    # Initialize bot and dispatcher
    bot = Bot(token=settings.BOT_TOKEN)
    dp = Dispatcher()
    
    # Setup dependency injection
    api_client = APIClient()
    dp.workflow_data.update(api_client=api_client)
    
    # Include routers
    dp.include_router(auth.router)
    dp.include_router(products.router)
    dp.include_router(common.router)
    
    try:
        await dp.start_polling(bot)
    finally:
        await api_client.close()
        await bot.session.close()

if __name__ == "__main__":
    asyncio.run(main())