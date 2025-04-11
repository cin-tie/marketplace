from aiogram import Router
from aiogram.types import Message
from aiogram.filters import Command

router = Router()

@router.message(Command("help"))
async def cmd_help(message: Message):
    await message.answer(
        "🤖 <b>Marketplace Bot Help</b>\n\n"
        "Доступные команды:\n"
        "/start - Начать работу с ботом\n"
        "/verify - Подтвердить Telegram аккаунт\n"
        "/products - Просмотр ваших товаров\n"
        "/add_product - Добавить новый товар\n"
        "/help - Показать это сообщение\n\n"
        "Для подтверждения Telegram аккаунта вам потребуется токен, "
        "который вы получите при регистрации на сайте.",
        parse_mode="HTML"
    )