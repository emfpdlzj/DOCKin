# 헬스체크
from fastapi import APIRouter
from ..core.config import settings

router = APIRouter(tags=["health"])


@router.get("/health")
def health():
    # 단순 헬스 응답
    return {"status": "ok", "app": settings.app_name, "env": settings.app_env}


@router.get("/ready")
def ready():
    # 준비 상태와 플래그 노출
    return {
        "ready": True,
        "openai_enabled": settings.openai_enabled,
        "translate_enabled": settings.translate_enabled,
        "chat_model": settings.chat_model,
    }
