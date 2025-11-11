# translate.py
# /v1/translate 요청(request)과 응답(response)에 사용하는 Pydantic 스키마 정의 파일
# 번역기(NLLB 등) 호출 전/후에 공통으로 사용될 예정

from typing import Optional
from pydantic import BaseModel, Field


# TranslateRequest 모델
# /v1/translate 엔드포인트의 요청(request) 바디 형식 정의
# - text: 번역할 원문 문자열
# - source: 원문 언어 코드 (auto, ko, en 등)
# - target: 목표 언어 코드
# - domain: 적용 도메인 (예: shipyard)
# - traceId: 트레이스 추적용 식별자 (옵션)
class TranslateRequest(BaseModel):
    text: str = Field(..., min_length=1, description="번역할 원문 텍스트")
    source: str = Field(default="auto", description="원문 언어 코드")
    target: str = Field(..., description="번역 대상 언어 코드")
    domain: str = Field(default="shipyard", description="도메인명")
    traceId: Optional[str] = None


# TranslateResponse 모델
# /v1/translate 응답(response) 형식 정의
# - translated: 번역 결과 문자열
# - model: 사용된 번역 모델명
# - traceId: 요청과 대응되는 추적 ID
class TranslateResponse(BaseModel):
    translated: str
    model: str = "mock-nllb"
    traceId: Optional[str] = None
