# /v1/chat 요청과 응답 스키마 정의 파일
# 스프링이 세션과 저장을 관리. FastAPI는 한 턴 생성만 수행

from typing import List, Literal, Optional, Dict
from pydantic import BaseModel, Field

class Message(BaseModel):
    # 대화 메시지 모델
    # role 값은 system, user, assistant 중 하나
    # content 는 텍스트 본문
    role: Literal["system", "user", "assistant"]
    content: str = Field(..., min_length=1)

class ChatRequest(BaseModel):
    # /v1/chat 요청 바디 모델
    # messages 는 스프링이 관리하는 직전 대화 맥락
    # sessionId 는 스프링 세션 식별자
    # userId 는 호출 사용자 식별자
    # domain 은 도메인 태그
    # lang 은 언어 코드
    # traceId 는 추적 식별자
    messages: List[Message]
    sessionId: Optional[str] = None
    userId: Optional[str] = None
    domain: str = Field(default="shipyard")
    lang: str = Field(default="ko")
    traceId: Optional[str] = None

class ChatResponse(BaseModel):
    # /v1/chat 응답 바디 모델
    # reply 는 assistant 가 생성한 한 턴 텍스트
    # model 은 사용 모델명
    # usage 는 토큰 사용량 메타
    # traceId 는 요청과 매칭되는 추적 식별자
    reply: str
    model: str = "mock"
    usage: Dict[str, int] = {"input": 0, "output": 0}
    traceId: Optional[str] = None