# TTS 라우터 정의 파일
from fastapi import APIRouter, Depends
from ..core.auth import require_service_token
from ..schemas.tts import TtsRequest, TtsResponse

router = APIRouter(tags=["tts"])


@router.post(
    "/v1/tts", response_model=TtsResponse, dependencies=[Depends(require_service_token)]
)
async def tts(req: TtsRequest):
    # 모의 파일 URL 생성 표현
    fake_url = "https://mock/audio/tts-123.wav"
    # 모의 응답 생성 표현
    return TtsResponse(
        audioUrl=fake_url,
        provider="mock-tts",
        traceId=req.traceId,
        meta={"lang": req.lang, "voice": req.voice, "lengthSec": 2.3},
    )
