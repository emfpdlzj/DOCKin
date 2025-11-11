# 잡 스키마
from typing import Optional, Literal, Dict, Any, List
from pydantic import BaseModel

JobType = Literal["whisper.stt", "yolo.detect"]


class JobInput(BaseModel):
    # 서명된 미디어 URL 표현
    mediaUrl: str
    # 추가 파라미터 표현
    params: Dict[str, Any] = {}


class JobCreate(BaseModel):
    # 작업 식별자 표현
    jobId: str
    # 작업 유형 표현
    type: JobType
    # 입력 묶음 표현
    input: JobInput
    # 콜백 URL 표현
    callbackUrl: str
    # 추적 식별자 표현
    traceId: Optional[str] = None


class JobAccepted(BaseModel):
    # 접수 여부 표현
    accepted: bool
    # 작업 식별자 에코 표현
    jobId: str


class JobResult(BaseModel):
    # 처리 상태 표현
    status: Literal["SUCCEEDED", "FAILED"]
    # 결과 본문 표현
    result: Dict[str, Any] | None = None
    # 산출물 링크 목록 표현
    artifacts: List[Dict[str, str]] | None = None
    # 메트릭 표현
    metrics: Dict[str, Any] | None = None
    # 오류 메시지 표현
    error: str | None = None
    # 추적 식별자 표현
    traceId: Optional[str] = None
    # 서버 처리 시각 표현
    serverTimestamp: Optional[str] = None
