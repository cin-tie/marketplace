from aiogram import Router, F
from aiogram.types import Message, CallbackQuery
from aiogram.filters import Command
from aiogram.fsm.context import FSMContext
from aiogram.fsm.state import State, StatesGroup

from loguru import logger
from config.config import settings

from services.api_client import APIClient
from models.schemas import TelegramVerificationRequest

router = Router()

class VerificationStates(StatesGroup):
    waiting_for_token = State()

@router.message(Command("start"))
async def cmd_start(message: Message, api_client: APIClient):
    user = message.from_user
    telegram_username = f"@{user.username}" if user.username else str(user.id)
    
    # Pass both telegram username and chat_id
    response = await api_client.verify_telegram(
        telegram=telegram_username,
        chat_id=message.chat.id
    )
    
    if response and response.get("success"):
        if response.get("message") == "Telegram already verified":
            await message.answer(
                "✅ Ваш Telegram уже подтвержден!\n"
                "Используйте /products для управления своими товарами."
            )
        else:
            verification_token = response.get("message")
            await message.answer(
                f"🔐 Для подтверждения Telegram аккаунта перейдите по ссылке:\n"
                f"{settings.FRONTEND_URL}/verify-telegram?token={verification_token}\n\n"
                "После подтверждения вы сможете управлять своими товарами через бота."
            )
    else:
        await message.answer(
            "❌ Ваш Telegram не найден в системе. Пожалуйста, зарегистрируйтесь "
            "на сайте и укажите этот Telegram в профиле."
        )

@router.message(Command("verify"))
async def cmd_verify(message: Message, state: FSMContext):
    await message.answer(
        "Пожалуйста, введите токен подтверждения, который вы получили при регистрации:"
    )
    await state.set_state(VerificationStates.waiting_for_token)

@router.message(VerificationStates.waiting_for_token)
async def process_verification_token(message: Message, state: FSMContext, api_client: APIClient):
    token = message.text.strip()
    chat_id = message.chat.id
    
    response = await api_client.link_telegram_account(token, chat_id)
    
    if response and response.get("success"):
        await message.answer(
            "✅ Ваш Telegram успешно подтвержден и привязан к аккаунту!\n"
            "Теперь вы можете управлять своими товарами через бота."
        )
    else:
        await message.answer(
            "❌ Неверный токен подтверждения. Пожалуйста, проверьте токен и попробуйте снова."
        )
    
    await state.clear()