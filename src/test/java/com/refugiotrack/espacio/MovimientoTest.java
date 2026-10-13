package com.refugiotrack.espacio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.refugiotrack.seguridad.Rol;
import com.refugiotrack.seguridad.Usuario;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class MovimientoTest {

    @Test
    void movimientoEsInmutable() {
        UUID id = UUID.randomUUID();
        TipoMovimiento tipo = TipoMovimiento.TRASLADO;
        LocalDateTime fechaHora = LocalDateTime.of(2026, 4, 1, 9, 30);
        String motivo = "reubicacion por capacidad";
        Ubicacion origen = new UbicacionExterna(UUID.randomUUID(), "Sede origen", "contacto origen");
        Ubicacion destino = new UbicacionExterna(UUID.randomUUID(), "Sede destino", null);
        Usuario responsable = new Usuario(UUID.randomUUID(), "ana", "hash", true, Rol.ADMISIONES);

        Movimiento movimiento = new Movimiento(id, tipo, fechaHora, motivo, origen, destino, responsable);

        assertThat(movimiento.esInmutable()).isTrue();
        assertThat(movimiento.getId()).isEqualTo(id);
        assertThat(movimiento.getTipo()).isEqualTo(tipo);
        assertThat(movimiento.getFechaHora()).isEqualTo(fechaHora);
        assertThat(movimiento.getMotivo()).isEqualTo(motivo);
        assertThat(movimiento.getOrigen()).isSameAs(origen);
        assertThat(movimiento.getDestino()).isSameAs(destino);
        assertThat(movimiento.getResponsable()).isSameAs(responsable);
    }

    @Test
    void movimientoRechazaResponsableNulo() {
        assertThatThrownBy(() -> new Movimiento(
                UUID.randomUUID(), TipoMovimiento.EGRESO, LocalDateTime.now(),
                "egreso documentado", null, null, null))
                .isInstanceOf(NullPointerException.class);
    }
}
