# /v1/translate 라우터 정의 파일
# 서비스 토큰 인증을 의존성으로 적용

from fastapi import APIRouter, Depends
from ..schemas.translate import TranslateRequest, TranslateResponse
from ..core.auth import require_service_token

router = APIRouter(tags=["translate"])


@router.post(
    "/v1/translate",
    response_model=TranslateResponse,
    dependencies=[Depends(require_service_token)],
)
async def translate(req: TranslateRequest):
    # 모의 번역 수행
    translated = req.text[::-1]
    # 응답 모델 구성
    return TranslateResponse(
        translated=translated, model="mock-nllb", traceId=req.traceId
    )
