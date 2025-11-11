# 환경설정 로더
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    app_name: str = "fastapi-ai"
    app_env: str = "local"
    app_port: int = 8000
    openai_api_key: str = ""
    chat_model: str = "gpt-4o-mini"
    openai_enabled: bool = True

    class Config:
        env_file = ".env"


settings = Settings()
