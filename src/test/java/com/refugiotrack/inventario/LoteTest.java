package com.refugiotrack.inventario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class LoteTest {

    private static Lote loteAceptable(int cantidad) {
        return new Lote(UUID.randomUUID(), BigDecimal.valueOf(cantidad), Unidad.UNIDAD,
                CondicionBien.ACEPTABLE, OrigenLote.COMPRA, LocalDate.now(), null);
    }

    @Test
    void vencidoEsFalsoSinVencimiento() {
        Lote lote = loteAceptable(10);

        assertThat(lote.vencido(LocalDate.now().plusYears(10))).isFalse();
    }

    @Test
    void vencidoEsVerdaderoTrasLaFecha() {
        Lote vencidoAyer = new Lote(UUID.randomUUID(), BigDecimal.TEN, Unidad.UNIDAD,
                CondicionBien.ACEPTABLE, OrigenLote.COMPRA, LocalDate.now(),
                LocalDate.now().minusDays(1));
        Lote venceManana = new Lote(UUID.randomUUID(), BigDecimal.TEN, Unidad.UNIDAD,
                CondicionBien.ACEPTABLE, OrigenLote.COMPRA, LocalDate.now(),
                LocalDate.now().plusDays(1));

        assertThat(vencidoAyer.vencido(LocalDate.now())).isTrue();
        assertThat(venceManana.vencido(LocalDate.now())).isFalse();
    }

    @Test
    void consumirReduceLaCantidadYAgota() {
        Lote lote = loteAceptable(10);

        lote.consumir(BigDecimal.valueOf(4));
        assertThat(lote.getCantidadDisponible()).isEqualByComparingTo("6");

        lote.consumir(BigDecimal.valueOf(6));
        assertThat(lote.getCantidadDisponible()).isEqualByComparingTo("0");
        assertThat(lote.getEstado()).isEqualTo(EstadoLote.AGOTADO);
    }

    @Test
    void consumirNoSuperaLoDisponible() {
        Lote lote = loteAceptable(5);

        assertThatThrownBy(() -> lote.consumir(BigDecimal.valueOf(6)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void consumirRechazaCantidadNoPositiva() {
        Lote lote = loteAceptable(5);

        assertThatThrownBy(() -> lote.consumir(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> lote.consumir(BigDecimal.valueOf(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void consumirRechazaLoteVencido() {
        Lote lote = new Lote(UUID.randomUUID(), BigDecimal.TEN, Unidad.UNIDAD,
                CondicionBien.ACEPTABLE, OrigenLote.COMPRA, LocalDate.now(),
                LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> lote.consumir(BigDecimal.ONE))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void consumirRechazaLoteBloqueado() {
        Lote lote = new Lote(UUID.randomUUID(), BigDecimal.TEN, Unidad.UNIDAD,
                CondicionBien.NO_ACEPTABLE, OrigenLote.COMPRA, LocalDate.now(), null);

        assertThatThrownBy(() -> lote.consumir(BigDecimal.ONE))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void estadoInicialEsBloqueadoSiNoAceptable() {
        Lote lote = new Lote(UUID.randomUUID(), BigDecimal.TEN, Unidad.UNIDAD,
                CondicionBien.VENCIDO, OrigenLote.COMPRA, LocalDate.now(), null);

        assertThat(lote.getEstado()).isEqualTo(EstadoLote.BLOQUEADO);
    }
}
