package com.refugiotrack.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Lote de un recurso: unidad de trazabilidad que agrupa una cantidad concreta
 * con su condición, origen y vencimiento. Concentra las reglas de consumo y
 * de transición de estado.
 */
public class Lote {

    /**
     * Ordena los lotes por vencimiento ascendente con los vencimientos nulos
     * al final, para priorizar el vencimiento más próximo (regla FIFO).
     */
    public static final Comparator<Lote> POR_VENCIMIENTO_MAS_PROXIMO =
            Comparator.comparing(Lote::getVencimiento, Comparator.nullsLast(Comparator.naturalOrder()));

    private final UUID id;
    private final BigDecimal cantidadInicial;
    private BigDecimal cantidadDisponible;
    private final Unidad unidad;
    private final CondicionBien condicion;
    private final OrigenLote origen;
    private final LocalDate fechaIngreso;
    private final LocalDate vencimiento;
    private EstadoLote estado;
    private final Set<MovimientoInventario> historial;

    public Lote(UUID id, BigDecimal cantidadInicial, Unidad unidad, CondicionBien condicion,
                OrigenLote origen, LocalDate fechaIngreso, LocalDate vencimiento) {
        this.id = Objects.requireNonNull(id, "id no puede ser nulo");
        this.cantidadInicial = Objects.requireNonNull(cantidadInicial, "cantidadInicial no puede ser nula");
        if (cantidadInicial.signum() <= 0) {
            throw new IllegalArgumentException("cantidadInicial debe ser positiva");
        }
        this.unidad = Objects.requireNonNull(unidad, "unidad no puede ser nula");
        this.condicion = Objects.requireNonNull(condicion, "condicion no puede ser nula");
        this.origen = Objects.requireNonNull(origen, "origen no puede ser nulo");
        this.fechaIngreso = Objects.requireNonNull(fechaIngreso, "fechaIngreso no puede ser nula");
        this.vencimiento = vencimiento;
        this.cantidadDisponible = cantidadInicial;
        this.estado = condicion == CondicionBien.ACEPTABLE ? EstadoLote.DISPONIBLE : EstadoLote.BLOQUEADO;
        this.historial = new LinkedHashSet<>();
    }

    /**
     * Indica si el lote está vencido a la fecha indicada. Un lote sin
     * vencimiento nunca está vencido.
     *
     * @param fecha fecha de referencia; no puede ser nula
     * @return {@code true} si existe vencimiento y la fecha es posterior a él
     */
    public boolean vencido(LocalDate fecha) {
        Objects.requireNonNull(fecha, "fecha no puede ser nula");
        return vencimiento != null && fecha.isAfter(vencimiento);
    }

    /**
     * Reduce la cantidad disponible del lote y registra su agotamiento cuando
     * corresponde. La dirección del movimiento se determina externamente a
     * través del historial.
     *
     * @param cantidad cantidad a consumir; no puede ser nula y debe ser positiva
     * @throws IllegalArgumentException si la cantidad no es positiva o supera lo disponible
     * @throws IllegalStateException si el lote no está disponible o está vencido
     */
    public void consumir(BigDecimal cantidad) {
        Objects.requireNonNull(cantidad, "cantidad no puede ser nula");
        if (cantidad.signum() <= 0) {
            throw new IllegalArgumentException("cantidad debe ser positiva");
        }
        if (estado != EstadoLote.DISPONIBLE) {
            throw new IllegalStateException("no se puede consumir de un lote no disponible");
        }
        if (vencido(LocalDate.now())) {
            throw new IllegalStateException("no se puede consumir de un lote vencido");
        }
        if (cantidad.compareTo(cantidadDisponible) > 0) {
            throw new IllegalArgumentException("no se puede consumir más de lo disponible");
        }
        this.cantidadDisponible = this.cantidadDisponible.subtract(cantidad);
        if (this.cantidadDisponible.signum() == 0) {
            this.estado = EstadoLote.AGOTADO;
        }
    }

    /**
     * Registra un movimiento en el historial de trazabilidad del lote.
     *
     * @param movimiento movimiento a registrar; no puede ser nulo
     */
    public void agregarMovimiento(MovimientoInventario movimiento) {
        this.historial.add(Objects.requireNonNull(movimiento, "movimiento no puede ser nulo"));
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getCantidadInicial() {
        return cantidadInicial;
    }

    public BigDecimal getCantidadDisponible() {
        return cantidadDisponible;
    }

    public Unidad getUnidad() {
        return unidad;
    }

    public CondicionBien getCondicion() {
        return condicion;
    }

    public OrigenLote getOrigen() {
        return origen;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public LocalDate getVencimiento() {
        return vencimiento;
    }

    public EstadoLote getEstado() {
        return estado;
    }

    /**
     * Historial de movimientos del lote, expuesto como vista inmutable.
     */
    public Set<MovimientoInventario> getHistorial() {
        return Collections.unmodifiableSet(historial);
    }
}
