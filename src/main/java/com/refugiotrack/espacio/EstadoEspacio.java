package com.refugiotrack.espacio;

/**
 * Estado operativo de un espacio. Solo un espacio disponible puede admitir
 * nuevos animales.
 */
public enum EstadoEspacio {

    DISPONIBLE,
    EN_MANTENIMIENTO,
    CERRADO
}
