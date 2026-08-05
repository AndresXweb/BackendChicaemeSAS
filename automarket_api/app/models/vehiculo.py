from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import relationship

from app.config.database import Base


class Vehiculo(Base):
    __tablename__ = "vehiculos"

    id = Column(Integer, primary_key=True, index=True)
    usuario_id = Column(Integer, ForeignKey("usuarios.id"), nullable=False)
    marca = Column(String(50), nullable=False)
    modelo = Column(String(50), nullable=False)
    anio = Column(Integer, nullable=False)
    kilometraje = Column(Integer, nullable=False)
    tipo_combustible = Column(String(30), nullable=False)
    transmision = Column(String(30), nullable=False)
    color = Column(String(30), nullable=True)
    descripcion = Column(String(500), nullable=True)

    propietario = relationship("Usuario", back_populates="vehiculos")
    publicaciones = relationship("Publicacion", back_populates="vehiculo", cascade="all, delete-orphan")