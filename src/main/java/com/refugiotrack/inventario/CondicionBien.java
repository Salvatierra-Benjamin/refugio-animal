package com.refugiotrack.inventario;

/**
 * Condición en la que se recibe un bien. Solo los bienes aceptables quedan
 * disponibles; el resto permanece bloqueado hasta su disposición.
 */
public enum CondicionBien {

    ACEPTABLE,
    NO_ACEPTABLE,
    VENCIDO,
    INSEGURO
}
