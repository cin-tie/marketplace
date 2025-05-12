from aiogram import Router
from aiogram.filters import Command

from .start import start_handler
from .verification import verification_handler
from .registration.handlers import router as registration_router
from .common import router as common_router

router = Router()

router.include_router(registration_router)
router.include_router(common_router)

router.message.register(start_handler, Command("start"))
router.message.register(verification_handler, Command("verificate"))