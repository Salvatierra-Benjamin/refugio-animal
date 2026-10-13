package com.refugiotrack.inventario;

/**
 * Tipo de movimiento que da cuenta del sentido del cambio de stock: la
 * cantidad registrada es una magnitud y la dirección la determina el tipo.
 */
public enum TipoMovimientoInventario {

    INGRESO,
    CONSUMO,
    AJUSTE,
    MERMA,
    VENCIMIENTO
}
