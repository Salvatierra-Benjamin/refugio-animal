package com.refugiotrack.espacio;

import java.util.Objects;
import java.util.UUID;

/**
 * Ubicación donde puede permanecer un animal. Las especializaciones distinguen
 * un espacio interno de una ubicación externa.
 */
public abstract class Ubicacion {

    private final UUID id;
    private final TipoUbicacion tipo;

    protected Ubicacion(UUID id, TipoUbicacion tipo) {
        this.id = Objects.requireNonNull(id, "id no puede ser nulo");
        this.tipo = Objects.requireNonNull(tipo, "tipo no puede ser nulo");
    }

    public UUID getId() {
        return id;
    }

    public TipoUbicacion getTipo() {
        return tipo;
    }
}
