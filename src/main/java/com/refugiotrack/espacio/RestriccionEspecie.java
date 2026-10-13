package com.refugiotrack.espacio;

import com.refugiotrack.animal.Animal;

import java.util.Objects;

/**
 * Exige que la especie del animal esté entre las admitidas por el espacio.
 * No conserva estado propio: lee las especies admitidas del espacio.
 */
public class RestriccionEspecie implements ReglaEspacio {

    @Override
    public String id() {
        return "restriccion-especie";
    }

    @Override
    public boolean cumple(Animal animal, Espacio espacio) {
        Objects.requireNonNull(animal, "animal no puede ser nulo");
        Objects.requireNonNull(espacio, "espacio no puede ser nulo");
        return espacio.getEspeciesAdmitidas().contains(animal.getEspecie());
    }
}
