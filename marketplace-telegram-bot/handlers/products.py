from aiogram import Router, F
from aiogram.types import Message, CallbackQuery, ReplyKeyboardRemove
from aiogram.filters import Command
from aiogram.fsm.context import FSMContext
from aiogram.fsm.state import State, StatesGroup
from aiogram.utils.keyboard import ReplyKeyboardBuilder

from loguru import logger

from services.api_client import APIClient
from models.schemas import ProductCreate

router = Router()

class ProductStates(StatesGroup):
    waiting_for_product_name = State()
    waiting_for_product_price = State()

@router.message(Command("products"))
async def cmd_products(message: Message, api_client: APIClient):
    products = await api_client.get_user_products(message.chat.id)
    
    if not products:
        await message.answer("У вас пока нет товаров. Используйте /add_product чтобы добавить первый товар.")
        return
    
    products_text = "\n".join(
        f"{idx + 1}. {product['name']} - ${product['price']:.2f}"
        for idx, product in enumerate(products)
    )
    
    await message.answer(
        f"📦 Ваши товары:\n\n{products_text}\n\n"
        "Используйте /add_product чтобы добавить новый товар."
    )

@router.message(Command("add_product"))
async def cmd_add_product(message: Message, state: FSMContext):
    await message.answer(
        "Введите название товара:",
        reply_markup=ReplyKeyboardRemove()
    )
    await state.set_state(ProductStates.waiting_for_product_name)

@router.message(ProductStates.waiting_for_product_name)
async def process_product_name(message: Message, state: FSMContext):
    await state.update_data(name=message.text)
    await message.answer("Теперь введите цену товара:")
    await state.set_state(ProductStates.waiting_for_product_price)

@router.message(ProductStates.waiting_for_product_price)
async def process_product_price(message: Message, state: FSMContext, api_client: APIClient):
    try:
        price = float(message.text)
        if price <= 0:
            raise ValueError
    except ValueError:
        await message.answer("Пожалуйста, введите корректную цену (положительное число):")
        return
    
    data = await state.get_data()
    product_data = ProductCreate(name=data['name'], price=price)
    
    product = await api_client.create_product(product_data, message.chat.id)
    
    if product:
        await message.answer(
            f"✅ Товар успешно добавлен!\n\n"
            f"Название: {product['name']}\n"
            f"Цена: ${product['price']:.2f}"
        )
    else:
        await message.answer("❌ Не удалось добавить товар. Пожалуйста, попробуйте позже.")
    
    await state.clear()