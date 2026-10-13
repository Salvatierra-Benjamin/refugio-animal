package com.refugiotrack.inventario;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Alerta emitida por el subsistema de inventario ante una condición que
 * requiere atención. Nace sin atender y se marca atendida una sola vez.
 */
public class Alerta {

    private final UUID id;
    private final TipoAlerta tipo;
    private final LocalDateTime generada;
    private boolean atendida;

    public Alerta(UUID id, TipoAlerta tipo, LocalDateTime generada) {
        this.id = Objects.requireNonNull(id, "id no puede ser nulo");
        this.tipo = Objects.requireNonNull(tipo, "tipo no puede ser nulo");
        this.generada = Objects.requireNonNull(generada, "generada no puede ser nula");
        this.atendida = false;
    }

    /**
     * Marca la alerta como atendida.
     */
    public void marcarAtendida() {
        this.atendida = true;
    }

    public UUID getId() {
        return id;
    }

    public TipoAlerta getTipo() {
        return tipo;
    }

    public LocalDateTime getGenerada() {
        return generada;
    }

    public boolean isAtendida() {
        return atendida;
    }
}
