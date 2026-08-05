from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.config.database import get_db
from app.middlewares.auth import get_current_user
from app.models.usuario import Usuario
from app.models.publicacion import EstadoPublicacion
from app.schemas.publicacion import PublicacionCreate
from app.controllers import publicacion_controller

router = APIRouter(prefix="/api/publicaciones", tags=["Publicaciones"])


@router.post("", status_code=201)
def crear_publicacion(
    publicacion_data: PublicacionCreate,
    db: Session = Depends(get_db),
    usuario_actual: Usuario = Depends(get_current_user),
):
    return publicacion_controller.crear_publicacion(db, usuario_actual, publicacion_data)


@router.get("")
def listar_publicaciones(db: Session = Depends(get_db)):
    return publicacion_controller.listar_publicaciones(db)


@router.get("/{publicacion_id}")
def obtener_publicacion(publicacion_id: int, db: Session = Depends(get_db)):
    return publicacion_controller.obtener_publicacion(db, publicacion_id)


@router.patch("/{publicacion_id}/estado")
def cambiar_estado(
    publicacion_id: int,
    nuevo_estado: EstadoPublicacion,
    db: Session = Depends(get_db),
    usuario_actual: Usuario = Depends(get_current_user),
):
    return publicacion_controller.cambiar_estado_publicacion(db, usuario_actual, publicacion_id, nuevo_estado)


@router.delete("/{publicacion_id}")
def eliminar_publicacion(
    publicacion_id: int,
    db: Session = Depends(get_db),
    usuario_actual: Usuario = Depends(get_current_user),
):
    return publicacion_controller.eliminar_publicacion(db, usuario_actual, publicacion_id)