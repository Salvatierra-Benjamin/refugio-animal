package com.refugiotrack.inventario;

/**
 * Estado operativo de un lote. Solo un lote disponible puede consumirse.
 */
public enum EstadoLote {

    DISPONIBLE,
    BLOQUEADO,
    AGOTADO
}
