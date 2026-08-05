from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.config.database import get_db
from app.middlewares.auth import get_current_user
from app.models.usuario import Usuario
from app.schemas.vehiculo import VehiculoCreate
from app.controllers import vehiculo_controller

router = APIRouter(prefix="/api/vehiculos", tags=["Vehículos"])


@router.post("", status_code=201)
def crear_vehiculo(
    vehiculo_data: VehiculoCreate,
    db: Session = Depends(get_db),
    usuario_actual: Usuario = Depends(get_current_user),
):
    return vehiculo_controller.crear_vehiculo(db, usuario_actual, vehiculo_data)


@router.get("")
def listar_vehiculos(db: Session = Depends(get_db)):
    return vehiculo_controller.listar_vehiculos(db)


@router.get("/{vehiculo_id}")
def obtener_vehiculo(vehiculo_id: int, db: Session = Depends(get_db)):
    return vehiculo_controller.obtener_vehiculo(db, vehiculo_id)


@router.delete("/{vehiculo_id}")
def eliminar_vehiculo(
    vehiculo_id: int,
    db: Session = Depends(get_db),
    usuario_actual: Usuario = Depends(get_current_user),
):
    return vehiculo_controller.eliminar_vehiculo(db, usuario_actual, vehiculo_id)