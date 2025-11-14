from fastapi import APIRouter
from app.core.config import settings

router = APIRouter()


@router.get("/health")
async def health():
    return {"status": "ok", "app": "fastapi-ai"}


@router.get("/ready")
async def ready():
    return {
        "ready": True,
        "model": settings.openai_model,
        "openai_key_loaded": settings.openai_api_key is not None,
    }
