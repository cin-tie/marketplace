from aiogram import Router
from aiogram.types import Message
from aiogram.filters import Command

router = Router()

@router.message(Command("start"))
async def start_handler(msg: Message):
    await msg.answer(
        "Hello! I'm a bot to work with marketplace(LINK HERE).\n\n"
        "Available commands:\n"
        "/start - show this message again\n"
        "/verificate - verificate Telegram to account\n"
        "/register - registrate new user\n\n"
    )