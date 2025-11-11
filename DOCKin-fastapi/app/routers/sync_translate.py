# /v1/translate 동기 엔드포인트 라우터 정의 파일
# 요청 스키마는 app/schemas/translate.py 사용
# 현재는 모의 동작으로 문자열을 뒤집어서 번역 결과처럼 회신

from fastapi import APIRouter
from ..schemas.translate import TranslateRequest, TranslateResponse

router = APIRouter(tags=["translate"])


@router.post("/v1/translate", response_model=TranslateResponse)
async def translate(req: TranslateRequest):
    # 간단한 모의 번역 수행
    translated = req.text[::-1]
    # 응답 모델 구성
    return TranslateResponse(
        translated=translated, model="mock-nllb", traceId=req.traceId
    )
