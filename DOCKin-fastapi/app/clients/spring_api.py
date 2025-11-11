# 스프링 콜백 HTTP 클라이언트
import httpx
from typing import Dict, Any
from ..core.config import settings


async def post_callback(url: str, body: Dict[str, Any]) -> None:
    # 콜백 헤더 구성
    headers = {
        "Content-Type": "application/json",
        "X-Callback-Token": settings.callback_token,
    }
    # HTTP 전송
    async with httpx.AsyncClient(timeout=20) as client:
        resp = await client.post(url, json=body, headers=headers)
        resp.raise_for_status()
