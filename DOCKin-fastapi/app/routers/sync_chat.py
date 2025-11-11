# /v1/chat 라우터 정의 파일
# 스프링 주도형 계약에 맞게 한 턴 생성만 수행
# 서비스 토큰 인증을 의존성으로 적용

from fastapi import APIRouter, Depends
from ..schemas.chat import ChatRequest, ChatResponse
from ..core.auth import require_service_token

router = APIRouter(tags=["chat"])


@router.post(
    "/v1/chat",
    response_model=ChatResponse,
    dependencies=[Depends(require_service_token)],
)
async def chat(req: ChatRequest):
    # 마지막 user 메시지를 찾음
    last_user = next(
        (m.content for m in reversed(req.messages) if m.role == "user"), ""
    )
    # 모의 응답 생성
    reply = f"[{req.domain}/{req.lang}] {last_user}"
    # 응답 모델 구성
    return ChatResponse(
        reply=reply, model="mock", usage={"input": 0, "output": 0}, traceId=req.traceId
    )
