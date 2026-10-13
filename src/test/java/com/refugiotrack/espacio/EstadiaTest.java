package com.refugiotrack.espacio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.refugiotrack.animal.Animal;
import com.refugiotrack.animal.Especie;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class EstadiaTest {

    private static Animal animal() {
        return new Animal(
                UUID.randomUUID(), Especie.PERRO, "firulais", "TMP-001",
                "mestizo", "rescatado", null);
    }

    private static Estadia estadia(LocalDateTime inicio) {
        Ubicacion ubicacion = new UbicacionExterna(UUID.randomUUID(), "Hogar transito", null);
        return new Estadia(UUID.randomUUID(), animal(), ubicacion, inicio);
    }

    @Test
    void cerrarTransicionaDeActivaAFinalizada() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime fin = inicio.plusDays(2);
        Estadia estadia = estadia(inicio);

        estadia.cerrar(fin);

        assertThat(estadia.getEstado()).isEqualTo(EstadoEstadia.FINALIZADA);
        assertThat(estadia.getFin()).isEqualTo(fin);
    }

    @Test
    void cerrarDosVecesLanzaExcepcion() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 10, 0);
        Estadia estadia = estadia(inicio);
        estadia.cerrar(inicio.plusDays(1));

        assertThatThrownBy(() -> estadia.cerrar(inicio.plusDays(2)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cerrarConFinAnteriorAlInicioLanzaExcepcion() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 10, 0);
        Estadia estadia = estadia(inicio);

        assertThatThrownBy(() -> estadia.cerrar(inicio.minusSeconds(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
