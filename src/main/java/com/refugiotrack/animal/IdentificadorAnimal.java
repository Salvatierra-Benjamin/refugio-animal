package com.refugiotrack.animal;

import java.util.Objects;

/**
 * Valor de objeto que identifica a un animal: combina el tipo de identificador
 * con su valor concreto. La igualdad se determina por ambos componentes, lo que
 * permite descartar duplicados al consolidar la identidad.
 */
public record IdentificadorAnimal(TipoIdentificador tipo, String valor) {

    public IdentificadorAnimal {
        Objects.requireNonNull(tipo, "tipo no puede ser nulo");
        Objects.requireNonNull(valor, "valor no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("valor no puede estar vacío");
        }
    }
}
