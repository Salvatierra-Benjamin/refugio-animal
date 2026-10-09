# Diagrama de clases — RefugioTrack

Modelo de dominio de la **Entrega 1** (dominio y diseño orientado a objetos). El diagrama
está partido por *bounded context* para que sea legible: cada sección se puede leer sola y
los nombres de clase son únicos en todo el modelo.

> Estado: diseño. El código Java de cada clase se implementa sobre esta base en los
> siguientes pasos. La estructura de paquetes ya existe bajo `src/main/java/com/refugiotrack`.

## Cómo leerlo

1. Empezá por el **mapa de módulos** para ver los límites y las dependencias.
2. Bajá al diagrama del módulo que te interesa (animal, espacio, inventario, donaciones, etc.).
3. Cerrá con **decisiones de modelado**, **enumeraciones** y **trazabilidad**.

**Leyenda**

| Notación | Significado |
|---|---|
| `*--` | Composición: la parte no existe sin el agregado raíz |
| `o--` | Agregación: referencia a otra entidad del dominio |
| `-->` | Asociación simple |
| `..>` | Dependencia (usa, no posee) |
| `<\|--` | Herencia |
| `..\|>` | Realización de interfaz |
| `"1"`, `"*"`, `"0..1"` | Multiplicidad |

---

## Mapa de módulos

```mermaid
flowchart TD
    admision[admision] --> animal[animal]
    espacio[espacio] --> animal
    cuidado[cuidado] --> animal
    cuidado --> inventario[inventario]
    cuidado --> espacio
    donacion[donacion] --> inventario
    adopcion[adopcion] --> animal
    adopcion --> espacio
    inventario --> shared[shared]
    donacion --> shared
    espacio --> shared
    animal --> shared
    auditoria[auditoria] -.-> animal
    auditoria -.-> donacion
    archivo[archivo] -.-> animal
    archivo -.-> admision
    archivo -.-> donacion
    configuracion[configuracion] -.-> inventario
    configuracion -.-> adopcion
    notificacion[notificacion] -.-> donacion
    seguridad[seguridad] -.-> auditoria

    classDef ctx fill:#eef,stroke:#557,color:#114;
    class admision,animal,espacio,cuidado,inventario,donacion,adopcion,auditoria,archivo,configuracion,notificacion,seguridad,shared ctx;
```

Regla de dependencia: los módulos de dominio **no** dependen de infraestructura. `shared` no
depende de nadie; `seguridad` y `configuracion` son transversales.

---

## 1. Seguridad y personas

```mermaid
classDiagram
    class Usuario {
        +UUID id
        +String nombreUsuario
        +String hashCredencial
        +boolean activo
        +Rol rol
    }
    class Rol {
        <<enumeration>>
        ADMINISTRADOR
        ADMISIONES
        CUIDADOR
        VETERINARIO
        ADOPCIONES
    }
    class Permiso {
        <<enumeration>>
        GESTIONAR_CONFIGURACION
        CONFIRMAR_ADMISION
        AUTORIZAR_EXCEPCION
        REGISTRAR_CONSUMO
        DECIDIR_DONACION
        FORMALIZAR_ADOPCION
        VER_AUDITORIA
    }

    Usuario --> Rol : tiene
    Rol --> "*" Permiso : concede
```

---

## 2. Animal e ingreso

