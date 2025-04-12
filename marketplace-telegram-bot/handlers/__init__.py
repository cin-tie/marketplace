"""Package containing all bot handlers."""
from .auth import router as auth_router  # noqa: F401
from .products import router as products_router  # noqa: F401
from .common import router as common_router  # noqa: F401

__all__ = ["auth_router", "products_router", "common_router"]