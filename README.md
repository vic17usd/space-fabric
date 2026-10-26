# Space Fabric — Minecraft Java 26.1

La versión 0.18.2 adapta las ocho menas terrestres al estilo de las tres referencias
aportadas, con **31 texturas de 128px** para menas, materias primas y productos.
[Vista previa y asignación de minerales](docs/earth-geology.md).
La versión 0.18.1 corrige los cohetes rosas y negros del inventario: las nueve
variantes usan ahora un único atlas de texturas, como exige Minecraft 26.1.
La versión 0.18.0 añade **ocho minerales terrestres al Overworld**: bauxita,
casiterita, esfalerita, galena, argentita, pentlandita, ilmenita y fluorita, con
texturas originales, refinado, bloques de almacenamiento y vetas naturales.
[Guía, alturas y vista previa de las texturas](docs/earth-geology.md).
La versión 0.17.0 añade **nueve cohetes planetarios** basados en el modelo 3D actual,
con módulos exteriores distintos, kits de conversión y protección ambiental por
capacidades. Consulta la [guía de cohetes planetarios, recetas y destinos](docs/planet-rockets.md).
La versión 0.16.0 añade presión y composición atmosférica de referencia, exposición
muy rápida a escala 1:72 y cuatro conjuntos de traje con límites distintos.
**El hierro común ya no protege.** Consulta la [guía de presión, tiempos y trajes](docs/pressure-and-suits.md)
antes de actualizar una partida con el personaje en otro planeta.
La versión 0.15.0 integra cuatro habitantes de Venus adaptados al ambiente y su
gravedad: Acidstone Slime, Obsidian Lavafang, Ashplume Emberwalker y Sulfurwing
Copper Harbinger, que patrulla volando. [Guía de fauna venusiana](docs/venus-fauna.md).
La versión 0.14.0 añade una cabina 3D para el piloto, con paneles, consola,
mandos y un visor circular abierto al mundo. Conserva el exterior y la ocultación
del ocupante en tercera persona.
La versión 0.13.0 añade **Soulfire Netherite Sanctum** bajo Marte: Basalt Ember
Golem en el altar y cuatro Basalt Bosswarden, con una estructura por región de
20.000 × 20.000 bloques y centros separados 20.000 bloques. Incluye la corrección
de UV del cohete para instalaciones con optimizadores gráficos.
Consulta la [guía del santuario, jefe y distribución](docs/martian-sanctum.md).
La fauna de superficie de 0.11.0 y las parcelas sin pedestal se conservan.
Consulta la [tabla de aparición, IA y archivos de la fauna marciana](docs/martian-fauna.md).
El comerciante conserva el modelo, saludo e intercambios de `Aldeano Marciano.jar`.
Consulta la [guía de parcelas de Marte](docs/martian-surface-plots.md), la
[guía del asentamiento](docs/martian-haven.md) y la [guía de Breathforge](docs/martian-breathforge.md).
Incluye cohetes pilotables, refinerías de combustible y un mapa estelar
para viajar entre el Overworld y los nueve cuerpos celestes.
Consulta la [guía de cohetes, recetas y viajes](docs/space-travel.md).
Consulta la [guía de geología y refinado](docs/geology.md) y la [guía de ambientes](docs/environments.md).
Consulta la [guía completa de bloques y materiales](docs/materials.md).
Consulta la [guía de terreno y registro de biomas](docs/terrain.md).
Consulta la [tabla de dimensiones y claves Java](docs/dimensions.md).

