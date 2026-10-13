package com.refugiotrack.espacio;

import static org.assertj.core.api.Assertions.assertThat;

import com.refugiotrack.animal.Animal;
import com.refugiotrack.animal.Especie;
import com.refugiotrack.shared.Tamanio;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ReglaEspacioTest {

    private static Espacio espacio(Set<Especie> especies, Set<Tamanio> tamanios) {
        Sede sede = new Sede(UUID.randomUUID(), "Sede central", null, ZoneId.of("UTC"));
        return new Espacio(
                UUID.randomUUID(), sede, "C-01", TipoEspacio.CANIL,
                1, especies, tamanios);
    }

    private static Animal animal(Especie especie, Tamanio tamanio, LocalDateTime fechaIngreso) {
        return new Animal(
                UUID.randomUUID(), especie, "firulais", "TMP-001",
                "mestizo", "rescatado", null, tamanio, fechaIngreso);
    }

    @Test
    void restriccionEspecieCumpleSegunEspecieAdmitida() {
        RestriccionEspecie regla = new RestriccionEspecie();
        Espacio espacio = espacio(Set.of(Especie.PERRO), Set.of(Tamanio.MEDIANO));

        assertThat(regla.cumple(animal(Especie.PERRO, Tamanio.MEDIANO, null), espacio)).isTrue();
        assertThat(regla.cumple(animal(Especie.GATO, Tamanio.MEDIANO, null), espacio)).isFalse();
    }

    @Test
    void restriccionTamanioIgnoraTamanioDesconocido() {
        RestriccionTamanio regla = new RestriccionTamanio();
        Espacio espacio = espacio(Set.of(Especie.PERRO), Set.of(Tamanio.MEDIANO));

        assertThat(regla.cumple(animal(Especie.PERRO, null, null), espacio)).isTrue();
    }

    @Test
    void restriccionAislamientoRechazaIngresoReciente() {
        RestriccionAislamiento regla = new RestriccionAislamiento(Duration.ofDays(7));
        Espacio espacio = espacio(Set.of(Especie.PERRO), Set.of(Tamanio.MEDIANO));

        assertThat(regla.cumple(animal(Especie.PERRO, Tamanio.MEDIANO, LocalDateTime.now()), espacio))
                .isFalse();
    }
}
