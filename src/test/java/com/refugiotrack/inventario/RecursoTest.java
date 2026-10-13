package com.refugiotrack.inventario;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class RecursoTest {

    private static Recurso recursoConUmbral(int umbral) {
        return new Recurso(UUID.randomUUID(), "Alimento", CategoriaRecurso.ALIMENTO,
                Unidad.KILOGRAMO, true, BigDecimal.valueOf(umbral), false);
    }

    private static Lote lote(int cantidad, LocalDate vencimiento) {
        return new Lote(UUID.randomUUID(), BigDecimal.valueOf(cantidad), Unidad.KILOGRAMO,
                CondicionBien.ACEPTABLE, OrigenLote.COMPRA, LocalDate.now(), vencimiento);
    }

    @Test
    void cantidadDisponibleSumaLosLotes() {
        Recurso recurso = recursoConUmbral(0);
        recurso.agregarLote(lote(3, null));
        recurso.agregarLote(lote(7, null));

        assertThat(recurso.cantidadDisponible()).isEqualByComparingTo("10");
    }

    @Test
    void stockBajoComparaContraUmbral() {
        Recurso recurso = recursoConUmbral(5);
        recurso.agregarLote(lote(3, null));

        assertThat(recurso.stockBajo()).isTrue();

        recurso.agregarLote(lote(3, null));

        assertThat(recurso.stockBajo()).isFalse();
    }

    @Test
    void loteAptoMasProximoAVencerPriorizaElMasCercano() {
        Recurso recurso = recursoConUmbral(0);
        Lote en30 = lote(5, LocalDate.now().plusDays(30));
        Lote en10 = lote(5, LocalDate.now().plusDays(10));
        recurso.agregarLote(en30);
        recurso.agregarLote(en10);

        assertThat(recurso.loteAptoMasProximoAVencer(LocalDate.now())).contains(en10);
    }

    @Test
    void loteAptoIgnoraVencidosYBloqueados() {
        Recurso recurso = recursoConUmbral(0);
        Lote vencido = new Lote(UUID.randomUUID(), BigDecimal.TEN, Unidad.KILOGRAMO,
                CondicionBien.ACEPTABLE, OrigenLote.COMPRA, LocalDate.now(),
                LocalDate.now().minusDays(1));
        Lote bloqueado = new Lote(UUID.randomUUID(), BigDecimal.TEN, Unidad.KILOGRAMO,
                CondicionBien.NO_ACEPTABLE, OrigenLote.COMPRA, LocalDate.now(), null);
        recurso.agregarLote(vencido);
        recurso.agregarLote(bloqueado);

        assertThat(recurso.loteAptoMasProximoAVencer(LocalDate.now())).isEmpty();
    }
}
