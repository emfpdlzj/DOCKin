# /v1/chat 동기 엔드포인트 라우터 정의 파일
# 요청 스키마는 app/schemas/chat.py 사용
# 현재는 모의 동작으로 마지막 user 발화를 그대로 가공하여 회신

from fastapi import APIRouter
from ..schemas.chat import ChatRequest, ChatResponse

router = APIRouter(tags=["chat"])


@router.post("/v1/chat", response_model=ChatResponse)
async def chat(req: ChatRequest):
    # messages 리스트에서 마지막 user 메시지 추출
    last_user = next(
        (m.content for m in reversed(req.messages) if m.role == "user"), ""
    )
    # 간단한 모의 응답 생성
    reply = f"[{req.domain}/{req.lang}] {last_user}"
    # 응답 모델 구성
    return ChatResponse(
        reply=reply, model="mock", usage={"input": 0, "output": 0}, traceId=req.traceId
    )
