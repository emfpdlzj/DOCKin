# /v1/chat 동기 엔드포인트
# 서비스 토큰 인증 적용
# OPENAI_ENABLED=false 이면 모의 응답 반환

from fastapi import APIRouter, Depends, HTTPException, status
from ..schemas.chat import ChatRequest, ChatResponse
from ..core.auth import require_service_token
from ..services.chat_openai import chat_once
from ..core.config import settings

router = APIRouter(tags=["chat"])


@router.post(
    "/v1/chat",
    response_model=ChatResponse,
    dependencies=[Depends(require_service_token)],
)
async def chat(req: ChatRequest):
    # 개발 단계에서 OpenAI를 끄고 진행하는 토글
    if not settings.openai_enabled:
        # 마지막 user 메시지 찾기
        last_user = next(
            (m.content for m in reversed(req.messages) if m.role == "user"), ""
        )
        # 간단한 모의 응답 생성
        reply = f"[mock {req.domain}/{req.lang}] {last_user}"
        return ChatResponse(
            reply=reply,
            model="mock",
            usage={"input": 0, "output": 0},
            traceId=req.traceId,
        )
    # OpenAI 사용 시 예외 처리
    try:
        reply, usage = chat_once(req.model_dump())
        return ChatResponse(
            reply=reply,
            model=settings.chat_model or "openai",
            usage=usage,
            traceId=req.traceId,
        )
    except Exception as e:
        # insufficient_quota 등 429 상태를 클라이언트에 전달
        msg = str(e)
        if "insufficient_quota" in msg or "You exceeded your current quota" in msg:
            raise HTTPException(
                status_code=status.HTTP_429_TOO_MANY_REQUESTS,
                detail="insufficient_quota",
            )
        # 기타는 502로 전달
        raise HTTPException(
            status_code=status.HTTP_502_BAD_GATEWAY, detail="chat_provider_error"
        )
