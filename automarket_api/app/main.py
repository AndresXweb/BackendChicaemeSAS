from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.config.database import Base, engine
from app.models import usuario, vehiculo, publicacion  # noqa: F401
from app.routes import usuario_routes, vehiculo_routes, publicacion_routes

Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="AutoMarket API",
    description="API REST para marketplace de compra, venta y permuta de vehículos",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(usuario_routes.router)
app.include_router(vehiculo_routes.router)
app.include_router(publicacion_routes.router)


@app.get("/")
def root():
    return {"success": True, "message": "AutoMarket API funcionando correctamente", "data": None}