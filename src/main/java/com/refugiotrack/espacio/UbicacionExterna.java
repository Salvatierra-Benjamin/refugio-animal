package com.refugiotrack.espacio;

import java.util.Objects;
import java.util.UUID;

/**
 * Ubicación fuera de las sedes del refugio, identificada por la institución
 * receptora. El responsable puede omitirse si no se conoce.
 */
public class UbicacionExterna extends Ubicacion {

    private final String institucion;
    private final String responsable;

    public UbicacionExterna(UUID id, String institucion, String responsable) {
        super(id, TipoUbicacion.EXTERNA);
        this.institucion = requerirTexto(institucion, "institucion");
        this.responsable = responsable;
    }

    public String getInstitucion() {
        return institucion;
    }

    public String getResponsable() {
        return responsable;
    }

    private static String requerirTexto(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío");
        }
        return valor;
    }
}
