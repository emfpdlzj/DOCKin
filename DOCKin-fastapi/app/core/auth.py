# X-Service-Token 인증 디펜던시
from fastapi import Header, HTTPException, status
from .config import settings


async def require_service_token(x_service_token: str = Header(None)):
    # 헤더 누락 처리
    if not x_service_token:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail="missing_service_token"
        )
    # 토큰 불일치 처리
    if x_service_token != settings.service_token:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN, detail="invalid_service_token"
        )
    # 통과 시 반환값 없음
    return True
