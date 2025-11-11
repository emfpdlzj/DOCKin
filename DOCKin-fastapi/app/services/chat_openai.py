# OpenAI 챗봇 호출 어댑터.
# 스프링이 넘긴 messages 를 OpenAI 포맷으로 변환해 한 턴 응답 생성.

from typing import List, Dict, Tuple
from openai import OpenAI
from ..core.config import settings

# OpenAI 클라이언트 싱글턴 캐시.
_client: OpenAI | None = None


def get_client() -> OpenAI:
    # 환경변수에 키가 없으면 예외 발생.
    if not settings.openai_api_key:
        raise RuntimeError("OPENAI_API_KEY not set.")
    global _client
    if _client is None:
        _client = OpenAI(api_key=settings.openai_api_key)
    return _client


def build_system_prompt(domain: str, lang: str) -> str:
    # 도메인과 언어 지침을 간단히 부여.
    return (
        f"You are a helpful assistant for shipyard domain. "
        f"Answer in {lang}. Keep answers concise and practical."
    )


def to_openai_messages(messages: List[Dict], domain: str, lang: str) -> List[Dict]:
    # 스프링이 넘긴 messages 를 OpenAI 포맷으로 변환.
    oai_msgs: List[Dict] = []
    sys = {"role": "system", "content": build_system_prompt(domain, lang)}
    oai_msgs.append(sys)
    for m in messages:
        oai_msgs.append({"role": m["role"], "content": m["content"]})
    return oai_msgs


def chat_once(payload: Dict) -> Tuple[str, Dict]:
    # 단발 생성 수행.
    # payload 는 ChatRequest.dict() 와 동일 형태 가정.
    client = get_client()
    model = settings.chat_model
    domain = payload.get("domain", "shipyard")
    lang = payload.get("lang", "ko")
    msgs = to_openai_messages(payload["messages"], domain, lang)

    # Chat Completions API 호출.
    resp = client.chat.completions.create(
        model=model,
        messages=msgs,
        temperature=0.3,
    )

    # 답변과 사용량 추출.
    reply = resp.choices[0].message.content or ""
    usage = {
        "input": getattr(resp.usage, "prompt_tokens", 0),
        "output": getattr(resp.usage, "completion_tokens", 0),
    }
    return reply, usage
