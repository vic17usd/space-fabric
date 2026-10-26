# Space Fabric — Minecraft Java 26.1

La versión 0.5.0 añade relojes solares, cielos y clima independientes, corrosión,
meteoritos y cavernas heladas orbitales. Consulta la [guía de ambientes](docs/environments.md).
Consulta la [guía completa de bloques y materiales](docs/materials.md).
Consulta la [guía de terreno y registro de biomas](docs/terrain.md).
Consulta la [tabla de dimensiones y claves Java](docs/dimensions.md).

Descarga del prototipo compilado: [space-fabric-0.5.0.jar](dist/space-fabric-0.5.0.jar)
(en GitHub, pulsa **Download raw file**).
[Checksum SHA-256](dist/space-fabric-0.5.0.jar.sha256).
Requiere Minecraft **26.1**, Java **25**, Fabric Loader y Fabric API **0.145.1+26.1**.
Coloca el JAR y Fabric API en la carpeta `mods/`. Es el prototipo descrito abajo;
todavía falta su validación dentro del juego.

Base modular en Java 25 para nueve dimensiones independientes: Mercurio, Venus,
Tierra, Luna, Marte, Júpiter, Saturno, Urano y Neptuno. Usa los nombres oficiales de
Minecraft 26.1, sin Yarn ni remapeo de versiones anteriores.

Implementa dimensiones por datos, gravedad para entidades vivas y daño atmosférico
para jugadores. El terreno tiene biomas propios y rasgos planetarios; faltan cohetes,
portales y simulación astronómica. La Luna se incluye como satélite explorable.

## Versiones y uso

| Componente | Versión fijada |
| --- | --- |
| Minecraft Java | 26.1 exacta |
| Java | JDK 25 |
| Gradle Wrapper | 9.8.2, distribución verificada con SHA-256 |
| Fabric Loom | 1.19.3 |
| Fabric Loader | 0.20.1 |
| Fabric API | 0.145.1+26.1 |

Con `JAVA_HOME` apuntando al JDK 25, desde la raíz del repositorio:

```sh
./gradlew build
python3 tools/validate_resources.py
./gradlew runClient
```

El artefacto será `build/libs/space-fabric-0.5.0.jar`. Instala el mod y Fabric API
en cliente y servidor. No se declara compatibilidad con 1.21.x ni con otras
revisiones de 26.1 sin validarlas por separado.

`./gradlew runServer` arranca el servidor de desarrollo. El primer arranque pide
aceptar la EULA de Minecraft en `run/eula.txt`: revísala y acéptala solo si estás
de acuerdo. El proyecto no la acepta automáticamente. Usa un mundo de pruebas nuevo.

## Arquitectura

```text
src/main/java/dev/vic17usd/spacefabric/
├── SpaceFabric.java                 # Entrada común y registro de sistemas
├── planet/
│   ├── Planet.java                  # Nueve perfiles, independientes de Minecraft
│   ├── Atmosphere.java              # Respirabilidad y categoría de presión
│   └── PlanetDimensions.java        # Claves y comprobación de mundos al arrancar
├── gravity/
│   └── PlanetGravity.java           # Modificador sincronizado de gravedad
├── equipment/
│   └── SuitProtection.java          # Sellado mediante cuatro tags de ítems
├── environment/                     # Relojes, clima, corrosión y meteoritos
├── mixin/                           # Clima separado y renderizado de cielo/niebla
├── atmosphere/
│   ├── Exposure.java                # Regla pura de exposición, probada con JUnit
│   ├── AtmosphereSystem.java        # Tick de servidor y aplicación de daño
│   └── PlanetDamageTypes.java       # Claves de los DamageType del datapack
├── block/                           # Regolito, roca caliente, menas y hielo de metano
├── item/                            # Materiales, botas térmicas y pestaña creativa
├── worldgen/                        # Biomas, registro de features y cráteres
├── fluid/                           # Lava viscosa, bloque y cubo
├── client/                          # Renderizado del fluido en Fabric 26.1
└── command/
    └── PlanetCommands.java          # /planetinfo, diagnóstico de lectura

src/main/resources/
├── fabric.mod.json
├── data/space_fabric/
│   ├── dimension/{mercury,...,neptune}.json
│   ├── dimension_type/{mercury,...,neptune}.json
│   ├── damage_type/{decompression,crushing_pressure,hypoxia}.json
│   └── tags/item/space_suit/{helmets,chestplates,leggings,boots}.json
├── data/minecraft/tags/damage_type/{bypasses_armor,bypasses_shield}.json
└── assets/space_fabric/lang/{es_es,en_us}.json
```

