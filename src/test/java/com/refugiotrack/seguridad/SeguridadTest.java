package com.refugiotrack.seguridad;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class SeguridadTest {

    @Test
    void usuarioGuardaElHashYNoLaCredencialEnClaro() {
        Usuario usuario = new Usuario(
                UUID.randomUUID(), "ana", "hash-de-prueba", true, Rol.ADMINISTRADOR);

        assertThat(usuario.getHashCredencial()).isEqualTo("hash-de-prueba");
        assertThat(usuario.isActivo()).isTrue();
        assertThat(usuario.getRol()).isEqualTo(Rol.ADMINISTRADOR);
    }

    @Test
    void usuarioRechazaCamposObligatoriosVaciosONulos() {
        assertThatThrownBy(() -> new Usuario(
                UUID.randomUUID(), " ", "hash", true, Rol.ADMISIONES))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Usuario(
                UUID.randomUUID(), "ana", " ", true, Rol.ADMISIONES))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Usuario(
                UUID.randomUUID(), "ana", "hash", true, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void cadaRolConcedeSusPermisosEsperados() {
        assertThat(Rol.ADMINISTRADOR.permisos()).containsExactlyInAnyOrder(
                Permiso.GESTIONAR_CONFIGURACION,
                Permiso.AUTORIZAR_EXCEPCION,
                Permiso.DECIDIR_DONACION,
                Permiso.VER_AUDITORIA);

        assertThat(Rol.ADMISIONES.permisos()).containsExactly(Permiso.CONFIRMAR_ADMISION);
        assertThat(Rol.CUIDADOR.permisos()).containsExactly(Permiso.REGISTRAR_CONSUMO);
        assertThat(Rol.VETERINARIO.permisos()).containsExactly(Permiso.REGISTRAR_CONSUMO);
        assertThat(Rol.ADOPCIONES.permisos()).containsExactly(Permiso.FORMALIZAR_ADOPCION);
    }

    @Test
    void desactivarImpideQueElUsuarioSigaActivo() {
        Usuario usuario = new Usuario(
                UUID.randomUUID(), "ana", "hash", true, Rol.CUIDADOR);

        usuario.desactivar();

        assertThat(usuario.isActivo()).isFalse();
    }
}
