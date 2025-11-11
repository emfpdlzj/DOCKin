# Redis 기반 단순 리스트 큐 유틸
# LPUSH 로 넣고 BRPOP 으로 꺼내는 구조 표현

import json
import os
from typing import Optional, Tuple
from redis.asyncio import Redis

QUEUE_KEY = "ai:queue"


def get_redis() -> Redis:
    # 환경변수에서 REDIS_URL 읽기 표현
    url = os.getenv("REDIS_URL", "redis://localhost:6379/0")
    return Redis.from_url(url, decode_responses=True)


async def enqueue(job: dict) -> None:
    # 잡을 JSON 문자열로 직렬화하여 큐에 넣기 표현
    r = get_redis()
    await r.lpush(QUEUE_KEY, json.dumps(job))
    await r.close()


async def blocking_pop(timeout: int = 5) -> Optional[Tuple[str, dict]]:
    # BRPOP 으로 블로킹 팝 수행 표현
    r = get_redis()
    item = await r.brpop(QUEUE_KEY, timeout=timeout)
    await r.close()
    if not item:
        return None
    key, value = item
    return key, json.loads(value)
