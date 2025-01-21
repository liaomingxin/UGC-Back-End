from pydantic_settings import BaseSettings
from functools import lru_cache

class Settings(BaseSettings):
    # Server Configuration
    APP_NAME: str = "UGC Content Generator"
    DEBUG: bool = True
    API_PREFIX: str = "/api"
    
    # OpenAI Configuration
    OPENAI_API_KEY: str = "sk-kh5dDiP76fy07AOKAfF9KSjR0Lpb5OdThhs29PWpOxLb2rIq"
    OPENAI_BASE_URL: str = "https://api.gpts.vin"
    
    # Selenium Configuration
    CHROME_DRIVER_PATH: str = "/usr/local/bin/chromedriver"
    
    class Config:
        env_file = ".env"

@lru_cache()
def get_settings():
    return Settings()

settings = get_settings() 