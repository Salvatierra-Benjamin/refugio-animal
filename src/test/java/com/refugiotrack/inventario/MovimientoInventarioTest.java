package com.refugiotrack.inventario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class MovimientoInventarioTest {

    private static MovimientoInventario movimientoValido() {
        return new MovimientoInventario(UUID.randomUUID(), TipoMovimientoInventario.INGRESO,
                BigDecimal.ONE, Unidad.UNIDAD, "ingreso por compra", LocalDateTime.now(), "IDEM-1");
    }

    @Test
    void movimientoRechazaCamposNulosOEnBlanco() {
        assertThatThrownBy(() -> new MovimientoInventario(UUID.randomUUID(),
                TipoMovimientoInventario.INGRESO, BigDecimal.ONE, Unidad.UNIDAD, " ",
                LocalDateTime.now(), "IDEM-1"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new MovimientoInventario(UUID.randomUUID(),
                TipoMovimientoInventario.INGRESO, BigDecimal.ONE, Unidad.UNIDAD, "motivo",
                LocalDateTime.now(), " "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new MovimientoInventario(UUID.randomUUID(),
                TipoMovimientoInventario.INGRESO, BigDecimal.ZERO, Unidad.UNIDAD, "motivo",
                LocalDateTime.now(), "IDEM-1"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new MovimientoInventario(UUID.randomUUID(),
                TipoMovimientoInventario.INGRESO, BigDecimal.valueOf(-1), Unidad.UNIDAD, "motivo",
                LocalDateTime.now(), "IDEM-1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void movimientoConservaSusCampos() {
        UUID id = UUID.randomUUID();
        TipoMovimientoInventario tipo = TipoMovimientoInventario.CONSUMO;
        BigDecimal cantidad = new BigDecimal("2.5");
        Unidad unidad = Unidad.KILOGRAMO;
        String motivo = "consumo diario";
        LocalDateTime fechaHora = LocalDateTime.of(2026, 4, 1, 9, 30);
        String clave = "IDEM-42";

        MovimientoInventario movimiento = new MovimientoInventario(id, tipo, cantidad, unidad,
                motivo, fechaHora, clave);

        assertThat(movimiento.getId()).isEqualTo(id);
        assertThat(movimiento.getTipo()).isEqualTo(tipo);
        assertThat(movimiento.getCantidad()).isEqualByComparingTo(cantidad);
        assertThat(movimiento.getUnidad()).isEqualTo(unidad);
        assertThat(movimiento.getMotivo()).isEqualTo(motivo);
        assertThat(movimiento.getFechaHora()).isEqualTo(fechaHora);
        assertThat(movimiento.getClaveIdempotencia()).isEqualTo(clave);
    }
}
