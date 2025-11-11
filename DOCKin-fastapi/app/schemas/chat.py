# 챗 스키마
from typing import List, Optional, Literal, Dict
from pydantic import BaseModel

Role = Literal["system", "user", "assistant"]


class Message(BaseModel):
    # 메시지 역할 표현
    role: Role
    # 메시지 본문 표현
    content: str


class ChatRequest(BaseModel):
    # 대화 메시지 목록 표현
    messages: List[Message]
    # 선택 세션 식별자 표현
    sessionId: Optional[str] = None
    # 선택 사용자 식별자 표현
    userId: Optional[str] = None
    # 도메인 표현
    domain: Optional[str] = "shipyard"
    # 언어 표현
    lang: Optional[str] = "ko"
    # 추적 식별자 표현
    traceId: Optional[str] = None


class ChatResponse(BaseModel):
    # 모델 응답 텍스트 표현
    reply: str
    # 사용 모델명 표현
    model: str
    # 토큰 사용량 표현
    usage: Dict[str, int] = {"input": 0, "output": 0}
    # 추적 식별자 에코 표현
    traceId: Optional[str] = None