`fabric.mod.json` declara `SpaceFabric` como entrada `main` y `environment: "*"`.
La entrada de cliente registra el renderizado de lava viscosa. Los Mixins separan
el clima y ajustan cielo/niebla; los eventos gestionan el resto. Minecraft sincroniza
el atributo vanilla de gravedad. Las
decisiones de daño se toman exclusivamente en el servidor.

## Registro de dimensiones

Fabric monta los recursos `data/` del mod como un datapack integrado. Por ejemplo,
`data/space_fabric/dimension/mars.json` contiene:

```json
{
  "type": "space_fabric:mars",
  "generator": {
    "type": "minecraft:noise",
    "biome_source": {
      "type": "minecraft:fixed",
      "biome": "space_fabric:mars"
    },
    "settings": "space_fabric:mars"
  }
}
```

El tipo apunta a `data/space_fabric/dimension_type/mars.json`, que define alturas,
luz, cielo, niebla y reglas ambientales. En 26.1 varias reglas usan `attributes`,
como `minecraft:gameplay/bed_rule`; no copies sin revisar los campos `bed_works` o
`effects` de tutoriales antiguos. El formato de datapacks de 26.1 es **101.1**.
Si extraes los datos a un datapack externo, añade su `pack.mcmeta`. Fabric monta
los recursos incluidos en el mod sin requerir ese archivo en este proyecto.

Java obtiene referencias a los mundos así:

```java
ResourceKey<Level> mars = ResourceKey.create(
        Registries.DIMENSION, SpaceFabric.id("mars"));
ServerLevel marsLevel = server.getLevel(mars);
```

Crear el `ResourceKey` **no registra ni crea un mundo**. Estos registros son
dinámicos: Minecraft carga sus JSON al abrir el mundo. No uses `Registry.register`
para añadir dimensiones en el inicializador. El servidor comprueba que las nueve
existan al arrancar y falla con un mensaje concreto si falta alguna.

Tras cambiar worldgen, cierra y vuelve a abrir el mundo; `/reload` no garantiza
crear dimensiones nuevas ni regenera chunks existentes. Usa un mundo nuevo para
pruebas de generación. Tierra es `space_fabric:earth`, con almacenamiento propio;
no reemplaza `minecraft:overworld`.

Son terrenos abiertos que se generan al explorar dentro de los límites normales
de Minecraft. No son esferas ni réplicas geográficas. Comparten semilla y algunos
generadores vanilla, pero sus chunks y modificaciones se guardan por separado.
Mercurio, Venus, Luna y Marte usan biomas y ajustes de ruido propios, sin océanos
de agua ni estructuras vanilla. Tierra conserva el Overworld. Los gigantes usan
un generador vacío, niebla densa y presión mortal por debajo de Y=160, incluso con
la escafandra básica. No generan una superficie sólida ni plataformas automáticas;
hay pequeños depósitos de hielo y meteoritos entre 512 y 768 bloques del origen.

## Gravedad con eventos

| Planeta | Gravedad relativa | Atmósfera del prototipo |
| --- | ---: | --- |
| Mercurio | 0.38 | Vacío, no respirable |
| Venus | 0.90 | Presión extrema, no respirable |
| Tierra | 1.00 | Normal, respirable |
| Luna | 0.165 | Vacío, no respirable |
| Marte | 0.38 | Tenue, no respirable |
| Júpiter | 2.53 | Presión extrema, no respirable |
| Saturno | 1.07 | Presión extrema, no respirable |
| Urano | 0.89 | Presión extrema, no respirable |
| Neptuno | 1.14 | Presión extrema, no respirable |

Las razones de gravedad son aproximadas. Las categorías de presión de los gigantes
son peligros de jugabilidad, no mediciones a una altitud física definida.

`PlanetGravity` usa el atributo vanilla `Attributes.GRAVITY`:

```java
gravity.addTransientModifier(new AttributeModifier(
        SpaceFabric.id("planet_gravity"),
        planet.gravityScale() - 1.0,
        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
```

En Marte, `0.08` pasa a `0.0304`. El modificador transitorio tiene un identificador
estable: no se acumula, no se guarda en NBT ni reemplaza el valor base. Se aplica
en carga de entidad, viaje entre dimensiones y respawn. Al volver al Overworld o
entrar en Tierra se retira solo nuestro modificador, conservando los de otros mods.

