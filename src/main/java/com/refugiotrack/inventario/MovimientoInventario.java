package com.refugiotrack.inventario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Movimiento de inventario: registro inmutable de un cambio de stock. La
 * cantidad es una magnitud y el sentido lo determina el tipo. Las
 * correcciones no modifican un movimiento previo; se registran como
 * movimientos nuevos con su propio motivo.
 */
public class MovimientoInventario {

    private final UUID id;
    private final TipoMovimientoInventario tipo;
    private final BigDecimal cantidad;
    private final Unidad unidad;
    private final String motivo;
    private final LocalDateTime fechaHora;
    private final String claveIdempotencia;

    public MovimientoInventario(UUID id, TipoMovimientoInventario tipo, BigDecimal cantidad,
                                Unidad unidad, String motivo, LocalDateTime fechaHora,
                                String claveIdempotencia) {
        this.id = Objects.requireNonNull(id, "id no puede ser nulo");
        this.tipo = Objects.requireNonNull(tipo, "tipo no puede ser nulo");
        this.cantidad = Objects.requireNonNull(cantidad, "cantidad no puede ser nula");
        if (cantidad.signum() <= 0) {
            throw new IllegalArgumentException("cantidad debe ser positiva");
        }
        this.unidad = Objects.requireNonNull(unidad, "unidad no puede ser nula");
        this.motivo = requerirTexto(motivo, "motivo");
        this.fechaHora = Objects.requireNonNull(fechaHora, "fechaHora no puede ser nula");
        this.claveIdempotencia = requerirTexto(claveIdempotencia, "claveIdempotencia");
    }

    public UUID getId() {
        return id;
    }

    public TipoMovimientoInventario getTipo() {
        return tipo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public Unidad getUnidad() {
        return unidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getClaveIdempotencia() {
        return claveIdempotencia;
    }

    private static String requerirTexto(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío");
        }
        return valor;
    }
}