```mermaid
classDiagram
    class Animal {
        +UUID id
        +Especie especie
        +String nombre
        +String identificacionProvisoria
        +String caracteristicas
        +String observaciones
        +LocalDate fechaNacimientoEstimada
        +EstadoAnimal estado
        +cambiarEstado(EstadoAnimal, Usuario, String)
        +consolidarIdentidad(IdentificadorAnimal)
    }
    class IdentificadorAnimal {
        <<value object>>
        +TipoIdentificador tipo
        +String valor
    }
    class Ingreso {
        +UUID id
        +OrigenIngreso origen
        +String motivo
        +LocalDateTime fechaHora
        +String evaluacionInicial
        +DecisionIngreso decision
        +String motivoDecision
        +decidir(DecisionIngreso, String, Usuario)
    }
    class EstadoAnimal {
        <<enumeration>>
        PENDIENTE_DE_EVALUACION
        EN_ADMISION
        EN_REFUGIO
        EN_TRANSITO_EXTERNO
        ADOPTADO
        EGRESADO
        FALLECIDO
    }
    class OrigenIngreso {
        <<enumeration>>
        RESCATE
        DERIVACION
        ENTREGA_VOLUNTARIA
        DEVOLUCION
    }
    class DecisionIngreso {
        <<enumeration>>
        PENDIENTE
        ADMITIDO
        RECHAZADO
        DERIVADO
    }
    class Especie {
        <<enumeration>>
        PERRO
        GATO
        OTRO
    }
    class TipoIdentificador {
        <<enumeration>>
        CHIP
        TATUAJE
        PROVISORIO
    }
    class Archivo

    Animal "1" o-- "*" Ingreso : episodios
    Animal "1" *-- "*" IdentificadorAnimal : identificadores
    Animal "1" *-- "*" Archivo : documentos
    Ingreso --> Animal : evalua
    Ingreso --> Usuario : responsable
    Ingreso --> OrigenIngreso
    Ingreso --> DecisionIngreso
    Animal --> EstadoAnimal
    Animal --> Especie
```

Una **devolución** no reabre el ingreso anterior: crea un nuevo `Ingreso` con
`origen = DEVOLUCION` sobre el mismo `Animal`.

---

## 3. Sedes, espacios, estadías y movimientos

```mermaid
classDiagram
    class Sede {
        +UUID id
        +String nombre
        +String direccion
        +ZoneId zonaHoraria
    }
    class Espacio {
        +UUID id
        +String codigo
        +TipoEspacio tipo
        +int capacidad
        +EstadoEspacio estado
        +Set~Especie~ especiesAdmitidas
        +Set~Tamanio~ tamaniosAdmitidos
        +int ocupacionActual()
        +boolean hayLugar()
        +boolean admite(Animal)
    }
    class Ubicacion {
        <<abstract>>
        +UUID id
        +TipoUbicacion tipo
    }
    class UbicacionExterna {
        +String institucion
        +String responsable
    }
    class Estadia {
        +UUID id
        +LocalDateTime inicio
        +LocalDateTime fin
        +EstadoEstadia estado
        +cerrar(LocalDateTime)
    }
    class Movimiento {
        +UUID id
        +TipoMovimiento tipo
        +LocalDateTime fechaHora
        +String motivo
        +esInmutable()
    }
    class ExcepcionCompatibilidad {
        +UUID id
        +String reglaIncumplida
        +String motivo
        +LocalDateTime fechaHora
    }
    class ReglaEspacio {
        <<interface>>
        +String id()
        +boolean cumple(Animal, Espacio)
    }
    class RestriccionEspecie
    class RestriccionTamanio
    class RestriccionAislamiento
    class EstadoEspacio {
        <<enumeration>>
        DISPONIBLE
        EN_MANTENIMIENTO
        CERRADO
    }
    class TipoEspacio {
        <<enumeration>>
        CANIL
        JAULA
        HABITACION
        OTRO
    }
    class EstadoEstadia {
        <<enumeration>>
        ACTIVA
        FINALIZADA
    }
    class TipoMovimiento {
        <<enumeration>>
        TRASLADO
        HOGAR_TRANSITO
        OTRA_INSTITUCION
        EGRESO
    }
    class TipoUbicacion {
        <<enumeration>>
        ESPACIO
        EXTERNA
    }

    Sede "1" *-- "*" Espacio
    Espacio --|> Ubicacion
    UbicacionExterna --|> Ubicacion
    Animal "1" -- "*" Estadia
    Estadia --> Ubicacion : ocupa
    Movimiento --> Ubicacion : origen
    Movimiento --> Ubicacion : destino
    Movimiento --> Usuario : responsable
    Movimiento "0..1" -- "1" Estadia : abre/cierra
    Espacio ..> ReglaEspacio : evalua
    ReglaEspacio <|.. RestriccionEspecie
    ReglaEspacio <|.. RestriccionTamanio
    ReglaEspacio <|.. RestriccionAislamiento
    ExcepcionCompatibilidad --> ReglaEspacio : incumple
    ExcepcionCompatibilidad --> Usuario : autoriza
```

Invariantes clave (se resuelven en los casos de uso, no en la UI):

