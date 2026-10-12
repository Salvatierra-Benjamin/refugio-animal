package com.refugiotrack.animal;

import java.util.Objects;
import java.util.Set;

/**
 * Estado operativo del animal dentro del refugio. Las transiciones permitidas
 * definen la máquina de estados: un animal avanza solo por los caminos
 * documentados y no puede saltar entre estados no consecutivos.
 */
public enum EstadoAnimal {

    PENDIENTE_DE_EVALUACION,
    EN_ADMISION,
    EN_REFUGIO,
    EN_TRANSITO_EXTERNO,
    ADOPTADO,
    EGRESADO,
    FALLECIDO;

    /**
     * Estados alcanzables de forma directa desde este estado.
     */
    public Set<EstadoAnimal> transicionesPermitidas() {
        return switch (this) {
            case PENDIENTE_DE_EVALUACION -> Set.of(EN_ADMISION);
            case EN_ADMISION -> Set.of(EN_REFUGIO, EGRESADO);
            case EN_REFUGIO -> Set.of(EN_TRANSITO_EXTERNO, ADOPTADO, EGRESADO, FALLECIDO);
            case EN_TRANSITO_EXTERNO -> Set.of(EN_REFUGIO);
            case ADOPTADO -> Set.of(EN_REFUGIO);
            case EGRESADO -> Set.of();
            case FALLECIDO -> Set.of();
        };
    }

    /**
     * Indica si es posible transicionar desde este estado hacia el estado
     * objetivo indicado.
     *
     * @param objetivo estado de destino; no puede ser nulo
     * @return {@code true} si la transición está permitida
     */
    public boolean puedeTransicionarA(EstadoAnimal objetivo) {
        Objects.requireNonNull(objetivo, "objetivo no puede ser nulo");
        return transicionesPermitidas().contains(objetivo);
    }
}
