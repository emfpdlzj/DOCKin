# FastAPI 엔트리포인트
import asyncio
from fastapi import FastAPI
from .routers import stt, tts
from fastapi.middleware.cors import CORSMiddleware
from .routers import health, sync_chat, sync_translate, jobs
from .workers.worker_stt import loop as stt_loop
from .workers.worker_detect import loop as det_loop

app = FastAPI(title="fastapi-ai")

# CORS 허용
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 라우터 등록
app.include_router(health.router)
app.include_router(sync_chat.router)
app.include_router(sync_translate.router)
app.include_router(jobs.router)
app.include_router(stt.router)
app.include_router(tts.router)


@app.on_event("startup")
async def start_bg_workers():
    # 백그라운드 워커 기동
    asyncio.create_task(stt_loop())
    asyncio.create_task(det_loop())


@app.get("/")
def root():
    # 루트 응답
    return {"hello": "fastapi-ai"}
