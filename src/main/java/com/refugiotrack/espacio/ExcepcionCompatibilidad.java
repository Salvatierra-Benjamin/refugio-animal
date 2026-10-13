package com.refugiotrack.espacio;

import com.refugiotrack.animal.Animal;
import com.refugiotrack.seguridad.Usuario;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Autorización puntual para alojar un animal en un espacio que no cumple una
 * regla de compatibilidad. Conserva la regla incumplida, el motivo y el
 * usuario autorizante para auditoría.
 */
public class ExcepcionCompatibilidad {

    private final UUID id;
    private final String reglaIncumplida;
    private final String motivo;
    private final LocalDateTime fechaHora;
    private final Animal animal;
    private final Espacio espacio;
    private final Usuario autorizante;

    public ExcepcionCompatibilidad(UUID id, String reglaIncumplida, String motivo,
                                   LocalDateTime fechaHora, Animal animal, Espacio espacio,
                                   Usuario autorizante) {
        this.id = Objects.requireNonNull(id, "id no puede ser nulo");
        this.reglaIncumplida = requerirTexto(reglaIncumplida, "reglaIncumplida");
        this.motivo = requerirTexto(motivo, "motivo");
        this.fechaHora = Objects.requireNonNull(fechaHora, "fechaHora no puede ser nula");
        this.animal = Objects.requireNonNull(animal, "animal no puede ser nulo");
        this.espacio = Objects.requireNonNull(espacio, "espacio no puede ser nulo");
        this.autorizante = Objects.requireNonNull(autorizante, "autorizante no puede ser nulo");
    }

    public UUID getId() {
        return id;
    }

    public String getReglaIncumplida() {
        return reglaIncumplida;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Animal getAnimal() {
        return animal;
    }

    public Espacio getEspacio() {
        return espacio;
    }

    public Usuario getAutorizante() {
        return autorizante;
    }

    private static String requerirTexto(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío");
        }
        return valor;
    }
}