- Un `Animal` bajo cuidado tiene **exactamente una** `Estadia` `ACTIVA`.
- Un `Movimiento` cierra la estadía previa y abre la nueva de forma **atómica**.
- Un `Espacio` no supera `capacidad`; la ocupación cuenta animales, no historial.
- Las reglas son **componibles** (`ReglaEspacio` = patrón *Specification/Strategy*); una
  excepción exige `ExcepcionCompatibilidad` con motivo y auditoría.

---

## 4. Inventario

```mermaid
classDiagram
    class Recurso {
        +UUID id
        +String nombre
        +CategoriaRecurso categoria
        +Unidad unidadBase
        +boolean perecedero
        +BigDecimal umbralStock
        +boolean requiereAutorizacion
    }
    class Lote {
        +UUID id
        +BigDecimal cantidadInicial
        +BigDecimal cantidadDisponible
        +Unidad unidad
        +CondicionBien condicion
        +OrigenLote origen
        +LocalDate fechaIngreso
        +LocalDate vencimiento
        +EstadoLote estado
        +boolean vencido(LocalDate)
        +consumir(BigDecimal)
    }
    class MovimientoInventario {
        +UUID id
        +TipoMovimientoInventario tipo
        +BigDecimal cantidad
        +Unidad unidad
        +String motivo
        +LocalDateTime fechaHora
        +String claveIdempotencia
    }
    class Alerta {
        +UUID id
        +TipoAlerta tipo
        +LocalDateTime generada
        +boolean atendida
    }
    class CategoriaRecurso {
        <<enumeration>>
        ALIMENTO
        AGUA
        MEDICAMENTO
        HIGIENE
        CAMA
    }
    class Unidad {
        <<enumeration>>
        UNIDAD
        KILOGRAMO
        GRAMO
        LITRO
        MILILITRO
    }
    class OrigenLote {
        <<enumeration>>
        COMPRA
        DONACION_ACEPTADA
        AJUSTE
    }
    class EstadoLote {
        <<enumeration>>
        DISPONIBLE
        BLOQUEADO
        AGOTADO
    }
    class TipoMovimientoInventario {
        <<enumeration>>
        INGRESO
        CONSUMO
        AJUSTE
        MERMA
        VENCIMIENTO
    }
    class TipoAlerta {
        <<enumeration>>
        STOCK_BAJO
        PROXIMO_VENCIMIENTO
        TAREA_CRITICA
    }
    class CondicionBien {
        <<enumeration>>
        ACEPTABLE
        NO_ACEPTABLE
        VENCIDO
        INSEGURO
    }

    Recurso "1" *-- "*" Lote
    Lote "1" -- "*" MovimientoInventario : historial
    Recurso "1" -- "*" Alerta : genera
```

Reglas: el consumo referencia un `Lote` concreto, prioriza el lote apto de vencimiento más
próximo, nunca usa lotes vencidos y jamás supera lo disponible. `MovimientoInventario` es
inmutable (las correcciones son movimientos nuevos con motivo).

---

## 5. Donaciones en especie

