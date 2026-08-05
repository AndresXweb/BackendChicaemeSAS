from sqlalchemy.orm import Session
from fastapi import HTTPException, status

from app.schemas.usuario import UsuarioCreate, UsuarioLogin, UsuarioResponse
from app.services import usuario_service
from app.utils.jwt_handler import create_access_token
from app.utils.response import success_response


def registrar_usuario(db: Session, usuario_data: UsuarioCreate) -> dict:
    usuario_existente = usuario_service.get_usuario_by_email(db, usuario_data.email)
    if usuario_existente:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Ya existe un usuario registrado con este email",
        )

    nuevo_usuario = usuario_service.create_usuario(db, usuario_data)
    return success_response(
        "Usuario registrado correctamente",
        UsuarioResponse.model_validate(nuevo_usuario).model_dump(mode="json"),
    )


def login_usuario(db: Session, credenciales: UsuarioLogin) -> dict:
    usuario = usuario_service.authenticate_usuario(db, credenciales.email, credenciales.password)
    if not usuario:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Credenciales inválidas",
        )

    token = create_access_token({"sub": str(usuario.id), "email": usuario.email, "rol": usuario.rol.value})
    return success_response(
        "Inicio de sesión exitoso",
        {"access_token": token, "token_type": "bearer"},
    )