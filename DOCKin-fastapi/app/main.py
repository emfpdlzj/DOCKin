# FastAPI 애플리케이션 엔트리포인트
# 헬스체크와 동기 라우터를 등록

from fastapi import FastAPI
from .routers import health, sync_chat, sync_translate

app = FastAPI(title="fastapi-ai")

app.include_router(health.router)
app.include_router(sync_chat.router)
app.include_router(sync_translate.router)


@app.get("/")
def root():
    # 간단한 루트 응답
    return {"hello": "fastapi-ai"}
