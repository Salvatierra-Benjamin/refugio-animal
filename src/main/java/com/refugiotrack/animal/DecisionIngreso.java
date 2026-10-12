package com.refugiotrack.animal;

/**
 * Resultado de la decisión de admisión de un ingreso. Un ingreso recién
 * registrado queda pendiente hasta que se decide; la decisión es definitiva.
 */
public enum DecisionIngreso {

    PENDIENTE,
    ADMITIDO,
    RECHAZADO,
    DERIVADO
}
