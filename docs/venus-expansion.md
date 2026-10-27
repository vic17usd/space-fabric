# Fauna y asentamientos de Venus — 0.28.0 / Fabric 26.1

Los cuatro archivos aportados están integrados en el JAR principal del mod.
Sustituye el JAR por **space-fabric-0.28.0.jar en cliente y servidor** y reinicia.
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

| Estructura | Tamaño adaptado | Bloques finales | Plantillas pequeñas |
|---|---|---:|---:|
| Copper Crown Village | 137×79×130 | 61.385 | 65 |
| Sunstone Pyramid Bazaar | 173×60×171 | 83.499 | 104 |

Se generan sobre terreno nivelado de basalto en Venus. Se conservan edificios,
torres y pirámides, retirando los basamentos del editor y adaptando materiales:
madera por piedra/basalto, lana
por terracota y agua por vidrio verde que representa depósitos sellados. No se
introduce agua líquida ni madera combustible en estas construcciones.

Cada ubicación elige una de las dos mediante un conjunto compartido de estructuras,
con `spacing: 128`, `separation: 64`: una oportunidad por región de 2048×2048 bloques,
sujeta al bioma, a un relieve suave y al espacio vertical disponible. Así sus parcelas no se
amontonan. Las estructuras se generan en **chunks nuevos**; el contenido existente
del mundo se conserva.

La altura se calcula una sola vez con una cuadrícula acotada de muestras. Cada
chunk prepara solo sus columnas de parcela y coloca sus propias piezas. Las piezas
de edificio miden como máximo 16×16 en planta: no se recorre una plantilla de
200.000 bloques completa por cada chunk. La pieza de suelo se procesa antes de las
piezas de construcción del mismo chunk. Las rotaciones, la altura y el origen se
guardan para que la construcción continúe al cargar los chunks vecinos.

## Integración con el suelo — 0.28.0

Se corrigen dos causas independientes del problema:

- **El modelo original:** Copper Crown Village incluía plataformas de piedra de
  2, 14 y 38 bloques. La adaptación elimina 141.350 bloques de esos basamentos
  y baja las secciones completas de edificios al mismo nivel. Se retira la vieja
  escalera al pedestal central. El bazar pierde sus dos capas inferiores; la
  pirámide conserva sus peldaños como parte de su arquitectura.
- **La altura del terreno:** se usa una mediana de alturas, limitada a cuatro
  bloques sobre la muestra más baja. Los lugares con más de ocho bloques de
  desnivel entre muestras se rechazan. Ya no se eleva todo el conjunto hasta el
  pico más alto de una zona volcánica.

Las cornisas desconectadas reciben pequeñas uniones de mampostería. Los toldos
sueltos reciben patas en sus esquinas, sin levantar un macizo debajo de ellos.
Se añaden 32 bloques de apoyo en la aldea y 1.648 en el bazar; ningún apoyo nuevo
supera nueve bloques. Cada parte importada tiene conexión con el piso o con otro
elemento apoyado. El suelo plano de los edificios queda en la cota de la parcela.

Una franja de **16 bloques** conecta gradualmente esa parcela con la superficie
original. En el borde exterior se conserva el terreno, evitando el corte vertical
de una plataforma rectangular. El muestreo de ruido sigue siendo acotado y no
carga plantillas en la ruta de `/locate`; las pendientes grandes se descartan
al detectar la diferencia, sin completar el resto del muestreo.

Los controles se guardan en los JSON de cada estructura: `padding: 16` y
`max_terrain_relief: 8`. Las rotaciones, la altura y el ancho de transición se
serializan en las piezas. Los inicios de estructura de versiones anteriores
siguen cargando sus datos originales. Las plantillas de `venus/surface_v2/`
son exclusivas de los nuevos conjuntos. Se conservan intactos los 179 archivos
NBT anteriores porque sus identificadores están guardados en mundos existentes.

**Partidas existentes:** actualizar el JAR no mueve ni elimina edificios ya
guardados, ni sus bases anteriores. La corrección se observa en nuevos inicios
de estructura en chunks aún no generados. Para una revisión manual, usa un mundo
de prueba o una zona nueva. La generación natural puede descartar más ubicaciones
que antes porque ahora exige terreno adecuado.

La importación es reproducible a partir de los JSON originales conservados:

```sh
python3 tools/import_venus_structures.py --stored-sources
```

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
- `worldgen/structure/VenusTerrain`: referencia baja, rechazo de pendientes y
  transición de la parcela hacia el terreno original.
- `data/space_fabric/worldgen/biome/venus.json`: pesos y grupos editables.
- `data/space_fabric/worldgen/structure/{copper_crown_village,sunstone_pyramid_bazaar}.json`:
  dimensiones y referencias de piezas; `worldgen/structure_set/venus_settlements.json`:
  frecuencia de generación.
- `tools/import_venus_structures.py`: importación repetible de las construcciones.
- [Atribuciones y archivos originales](../third_party/venus_expansion/README.md).

Las pruebas verifican los límites de aparición, la franja nocturna, atributos,
huevos y hábitat de las seis especies. Se cargan todas las plantillas con los
codecs nativos, se cuentan los bloques, se comprueban paletas, dimensiones y
conexión de todas las partes al piso,
y se generan ambas estructuras con el generador real de Venus, incluyendo guardado
de piezas y preparación de suelo limitada al chunk. Se colocan todas las piezas
de ambos edificios, chunk por chunk, y se comprueba que cada bloque llega a su
posición transformada y que el piso tiene soporte. El chequeo de render por CPU
anima los nuevos rigs en vuelo, ataque y retirada y comprueba vértices y UV.

La compilación y las pruebas no abren una partida ni una GPU: la observación de
apariciones, construcciones completas, IA y renderizado con Sodium en el juego
sigue pendiente. `/locate` también puede tardar por la búsqueda normal del mundo;
la división en piezas evita cargar las plantillas completas en su muestreo inicial.

La revisión 0.28.0 pasa **380 pruebas JUnit y siete chequeos de render por CPU**.
El JAR contiene 169 plantillas nuevas y mantiene byte por byte las 179 anteriores.
También se comprueban la regeneración determinista del importador y el rechazo
de un toldo que necesitaría un pilar de veinte bloques.
