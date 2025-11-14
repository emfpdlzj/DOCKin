from pydantic import BaseModel


class SttRequest(BaseModel):
    mediaUrl: str | None = None
    lang: str | None = None
    traceId: str | None = None


class SttResponse(BaseModel):
    text: str
    provider: str
    traceId: str | None = None
