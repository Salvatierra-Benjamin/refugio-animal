# RefugioTrack

Plataforma para coordinar ingresos, estadías, recursos y cuidados de un refugio animal, con
trazabilidad suficiente para operar con seguridad y explicar las decisiones del equipo.

- Enunciado: [`enunciado-refugio-animal.md`](enunciado-refugio-animal.md)
- Modelo de dominio: [`docs/diagrama-de-clases.md`](docs/diagrama-de-clases.md)

## Estado actual

**Entrega 1 — Dominio y diseño OO.** El repositorio contiene la estructura del proyecto Maven
y el diagrama de clases. Las clases Java del dominio se implementan a continuación.

## Requisitos

- JDK 17 o superior (probado con 25).
- No necesitás Maven instalado: el proyecto incluye el **Maven Wrapper**.

## Construir y probar

```bash
./mvnw test        # compila y ejecuta la suite de pruebas
./mvnw package     # genera el jar en target/
```

## Estructura

```
src/main/java/com/refugiotrack/
  shared/          elementos transversales (valor, unidades, contratos base)
  seguridad/       usuarios, roles y permisos
  animal/          identidad, ficha y estados del animal
  admision/        avisos, evaluación e ingresos
  espacio/         sedes, espacios, estadías, movimientos y reglas
  inventario/      recursos, lotes, movimientos de stock y alertas
  donacion/        donantes, donaciones, recepción e inspección
  cuidado/         tareas, registros, consumos y salud
  adopcion/        postulaciones, reservas, adopciones y tránsito
  auditoria/       eventos de auditoría
  archivo/         evidencias vinculadas a entidades
  configuracion/   catálogos y políticas configurables
  notificacion/    (Entrega 2) notificaciones, outbox e integraciones
docs/
  diagrama-de-clases.md
```
