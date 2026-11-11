from functools import lru_cache
from pydantic import BaseModel
import os


class Settings(BaseModel):
    earthdata_username: str = os.getenv("EARTHDATA_USERNAME", "")
    earthdata_password: str = os.getenv("EARTHDATA_PASSWORD", "")
    database_url: str = os.getenv("DATABASE_URL", "sqlite:///./kisan.db")
    redis_url: str = os.getenv("REDIS_URL", "redis://localhost:6379/0")
    fcm_server_key: str = os.getenv("FCM_SERVER_KEY", "")
    demo_mode: bool = os.getenv("DEMO_MODE", "true").lower() == "true"


@lru_cache
def get_settings() -> Settings:
    return Settings()
