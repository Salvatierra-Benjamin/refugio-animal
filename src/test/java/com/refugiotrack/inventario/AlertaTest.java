package com.refugiotrack.inventario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class AlertaTest {

    @Test
    void alertaIniciaNoAtendidaYMarcaAtendida() {
        Alerta alerta = new Alerta(UUID.randomUUID(), TipoAlerta.STOCK_BAJO, LocalDateTime.now());

        assertThat(alerta.isAtendida()).isFalse();

        alerta.marcarAtendida();

        assertThat(alerta.isAtendida()).isTrue();
    }
}
