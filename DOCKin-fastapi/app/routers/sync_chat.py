from fastapi import APIRouter
from openai import OpenAI
from app.schemas.chat import ChatRequest, ChatResponse
from app.core.config import settings
from faster_whisper import WhisperModel

# whisper tiny or base 추천
model = WhisperModel("tiny", device="cpu")

router = APIRouter()
client = OpenAI(api_key=settings.openai_api_key)


@router.post("/api/chat", response_model=ChatResponse)
async def chat(req: ChatRequest):
    completion = client.chat.completions.create(
        model=settings.openai_model, messages=[m.model_dump() for m in req.messages]
    )

    reply = completion.choices[0].message.content

    return ChatResponse(reply=reply, model=settings.openai_model, traceId=req.traceId)
