import os
from dotenv import load_dotenv
from pydantic_settings import BaseSettings
from aiogram.utils.token import validate_token
from typing import Optional

load_dotenv()

class Settings(BaseSettings):
    BOT_TOKEN: str
    BACKEND_URL: str = "http://localhost:8080"
    FRONTEND_URL: str = "http://localhost"
    LOG_LEVEL: str = "INFO"

    def __init__(self, **data):
        super().__init__(**data)
        try:
            validate_token(self.BOT_TOKEN)
        except ValueError as e:
            raise ValueError(f"Invalid Telegram bot token: {e}")

settings = Settings(
    BOT_TOKEN=os.getenv("BOT_TOKEN", ""),
    BACKEND_URL=os.getenv("BACKEND_URL", "http://localhost:8080"),
    LOG_LEVEL=os.getenv("LOG_LEVEL", "INFO")
)