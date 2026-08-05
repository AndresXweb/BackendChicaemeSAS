from sqlalchemy.orm import Session

from app.models.vehiculo import Vehiculo
from app.schemas.vehiculo import VehiculoCreate


def create_vehiculo(db: Session, usuario_id: int, vehiculo_data: VehiculoCreate) -> Vehiculo:
    nuevo_vehiculo = Vehiculo(usuario_id=usuario_id, **vehiculo_data.model_dump())
    db.add(nuevo_vehiculo)
    db.commit()
    db.refresh(nuevo_vehiculo)
    return nuevo_vehiculo


def get_vehiculo_by_id(db: Session, vehiculo_id: int) -> Vehiculo | None:
    return db.query(Vehiculo).filter(Vehiculo.id == vehiculo_id).first()


def get_vehiculos_by_usuario(db: Session, usuario_id: int) -> list[Vehiculo]:
    return db.query(Vehiculo).filter(Vehiculo.usuario_id == usuario_id).all()


def get_all_vehiculos(db: Session) -> list[Vehiculo]:
    return db.query(Vehiculo).all()


def delete_vehiculo(db: Session, vehiculo: Vehiculo) -> None:
    db.delete(vehiculo)
    db.commit()