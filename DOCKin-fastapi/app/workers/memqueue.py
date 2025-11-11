# asyncio 인메모리 큐
import asyncio
from typing import Optional

_queue: asyncio.Queue = asyncio.Queue()


async def enqueue(job: dict) -> None:
    # 큐에 작업 추가
    await _queue.put(job)


async def blocking_pop(timeout: int = 5) -> Optional[dict]:
    # 타임아웃 기반 팝
    try:
        return await asyncio.wait_for(_queue.get(), timeout=timeout)
    except asyncio.TimeoutError:
        return None
