from pydantic import BaseModel, ConfigDict


class VehiculoBase(BaseModel):
    marca: str
    modelo: str
    anio: int
    kilometraje: int
    tipo_combustible: str
    transmision: str
    color: str | None = None
    descripcion: str | None = None


class VehiculoCreate(VehiculoBase):
    pass


class VehiculoResponse(VehiculoBase):
    model_config = ConfigDict(from_attributes=True)

    id: int
    usuario_id: int