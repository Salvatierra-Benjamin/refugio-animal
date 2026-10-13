package com.refugiotrack.espacio;

import com.refugiotrack.animal.Animal;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Impide que un animal en período de aislamiento ocupe un espacio común. Un
 * animal se considera en aislamiento mientras no haya transcurrido el período
 * configurado desde su fecha de ingreso. Si la fecha de ingreso no está
 * registrada, la restricción no bloquea.
 */
public class RestriccionAislamiento implements ReglaEspacio {

    private final Duration periodoAislamiento;

    public RestriccionAislamiento(Duration periodoAislamiento) {
        this.periodoAislamiento = Objects.requireNonNull(
                periodoAislamiento, "periodoAislamiento no puede ser nulo");
    }

    @Override
    public String id() {
        return "restriccion-aislamiento";
    }

    @Override
    public boolean cumple(Animal animal, Espacio espacio) {
        Objects.requireNonNull(animal, "animal no puede ser nulo");
        Objects.requireNonNull(espacio, "espacio no puede ser nulo");
        if (animal.getFechaIngreso() == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(animal.getFechaIngreso().plus(periodoAislamiento));
    }
}
