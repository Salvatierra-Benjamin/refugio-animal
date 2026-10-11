package com.refugiotrack.seguridad;

/**
 * Acción sensible que puede ejecutar un usuario según el rol que posee. Cada
 * permiso se verifica en el servidor, nunca solo en la interfaz.
 */
public enum Permiso {

    GESTIONAR_CONFIGURACION,
    CONFIRMAR_ADMISION,
    AUTORIZAR_EXCEPCION,
    REGISTRAR_CONSUMO,
    DECIDIR_DONACION,
    FORMALIZAR_ADOPCION,
    VER_AUDITORIA
}
