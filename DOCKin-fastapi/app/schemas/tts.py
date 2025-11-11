# TTS 스키마 정의 파일
from typing import Optional, Dict, Any
from pydantic import BaseModel


class TtsRequest(BaseModel):
    # 합성할 텍스트 표현
    text: str
    # 언어 코드 표현
    lang: Optional[str] = "ko"
    # 음색 식별자 표현
    voice: Optional[str] = "female_1"
    # 추적 식별자 표현
    traceId: Optional[str] = None


class TtsResponse(BaseModel):
    # 오디오 파일 URL 표현
    audioUrl: str
    # 공급자 이름 표현
    provider: str = "mock-tts"
    # 추적 식별자 표현
    traceId: Optional[str] = None
    # 부가 메타 표현
    meta: Dict[str, Any] = {}
