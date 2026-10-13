package com.refugiotrack.espacio;

import com.refugiotrack.animal.Animal;

/**
 * Regla de compatibilidad entre un animal y un espacio. Sigue el patrón
 * Specification/Strategy componible: cada implementación evalúa una
 * restricción y puede combinarse con otras sobre el mismo espacio.
 */
public interface ReglaEspacio {

    /**
     * Identificador estable de la regla. Se utiliza para auditoría.
     */
    String id();

    /**
     * Evalúa si el animal es compatible con el espacio según esta regla.
     *
     * @param animal animal a evaluar; no puede ser nulo
     * @param espacio espacio de destino; no puede ser nulo
     * @return {@code true} si la regla se cumple
     */
    boolean cumple(Animal animal, Espacio espacio);
}
