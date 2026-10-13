package com.refugiotrack.espacio;

import com.refugiotrack.seguridad.Usuario;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Registro inmutable de un movimiento de un animal. Las correcciones no se
 * aplican sobre el movimiento existente: se modelan como movimientos nuevos.
 * El origen y el destino pueden omitirse, por ejemplo en un egreso sin destino
 * o en un alta sin origen.
 */
public class Movimiento {

    private final UUID id;
    private final TipoMovimiento tipo;
    private final LocalDateTime fechaHora;
    private final String motivo;
    private final Ubicacion origen;
    private final Ubicacion destino;
    private final Usuario responsable;

    public Movimiento(UUID id, TipoMovimiento tipo, LocalDateTime fechaHora, String motivo,
                      Ubicacion origen, Ubicacion destino, Usuario responsable) {
        this.id = Objects.requireNonNull(id, "id no puede ser nulo");
        this.tipo = Objects.requireNonNull(tipo, "tipo no puede ser nulo");
        this.fechaHora = Objects.requireNonNull(fechaHora, "fechaHora no puede ser nula");
        this.motivo = requerirTexto(motivo, "motivo");
        this.origen = origen;
        this.destino = destino;
        this.responsable = Objects.requireNonNull(responsable, "responsable no puede ser nulo");
    }

    /**
     * Indica que el movimiento es inmutable. Todos los campos son finales; las
     * correcciones se modelan como movimientos nuevos.
     */
    public boolean esInmutable() {
        return true;
    }

    public UUID getId() {
        return id;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public Ubicacion getOrigen() {
        return origen;
    }

    public Ubicacion getDestino() {
        return destino;
    }

    public Usuario getResponsable() {
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