```mermaid
classDiagram
    class Donante {
        +UUID id
        +TipoDonante tipo
        +String nombre
        +boolean activo
        +PreferenciaContacto preferencia
    }
    class ContactoDonante {
        <<value object>>
        +String email
        +String telefono
    }
    class Donacion {
        +UUID id
        +LocalDateTime registrada
        +EstadoDonacion estado
        +String claveIdempotencia
        +String observaciones
        +resumirEstado()
    }
    class LineaDonacion {
        +UUID id
        +BigDecimal cantidadOfrecida
        +BigDecimal cantidadRecibida
        +BigDecimal cantidadAceptada
        +BigDecimal cantidadRechazada
        +Unidad unidad
        +EstadoLinea estado
        +String motivoRechazo
        +decidir(BigDecimal aceptada, String motivo, Usuario)
    }
    class Recepcion {
        +UUID id
        +LocalDateTime fechaHora
        +String observaciones
    }
    class Inspeccion {
        +UUID id
        +BigDecimal cantidadInspeccionada
        +CondicionBien condicion
        +LocalDate vencimiento
        +String resultado
    }
    class DecisionLinea {
        +UUID id
        +BigDecimal cantidadAceptada
        +BigDecimal cantidadRechazada
        +String motivoRechazo
        +LocalDateTime fechaHora
    }
    class Constancia {
        +UUID id
        +TipoConstancia tipo
        +EstadoEntrega estado
        +int intentos
        +LocalDateTime fechaEnvio
    }
    class TipoDonante {
        <<enumeration>>
        PERSONA
        ORGANIZACION
    }
    class PreferenciaContacto {
        <<enumeration>>
        EMAIL
        SMS
        NINGUNA
    }
    class EstadoDonacion {
        <<enumeration>>
        REGISTRADA
        RECIBIDA
        EN_INSPECCION
        ACEPTADA
        PARCIALMENTE_ACEPTADA
        RECHAZADA
        CERRADA
    }
    class EstadoLinea {
        <<enumeration>>
        PENDIENTE
        ACEPTADA
        PARCIALMENTE_ACEPTADA
        RECHAZADA
    }
    class TipoConstancia {
        <<enumeration>>
        AGRADECIMIENTO
        CONSTANCIA
    }
    class EstadoEntrega {
        <<enumeration>>
        PENDIENTE
        ENVIADA
        FALLIDA
    }
    class Recurso
    class Lote
    class OrigenLote

    Donante "1" *-- "1" ContactoDonante
    Donante "1" o-- "*" Donacion : aportes
    Donante "1" -- "*" Constancia : comunicaciones
    Donacion "1" *-- "1..*" LineaDonacion
    Donacion "1" *-- "*" Recepcion
    LineaDonacion --> Recurso : referencia
    LineaDonacion "1" -- "*" Inspeccion
    LineaDonacion "1" *-- "*" DecisionLinea
    DecisionLinea ..> Lote : crea si acepta
    Lote --> OrigenLote
    Constancia --> Donacion
```

Invariantes: `aceptada + rechazada <= recibida`; solo lo **aceptado** genera `Lote`; lo
rechazado o vencido queda en el registro de inspección para trazabilidad pero nunca en stock
utilizable; las correcciones se auditan, no sobrescriben.

---

## 6. Cuidados

```mermaid
classDiagram
    class TareaCuidado {
        +UUID id
        +TipoCuidado tipo
        +LocalDateTime programada
        +EstadoTarea estado
        +Recurrencia recurrencia
        +boolean esRecurrente()
        +completar(Usuario)
        +omitir(String motivo, Usuario)
    }
    class RegistroCuidado {
        +UUID id
        +TipoCuidado tipo
        +LocalDateTime fechaHora
        +String resultado
        +String observacion
    }
    class Consumo {
        +UUID id
        +BigDecimal cantidad
        +Unidad unidad
        +String claveIdempotencia
    }
    class IndicacionMedica {
        +UUID id
        +String indicacion
        +BigDecimal dosis
        +Unidad unidad
        +ViaAdministracion via
        +LocalDateTime horario
        +boolean omitida
        +String motivoOmission
    }
    class EventoSalud {
        +UUID id
        +String descripcion
        +LocalDateTime fechaHora
    }
    class Recurrencia {
        <<value object>>
        +String patron
        +int intervalo
    }
    class TipoCuidado {
        <<enumeration>>
        ALIMENTACION
        AGUA
        LIMPIEZA
        MEDICACION
        OBSERVACION
        OTRO
    }
    class EstadoTarea {
        <<enumeration>>
        PENDIENTE
        COMPLETADA
        OMITIDA
    }
    class ViaAdministracion {
        <<enumeration>>
        ORAL
        TOPICA
        INYECTABLE
        OTRA
    }
    class Animal
    class Espacio
    class Lote
    class MovimientoInventario

    TareaCuidado --> Animal : objetivo
    TareaCuidado --> Espacio : objetivo
    TareaCuidado --> Usuario : asignado
    TareaCuidado --> Recurrencia
    RegistroCuidado --> TareaCuidado : ejecuta
    RegistroCuidado --> Animal
    RegistroCuidado "1" *-- "*" Consumo
    Consumo --> Lote : usa
    Consumo ..> MovimientoInventario : genera
    IndicacionMedica --> Animal
    IndicacionMedica --> Usuario : autoriza
    EventoSalud --> Animal
    EventoSalud --> Usuario : registra
```

El **registro de cuidado y el descuento de stock** se confirman juntos o no se confirma
ninguno (transacción). El sistema alerta tareas vencidas; no decide dosis.

