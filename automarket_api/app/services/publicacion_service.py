from sqlalchemy.orm import Session

from app.models.publicacion import Publicacion, EstadoPublicacion
from app.schemas.publicacion import PublicacionCreate


def create_publicacion(db: Session, publicacion_data: PublicacionCreate) -> Publicacion:
    nueva_publicacion = Publicacion(**publicacion_data.model_dump())
    db.add(nueva_publicacion)
    db.commit()
    db.refresh(nueva_publicacion)
    return nueva_publicacion


def get_publicacion_by_id(db: Session, publicacion_id: int) -> Publicacion | None:
    return db.query(Publicacion).filter(Publicacion.id == publicacion_id).first()


def get_all_publicaciones(db: Session) -> list[Publicacion]:
    return db.query(Publicacion).all()


def update_estado_publicacion(db: Session, publicacion: Publicacion, nuevo_estado: EstadoPublicacion) -> Publicacion:
    publicacion.estado = nuevo_estado
    db.commit()
    db.refresh(publicacion)
    return publicacion


def delete_publicacion(db: Session, publicacion: Publicacion) -> None:
    db.delete(publicacion)
    db.commit()