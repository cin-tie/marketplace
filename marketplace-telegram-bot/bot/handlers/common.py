from aiogram import Router
from aiogram.types import Message

router = Router()

@router.message()
async def message_handler(msg: Message):
    await msg.answer(f"Your ID: {msg.from_user.id}")