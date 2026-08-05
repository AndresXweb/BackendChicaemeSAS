from datetime import datetime
from pydantic import BaseModel, EmailStr, ConfigDict

from app.models.usuario import RolUsuario


class UsuarioBase(BaseModel):
    nombre: str
    email: EmailStr
    telefono: str | None = None


class UsuarioCreate(UsuarioBase):
    password: str
    rol: RolUsuario = RolUsuario.comprador


class UsuarioLogin(BaseModel):
    email: EmailStr
    password: str


class UsuarioResponse(UsuarioBase):
    model_config = ConfigDict(from_attributes=True)

    id: int
    rol: RolUsuario
    fecha_registro: datetime