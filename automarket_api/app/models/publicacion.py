from sqlalchemy import Column, Integer, String, Numeric, DateTime, ForeignKey, Enum
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
import enum

from app.config.database import Base


class TipoPublicacion(str, enum.Enum):
    venta = "venta"
    permuta = "permuta"


class EstadoPublicacion(str, enum.Enum):
    activa = "activa"
    vendida = "vendida"
    pausada = "pausada"


class Publicacion(Base):
    __tablename__ = "publicaciones"

    id = Column(Integer, primary_key=True, index=True)
    vehiculo_id = Column(Integer, ForeignKey("vehiculos.id"), nullable=False)
    tipo = Column(Enum(TipoPublicacion), nullable=False)
    precio = Column(Numeric(12, 2), nullable=True)
    estado = Column(Enum(EstadoPublicacion), default=EstadoPublicacion.activa, nullable=False)
    fecha_publicacion = Column(DateTime(timezone=True), server_default=func.now())

    vehiculo = relationship("Vehiculo", back_populates="publicaciones")