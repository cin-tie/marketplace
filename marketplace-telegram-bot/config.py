import os
from dotenv import load_dotenv
from pydantic_settings import BaseSettings

load_dotenv()

class Config(BaseSettings):
    BOT_TOKEN: str = os.getenv("BOT_TOKEN")
    BACKEND_URL: str = os.getenv("BACKEND_URL", "http://backend:8080")
    FRONTEND_URL: str = os.getenv("FRONTEND_URL", "http://localhost:3000")
    LOG_LEVEL: str = os.getenv("LOG_LEVEL", "INFO")
    
    DB_HOST: str = os.getenv("DB_HOST", "database")
    DB_PORT: str = os.getenv("DB_PORT", "5432")
    DB_USER: str = os.getenv("DB_USER", "postuser")
    DB_PASSWORD: str = os.getenv("DB_PASSWORD", "postpass")
    DB_NAME: str = os.getenv("DB_NAME", "marketplace")
    
    SMTP_HOST: str = os.getenv("SMTP_HOST", "mailhog")
    SMTP_PORT: int = int(os.getenv("SMTP_PORT", 1025))
    SMTP_USER: str = os.getenv("SMTP_USER", "")
    SMTP_PASSWORD: str = os.getenv("SMTP_PASSWORD", "")
    EMAIL_FROM: str = os.getenv("EMAIL_FROM", "noreply@marketplace.com")

    API_KEY: str = os.getenv("API_KEY", "your-secret-api-key-12345")
    
    @property
    def DB_CONFIG(self):
        return {
            "host": self.DB_HOST,
            "port": self.DB_PORT,
            "user": self.DB_USER,
            "password": self.DB_PASSWORD,
            "database": self.DB_NAME
        }

config = Config()