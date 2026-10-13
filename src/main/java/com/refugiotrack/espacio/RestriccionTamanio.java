package com.refugiotrack.espacio;

import com.refugiotrack.animal.Animal;
import com.refugiotrack.shared.Tamanio;

import java.util.Objects;

/**
 * Exige que el tamaño del animal esté entre los admitidos por el espacio.
 * Un tamaño desconocido no bloquea la compatibilidad.
 */
public class RestriccionTamanio implements ReglaEspacio {

    @Override
    public String id() {
        return "restriccion-tamanio";
    }

    @Override
    public boolean cumple(Animal animal, Espacio espacio) {
        Objects.requireNonNull(animal, "animal no puede ser nulo");
        Objects.requireNonNull(espacio, "espacio no puede ser nulo");
        Tamanio tamanio = animal.getTamanio();
        if (tamanio == null) {
            return true;
        }
        return espacio.getTamaniosAdmitidos().contains(tamanio);
    }
}
