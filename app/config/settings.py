from pydantic_settings import BaseSettings
from functools import lru_cache

class Settings(BaseSettings):
    """
    配置类，用于加载和存储应用程序的环境变量配置。
    """

    # Server Configuration
    APP_NAME: str = "UGC Content Generator"  # 应用名称
    DEBUG: bool = True  # 调试模式开关
    API_PREFIX: str = "/api"  # API 路由前缀

    # OpenAI Configuration
    OPENAI_API_KEY: str = "sk-kh5dDiP76fy07AOKAfF9KSjR0Lpb5OdThhs29PWpOxLb2rIq"
    OPENAI_BASE_URL: str = "https://api.gpts.vin"

    # Selenium Configuration
    CHROME_DRIVER_PATH: str = "/usr/local/bin/chromedriver"  # ChromeDriver 路径

    class Config:
        """
        Pydantic 配置类，用于指定环境变量文件。
        """
        env_file = ".env"

@lru_cache()
def get_settings():
    """
    获取配置实例，并通过 LRU 缓存优化。
    返回：
        Settings: 配置实例。
    """
    return Settings()

# 全局配置实例
settings = get_settings()
