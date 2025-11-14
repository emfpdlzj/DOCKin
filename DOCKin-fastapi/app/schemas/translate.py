from pydantic import BaseModel


class TranslateRequest(BaseModel):
    text: str
    source: str
    target: str
    traceId: str | None = None


class TranslateResponse(BaseModel):
    translated: str
    model: str
    traceId: str | None = None
