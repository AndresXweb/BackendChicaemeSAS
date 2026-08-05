from datetime import datetime
from decimal import Decimal
from pydantic import BaseModel, ConfigDict

from app.models.publicacion import TipoPublicacion, EstadoPublicacion


class PublicacionBase(BaseModel):
    tipo: TipoPublicacion
    precio: Decimal | None = None


class PublicacionCreate(PublicacionBase):
    vehiculo_id: int


class PublicacionResponse(PublicacionBase):
    model_config = ConfigDict(from_attributes=True)

    id: int
    vehiculo_id: int
    estado: EstadoPublicacion
    fecha_publicacion: datetime