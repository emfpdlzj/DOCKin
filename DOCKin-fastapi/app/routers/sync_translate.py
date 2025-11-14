from fastapi import APIRouter
from app.schemas.translate import TranslateRequest, TranslateResponse
from transformers import AutoModelForSeq2SeqLM, AutoTokenizer
import torch

router = APIRouter()

# 모델 로드 (서버 시작 시 단 1회)
tokenizer = AutoTokenizer.from_pretrained("facebook/nllb-200-distilled-600M")
model = AutoModelForSeq2SeqLM.from_pretrained("facebook/nllb-200-distilled-600M")

# 언어 코드 매핑 (NLLB 내부 코드)
NLLB_LANG_MAP = {
    "ko": "kor_Hang",
    "en": "eng_Latn",
    "vi": "vie_Latn",
    "th": "tha_Thai",
    "zh": "zho_Hans",
    "id": "ind_Latn",
    # 필요하면 계속 추가 가능
}


@router.post("/api/translate", response_model=TranslateResponse)
async def translate(req: TranslateRequest):

    src = NLLB_LANG_MAP.get(req.source, "kor_Hang")
    tgt = NLLB_LANG_MAP.get(req.target, "eng_Latn")

    # NLLB는 tokenizer에 source_lang 지정이 필수
    inputs = tokenizer(
        req.text, return_tensors="pt", padding=True, truncation=True, src_lang=src
    )

    with torch.no_grad():
        outputs = model.generate(
            **inputs, forced_bos_token_id=tokenizer.lang_code_to_id[tgt], max_length=200
        )

    translated = tokenizer.decode(outputs[0], skip_special_tokens=True)

    return TranslateResponse(
        translated=translated, model="NLLB-200-600M", traceId=req.traceId or ""
    )