Minecraft sincroniza el atributo con el cliente, necesario para predecir el
movimiento del jugador. El impulso vanilla de salto permanece y produce saltos
más altos con baja gravedad. El daño de caída conserva las reglas vanilla.
Afecta a jugadores y mobs vivos que tengan el atributo; proyectiles, objetos
caídos, vehículos y bloques en caída requieren tratamiento adicional.

Un Mixin sería útil para esos otros tipos de entidad o para cambiar la dirección
de gravedad. No hace falta para la gravedad vertical de estas entidades vivas.

## Atmósfera y escafandra

Cada 20 ticks del mundo, `AtmosphereSystem` inflige **2 puntos de salud, un corazón**
a jugadores expuestos. El tiempo se mide en ticks de simulación. Creativo y
espectador quedan exentos. Se prioriza descompresión o presión extrema sobre
hipoxia para no aplicar dos golpes juntos. En aire no respirable a presión normal
se aplica hipoxia. No hay período de gracia al entrar en un planeta.

Los JSON registran los daños `space_fabric:decompression`,
`space_fabric:crushing_pressure` y `space_fabric:hypoxia`. Sus tags evitan que una
armadura ordinaria o un escudo absorban el peligro. En los gigantes la zona profunda ignora el traje básico. La invulnerabilidad y el resto
del sistema de daño conservan sus reglas vanilla.

**El conjunto completo de hierro funciona como traje provisional.** Las botas térmicas
pueden reemplazar las de hierro para caminar sobre roca caliente. Las cuatro
etiquetas `space_suit/*` permiten reemplazarlo por futuros ítems del mod o equipo
de otro mod mediante datapack. Un casco aislado no protege. Esta fase considera
el traje sellado y con oxígeno ilimitado: faltan tanques, consumo, fugas,
temperatura, radiación, hábitats y protección de mobs. La lluvia ácida de Venus
aumenta progresivamente el veneno y desgasta la armadura, incluso con traje sellado;
consulta la guía de ambientes. Tampoco sustituye el
ahogamiento vanilla bajo el agua. Para añadir oxígeno, incorpora la carga del
tanque en `SuitProtection` y guarda su estado en componentes de ítem o attachments.

## Validación y prueba manual

`./gradlew test` prueba Tierra segura, planetas hostiles, protección del traje,
inmunidad, prioridad de presión e hipoxia a presión normal.
También usa Fabric Loader JUnit y los codecs reales de Minecraft para interpretar
los nueve tipos de dimensión, sus generadores y los tres tipos de daño: **109 casos**
en total. `tools/validate_resources.py` comprueba además JSON y referencias internas.
Estas pruebas no arrancan un mundo ni comprueban la sincronización del cliente.

En un mundo nuevo con comandos habilitados:

1. Arranca el cliente y confirma en el log que se cargaron las nueve dimensiones.
2. Usa `/gamemode spectator` y
   `/execute in space_fabric:mars run tp @s 0 180 0`. Inspecciona el terreno antes
   de salir de espectador. `/planetinfo` muestra el perfil y el atributo de gravedad.
3. En suelo seguro o una plataforma creada en creativo, pasa a supervivencia sin
   equipo: debe llegar un golpe por intervalo. Evita agua, mobs y caídas al probar.
4. Equipa las cuatro piezas de hierro: cesa el daño atmosférico. Retira una: vuelve.
5. Repite en Venus para presión extrema y en Tierra para ausencia de daño. Prueba
   cambios de dimensión, muerte/respawn y reconexión.
6. Compara saltos en Marte y Tierra. Al regresar a `minecraft:overworld`, verifica
   con `/planetinfo` que se retiró nuestro modificador.
7. Visita las nueve dimensiones en espectador. En los gigantes construye una plataforma
   por encima de Y=160 antes de dejar el vuelo y lleva la escafandra completa.

Los comandos vanilla de viaje requieren permisos de operador. `/planetinfo` es
de lectura y está disponible para jugadores normales. Consulta
[docs/validation.md](docs/validation.md) para los resultados reales del entorno;
este procedimiento manual no implica que esas pruebas ya se hayan ejecutado.

## Referencias

- [Plantilla oficial Fabric para 26.1](https://github.com/FabricMC/fabric-example-mod/tree/26.1)
- [Fabric API 26.1](https://github.com/FabricMC/fabric/tree/26.1)
- [Atributos en Fabric](https://docs.fabricmc.net/develop/entities/attributes)
- [Datos vanilla de 26.1 para contrastar esquemas](https://github.com/misode/mcmeta/tree/26.1-data)
