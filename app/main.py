from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.utils.logger import setup_logger
from app.config.settings import settings

# 设置日志
logger = setup_logger()

# 创建FastAPI应用
app = FastAPI(
    title=settings.APP_NAME,
    debug=settings.DEBUG,
)

# 配置CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 导入路由
from app.api import content

# 注册路由
app.include_router(content.router, prefix=settings.API_PREFIX)

@app.on_event("startup")
async def startup_event():
    logger.info("Starting up UGC Content Generator...")

@app.on_event("shutdown")
async def shutdown_event():
    logger.info("Shutting down UGC Content Generator...") 