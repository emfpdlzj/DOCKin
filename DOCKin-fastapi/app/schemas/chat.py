# chat.py
# /v1/chat 요청(request)과 응답(response)에 사용하는 Pydantic 스키마 정의 파일
# 이 스키마는 OpenAI API와의 연동 전/후 공통으로 사용

from typing import List, Literal, Optional, Dict
from pydantic import BaseModel, Field


# 메시지(Message) 모델
# 대화 내에서 한 발화(turn)를 표현한다.
# role: system / user / assistant 중 하나
# content: 실제 텍스트 내용
class Message(BaseModel):
    role: Literal["system", "user", "assistant"]
    content: str = Field(..., min_length=1, description="메시지 본문 텍스트")


# ChatRequest 모델
# /v1/chat 엔드포인트의 요청(request) 바디 형식 정의
# - messages: 대화 전체 맥락 (리스트 형태)
# - domain: 적용 도메인 (예: shipyard)
# - lang: 언어 코드 (ko, en, vi 등)
# - traceId: 트레이스 추적용 식별자 (옵션)
class ChatRequest(BaseModel):
    messages: List[Message]
    domain: str = Field(default="shipyard", description="도메인명")
    lang: str = Field(default="ko", description="언어코드")
    traceId: Optional[str] = None


# ChatResponse 모델
# /v1/chat 응답(response) 형식 정의
# - reply: 모델이 생성한 답변 텍스트
# - model: 사용된 모델명 (예: gpt-4o-mini)
# - usage: 토큰 사용량 등 메타데이터
# - traceId: 요청과 대응되는 추적 ID
class ChatResponse(BaseModel):
    reply: str
    model: str = "mock"
    usage: Dict[str, int] = {"input": 0, "output": 0}
    traceId: Optional[str] = None
