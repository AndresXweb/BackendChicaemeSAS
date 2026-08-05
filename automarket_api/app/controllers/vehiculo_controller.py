from sqlalchemy.orm import Session
from fastapi import HTTPException, status

from app.models.usuario import Usuario
from app.schemas.vehiculo import VehiculoCreate, VehiculoResponse
from app.services import vehiculo_service
from app.utils.response import success_response


def crear_vehiculo(db: Session, usuario_actual: Usuario, vehiculo_data: VehiculoCreate) -> dict:
    nuevo_vehiculo = vehiculo_service.create_vehiculo(db, usuario_actual.id, vehiculo_data)
    return success_response(
        "Vehículo registrado correctamente",
        VehiculoResponse.model_validate(nuevo_vehiculo).model_dump(),
    )


def listar_vehiculos(db: Session) -> dict:
    vehiculos = vehiculo_service.get_all_vehiculos(db)
    data = [VehiculoResponse.model_validate(v).model_dump() for v in vehiculos]
    return success_response("Vehículos obtenidos correctamente", data)


def obtener_vehiculo(db: Session, vehiculo_id: int) -> dict:
    vehiculo = vehiculo_service.get_vehiculo_by_id(db, vehiculo_id)
    if not vehiculo:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Vehículo no encontrado")
    return success_response("Vehículo obtenido correctamente", VehiculoResponse.model_validate(vehiculo).model_dump())


def eliminar_vehiculo(db: Session, usuario_actual: Usuario, vehiculo_id: int) -> dict:
    vehiculo = vehiculo_service.get_vehiculo_by_id(db, vehiculo_id)
    if not vehiculo:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Vehículo no encontrado")
    if vehiculo.usuario_id != usuario_actual.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="No tienes permiso para eliminar este vehículo")

    vehiculo_service.delete_vehiculo(db, vehiculo)
    return success_response("Vehículo eliminado correctamente")