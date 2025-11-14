from fastapi import APIRouter, HTTPException
from app.schemas.translate import TranslateRequest, TranslateResponse
from transformers import MarianMTModel, MarianTokenizer

router = APIRouter()

# 지원 언어쌍별 MarianMT 모델 매핑.
MODEL_NAME_MAP: dict[tuple[str, str], str] = {
    ("ko", "en"): "Helsinki-NLP/opus-mt-ko-en",
    ("en", "ko"): "Helsinki-NLP/opus-mt-en-ko",
    ("en", "vi"): "Helsinki-NLP/opus-mt-en-vi",
    ("vi", "en"): "Helsinki-NLP/opus-mt-vi-en",
    ("en", "zh"): "Helsinki-NLP/opus-mt-en-zh",
    ("zh", "en"): "Helsinki-NLP/opus-mt-zh-en",
    ("en", "th"): "Helsinki-NLP/opus-mt-en-th",
    ("th", "en"): "Helsinki-NLP/opus-mt-th-en",
}

# 로드된 모델과 토크나이저 캐시.
model_cache: dict[str, MarianMTModel] = {}
tokenizer_cache: dict[str, MarianTokenizer] = {}


def get_model_and_tokenizer(
    src: str, tgt: str
) -> tuple[MarianMTModel, MarianTokenizer, str]:
    # 언어쌍에 맞는 모델 이름 조회.
    key = (src, tgt)
    if key not in MODEL_NAME_MAP:
        raise HTTPException(
            status_code=400,
            detail=f"지원하지 않는 번역 언어쌍입니다. source={src}, target={tgt}",
        )
    model_name = MODEL_NAME_MAP[key]

    # 캐시에 없으면 로드.
    if model_name not in model_cache:
        tokenizer_cache[model_name] = MarianTokenizer.from_pretrained(model_name)
        model_cache[model_name] = MarianMTModel.from_pretrained(model_name)

    return model_cache[model_name], tokenizer_cache[model_name], model_name


@router.post("/api/translate", response_model=TranslateResponse)
async def translate(req: TranslateRequest):
    # 모델과 토크나이저 로드.
    model, tokenizer, model_name = get_model_and_tokenizer(req.source, req.target)

    # 입력 토크나이징.
    inputs = tokenizer(
        req.text,
        return_tensors="pt",
        padding=True,
        truncation=True,
        max_length=256,
    )

    # 번역 토큰 생성.
    generated = model.generate(
        **inputs,
        max_length=256,
        num_beams=4,
        early_stopping=True,
    )

    # 텍스트 디코딩.
    translated = tokenizer.batch_decode(generated, skip_special_tokens=True)[0]

    return TranslateResponse(
        translated=translated,
        model=model_name,
        traceId=req.traceId,
    )
