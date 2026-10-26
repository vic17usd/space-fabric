Desde 0.16.0, la exposición, los trajes y la precipitación de Venus se actualizan en
[Presión y trajes](pressure-and-suits.md). La descripción de lluvia ácida de esta guía
corresponde al sistema anterior, conservado para datapacks que lo reactiven.

# Ambientes, ciclos solares y riesgos — Minecraft 26.1, versión 0.5.0

La implementación usa `EnvironmentAttribute`, `WorldClock` y `Timeline` de
Minecraft 26.1. Los Mixins se limitan al clima independiente, la compatibilidad
del sueño y los cambios dinámicos de cielo/niebla. No usa las APIs antiguas de
`DimensionEffects` de tutoriales de 1.21.

## Perfiles visuales

| Destino | Cielo base | Niebla normal (inicio–fin, bloques) | Luz solar relativa | Estrellas |
| --- | --- | --- | ---: | --- |
| Mercurio | Negro absoluto | 1024–4096 | 1.00 | Visibles también durante el día |
| Venus | Ocre | 0–28; hasta 8 en tormenta | 0.45 | Ocultas; disco solar oculto |
| Tierra | Azul | 0–256 | 1.00 | Ciclo nocturno vanilla |
| Luna | Negro absoluto | 1024–4096 | 1.00 | Visibles también durante el día |
| Marte | Rojo/naranja | 0–112; hasta 12 en tormenta | 0.80 | Tenues de noche; ocultadas por polvo |
| Júpiter | Crema/marrón | 0–32 | 0.35 | Ocultas; disco solar oculto |
| Saturno | Dorado | 0–40 | 0.25 | Ocultas; disco solar oculto |
| Urano | Cian | 0–36 | 0.16 | Ocultas; disco solar oculto |
| Neptuno | Azul | 0–28 | 0.12 | Ocultas; disco solar oculto |

Las distancias controlan el desvanecimiento de niebla del motor; no son una
simulación volumétrica de gases. La luz indicada es el multiplicador del mediodía,
que la timeline reduce durante la noche. Las estrellas de los mundos sin atmósfera
son una decisión visual jugable; no se simula el deslumbramiento óptico del Sol.

La configuración está en `data/space_fabric/dimension_type/<destino>.json` y en los
biomas propios. Ejemplo abreviado de Mercurio:

```json
{
  "default_clock": "space_fabric:mercury",
  "timelines": ["space_fabric:mercury_day"],
  "attributes": {
    "minecraft:visual/sky_color": "#000000",
    "minecraft:visual/fog_color": "#000000",
    "minecraft:visual/fog_start_distance": 1024,
    "minecraft:visual/fog_end_distance": 4096,
    "minecraft:visual/sky_light_factor": 1.0,
    "minecraft:visual/star_brightness": 0.8,
    "minecraft:visual/sunrise_sunset_color": "#00000000"
  }
}
```

Es un fragmento explicativo: los archivos reales conservan las alturas y los demás
campos obligatorios del tipo de dimensión. No reemplaces un archivo completo por
este fragmento.

`PlanetSkyMixin` refuerza el negro opaco en el render state de Luna/Mercurio y
elimina su halo de amanecer. Oculta el disco lunar terrestre en otros planetas y
el Sol visible tras las atmósferas opacas. `PlanetFogMixin` interpola la niebla de
Marte/Venus durante tormentas, sin ampliar nieblas más cortas ya existentes y sin
alterar la niebla de agua/lava/nieve en polvo. Bajo un techo deja de aplicar la
capa de tormenta exterior; la niebla base de la dimensión permanece.

Los Mixins de cliente solo aparecen en `space_fabric.client.mixins.json`, declarado
para `environment: client` en `fabric.mod.json`. Un servidor dedicado no necesita
cargar clases gráficas. Están escritos contra nombres oficiales y firmas exactas
de Minecraft 26.1, con `remap = false`.

## Día solar y relojes independientes

Se escala un día terrestre a los 24 000 ticks/20 minutos de Minecraft. Para la
iluminación interesa el **día solar**, que puede diferir de la rotación sidérea.

| Destino | Día solar relativo a Tierra | Duración aproximada en el juego |
| --- | ---: | ---: |
| Mercurio | 175.94 | 58.65 horas |
| Venus | 116.75 | 38.92 horas |
| Tierra | 1.00 | 20 minutos |
| Luna | 29.5306 | 9.84 horas |
| Marte | 1.02749 | 20.55 minutos |
| Júpiter | 0.41354 | 8.27 minutos |
| Saturno | 0.444 | 8.88 minutos |
| Urano | 0.71833 | 14.37 minutos |
| Neptuno | 0.67125 | 13.43 minutos |

