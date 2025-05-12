from aiogram import Router, Bot
import requests
import logging
from aiogram.filters import Command
from aiogram.types import Message

from config import config

router = Router()

@router.message(Command("verification"))
async def verification_handler(msg: Message, bot: Bot):
    try:
        await bot.delete_message(chat_id=msg.chat.id, message_id=msg.message_id)

        response = requests.post(
            f"{config.BACKEND_URL}/api/telegram/verify",
            json={
                "telegramUsername": "@" + msg.from_user.username,
                "telegramId": msg.from_user.id,
                "telegramChatId": msg.chat.id
            },
            headers={
                "Content-Type": "application/json",
                "X-API-KEY": config.API_KEY
            }
        )

        if response.status_code == 200:
            await msg.answer("Your telegram connected to account successfully")
        elif response.status_code == 404:
            await msg.answer("User with such telegram is not found. First register")
        else:
            await msg.answer("Warning during connecting telegram")
    except Exception as e:
        logging.error(f"Telegram verification error: {e}")
        await msg.answer("An error during cinnection to server occured")