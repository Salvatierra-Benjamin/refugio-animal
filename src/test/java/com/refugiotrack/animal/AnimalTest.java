package com.refugiotrack.animal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.refugiotrack.seguridad.Rol;
import com.refugiotrack.seguridad.Usuario;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class AnimalTest {

    private static Usuario actor() {
        return new Usuario(UUID.randomUUID(), "ana", "hash", true, Rol.ADMISIONES);
    }

    private static Animal animalNuevo() {
        return new Animal(
                UUID.randomUUID(), Especie.PERRO, "firulais", "TMP-001",
                "mestizo, tamaño mediano", "rescatado de la calle", LocalDate.of(2022, 3, 15));
    }

    private static Animal enEstado(EstadoAnimal objetivo) {
        Animal animal = animalNuevo();
        Usuario actor = actor();
        switch (objetivo) {
            case EN_ADMISION:
                animal.cambiarEstado(EstadoAnimal.EN_ADMISION, actor, "evaluación iniciada");
                break;
            case EN_REFUGIO:
                animal.cambiarEstado(EstadoAnimal.EN_ADMISION, actor, "evaluación iniciada");
                animal.cambiarEstado(EstadoAnimal.EN_REFUGIO, actor, "admisión confirmada");
                break;
            case EN_TRANSITO_EXTERNO:
                animal.cambiarEstado(EstadoAnimal.EN_ADMISION, actor, "evaluación iniciada");
                animal.cambiarEstado(EstadoAnimal.EN_REFUGIO, actor, "admisión confirmada");
                animal.cambiarEstado(EstadoAnimal.EN_TRANSITO_EXTERNO, actor, "tránsito externo");
                break;
            case ADOPTADO:
                animal.cambiarEstado(EstadoAnimal.EN_ADMISION, actor, "evaluación iniciada");
                animal.cambiarEstado(EstadoAnimal.EN_REFUGIO, actor, "admisión confirmada");
                animal.cambiarEstado(EstadoAnimal.ADOPTADO, actor, "adopción completada");
                break;
            case EGRESADO:
                animal.cambiarEstado(EstadoAnimal.EN_ADMISION, actor, "evaluación iniciada");
                animal.cambiarEstado(EstadoAnimal.EGRESADO, actor, "egreso documentado");
                break;
            case FALLECIDO:
                animal.cambiarEstado(EstadoAnimal.EN_ADMISION, actor, "evaluación iniciada");
                animal.cambiarEstado(EstadoAnimal.EN_REFUGIO, actor, "admisión confirmada");
                animal.cambiarEstado(EstadoAnimal.FALLECIDO, actor, "fallecimiento registrado");
                break;
            case PENDIENTE_DE_EVALUACION:
                break;
        }
        return animal;
    }

    @Test
    void elEstadoInicialEsPendienteDeEvaluacion() {
        assertThat(animalNuevo().getEstado()).isEqualTo(EstadoAnimal.PENDIENTE_DE_EVALUACION);
    }

    @Test
    void elMapaDeTransicionesEsElDocumentado() {
        assertThat(EstadoAnimal.PENDIENTE_DE_EVALUACION.transicionesPermitidas())
                .containsExactly(EstadoAnimal.EN_ADMISION);
        assertThat(EstadoAnimal.EN_ADMISION.transicionesPermitidas())
                .containsExactlyInAnyOrder(EstadoAnimal.EN_REFUGIO, EstadoAnimal.EGRESADO);
        assertThat(EstadoAnimal.EN_REFUGIO.transicionesPermitidas())
                .containsExactlyInAnyOrder(
                        EstadoAnimal.EN_TRANSITO_EXTERNO, EstadoAnimal.ADOPTADO,
                        EstadoAnimal.EGRESADO, EstadoAnimal.FALLECIDO);
        assertThat(EstadoAnimal.EN_TRANSITO_EXTERNO.transicionesPermitidas())
                .containsExactly(EstadoAnimal.EN_REFUGIO);
        assertThat(EstadoAnimal.ADOPTADO.transicionesPermitidas())
                .containsExactly(EstadoAnimal.EN_REFUGIO);
        assertThat(EstadoAnimal.EGRESADO.transicionesPermitidas()).isEmpty();
        assertThat(EstadoAnimal.FALLECIDO.transicionesPermitidas()).isEmpty();
    }

    @Test
    void cadaTransicionPermitidaCambiaElEstado() {
        for (EstadoAnimal origen : EstadoAnimal.values()) {
            for (EstadoAnimal destino : origen.transicionesPermitidas()) {
                Animal animal = enEstado(origen);

                animal.cambiarEstado(destino, actor(), "transición permitida");

                assertThat(animal.getEstado()).isEqualTo(destino);
            }
        }
    }

    @Test
    void cadaTransicionNoPermitidaLanzaIllegalStateException() {
        for (EstadoAnimal origen : EstadoAnimal.values()) {
            for (EstadoAnimal destino : EstadoAnimal.values()) {
                if (origen.transicionesPermitidas().contains(destino)) {
                    continue;
                }
                Animal animal = enEstado(origen);

                assertThatThrownBy(() -> animal.cambiarEstado(destino, actor(), "transición inválida"))
                        .as("transición no permitida: %s -> %s", origen, destino)
                        .isInstanceOf(IllegalStateException.class);
            }
        }
    }

    @Test
    void cambiarEstadoRechazaObjetivoActorYMotivoNulosOEnBlanco() {
        Animal animal = animalNuevo();

        assertThatThrownBy(() -> animal.cambiarEstado(null, actor(), "motivo"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> animal.cambiarEstado(EstadoAnimal.EN_ADMISION, null, "motivo"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> animal.cambiarEstado(EstadoAnimal.EN_ADMISION, actor(), null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> animal.cambiarEstado(EstadoAnimal.EN_ADMISION, actor(), " "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void consolidarIdentidadAgregaYDesduplicaIdentificadores() {
        Animal animal = animalNuevo();
        IdentificadorAnimal chip = new IdentificadorAnimal(TipoIdentificador.CHIP, "CHIP-123");

        animal.consolidarIdentidad(chip);
        assertThat(animal.getIdentificadores()).containsExactly(chip);

        animal.consolidarIdentidad(chip);
        assertThat(animal.getIdentificadores()).hasSize(1);

        IdentificadorAnimal tatuaje = new IdentificadorAnimal(TipoIdentificador.TATUAJE, "TAT-9");
        animal.consolidarIdentidad(tatuaje);
        assertThat(animal.getIdentificadores()).containsExactlyInAnyOrder(chip, tatuaje);
    }

    @Test
    void consolidarIdentidadRechazaIdentificadorNulo() {
        Animal animal = animalNuevo();

        assertThatThrownBy(() -> animal.consolidarIdentidad(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getIdentificadoresDevuelveUnaVistaInmodificable() {
        Animal animal = animalNuevo();
        animal.consolidarIdentidad(new IdentificadorAnimal(TipoIdentificador.CHIP, "CHIP-123"));

        assertThatThrownBy(() -> animal.getIdentificadores().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
