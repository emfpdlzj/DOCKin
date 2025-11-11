# YOLO 워커 루프
import asyncio
from datetime import datetime, timezone
from ..workers.memqueue import blocking_pop
from ..clients.spring_api import post_callback
from ..schemas.job import JobResult


async def handle_detect(job: dict) -> None:
    # 모의 감지 결과
    dets = [
        {"cls": "helmet", "conf": 0.91, "bbox": [100, 120, 80, 60]},
        {"cls": "vest", "conf": 0.88, "bbox": [95, 200, 90, 140]},
    ]
    patch = {
        "entityName": "WorkLogs",
        "entityId": str(job.get("input", {}).get("params", {}).get("logId", "")),
        "clientTimestamp": datetime.now(timezone.utc).isoformat(),
        "changes": {"detections": dets, "yoloStatus": "SUCCEEDED"},
    }
    payload = JobResult(
        status="SUCCEEDED",
        result={"patch": patch},
        artifacts=None,
        metrics={"latency_ms": 120},
        error=None,
        traceId=job.get("traceId"),
        serverTimestamp=datetime.now(timezone.utc).isoformat(),
    ).model_dump()
    await post_callback(job["callbackUrl"], payload)


async def loop() -> None:
    # 무한 루프
    while True:
        job = await blocking_pop(timeout=5)
        if not job:
            continue
        if job.get("type") == "yolo.detect":
            try:
                await handle_detect(job)
            except Exception:
                pass
