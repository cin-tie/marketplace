import logging
import requests
from aiogram import Router
from aiogram.types import Message
from aiogram.filters import Command

from config import config

router = Router()

@router.message(Command("telegram"))
async def telegram_handler(msg: Message):
    try:
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
            await msg.answer("✅ Ваш Telegram успешно привязан к аккаунту!")
        elif response.status_code == 404:
            await msg.answer("❌ Пользователь с таким Telegram не найден. Зарегистрируйтесь сначала на сайте.")
        else:
            await msg.answer("⚠️ Произошла ошибка при привязке Telegram")
    except Exception as e:
        logging.error(f"Telegram verification error: {e}")
        await msg.answer("⚠️ Произошла ошибка при соединении с сервером")


@router.message(Command("start"))
async def start_handler(msg: Message):
    await msg.answer("Привет! Я помогу тебе узнать твой ID, просто отправь мне любое сообщение")


@router.message()
async def message_handler(msg: Message):
    await msg.answer(f"Твой ID: {msg.from_user.id}")