Los gigantes tienen rotación diferencial y carecen de una superficie de referencia
terrestre. Sus cifras son aproximaciones de jugabilidad. Venus y Urano invierten
la pista angular para representar el giro retrógrado. No se modelan órbitas,
inclinación axial, estaciones ni precesión.

Cada destino tiene su `world_clock/<destino>.json` —un registro vacío `{}` es
válido— y una `timeline/<destino>_day.json` con período de 24 000 ticks de ese reloj.
`PlanetRotation` aplica la velocidad al iniciar o recargar datos:

```java
server.clockManager().setRate(clock, (float) (1.0 / solarDayEarthDays));
```

El gestor nativo mantiene fracciones de tick, guarda los contadores y sincroniza
la tasa con los clientes. No se asigna `setDayTime` cada tick ni se modifica el
tiempo del Overworld para simular otro planeta. La regla `minecraft:advance_time`
pausa/reanuda los relojes planetarios. El clima y la corrosión avanzan con ticks
de simulación, no con el reloj solar acelerado o ralentizado.

Tierra también tiene su reloj independiente. Sus pistas de fases lunares y horarios
de aldeanos apuntan a ese reloj. `PlanetSleepClockMixin` adapta el despertar al
próximo amanecer del reloj propio; dormir en Tierra no adelanta el Overworld.

Los JSON completos se generaron a partir de las pistas vanilla de 26.1 mediante
`tools/generate_environment_resources.py`. Para regenerarlos se necesita el JAR
de desarrollo ya descargado por Gradle. No es un paso necesario para compilar y
reescribe los archivos generados: conserva antes tus personalizaciones.

## Clima que no se filtra entre dimensiones

En 26.1, el `WeatherData` vanilla pertenece al servidor y algunos paquetes se
difunden globalmente. Cambiar ese objeto desde Venus también puede afectar otros
mundos. `ServerLevelWeatherMixin` sustituye el acceso y avance de clima únicamente
en las dimensiones del mod; cada una tiene su propio `SavedDataType<WeatherData>`.

Los datos quedan guardados bajo `space_fabric:weather_<destino>` en el almacenamiento
del servidor. El código usa el codec y sistema de guardado nativos. El Mixin
filtra además los broadcasts vanilla globales para que no sobrescriban el estado
visual de los jugadores que están en planetas. La lluvia se sincroniza solo con
los jugadores del nivel correspondiente.

| Mundo | Intervalo despejado | Duración de tormenta |
| --- | --- | --- |
| Venus | 1–4 minutos | 1–3 minutos |
| Marte | 6.7–20 minutos | 2–5 minutos |
| Tierra | 10–140 minutos | 10–20 minutos |
| Restantes | Despejado; nubes/niebla estáticas según perfil | Sin lluvia natural |

Son tiempos de simulación a 20 TPS. El primer intervalo empieza despejado; los
siguientes varían con el RNG del nivel y sus temporizadores se conservan al
guardar. La regla `minecraft:advance_weather` detiene los temporizadores. Los
comandos de operador permiten forzar el estado incluso con esa regla pausada.

Marte mantiene `has_precipitation: false`: el estado de tormenta controla polvo,
partículas y niebla, sin lluvia de agua. Venus conserva precipitación. El clima
se interpola durante unos segundos al empezar o terminar.

## Corrosión de Venus

`VenusAcidSystem` usa un attachment de Fabric no persistente por jugador. Se cuenta
una exposición por segundo de simulación bajo precipitación directa en Venus.
Al cubrirse, morir, reconectar o cambiar de dimensión, empieza una nueva exposición.
No hay mapas estáticos de jugadores ni estado de exposición que sobreviva a mundos.

| Tiempo continuo expuesto | Veneno si la escafandra está incompleta | Desgaste de cada pieza mojada |
| --- | --- | --- |
| 1–14 segundos | I | 1 punto cada 5 segundos |
| 15–44 segundos | II | 2 puntos cada 5 segundos |
| 45 segundos o más | III | 3 puntos cada 5 segundos |

Una escafandra completa evita el veneno, pero el ácido sigue desgastando su
armadura. Se utiliza `ItemStack.hurtAndBreak`, respetando Irrompibilidad y la
notificación de rotura. Si se rompe una pieza, la evaluación atmosférica del mismo
intervalo ya reconoce la pérdida de sellado.

Creativo y espectador quedan exentos. Refugiarse impide nuevas aplicaciones, aunque
el veneno anterior puede durar hasta tres segundos más. Un techo no es un hábitat
presurizado: no impide el daño de presión si falta escafandra. El veneno vanilla
no mata por sí solo; presión, lava y meteoritos sí pueden hacerlo.

