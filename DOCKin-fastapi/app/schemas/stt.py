# STT 스키마 정의 파일
from typing import Optional, Dict, Any
from pydantic import BaseModel, HttpUrl


class SttUrlRequest(BaseModel):
    # 미디어 URL 표현
    mediaUrl: HttpUrl
    # 언어 코드 표현
    lang: Optional[str] = "ko"
    # 추적 식별자 표현
    traceId: Optional[str] = None


class SttResponse(BaseModel):
    # 인식 텍스트 표현
    text: str
    # 공급자 이름 표현
    provider: str = "mock-whisper"
    # 추적 식별자 표현
    traceId: Optional[str] = None
    # 부가 메타 표현
    meta: Dict[str, Any] = {}