---

## 7. Adopción y tránsito

```mermaid
classDiagram
    class Postulante {
        +UUID id
        +TipoPersona tipo
        +String nombre
        +boolean activo
    }
    class Postulacion {
        +UUID id
        +TipoPostulacion tipo
        +EstadoPostulacion estado
        +LocalDateTime creada
        +String motivoCierre
        +avanzar(EstadoPostulacion, Usuario)
        +cerrar(String motivo, Usuario)
    }
    class Verificacion {
        +UUID id
        +String tipo
        +boolean aprobada
        +String observaciones
    }
    class Entrevista {
        +UUID id
        +LocalDateTime fechaHora
        +boolean aprobada
        +String observaciones
    }
    class Reserva {
        +UUID id
        +LocalDateTime inicio
        +LocalDateTime vencimiento
        +EstadoReserva estado
        +boolean vigente(LocalDateTime)
        +liberar()
    }
    class Adopcion {
        +UUID id
        +LocalDate fecha
        +String acuerdo
        +String seguimiento
    }
    class HogarTransito {
        +UUID id
        +LocalDateTime inicio
        +LocalDateTime fin
        +String responsableTransito
    }
    class TipoPersona {
        <<enumeration>>
        PERSONA
        ORGANIZACION
    }
    class TipoPostulacion {
        <<enumeration>>
        ADOPCION
        TRANSITO
    }
    class EstadoPostulacion {
        <<enumeration>>
        RECIBIDA
        EN_REVISION
        ENTREVISTA
        APROBADA
        RECHAZADA
        RESERVA_VIGENTE
        FORMALIZADA
        CANCELADA
    }
    class EstadoReserva {
        <<enumeration>>
        VIGENTE
        VENCIDA
        LIBERADA
        FORMALIZADA
    }
    class Animal
    class UbicacionExterna

    Postulacion --> Animal : sobre
    Postulacion --> Postulante : solicita
    Postulacion "1" *-- "*" Verificacion
    Postulacion "1" *-- "*" Entrevista
    Postulacion "1" -- "0..1" Reserva
    Reserva --> Animal
    Postulacion "1" -- "0..1" Adopcion
    Adopcion --> Animal
    HogarTransito --|> UbicacionExterna
```

No puede haber **dos reservas vigentes** para el mismo animal. El tránsito es una
`UbicacionExterna`, nunca una adopción.

---

## 8. Auditoría, archivos, configuración y outbox

```mermaid
classDiagram
    class EventoAuditoria {
        +UUID id
        +String accion
        +String entidadTipo
        +String entidadId
        +LocalDateTime instante
        +String datos
        +esInmutable()
    }
    class Archivo {
        +UUID id
        +String nombre
        +String tipoMime
        +long tamano
        +String ruta
        +EntidadArchivo entidadTipo
        +String entidadId
    }
    class Configuracion {
        +UUID id
        +String clave
        +String valor
        +String descripcion
    }
    class EventoDominio {
        <<transactional outbox>>
        +UUID id
        +String tipo
        +String payload
        +EstadoTrabajo estado
        +int intentos
        +String claveIdempotencia
    }
    class Notificacion {
        +UUID id
        +String canal
        +EstadoEntrega estado
        +int intentos
        +String claveIdempotencia
    }
    class EntidadArchivo {
        <<enumeration>>
        ANIMAL
        INGRESO
        INSPECCION
        ADOPCION
    }
    class EstadoTrabajo {
        <<enumeration>>
        PENDIENTE
        PROCESADO
        FALLIDO
    }
    class EstadoEntrega {
        <<enumeration>>
        PENDIENTE
        ENVIADA
        FALLIDA
    }

    EventoAuditoria --> Usuario : actor
    Archivo --> EntidadArchivo
    Notificacion --> EventoDominio : procesa
```

`EventoAuditoria` **nunca** guarda credenciales ni secretos. `EventoDominio` implementa el
patrón *transactional outbox* (Entrega 2): se registra en la misma transacción del cambio de
dominio y se procesa después; los reintentos son idempotentes.

---

## Enumeraciones (resumen)

