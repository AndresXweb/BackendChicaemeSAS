from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.config.database import get_db
from app.schemas.usuario import UsuarioCreate, UsuarioLogin
from app.controllers import usuario_controller

router = APIRouter(prefix="/api/usuarios", tags=["Usuarios"])


@router.post("/registro", status_code=201)
def registro(usuario_data: UsuarioCreate, db: Session = Depends(get_db)):
    return usuario_controller.registrar_usuario(db, usuario_data)


@router.post("/login")
def login(credenciales: UsuarioLogin, db: Session = Depends(get_db)):
    return usuario_controller.login_usuario(db, credenciales)