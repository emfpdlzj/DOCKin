from pydantic import BaseModel


class Message(BaseModel):
    role: str
    content: str


class ChatRequest(BaseModel):
    messages: list[Message]
    domain: str | None = None
    lang: str | None = None
    traceId: str | None = None


class ChatResponse(BaseModel):
    reply: str
    model: str
    traceId: str | None = None