| Enum | Valores |
|---|---|
| `Rol` | ADMINISTRADOR, ADMISIONES, CUIDADOR, VETERINARIO, ADOPCIONES |
| `EstadoAnimal` | PENDIENTE_DE_EVALUACION, EN_ADMISION, EN_REFUGIO, EN_TRANSITO_EXTERNO, ADOPTADO, EGRESADO, FALLECIDO |
| `OrigenIngreso` | RESCATE, DERIVACION, ENTREGA_VOLUNTARIA, DEVOLUCION |
| `DecisionIngreso` | PENDIENTE, ADMITIDO, RECHAZADO, DERIVADO |
| `EstadoEspacio` | DISPONIBLE, EN_MANTENIMIENTO, CERRADO |
| `TipoMovimiento` | TRASLADO, HOGAR_TRANSITO, OTRA_INSTITUCION, EGRESO |
| `CategoriaRecurso` | ALIMENTO, AGUA, MEDICAMENTO, HIGIENE, CAMA |
| `OrigenLote` | COMPRA, DONACION_ACEPTADA, AJUSTE |
| `TipoMovimientoInventario` | INGRESO, CONSUMO, AJUSTE, MERMA, VENCIMIENTO |
| `EstadoDonacion` | REGISTRADA, RECIBIDA, EN_INSPECCION, ACEPTADA, PARCIALMENTE_ACEPTADA, RECHAZADA, CERRADA |
| `EstadoLinea` | PENDIENTE, ACEPTADA, PARCIALMENTE_ACEPTADA, RECHAZADA |
| `EstadoTarea` | PENDIENTE, COMPLETADA, OMITIDA |
| `EstadoPostulacion` | RECIBIDA, EN_REVISION, ENTREVISTA, APROBADA, RECHAZADA, RESERVA_VIGENTE, FORMALIZADA, CANCELADA |
| `EstadoReserva` | VIGENTE, VENCIDA, LIBERADA, FORMALIZADA |

---

## Trazabilidad donante → consumo

```mermaid
flowchart LR
    D[Donante] --> DN[Donacion]
    DN --> L[LineaDonacion]
    L --> R[Recepcion]
    L --> I[Inspeccion]
    L --> DE[DecisionLinea]
    DE -->|solo lo aceptado| LO[Lote]
    LO --> MI[MovimientoInventario CONSUMO]
    MI --> C[Consumo]
    C --> RC[RegistroCuidado]
    RC --> A[Animal]
```

Cada eslabón conserva actor, instante y motivo cuando corresponde.

---

## Decisiones de modelado

| Tema | Decisión | Motivo |
|---|---|---|
| Reglas de espacio | `ReglaEspacio` como interfaz componible (Specification/Strategy) | Evita condicionales dispersos; cada regla se audita sola |
| Ubicación | `Ubicacion` abstracta con `Espacio` y `UbicacionExterna` | Unifica estadías internas y tránsito sin confundir adopción |
| Movimientos | Inmutables; las correcciones son movimientos nuevos | Preserva historial y auditoría |
| Inventario | `Lote` como unidad de trazabilidad; consumo referencia lote | Permite FIFO por vencimiento y cadena hasta el donante |
| Donaciones | `LineaDonacion` con decisión propia y `DecisionLinea` separada | Aceptación parcial e independiente por línea |
| Fechas | `LocalDate` para calendario (vencimientos) y `LocalDateTime` para instantes | La zona horaria se resuelve con `Sede.zonaHoraria` |
| Eventos | Patrón *transactional outbox* (`EventoDominio`) | No se pierde el evento ante fallo del proveedor y es reintentable |
| Idempotencia | `claveIdempotencia` en consumos, donaciones y trabajos | Reintentos sin duplicar efectos |

---

## Cobertura de la Entrega 1

- [x] Ingresos y fichas de animales (módulos `animal`, `admision`)
- [x] Espacios/capacidad, estadías y movimientos (`espacio`)
- [x] Reglas configurables evaluables y componibles (`ReglaEspacio`)
- [x] Inventario por lotes (`inventario`)
- [x] Donantes/donaciones con recepción e inspección por línea (`donacion`)
- [x] Registros de cuidado (`cuidado`)
- [ ] Clases Java + pruebas de invariantes (siguiente paso)

## Siguiente paso

Implementar las clases de cada módulo sobre esta base, empezando por `animal` y `espacio`
(las invariantes de ocupación son las que más condicionan el resto), con pruebas JUnit 5 +
AssertJ.
