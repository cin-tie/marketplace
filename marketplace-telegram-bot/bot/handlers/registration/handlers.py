from aiogram import Router, F, Bot
from aiogram.types import Message
from aiogram.filters import Command
from aiogram.fsm.context import FSMContext
import re
from typing import List

from config import config
from .states import RegistrationStates
import requests
import logging

router = Router()

USERNAME_PATTERN = re.compile(r'^[a-zA-Z0-9_]{6,256}$')
EMAIL_PATTERN = re.compile(r'^[\w\.-]+@[\w\.-]+\.\w+$')
PASSWORD_PATTERN = re.compile(r'^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$')

async def cleanup_registration_messages(chat_id: int, message_ids: List[int], bot: Bot):
    for msg_id in message_ids:
        try:
            await bot.delete_message(chat_id=chat_id, message_id=msg_id)
        except Exception as e:
            logging.warning(f"Failed to delete message {msg_id}: {e}")

@router.message(Command("register"))
async def start_registration(msg: Message, state: FSMContext, bot: Bot):
    try:
        data = {
            'messages_to_delete': [msg.message_id],
            'registration_messages': []
        }
        await state.update_data(data)

        await bot.delete_message(chat_id=msg.chat.id, message_id=msg.message_id)

        responseId = requests.get(
            f"{config.BACKEND_URL}/api/telegram/check-userId/{msg.from_user.id}",
            headers={"X-API-KEY": config.API_KEY}
        )
        responseUser = requests.get(
            f"{config.BACKEND_URL}/api/telegram/check-user/{msg.from_user.username}",
            headers={"X-API-KEY": config.API_KEY}
        )

        if responseId.status_code == 200 or responseUser.status_code == 200:
            user_data = responseId.json() if responseId.status_code == 200 else responseUser.json()
            message = (
                "You have already registrated in system!\n\n"
                f"Username: {user_data['username']}\n"
                f"Email: {user_data['email']}\n"
                f"Email verified: {"Yes" if user_data['isEmailVerified'] else "No"}\n"
                f"Telegram verified: {"Yes" if user_data['isTelegramVerified'] else "No"}"
            )
            sent_msg = await msg.answer(message)
            
            data = await state.get_data()
            data['messages_to_delete'].append(sent_msg.message_id)
            await state.update_data(data)
            return

        sent_msg = await msg.answer("Let's start registration. Send your username (6-256 characters, letters, numbers, _):")

        data = await state.get_data()
        data['messages_to_delete'].append(sent_msg.message_id)
        data['registration_messages'].append(sent_msg.message_id)
        await state.update_data(data)

        await state.set_state(RegistrationStates.waiting_for_username)
    
    except Exception as e:
        logging.error(f"Error checking user: {e}")
        await msg.answer("An error during registration occured")
        await state.clear()
    

@router.message(RegistrationStates.waiting_for_username, F.text)
async def process_username(msg: Message, state: FSMContext):
    username = msg.text.strip()

    data = await state.get_data()
    messages_to_delete = data.get('messages_to_delete', [])
    messages_to_delete.append(msg.message_id)
    await state.update_data(messages_to_delete=messages_to_delete)

    if not USERNAME_PATTERN.fullmatch(username):
        sent_msg = await msg.answer(
                "Incorrect username. Username must meet the requirements\n"
                "- 6-256 characters\n"
                "- Only letters (a-z, A-Z), numbers (0-9) and underscores (_)\n"
                "Try again:"
            )
        messages_to_delete.append(sent_msg.message_id)
        await state.update_data(messages_to_delete=messages_to_delete)
        return
    
    await state.update_data(username=username)
    sent_msg = await msg.answer("Great! Now send me your email:")
    messages_to_delete.append(sent_msg.message_id)
    await state.update_data(messages_to_delete=messages_to_delete)
    await state.set_state(RegistrationStates.waiting_for_email)

@router.message(RegistrationStates.waiting_for_email, F.text)
async def process_email(msg: Message, state: FSMContext):
    email = msg.text.strip()

    data = await state.get_data()
    messages_to_delete = data.get('messages_to_delete', [])
    messages_to_delete.append(msg.message_id)
    await state.update_data(messages_to_delete=messages_to_delete)
    
    if not EMAIL_PATTERN.fullmatch(email):
        sent_msg = await msg.answer(
            "Incorrect email. Email should be valid in the format example@domain.com\n"
            "Try again"
        )
        messages_to_delete.append(sent_msg.message_id)
        await state.update_data(messages_to_delete=messages_to_delete)
        return
    
    await state.update_data(email=email)
    sent_msg = await msg.answer(
        "Great! Now create a password. Requirements:\n"
        "- Minimum 8 characters\n"
        "- At least one uppercase letter\n"
        "- At least one lowercase letter\n"
        "- At least one number\n"
        "Enter password:"
    )
    messages_to_delete.append(sent_msg.message_id)
    await state.update_data(messages_to_delete=messages_to_delete)
    await state.set_state(RegistrationStates.waiting_for_password)

@router.message(RegistrationStates.waiting_for_password, F.text)
async def process_password(msg: Message, state: FSMContext, bot: Bot):
    password = msg.text.strip()

    data = await state.get_data()
    messages_to_delete = data.get('messages_to_delete', [])
    messages_to_delete.append(msg.message_id)
    await state.update_data(messages_to_delete=messages_to_delete)

    if not PASSWORD_PATTERN.fullmatch(password):
        sent_msg = await msg.answer(
            "Password does not meet the requirements:\n"
            "- Minimum 8 characters\n"
            "- At least one uppercase letter\n"
            "- At least one lowercase letter\n"
            "- At least one number\n"
            "Try again:"
        )
        messages_to_delete.append(sent_msg.message_id)
        await state.update_data(messages_to_delete=messages_to_delete)
        return
    
    user_data = await state.get_data()
    telegram_username = "@" + msg.from_user.username if msg.from_user.username else None
    
    if not telegram_username:
        await msg.answer("You don't have a username set in Telegram. Please set it in Telegram settings and try again.")
        await state.clear()
        return

    try:
        response = requests.post(
            f"{config.BACKEND_URL}/api/telegram/register",
            json={
                "username": user_data["username"],
                "email": user_data["email"],
                "telegram": telegram_username,
                "password": password,
                "telegramId": msg.from_user.id
            },
            headers={
                "Content-Type": "application/json",
                "X-API-KEY": config.API_KEY
            }
        )

        if response.status_code == 200:
            await cleanup_registration_messages(msg.chat.id, messages_to_delete, bot)
           
            await msg.answer(
                "Register successful!\n\n"
                "Your account was created and Telegram connected automatically.\n"
                "Verification message was send to your email."
            )
        else:
            error_data = response.json()
            errors = "\n".join(error_data.get("errors", ["Unknown error"]))
            sent_msg = await msg.answer(f"Registration error:\n{errors}")
            messages_to_delete.append(sent_msg.message_id)
            await state.update_data(messages_to_delete=messages_to_delete)
    except Exception as e:
        logging.error(f"Registration error: {e}")
        sent_msg = await msg.answer("An error occurred during registration")
        messages_to_delete.append(sent_msg.message_id)
        await state.update_data(messages_to_delete=messages_to_delete)
    finally:
        await state.clear()