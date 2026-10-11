package com.refugiotrack.seguridad;

import java.util.Set;

/**
 * Rol operativo de un usuario del refugio. Cada rol concede un conjunto
 * acotado de {@link Permiso}s sobre acciones sensibles, aplicando el principio
 * de mínimo privilegio.
 */
public enum Rol {

    ADMINISTRADOR(
            Permiso.GESTIONAR_CONFIGURACION,
            Permiso.AUTORIZAR_EXCEPCION,
            Permiso.DECIDIR_DONACION,
            Permiso.VER_AUDITORIA
    ),
    ADMISIONES(
            Permiso.CONFIRMAR_ADMISION
    ),
    CUIDADOR(
            Permiso.REGISTRAR_CONSUMO
    ),
    VETERINARIO(
            Permiso.REGISTRAR_CONSUMO
    ),
    ADOPCIONES(
            Permiso.FORMALIZAR_ADOPCION
    );

    private final Set<Permiso> permisos;

    Rol(Permiso... permisos) {
        this.permisos = Set.of(permisos);
    }

    /**
     * Permisos que concede este rol.
     */
    public Set<Permiso> permisos() {
        return permisos;
    }
}