`StormParticles` añade polvo rojo en Marte y motas amarilloverdes en Venus para
acompañar la niebla. La lluvia de Venus conserva los sprites y sonido vanilla;
no se presenta como una simulación química del ácido sulfúrico.

## Meteoritos interactivos

`MeteorSystem` opera solo en Luna y Mercurio. Cada 200 ticks tiene una probabilidad
de 1/24 de intentar un meteorito cerca de un jugador vivo en supervivencia. El
intervalo medio de intentos es de unos cuatro minutos por nivel, no por jugador.

Usa una `FallingBlockEntity` vanilla con el bloque de meteorito ya registrado:

```java
var meteor = FallingBlockEntity.fall(level, position, ModBlocks.JOVIAN_METEORITE.defaultBlockState());
meteor.dropItem = false;
meteor.setHurtsEntities(1.5F, 8);
```

Comprueba un chunk ya cargado y espacio libre sobre el terreno; nunca fuerza
generación para lanzar un evento. El impacto puede hacer hasta ocho puntos de
daño y dejar un fragmento sólido. No hay explosión ni excavación dinámica de un
cráter. Respeta `minecraft:mob_griefing`; al desactivarlo no lanza meteoritos.
La entidad vanilla ya aporta renderizado, red, caída y guardado.

## Cráteres y cuevas heladas

Los cráteres de Luna/Mercurio se mantienen. Marte añade el placed feature
`space_fabric:mars_crater`, con probabilidad 1/4 por chunk; su fondo usa piedra
marciana. Todos referencian el configured feature `space_fabric:crater`, cuyo tipo
Java se registra en `BuiltInRegistries.FEATURE`. Son cráteres pequeños de 8–12
bloques de diámetro, no macrocuencas.

Urano y Neptuno siguen sin tener corteza. `FrozenCaveFeature` genera cuerpos de
hielo de metano de aproximadamente 13 bloques de anchura y 45 de altura en la banda
orbital ya existente, con una cámara de aire bajo unos 28 bloques de hielo y una
galería lateral escalonada. Las escrituras quedan dentro del chunk de decoración.
Su centro está en Y=218 y la cámara por encima de la zona mortal Y<160.

El configured feature de Urano contiene:

```json
{
  "type": "space_fabric:frozen_cave",
  "config": {
    "state": {"Name": "space_fabric:uranian_methane_ice"}
  }
}
```

Su placed feature:

```json
{
  "feature": "space_fabric:uranus_frozen_cave",
  "placement": [
    {"type": "minecraft:rarity_filter", "chance": 16},
    {"type": "minecraft:biome"}
  ]
}
```

Se añade al bioma mediante `BiomeModifications.addFeature` en el paso
`TOP_LAYER_MODIFICATION`. Neptuno tiene archivos separados con su propio hielo.
Las cavernas solo aparecen en chunks nuevos, en cuerpos de la banda a 512–768
bloques del origen. No se afirma que exista un subsuelo planetario continuo.

## Comandos de prueba

Disponibles para operadores en el planeta actual:

```mcfunction
planetenvironment info
planetenvironment noon
planetenvironment midnight
planetenvironment storm
planetenvironment clear
planetenvironment meteor
```

`storm`/`clear` fijan dos minutos del estado solicitado; la transición visual no
es instantánea. Usa estos comandos para el clima planetario: el comando vanilla
`weather` no es la interfaz de este sistema independiente. `meteor` requiere
Luna/Mercurio, `mob_griefing` y un chunk adecuado; informa si no puede generarlo.
Los cambios de hora se sincronizan sin modificar la hora de otros mundos.

## Evidencia y límites de validación

`./gradlew build` compila el código y ejecuta 109 casos. Las pruebas cargan todos
los registros, comparan iluminación de mediodía/noche con timelines reales,
comprueban el Mixin climático aplicado a `ServerLevel`, las firmas exactas de
los métodos gráficos, la progresión ácida y el codec de clima.

La feature de cavernas se ejecuta sobre un volumen de bloques de prueba: se verifica
que construye hielo, conserva cámara/entrada y no escribe fuera de su chunk.
Esto no equivale a generar y explorar una partida completa.

No se ejecutó un cliente gráfico, una sesión multijugador ni el arranque de un
servidor de juego. Sigue pendiente revisar apariencia, transición de clima entre
dimensiones, guardado/reconexión de relojes, corrosión real de armadura, impactos
y compatibilidad con otros mods de renderizado. Usa un mundo de pruebas nuevo
antes de preparar el sistema de transporte.
