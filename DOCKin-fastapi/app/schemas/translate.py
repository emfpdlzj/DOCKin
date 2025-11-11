# 번역 스키마
from typing import Optional
from pydantic import BaseModel


class TranslateRequest(BaseModel):
    # 원문 표현
    text: str
    # 원문 언어 표현
    source: str
    # 대상 언어 표현
    target: str
    # 도메인 표현
    domain: Optional[str] = "shipyard"
    # 추적 식별자 표현
    traceId: Optional[str] = None


class TranslateResponse(BaseModel):
    # 번역 결과 표현
    translated: str
    # 사용 모델명 표현
    model: str
    # 추적 식별자 표현
    traceId: Optional[str] = None
