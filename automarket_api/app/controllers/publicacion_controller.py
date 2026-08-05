from sqlalchemy.orm import Session
from fastapi import HTTPException, status

from app.models.usuario import Usuario
from app.models.publicacion import EstadoPublicacion
from app.schemas.publicacion import PublicacionCreate, PublicacionResponse
from app.services import publicacion_service, vehiculo_service
from app.utils.response import success_response


def crear_publicacion(db: Session, usuario_actual: Usuario, publicacion_data: PublicacionCreate) -> dict:
    vehiculo = vehiculo_service.get_vehiculo_by_id(db, publicacion_data.vehiculo_id)
    if not vehiculo:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Vehículo no encontrado")
    if vehiculo.usuario_id != usuario_actual.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="No puedes publicar un vehículo que no es tuyo")

    nueva_publicacion = publicacion_service.create_publicacion(db, publicacion_data)
    return success_response(
        "Publicación creada correctamente",
        PublicacionResponse.model_validate(nueva_publicacion).model_dump(mode="json"),
    )


def listar_publicaciones(db: Session) -> dict:
    publicaciones = publicacion_service.get_all_publicaciones(db)
    data = [PublicacionResponse.model_validate(p).model_dump(mode="json") for p in publicaciones]
    return success_response("Publicaciones obtenidas correctamente", data)


def obtener_publicacion(db: Session, publicacion_id: int) -> dict:
    publicacion = publicacion_service.get_publicacion_by_id(db, publicacion_id)
    if not publicacion:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Publicación no encontrada")
    return success_response(
        "Publicación obtenida correctamente",
        PublicacionResponse.model_validate(publicacion).model_dump(mode="json"),
    )


def cambiar_estado_publicacion(db: Session, usuario_actual: Usuario, publicacion_id: int, nuevo_estado: EstadoPublicacion) -> dict:
    publicacion = publicacion_service.get_publicacion_by_id(db, publicacion_id)
    if not publicacion:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Publicación no encontrada")
    if publicacion.vehiculo.usuario_id != usuario_actual.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="No tienes permiso sobre esta publicación")

    actualizada = publicacion_service.update_estado_publicacion(db, publicacion, nuevo_estado)
    return success_response(
        "Estado de la publicación actualizado",
        PublicacionResponse.model_validate(actualizada).model_dump(mode="json"),
    )


def eliminar_publicacion(db: Session, usuario_actual: Usuario, publicacion_id: int) -> dict:
    publicacion = publicacion_service.get_publicacion_by_id(db, publicacion_id)
    if not publicacion:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Publicación no encontrada")
    if publicacion.vehiculo.usuario_id != usuario_actual.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="No tienes permiso sobre esta publicación")

    publicacion_service.delete_publicacion(db, publicacion)
    return success_response("Publicación eliminada correctamente")