Descarga del prototipo compilado: [space-fabric-0.18.2.jar](dist/space-fabric-0.18.2.jar)
(en GitHub, pulsa **Download raw file**).
[Checksum SHA-256](dist/space-fabric-0.18.2.jar.sha256).
Fauna original: **17usdd using [CreativeMode.net](https://creativemode.net)**;
[páginas oficiales y atribución](third_party/martian_fauna/README.md),
[licencia de sus recursos](https://creativemode.net/mod-license).
[Procedencia de los cuatro habitantes de Venus](third_party/venus_fauna/README.md).
Santuario y jefe: [procedencia de los tres JAR originales](third_party/martian_sanctum/README.md).
Requiere Minecraft **26.1**, Java **25**, Fabric Loader y Fabric API **0.145.1+26.1**.
Coloca el JAR y Fabric API en la carpeta `mods/`. Es el prototipo descrito abajo;
todavía falta su validación dentro del juego.

Base modular en Java 25 para nueve dimensiones independientes: Mercurio, Venus,
Tierra, Luna, Marte, Júpiter, Saturno, Urano y Neptuno. Usa los nombres oficiales de
Minecraft 26.1, sin Yarn ni remapeo de versiones anteriores.

Implementa dimensiones por datos, gravedad para entidades vivas y daño atmosférico
para jugadores. El terreno tiene biomas propios y rasgos planetarios; los viajes
usan un cohete con un ocupante, tanque e inventario persistentes. La Luna se incluye como satélite explorable.

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

El artefacto será `build/libs/space-fabric-0.16.0.jar`. Instala el mod y Fabric API
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
├── space/                           # Cohete, destinos, reglas y llegada segura
├── entity/                          # Comerciantes y fauna marciana: IA, combate y soporte vital
├── machine/                         # Refinería, BlockEntity y motor de procesamiento
├── menu/                            # Menús de servidor e inventarios sincronizados
├── client/                          # Modelos animados, pantallas, fluido y atmósferas
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
La entrada de cliente registra el renderizado de lava viscosa, el modelo del cohete
y sus pantallas. Los Mixins separan
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
un generador vacío, niebla densa y presión que aumenta al descender. Un traje
solo protege dentro de sus límites de presión y temperatura. No generan una superficie sólida. Al llegar en cohete se
prepara una plataforma a Y=239; hay pequeños depósitos de hielo y meteoritos
entre 512 y 768 bloques del origen.

## Gravedad con eventos

| Planeta | Gravedad relativa | Atmósfera del prototipo |
| --- | ---: | --- |
| Mercurio | 0.38 | Vacío, no respirable |
| Venus | 0.90 | Presión extrema, no respirable |
| Tierra | 1.00 | Normal, respirable |
| Luna | 0.165 | Vacío, no respirable |
| Marte | 0.38 | Tenue, no respirable |
| Júpiter | 2.53 | H₂/He, presión variable, no respirable |
| Saturno | 1.07 | H₂/He, presión variable, no respirable |
| Urano | 0.89 | H₂/He/CH₄, presión variable, no respirable |
| Neptuno | 1.14 | H₂/He/CH₄, presión variable, no respirable |

Las razones de gravedad son aproximadas. Los gigantes usan el nivel de 1 bar a
Y=240 como referencia; sus capas inferiores siguen una aproximación barométrica.

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

El servidor evalúa la exposición cada tick con `AtmosphereProfile` y `PressureExposure`.
A escala 1:72, un presupuesto nominal de 120 segundos se convierte en 34 ticks de
juego (~1,7 s); Venus o una presión excesiva pueden matar en tres ticks (~0,15 s).
Son tiempos calibrados de juego, no tiempos médicos de supervivencia real.

Los conjuntos `vacuum_suit_*`, `venus_suit_*`, `cryo_suit_*` y `abyssal_suit_*`
requieren sus cuatro piezas correspondientes y tienen límites de presión y temperatura.
La armadura común y los conjuntos mezclados no protegen. Los trajes llevan soporte
de O₂ ilimitado y tienen recetas; el de vacío se fabrica con recursos del Overworld.
La cabina del cohete protege hasta 100 bar. Creativo y espectador están exentos.

La [guía de presión y trajes](docs/pressure-and-suits.md) incluye los valores de todos
los planetas, composición, recetas, etiquetas, límites, configuración y actualización.
Venus deja de tener lluvia ácida a nivel del suelo; sus nubes y depósitos conservan
el carácter químico del entorno. Los trajes venusiano y abisal resisten la corrosión.

## Validación y prueba manual

`./gradlew test` prueba Tierra segura, planetas hostiles, protección del traje,
inmunidad, prioridad de presión e hipoxia a presión normal.
También usa Fabric Loader JUnit y los codecs reales de Minecraft para interpretar
los nueve tipos de dimensión, sus generadores y los cuatro tipos de daño; también
comprueba refinería, persistencia del combustible, recetas, llegadas, estructuras, comerciantes y fauna: **248 casos**
en total. `tools/validate_resources.py` comprueba además JSON y referencias internas.
Estas pruebas no arrancan un mundo ni comprueban la sincronización del cliente.

En un mundo nuevo con comandos habilitados:

1. Arranca el cliente y confirma en el log que se cargaron las nueve dimensiones.
2. Usa `/gamemode spectator` y
   `/execute in space_fabric:mars run tp @s 0 180 0`. Inspecciona el terreno antes
   de salir de espectador. `/planetinfo` muestra el perfil y el atributo de gravedad.
3. En suelo seguro o una plataforma creada en creativo, pasa a supervivencia sin
   equipo: debe llegar un golpe por intervalo. Evita agua, mobs y caídas al probar.
4. Equipa el conjunto de vacío para Marte o el venusiano para Venus antes de pasar
   a supervivencia. Retirar una pieza inicia inmediatamente la exposición.
5. Repite en Venus para presión extrema y en Tierra para ausencia de daño. Prueba
   cambios de dimensión, muerte/respawn y reconexión.
6. Compara saltos en Marte y Tierra. Al regresar a `minecraft:overworld`, verifica
   con `/planetinfo` que se retiró nuestro modificador.
7. Visita las nueve dimensiones en espectador. En los gigantes construye una plataforma
   a Y=240 antes de dejar el vuelo y lleva el traje completo adecuado para la profundidad.

Los comandos vanilla de viaje requieren permisos de operador. `/planetinfo` es
de lectura y está disponible para jugadores normales. Consulta
[docs/validation.md](docs/validation.md) para los resultados reales del entorno;
este procedimiento manual no implica que esas pruebas ya se hayan ejecutado.

## Referencias

- [Plantilla oficial Fabric para 26.1](https://github.com/FabricMC/fabric-example-mod/tree/26.1)
- [Fabric API 26.1](https://github.com/FabricMC/fabric/tree/26.1)
- [Atributos en Fabric](https://docs.fabricmc.net/develop/entities/attributes)
- [Datos vanilla de 26.1 para contrastar esquemas](https://github.com/misode/mcmeta/tree/26.1-data)
