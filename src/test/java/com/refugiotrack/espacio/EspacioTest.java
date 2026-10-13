package com.refugiotrack.espacio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.refugiotrack.animal.Animal;
import com.refugiotrack.animal.Especie;
import com.refugiotrack.shared.Tamanio;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class EspacioTest {

    private static Sede sede() {
        return new Sede(UUID.randomUUID(), "Sede central", "Av. Siempre Viva 123", ZoneId.of("UTC"));
    }

    private static Espacio espacio(int capacidad, Set<Especie> especies, Set<Tamanio> tamanios) {
        return new Espacio(
                UUID.randomUUID(), sede(), "C-01", TipoEspacio.CANIL,
                capacidad, especies, tamanios);
    }

    private static Animal animal(Especie especie, Tamanio tamanio, LocalDateTime fechaIngreso) {
        return new Animal(
                UUID.randomUUID(), especie, "firulais", "TMP-001",
                "mestizo", "rescatado", null, tamanio, fechaIngreso);
    }

    @Test
    void ocuparYLiberarMantienenLaOcupacionDentroDeCapacidad() {
        Espacio espacio = espacio(1, Set.of(Especie.PERRO), Set.of(Tamanio.MEDIANO));

        assertThat(espacio.ocupacionActual()).isZero();
        assertThat(espacio.hayLugar()).isTrue();

        espacio.ocupar();

        assertThat(espacio.ocupacionActual()).isEqualTo(1);
        assertThat(espacio.hayLugar()).isFalse();
        assertThatThrownBy(espacio::ocupar).isInstanceOf(IllegalStateException.class);

        espacio.liberar();

        assertThat(espacio.ocupacionActual()).isZero();
        assertThat(espacio.hayLugar()).isTrue();
    }

    @Test
    void liberarConOcupacionCeroLanzaExcepcion() {
        Espacio espacio = espacio(1, Set.of(Especie.PERRO), Set.of(Tamanio.MEDIANO));

        assertThatThrownBy(espacio::liberar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void admiteRechazaEspecieNoAdmitida() {
        Espacio espacio = espacio(2, Set.of(Especie.PERRO), Set.of(Tamanio.MEDIANO));

        assertThat(espacio.admite(animal(Especie.GATO, Tamanio.MEDIANO, null))).isFalse();
        assertThat(espacio.admite(animal(Especie.PERRO, Tamanio.MEDIANO, null))).isTrue();
    }

    @Test
    void admiteRechazaTamanioNoAdmitido() {
        Espacio espacio = espacio(2, Set.of(Especie.PERRO), Set.of(Tamanio.MEDIANO));

        assertThat(espacio.admite(animal(Especie.PERRO, Tamanio.GRANDE, null))).isFalse();
        assertThat(espacio.admite(animal(Especie.PERRO, Tamanio.MEDIANO, null))).isTrue();
        assertThat(espacio.admite(animal(Especie.PERRO, null, null))).isTrue();
    }

    @Test
    void admiteRechazaEspacioCerradoOMantenimiento() {
        Espacio espacio = espacio(1, Set.of(Especie.PERRO), Set.of(Tamanio.MEDIANO));
        Animal perro = animal(Especie.PERRO, Tamanio.MEDIANO, null);

        assertThat(espacio.hayLugar()).isTrue();
        assertThat(espacio.admite(perro)).isTrue();

        espacio.cambiarEstado(EstadoEspacio.CERRADO);
        assertThat(espacio.admite(perro)).isFalse();

        espacio.cambiarEstado(EstadoEspacio.EN_MANTENIMIENTO);
        assertThat(espacio.admite(perro)).isFalse();
    }

    @Test
    void admiteConsideraLasReglas() {
        Espacio espacio = espacio(2, Set.of(Especie.PERRO), Set.of(Tamanio.MEDIANO));
        espacio.agregarRegla(new RestriccionAislamiento(Duration.ofDays(7)));

        Animal reciente = animal(Especie.PERRO, Tamanio.MEDIANO, LocalDateTime.now());
        Animal antiguo = animal(Especie.PERRO, Tamanio.MEDIANO, LocalDateTime.now().minusDays(30));

        assertThat(espacio.getEstado()).isEqualTo(EstadoEspacio.DISPONIBLE);
        assertThat(espacio.hayLugar()).isTrue();
        assertThat(espacio.admite(reciente)).isFalse();
        assertThat(espacio.admite(antiguo)).isTrue();
    }
}
