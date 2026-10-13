package com.refugiotrack.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Recurso del catálogo: insumo o bien cuya disponibilidad se controla a
 * través de lotes trazables. Es la raíz del agregado y concentra el cálculo
 * de stock, el control de umbrales y la selección del lote a consumir según
 * el criterio FIFO. Se asume que todos los lotes de un recurso comparten su
 * unidad base.
 */
public class Recurso {

    private final UUID id;
    private final String nombre;
    private final CategoriaRecurso categoria;
    private final Unidad unidadBase;
    private final boolean perecedero;
    private final BigDecimal umbralStock;
    private final boolean requiereAutorizacion;
    private final Set<Lote> lotes;
    private final Set<Alerta> alertas;

    public Recurso(UUID id, String nombre, CategoriaRecurso categoria, Unidad unidadBase,
                   boolean perecedero, BigDecimal umbralStock, boolean requiereAutorizacion) {
        this.id = Objects.requireNonNull(id, "id no puede ser nulo");
        this.nombre = requerirTexto(nombre, "nombre");
        this.categoria = Objects.requireNonNull(categoria, "categoria no puede ser nula");
        this.unidadBase = Objects.requireNonNull(unidadBase, "unidadBase no puede ser nula");
        this.perecedero = perecedero;
        this.umbralStock = Objects.requireNonNull(umbralStock, "umbralStock no puede ser nulo");
        if (umbralStock.signum() < 0) {
            throw new IllegalArgumentException("umbralStock no puede ser negativo");
        }
        this.requiereAutorizacion = requiereAutorizacion;
        this.lotes = new LinkedHashSet<>();
        this.alertas = new LinkedHashSet<>();
    }

    /**
     * Incorpora un lote al recurso como parte de su agregado.
     *
     * @param lote lote a incorporar; no puede ser nulo
     */
    public void agregarLote(Lote lote) {
        this.lotes.add(Objects.requireNonNull(lote, "lote no puede ser nulo"));
    }

    /**
     * Incorpora una alerta al recurso como parte de su agregado.
     *
     * @param alerta alerta a incorporar; no puede ser nula
     */
    public void agregarAlerta(Alerta alerta) {
        this.alertas.add(Objects.requireNonNull(alerta, "alerta no puede ser nula"));
    }

    /**
     * Cantidad total disponible, resultante de sumar la cantidad disponible de
     * todos los lotes. Se asume que los lotes comparten la unidad base del
     * recurso, por lo que la suma es homogénea.
     *
     * @return cantidad disponible total; nunca nula
     */
    public BigDecimal cantidadDisponible() {
        BigDecimal total = BigDecimal.ZERO;
        for (Lote lote : lotes) {
            total = total.add(lote.getCantidadDisponible());
        }
        return total;
    }

    /**
     * Indica si la cantidad disponible se encuentra por debajo del umbral de
     * stock configurado.
     *
     * @return {@code true} si la cantidad disponible es menor que el umbral
     */
    public boolean stockBajo() {
        return cantidadDisponible().compareTo(umbralStock) < 0;
    }

    /**
     * Selecciona el lote apto con el vencimiento más próximo (FIFO). Se
     * consideran aptos los lotes en estado {@link EstadoLote#DISPONIBLE} que
     * no estén vencidos a la fecha indicada.
     *
     * @param fecha fecha de referencia para determinar el vencimiento; no puede ser nula
     * @return el lote apto con vencimiento más próximo, o vacío si no hay ninguno
     */
    public Optional<Lote> loteAptoMasProximoAVencer(LocalDate fecha) {
        Objects.requireNonNull(fecha, "fecha no puede ser nula");
        return lotes.stream()
                .filter(lote -> lote.getEstado() == EstadoLote.DISPONIBLE && !lote.vencido(fecha))
                .min(Lote.POR_VENCIMIENTO_MAS_PROXIMO);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public CategoriaRecurso getCategoria() {
        return categoria;
    }

    public Unidad getUnidadBase() {
        return unidadBase;
    }

    public boolean isPerecedero() {
        return perecedero;
    }

    public BigDecimal getUmbralStock() {
        return umbralStock;
    }

    public boolean isRequiereAutorizacion() {
        return requiereAutorizacion;
    }

    /**
     * Lotes del recurso, expuestos como vista inmutable.
     */
    public Set<Lote> getLotes() {
        return Collections.unmodifiableSet(lotes);
    }

    /**
     * Alertas del recurso, expuestas como vista inmutable.
     */
    public Set<Alerta> getAlertas() {
        return Collections.unmodifiableSet(alertas);
    }

    private static String requerirTexto(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío");
        }
        return valor;
    }
}
