# 서비스 간 인증 유틸리티 정의 파일
# 스프링에서 호출 시 헤더 토큰을 검사

import os
from fastapi import Header, HTTPException, status

# 환경변수에서 서비스 토큰을 읽음
SERVICE_TOKEN = os.getenv("SERVICE_TOKEN", "change-me")


async def require_service_token(x_service_token: str = Header(None)):
    # 헤더가 없으면 401 반환
    if not x_service_token:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail="Missing X-Service-Token"
        )
    # 토큰이 불일치하면 403 반환
    if x_service_token != SERVICE_TOKEN:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN, detail="Invalid X-Service-Token"
        )
    # 통과 시 아무것도 반환하지 않음
    return None
