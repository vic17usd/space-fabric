# Fauna y asentamientos de Venus — 0.26.0 / Fabric 26.1

Los cuatro archivos aportados están integrados en el JAR principal del mod.
Sustituye el JAR por **space-fabric-0.26.0.jar en cliente y servidor** y reinicia.
Los JAR originales sirven de fuentes de importación; el mod entrega sus contenidos
adaptados y no necesita instalarlos como mods adicionales.

## Nuevos habitantes

| Mob | Comportamiento | Grupo natural | Peso |
|---|---|---:|---:|
| Copper Acidstinger Drone | Volador pequeño; ataques rápidos y retirada al quedar por debajo del 30% de salud; sigue a los grandes voladores cercanos | 1–2 | 5 |
| Sulfurwing Copperstinger | Volador agresivo de unos 3,2 bloques, con ataque de aguijón y núcleo de azufre como recompensa | 1 | 1 |

Se conservan modelos, texturas, animaciones de vuelo/ataque y la retirada del
Drone. El Copperstinger sigue siendo distinto de Sulfurwing Copper Harbinger,
el volador anterior. Ambos nuevos tienen huevos de aparición propios y aparecen
en el inventario creativo y en el catálogo de recetas del mod.

Se integran con `VenusMob`: resistencia al fuego, calor del suelo, veneno, falta
de oxígeno, presión y exposición térmica. Sus atributos reciben el modificador
normal de gravedad de Venus; el vuelo usa navegación aérea y sustentación propia.
Los habitantes venusianos no se atacan entre sí.

## Densidad y noche

La política afecta a **las nuevas apariciones naturales** de toda la fauna
venusiana del mod, no solo a los dos añadidos.

- **Pacíficos:** Acidstone Slime y Ashplume Emberwalker usan grupos de **1–2** y
  comparten un límite de **dos animales existentes en el chunk** al aprobar una
  nueva aparición. El recuento combina ambas especies en toda la columna vertical.
  Sus pesos bajan de 18/16 a 4/4. No se prellenan chunks nuevos con grupos de animales;
  estos se incorporan mediante las apariciones naturales en chunks activos.
- **Agresivos:** máximo de cuatro habitantes hostiles venusianos al aprobar una
  nueva aparición en el chunk. Obsidian Lavafang usa peso 8 y grupos 1–2; el
  Harbinger anterior, peso 2 y grupo 1. Los nuevos usan la tabla de arriba.
- **Noche:** en la franja 13000–22999 del reloj propio de Venus, la comprobación
  de horario admite los intentos válidos. De día solo admite **1 de cada 24**.
  Esto favorece claramente la noche y deja encuentros diurnos ocasionales.
- Harbinger y Copperstinger tienen además un control de proximidad: no se aprueba
  otra aparición del mismo gran volador dentro del entorno de 80×80 bloques del
  chunk candidato.

Se mantienen los límites globales de Minecraft, las comprobaciones de colisión,
la superficie sólida sin líquido, el bioma venusiano y la prohibición de aparición
hostil en Pacífico. El movimiento de animales entre chunks, los huevos, los comandos
y las entidades ya guardadas pueden reunir más individuos; no se eliminan mascotas
o mobs existentes para forzar el límite. Los agresivos vivos pueden seguir activos
al amanecer: la regla controla la aparición, no su comportamiento de combate.

El horario usa `getDefaultClockTime()` de Venus, no el reloj terrestre. Venus
conserva su día solar largo del sistema planetario. Para comprobar la noche:

```mcfunction
/execute in space_fabric:venus run time set space_fabric:venus_midnight
```

## Nuevas estructuras

| Estructura | Tamaño original | Bloques conservados | Plantillas pequeñas |
|---|---|---:|---:|
| Copper Crown Village | 137×117×130 | 202.703 | 75 |
| Sunstone Pyramid Bazaar | 173×62×171 | 82.273 | 104 |

Se generan sobre una parcela nivelada de basalto en Venus. Se conservan las formas
originales, con sustituciones para el ambiente: madera por piedra/basalto, lana
por terracota y agua por vidrio verde que representa depósitos sellados. No se
introduce agua líquida ni madera combustible en estas construcciones.

Cada ubicación elige una de las dos mediante un conjunto compartido de estructuras,
con `spacing: 128`, `separation: 64`: una oportunidad por región de 2048×2048 bloques,
sujeta al bioma y al espacio vertical disponible. Así sus grandes parcelas no se
amontonan. Las estructuras se generan en **chunks nuevos**; el contenido existente
del mundo se conserva.

La altura se calcula una sola vez con una cuadrícula acotada de muestras. Cada
chunk prepara solo sus columnas de parcela y coloca sus propias piezas. Las piezas
de edificio miden como máximo 16×16 en planta: no se recorre una plantilla de
200.000 bloques completa por cada chunk. La pieza de suelo se procesa antes de las
piezas de construcción del mismo chunk. Las rotaciones, la altura y el origen se
guardan para que la construcción continúe al cargar los chunks vecinos.

Comandos de localización, desde Venus:

```mcfunction
/execute in space_fabric:venus run locate structure space_fabric:copper_crown_village
/execute in space_fabric:venus run locate structure space_fabric:sunstone_pyramid_bazaar
```

Para revisar los mobs sin esperar a su aparición natural:

```mcfunction
/give @s space_fabric:copper_acidstinger_drone_spawn_egg
/give @s space_fabric:sulfurwing_copperstinger_spawn_egg
```

Las estructuras no fuerzan residentes: sus habitantes proceden de las apariciones
reguladas del bioma. La protección ambiental de los mobs no convierte los edificios
en espacios respirables para jugadores; continúa haciendo falta un traje adecuado.

## Implementación y verificación

- `entity/venus/ModVenusEntities`: registro de las seis especies y atributos.
- `entity/venus/VenusSpawnRules`: límites compartidos, superficie y reloj nocturno.
- `entity/venus/CopperAcidstingerDrone*`, `SulfurwingCopperstinger*`: IA, vuelo,
  ataques, perfiles y estados sincronizados; `client/venus/*`: modelos y animaciones.
- `worldgen/structure/VenusSurface*`: generación, parcela y piezas persistentes.
- `data/space_fabric/worldgen/biome/venus.json`: pesos y grupos editables.
- `data/space_fabric/worldgen/structure/{copper_crown_village,sunstone_pyramid_bazaar}.json`:
  dimensiones y referencias de piezas; `worldgen/structure_set/venus_settlements.json`:
  frecuencia de generación.
- `tools/import_venus_structures.py`: importación repetible de las construcciones.
- [Atribuciones y archivos originales](../third_party/venus_expansion/README.md).

Las pruebas verifican los límites de aparición, la franja nocturna, atributos,
huevos y hábitat de las seis especies. Se cargan todas las plantillas con los
codecs nativos, se cuentan los bloques, se comprueban paletas y dimensiones,
y se generan ambas estructuras con el generador real de Venus, incluyendo guardado
de piezas y preparación de suelo limitada al chunk. El chequeo de render por CPU
anima los nuevos rigs en vuelo, ataque y retirada y comprueba vértices y UV.

La compilación y las pruebas no abren una partida ni una GPU: la observación de
apariciones, construcciones completas, IA y renderizado con Sodium en el juego
sigue pendiente. `/locate` también puede tardar por la búsqueda normal del mundo;
la división en piezas evita cargar las plantillas completas en su muestreo inicial.
