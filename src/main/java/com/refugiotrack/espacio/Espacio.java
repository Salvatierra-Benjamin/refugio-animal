package com.refugiotrack.espacio;

import com.refugiotrack.animal.Animal;
import com.refugiotrack.animal.Especie;
import com.refugiotrack.shared.Tamanio;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Espacio de alojamiento de una sede, con capacidad, ocupación y reglas de
 * compatibilidad. El tipo de ubicación heredado es siempre
 * {@link TipoUbicacion#ESPACIO}; el tipo físico se expone aparte para no
 * colisionar con ese discriminador.
 */
public class Espacio extends Ubicacion {

    private final Sede sede;
    private final String codigo;
    private final TipoEspacio tipoEspacio;
    private final int capacidad;
    private EstadoEspacio estado;
    private final Set<Especie> especiesAdmitidas;
    private final Set<Tamanio> tamaniosAdmitidos;
    private int ocupacion;
    private final Set<ReglaEspacio> reglas;

    public Espacio(UUID id, Sede sede, String codigo, TipoEspacio tipoEspacio, int capacidad,
                   Set<Especie> especiesAdmitidas, Set<Tamanio> tamaniosAdmitidos) {
        super(id, TipoUbicacion.ESPACIO);
        this.sede = Objects.requireNonNull(sede, "sede no puede ser nula");
        this.codigo = requerirTexto(codigo, "codigo");
        this.tipoEspacio = Objects.requireNonNull(tipoEspacio, "tipoEspacio no puede ser nulo");
        if (capacidad < 1) {
            throw new IllegalArgumentException("capacidad debe ser mayor o igual a 1");
        }
        this.capacidad = capacidad;
        this.estado = EstadoEspacio.DISPONIBLE;
        this.especiesAdmitidas = new LinkedHashSet<>(
                Objects.requireNonNull(especiesAdmitidas, "especiesAdmitidas no puede ser nulo"));
        this.tamaniosAdmitidos = new LinkedHashSet<>(
                Objects.requireNonNull(tamaniosAdmitidos, "tamaniosAdmitidos no puede ser nulo"));
        this.ocupacion = 0;
        this.reglas = new LinkedHashSet<>();
    }

    /**
     * Ocupación actual del espacio.
     */
    public int ocupacionActual() {
        return ocupacion;
    }

    /**
     * Indica si queda capacidad libre.
     */
    public boolean hayLugar() {
        return ocupacion < capacidad;
    }

    /**
     * Incrementa la ocupación en uno.
     *
     * @throws IllegalStateException si no queda lugar
     */
    public void ocupar() {
        if (!hayLugar()) {
            throw new IllegalStateException("no hay lugar en el espacio");
        }
        ocupacion++;
    }

    /**
     * Decrementa la ocupación en uno.
     *
     * @throws IllegalStateException si la ocupación ya es cero
     */
    public void liberar() {
        if (ocupacion == 0) {
            throw new IllegalStateException("no se puede liberar un espacio sin ocupación");
        }
        ocupacion--;
    }

    /**
     * Indica si el animal puede ocupar este espacio. Se requiere que el espacio
     * esté disponible y tenga lugar, que admita la especie y, si el tamaño del
     * animal está informado, que ese tamaño esté admitido. Un tamaño desconocido
     * no bloquea. Todas las reglas registradas deben cumplirse.
     *
     * @param animal animal a evaluar; no puede ser nulo
     * @return {@code true} si el animal es compatible con el espacio
     */
    public boolean admite(Animal animal) {
        Objects.requireNonNull(animal, "animal no puede ser nulo");
        if (estado != EstadoEspacio.DISPONIBLE || !hayLugar()) {
            return false;
        }
        if (!especiesAdmitidas.contains(animal.getEspecie())) {
            return false;
        }
        Tamanio tamanio = animal.getTamanio();
        if (tamanio != null && !tamaniosAdmitidos.contains(tamanio)) {
            return false;
        }
        for (ReglaEspacio regla : reglas) {
            if (!regla.cumple(animal, this)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Incorpora una regla de compatibilidad.
     *
     * @param regla regla a incorporar; no puede ser nula
     */
    public void agregarRegla(ReglaEspacio regla) {
        reglas.add(Objects.requireNonNull(regla, "regla no puede ser nula"));
    }

    /**
     * Cambia el estado operativo del espacio. Se utiliza para registrar
     * disponibilidad, mantenimiento o cierre.
     *
     * @param estado nuevo estado; no puede ser nulo
     */
    public void cambiarEstado(EstadoEspacio estado) {
        this.estado = Objects.requireNonNull(estado, "estado no puede ser nulo");
    }

    public Sede getSede() {
        return sede;
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoEspacio getTipoEspacio() {
        return tipoEspacio;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public EstadoEspacio getEstado() {
        return estado;
    }

    /**
     * Especies admitidas, expuestas como vista inmutable.
     */
    public Set<Especie> getEspeciesAdmitidas() {
        return Collections.unmodifiableSet(especiesAdmitidas);
    }

    /**
     * Tamaños admitidos, expuestos como vista inmutable.
     */
    public Set<Tamanio> getTamaniosAdmitidos() {
        return Collections.unmodifiableSet(tamaniosAdmitidos);
    }

    public int getOcupacion() {
        return ocupacion;
    }

    /**
     * Reglas de compatibilidad, expuestas como vista inmutable.
     */
    public Set<ReglaEspacio> getReglas() {
        return Collections.unmodifiableSet(reglas);
    }

    private static String requerirTexto(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío");
        }
        return valor;
    }
}
