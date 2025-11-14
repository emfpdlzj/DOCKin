# 환경설정 로더
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    # 앱 기본 설정
    app_name: str = "fastapi-ai"
    app_env: str = "local"
    app_port: int = 8000

    # 인증 토큰
    service_token: str = "change-me"
    callback_token: str = "change-callback"

    # 모델 및 기능 토글
    openai_api_key: str | None = None
    openai_model: str = "gpt-4o-mini"

    class Config:
        env_file = ".env"


settings = Settings()
