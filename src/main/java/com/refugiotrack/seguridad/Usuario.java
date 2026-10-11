package com.refugiotrack.seguridad;

import java.util.Objects;
import java.util.UUID;

/**
 * Usuario del refugio que opera sobre acciones sensibles. Almacena el hash de
 * la credencial, nunca la credencial en texto plano (plaintext), y su {@link Rol} determina los
 * permisos disponibles.
 */
public class Usuario {

    private final UUID id;
    private final String nombreUsuario;
    private final String hashCredencial;
    private boolean activo;
    private Rol rol;

    public Usuario(UUID id, String nombreUsuario, String hashCredencial, boolean activo, Rol rol) {
        this.id = Objects.requireNonNull(id, "id no puede ser nulo");
        this.nombreUsuario = requerirTexto(nombreUsuario, "nombreUsuario");
        this.hashCredencial = requerirTexto(hashCredencial, "hashCredencial");
        this.activo = activo;
        this.rol = Objects.requireNonNull(rol, "rol no puede ser nulo");
    }

    private static String requerirTexto(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío");
        }
        return valor;
    }

    public UUID getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getHashCredencial() {
        return hashCredencial;
    }

    public boolean isActivo() {
        return activo;
    }

    public Rol getRol() {
        return rol;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void asignarRol(Rol rol) {
        this.rol = Objects.requireNonNull(rol, "rol no puede ser nulo");
    }
}
