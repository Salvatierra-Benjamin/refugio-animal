package com.refugiotrack.espacio;

import com.refugiotrack.animal.Animal;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Período en el que un animal permanece en una ubicación. Se abre activa y se
 * cierra una sola vez, con un fin no anterior al inicio.
 */
public class Estadia {

    private final UUID id;
    private final Animal animal;
    private final Ubicacion ubicacion;
    private final LocalDateTime inicio;
    private LocalDateTime fin;
    private EstadoEstadia estado;

    public Estadia(UUID id, Animal animal, Ubicacion ubicacion, LocalDateTime inicio) {
        this.id = Objects.requireNonNull(id, "id no puede ser nulo");
        this.animal = Objects.requireNonNull(animal, "animal no puede ser nulo");
        this.ubicacion = Objects.requireNonNull(ubicacion, "ubicacion no puede ser nula");
        this.inicio = Objects.requireNonNull(inicio, "inicio no puede ser nulo");
        this.fin = null;
        this.estado = EstadoEstadia.ACTIVA;
    }

    /**
     * Cierra la estadía. Solo puede cerrarse una estadía activa, y el fin no
     * puede ser anterior al inicio.
     *
     * @param fin instante de cierre; no puede ser nulo ni anterior al inicio
     * @throws IllegalStateException si la estadía no está activa
     * @throws IllegalArgumentException si el fin es anterior al inicio
     */
    public void cerrar(LocalDateTime fin) {
        Objects.requireNonNull(fin, "fin no puede ser nulo");
        if (estado != EstadoEstadia.ACTIVA) {
            throw new IllegalStateException("no se puede cerrar una estadía que no está activa");
        }
        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("fin no puede ser anterior al inicio");
        }
        this.fin = fin;
        this.estado = EstadoEstadia.FINALIZADA;
    }

    public UUID getId() {
        return id;
    }

    public Animal getAnimal() {
        return animal;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public EstadoEstadia getEstado() {
        return estado;
    }

    /**
     * Indica si la estadía sigue activa.
     */
    public boolean estaActiva() {
        return estado == EstadoEstadia.ACTIVA;
    }
}
