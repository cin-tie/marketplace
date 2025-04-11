from loguru import logger

def log_api_error(error: str, endpoint: str):
    logger.error(f"API Error in {endpoint}: {error}")