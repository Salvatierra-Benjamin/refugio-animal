package com.refugiotrack.espacio;

import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Sede del refugio: agrupa los espacios de alojamiento bajo una zona horaria
 * común. La dirección puede omitirse si aún no está registrada.
 */
public class Sede {

    private final UUID id;
    private final String nombre;
    private final String direccion;
    private final ZoneId zonaHoraria;
    private final Set<Espacio> espacios;

    public Sede(UUID id, String nombre, String direccion, ZoneId zonaHoraria) {
        this.id = Objects.requireNonNull(id, "id no puede ser nulo");
        this.nombre = requerirTexto(nombre, "nombre");
        this.direccion = direccion;
        this.zonaHoraria = Objects.requireNonNull(zonaHoraria, "zonaHoraria no puede ser nula");
        this.espacios = new LinkedHashSet<>();
    }

    /**
     * Incorpora un espacio a la sede.
     *
     * @param espacio espacio a incorporar; no puede ser nulo
     */
    public void agregarEspacio(Espacio espacio) {
        espacios.add(Objects.requireNonNull(espacio, "espacio no puede ser nulo"));
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public ZoneId getZonaHoraria() {
        return zonaHoraria;
    }

    /**
     * Espacios de la sede, expuestos como vista inmutable.
     */
    public Set<Espacio> getEspacios() {
        return Collections.unmodifiableSet(espacios);
    }

    private static String requerirTexto(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío");
        }
        return valor;
    }
}
