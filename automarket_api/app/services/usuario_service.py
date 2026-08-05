from sqlalchemy.orm import Session

from app.models.usuario import Usuario
from app.schemas.usuario import UsuarioCreate
from app.utils.security import hash_password, verify_password


def get_usuario_by_email(db: Session, email: str) -> Usuario | None:
    return db.query(Usuario).filter(Usuario.email == email).first()


def get_usuario_by_id(db: Session, usuario_id: int) -> Usuario | None:
    return db.query(Usuario).filter(Usuario.id == usuario_id).first()


def create_usuario(db: Session, usuario_data: UsuarioCreate) -> Usuario:
    nuevo_usuario = Usuario(
        nombre=usuario_data.nombre,
        email=usuario_data.email,
        telefono=usuario_data.telefono,
        password_hash=hash_password(usuario_data.password),
        rol=usuario_data.rol,
    )
    db.add(nuevo_usuario)
    db.commit()
    db.refresh(nuevo_usuario)
    return nuevo_usuario


def authenticate_usuario(db: Session, email: str, password: str) -> Usuario | None:
    usuario = get_usuario_by_email(db, email)
    if not usuario:
        return None
    if not verify_password(password, usuario.password_hash):
        return None
    return usuario