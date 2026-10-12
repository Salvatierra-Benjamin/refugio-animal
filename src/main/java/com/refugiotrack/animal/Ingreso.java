package com.refugiotrack.animal;

import com.refugiotrack.seguridad.Usuario;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Episodio de ingreso de un animal: registra origen, motivo, fecha y hora,
 * responsable, evaluación inicial y la decisión de admisión resultante. Un
 * ingreso se decide a lo sumo una vez; la decisión coordina el estado del
 * animal asociado.
 */
public class Ingreso {

    private final UUID id;
    private final Animal animal;
    private final OrigenIngreso origen;
    private final String motivo;
    private final LocalDateTime fechaHora;
    private final String evaluacionInicial;
    private DecisionIngreso decision;
    private String motivoDecision;
    private final Usuario responsable;

    public Ingreso(UUID id, Animal animal, OrigenIngreso origen, String motivo,
                   LocalDateTime fechaHora, String evaluacionInicial, Usuario responsable) {
        this.id = Objects.requireNonNull(id, "id no puede ser nulo");
        this.animal = Objects.requireNonNull(animal, "animal no puede ser nulo");
        this.origen = Objects.requireNonNull(origen, "origen no puede ser nulo");
        this.motivo = requerirTexto(motivo, "motivo");
        this.fechaHora = Objects.requireNonNull(fechaHora, "fechaHora no puede ser nula");
        this.evaluacionInicial = evaluacionInicial;
        this.responsable = Objects.requireNonNull(responsable, "responsable no puede ser nulo");
        this.decision = DecisionIngreso.PENDIENTE;
        this.motivoDecision = null;
    }

    /**
     * Registra la decisión de admisión del ingreso y coordina el estado del
     * animal asociado. Un ingreso no puede decidirse como pendiente ni decidirse
     * más de una vez.
     *
     * @param decision resultado de la decisión; no puede ser nulo ni {@code PENDIENTE}
     * @param motivo justificación de la decisión; no puede ser nulo ni vacío
     * @param actor usuario que ejecuta la decisión; no puede ser nulo
     * @throws IllegalArgumentException si la decisión es {@code PENDIENTE} o el motivo es nulo/vacío
     * @throws IllegalStateException si el ingreso ya fue decidido
     */
    public void decidir(DecisionIngreso decision, String motivo, Usuario actor) {
        Objects.requireNonNull(decision, "decision no puede ser nula");
        Objects.requireNonNull(actor, "actor no puede ser nulo");
        requerirTexto(motivo, "motivo");
        if (decision == DecisionIngreso.PENDIENTE) {
            throw new IllegalArgumentException("no se puede decidir un ingreso como pendiente");
        }
        if (this.decision != DecisionIngreso.PENDIENTE) {
            throw new IllegalStateException("el ingreso ya fue decidido");
        }
        this.decision = decision;
        this.motivoDecision = motivo;
        switch (decision) {
            case ADMITIDO -> animal.cambiarEstado(EstadoAnimal.EN_REFUGIO, actor, motivo);
            case RECHAZADO, DERIVADO -> animal.cambiarEstado(EstadoAnimal.EGRESADO, actor, motivo);
            default -> throw new IllegalStateException("decisión no contemplada: " + decision);
        }
    }

    public UUID getId() {
        return id;
    }

    public Animal getAnimal() {
        return animal;
    }

    public OrigenIngreso getOrigen() {
        return origen;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getEvaluacionInicial() {
        return evaluacionInicial;
    }

    public DecisionIngreso getDecision() {
        return decision;
    }

    public String getMotivoDecision() {
        return motivoDecision;
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
