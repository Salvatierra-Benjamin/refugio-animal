package com.refugiotrack.animal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.refugiotrack.seguridad.Rol;
import com.refugiotrack.seguridad.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class IngresoTest {

    private static Usuario actor() {
        return new Usuario(UUID.randomUUID(), "ana", "hash", true, Rol.ADMISIONES);
    }

    private static Animal animalNuevo() {
        return new Animal(
                UUID.randomUUID(), Especie.PERRO, "firulais", "TMP-001",
                "mestizo", null, LocalDate.of(2022, 3, 15));
    }

    private static Animal enAdmision() {
        Animal animal = animalNuevo();
        animal.cambiarEstado(EstadoAnimal.EN_ADMISION, actor(), "evaluación iniciada");
        return animal;
    }

    private static Animal adoptado() {
        Animal animal = enAdmision();
        animal.cambiarEstado(EstadoAnimal.EN_REFUGIO, actor(), "admisión confirmada");
        animal.cambiarEstado(EstadoAnimal.ADOPTADO, actor(), "adopción completada");
        return animal;
    }

    private static Ingreso ingresoPendiente(Animal animal, OrigenIngreso origen) {
        return new Ingreso(
                UUID.randomUUID(), animal, origen, "ingreso al refugio",
                LocalDateTime.now(), null, actor());
    }

    @Test
    void decidirAdmitidoFijaLaDecisionYMueveElAnimalARefugio() {
        Animal animal = enAdmision();
        Ingreso ingreso = ingresoPendiente(animal, OrigenIngreso.RESCATE);

        ingreso.decidir(DecisionIngreso.ADMITIDO, "evaluación satisfactoria", actor());

        assertThat(ingreso.getDecision()).isEqualTo(DecisionIngreso.ADMITIDO);
        assertThat(ingreso.getMotivoDecision()).isEqualTo("evaluación satisfactoria");
        assertThat(animal.getEstado()).isEqualTo(EstadoAnimal.EN_REFUGIO);
    }

    @Test
    void decidirRechazadoODerivadoMueveElAnimalAEgresado() {
        for (DecisionIngreso decision : new DecisionIngreso[]{DecisionIngreso.RECHAZADO, DecisionIngreso.DERIVADO}) {
            Animal animal = enAdmision();
            Ingreso ingreso = ingresoPendiente(animal, OrigenIngreso.RESCATE);

            ingreso.decidir(decision, "no apto para admisión", actor());

            assertThat(ingreso.getDecision()).isEqualTo(decision);
            assertThat(animal.getEstado()).isEqualTo(EstadoAnimal.EGRESADO);
        }
    }

    @Test
    void decidirPendienteLanzaIllegalArgumentException() {
        Animal animal = enAdmision();
        Ingreso ingreso = ingresoPendiente(animal, OrigenIngreso.RESCATE);

        assertThatThrownBy(() -> ingreso.decidir(DecisionIngreso.PENDIENTE, "sin decisión", actor()))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(ingreso.getDecision()).isEqualTo(DecisionIngreso.PENDIENTE);
        assertThat(animal.getEstado()).isEqualTo(EstadoAnimal.EN_ADMISION);
    }

    @Test
    void decidirDosVecesLanzaIllegalStateException() {
        Animal animal = enAdmision();
        Ingreso ingreso = ingresoPendiente(animal, OrigenIngreso.RESCATE);

        ingreso.decidir(DecisionIngreso.ADMITIDO, "evaluación satisfactoria", actor());

        assertThatThrownBy(() -> ingreso.decidir(DecisionIngreso.RECHAZADO, "cambio de parecer", actor()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void devolucionDeUnAnimalAdoptadoGeneraUnNuevoIngresoQueLoDevuelveARefugio() {
        Animal animal = adoptado();

        Ingreso ingreso = ingresoPendiente(animal, OrigenIngreso.DEVOLUCION);
        ingreso.decidir(DecisionIngreso.ADMITIDO, "devolución aceptada", actor());

        assertThat(ingreso.getOrigen()).isEqualTo(OrigenIngreso.DEVOLUCION);
        assertThat(ingreso.getDecision()).isEqualTo(DecisionIngreso.ADMITIDO);
        assertThat(animal.getEstado()).isEqualTo(EstadoAnimal.EN_REFUGIO);
    }
}
