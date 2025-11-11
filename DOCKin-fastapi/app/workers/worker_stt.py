# STT 워커 루프
import asyncio
from datetime import datetime, timezone
from ..workers.memqueue import blocking_pop
from ..clients.spring_api import post_callback
from ..schemas.job import JobResult


async def handle_stt(job: dict) -> None:
    # 모의 처리 텍스트
    text = "음성 인식 결과 예시"
    # 패치 바디 구성
    patch = {
        "entityName": "WorkLogs",
        "entityId": str(job.get("input", {}).get("params", {}).get("logId", "")),
        "clientTimestamp": datetime.now(timezone.utc).isoformat(),
        "changes": {
            "logText": text,
            "audioFileUrl": job.get("input", {}).get("mediaUrl", ""),
            "sttStatus": "SUCCEEDED",
        },
    }
    # 콜백 페이로드
    payload = JobResult(
        status="SUCCEEDED",
        result={"patch": patch},
        artifacts=None,
        metrics={"latency_ms": 100},
        error=None,
        traceId=job.get("traceId"),
        serverTimestamp=datetime.now(timezone.utc).isoformat(),
    ).model_dump()
    # 콜백 전송
    await post_callback(job["callbackUrl"], payload)


async def loop() -> None:
    # 무한 루프
    while True:
        job = await blocking_pop(timeout=5)
        if not job:
            continue
        if job.get("type") == "whisper.stt":
            try:
                await handle_stt(job)
            except Exception:
                # 에러는 스킵
                pass
