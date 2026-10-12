package com.refugiotrack.animal;

import com.refugiotrack.seguridad.Usuario;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Ficha del animal: identidad, especie y ciclo de vida operativo. Es la raíz
 * del agregado y concentra las reglas de transición de estado y de
 * consolidación de identidad. Los datos desconocidos pueden quedar
 * explícitamente sin informar.
 */
public class Animal {

    private final UUID id;
    private final Especie especie;
    private final String nombre;
    private final String identificacionProvisoria;
    private final String caracteristicas;
    private final String observaciones;
    private final LocalDate fechaNacimientoEstimada;
    private EstadoAnimal estado;
    private final Set<IdentificadorAnimal> identificadores;

    public Animal(UUID id, Especie especie, String nombre, String identificacionProvisoria,
                  String caracteristicas, String observaciones, LocalDate fechaNacimientoEstimada) {
        this.id = Objects.requireNonNull(id, "id no puede ser nulo");
        this.especie = Objects.requireNonNull(especie, "especie no puede ser nula");
        this.nombre = nombre;
        this.identificacionProvisoria = identificacionProvisoria;
        this.caracteristicas = caracteristicas;
        this.observaciones = observaciones;
        this.fechaNacimientoEstimada = fechaNacimientoEstimada;
        this.estado = EstadoAnimal.PENDIENTE_DE_EVALUACION;
        this.identificadores = new LinkedHashSet<>();
    }

    /**
     * Transiciona el animal hacia otro estado. El actor y el motivo se capturan
     * para trazabilidad de la decisión; la emisión del evento de auditoría se
     * difiere a una entrega posterior.
     *
     * @param objetivo estado de destino; no puede ser nulo
     * @param actor usuario que ejecuta la transición; no puede ser nulo
     * @param motivo justificación de la transición; no puede ser nulo ni vacío
     * @throws IllegalStateException si la transición no está permitida desde el estado actual
     */
    public void cambiarEstado(EstadoAnimal objetivo, Usuario actor, String motivo) {
        Objects.requireNonNull(objetivo, "objetivo no puede ser nulo");
        Objects.requireNonNull(actor, "actor no puede ser nulo");
        requerirTexto(motivo, "motivo");
        if (!estado.puedeTransicionarA(objetivo)) {
            throw new IllegalStateException(
                    "no se permite la transición de " + estado + " a " + objetivo);
        }
        this.estado = objetivo;
    }

    /**
     * Consolida la identidad del animal incorporando un identificador. Los
     * duplicados se descartan por igualdad del valor.
     *
     * @param identificador identificador a incorporar; no puede ser nulo
     */
    public void consolidarIdentidad(IdentificadorAnimal identificador) {
        this.identificadores.add(
                Objects.requireNonNull(identificador, "identificador no puede ser nulo"));
    }

    public UUID getId() {
        return id;
    }

    public Especie getEspecie() {
        return especie;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdentificacionProvisoria() {
        return identificacionProvisoria;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public LocalDate getFechaNacimientoEstimada() {
        return fechaNacimientoEstimada;
    }

    public EstadoAnimal getEstado() {
        return estado;
    }

    /**
     * Identificadores consolidados del animal, expuestos como vista inmutable.
     */
    public Set<IdentificadorAnimal> getIdentificadores() {
        return Collections.unmodifiableSet(identificadores);
    }

    private static String requerirTexto(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío");
        }
        return valor;
    }
}
