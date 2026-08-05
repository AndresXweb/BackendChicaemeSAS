from sqlalchemy import Column, Integer, String, DateTime, Enum
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
import enum

from app.config.database import Base


class RolUsuario(str, enum.Enum):
    comprador = "comprador"
    vendedor = "vendedor"
    admin = "admin"


class Usuario(Base):
    __tablename__ = "usuarios"

    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String(100), nullable=False)
    email = Column(String(150), unique=True, nullable=False, index=True)
    password_hash = Column(String(255), nullable=False)
    telefono = Column(String(20), nullable=True)
    rol = Column(Enum(RolUsuario), default=RolUsuario.comprador, nullable=False)
    fecha_registro = Column(DateTime(timezone=True), server_default=func.now())

    vehiculos = relationship("Vehiculo", back_populates="propietario", cascade="all, delete-orphan")