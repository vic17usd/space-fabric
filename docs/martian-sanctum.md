# Santuario subterráneo de Marte — Fabric 26.1, versión 0.13.0

Nombre visible actualizado en 0.30.1. [Catálogo y búsquedas en español](structure-names.md).

Se integra el santuario del JAR aportado con **Basalt Ember Golem** en el altar
central y **cuatro Basalt Bosswarden**. Se conservan sus modelos, animaciones y
texturas originales. No necesitas instalar los tres mods externos.

## Distribución: una estructura por cada región de 20.000 × 20.000

Se utiliza una cuadrícula fija con centros en `(8 + 20.000·n, 8 + 20.000·m)`.
Cada región de 20.000 bloques de lado contiene un centro; los vecinos de la
cuadrícula quedan separados exactamente 20.000 bloques. En el primer cuadrado
positivo `[0,20.000) × [0,20.000)` hay un único centro, cerca de `(8,8)`.
Es una distribución por área, no una garantía de una estructura por cualquier
trayecto de 20.000 bloques ni una cantidad por radio circular.

`data/space_fabric/worldgen/structure_set/soulfire_netherite_sanctum.json`:

```json
{
  "structures": [{"structure": "space_fabric:soulfire_netherite_sanctum", "weight": 1}],
  "placement": {
    "type": "minecraft:random_spread",
    "spacing": 1250,
    "separation": 1249,
    "spread_type": "linear",
    "salt": 624832017
  }
}
```

Las unidades son chunks: `1250 × 16 = 20.000`. La diferencia entre `spacing`
y `separation` es uno, por lo que el generador no desplaza aleatoriamente el
candidato dentro de la región. No se usa una probabilidad que pueda omitir regiones.
El bioma permitido es únicamente `space_fabric:mars`, presente en Marte.
Las copias que un operador coloque con comandos quedan fuera de esta distribución.

## Cámara profunda y diseño

| Elemento | Configuración |
| --- | --- |
| Edificio | 53 × 32 × 53 bloques |
| Base | Y=-60, cuatro bloques por encima del fondo del mundo |
| Techo del edificio | Y=-29 |
| Cubierta exterior | 16 capas de roca hasta Y=-13 |
| Jefe | Centro del altar, aproximadamente Y=-51 |
| Guardias | Cuatro posiciones cardinales alrededor del altar, aproximadamente Y=-56 |

El original contiene 40.606 bloques de piedra, ladrillos, obsidiana y obsidiana
llorosa. El conversor añade aire explícito para excavar la cámara; sella huecos
externos con piedra marciana, deja espacio para el jefe y construye una escalinata
ancha al altar. Cuatro braseros de fuego de almas completan la arena.

La cubierta rellena solo aire y fluidos con piedra marciana. Evita que un cañón
muy profundo deje el templo expuesto; en esos casos puede elevar localmente el
suelo del cañón sobre la cámara. No escribe por encima de Y=-13 ni aplana la
superficie habitual del planeta. La base mantiene cuatro capas inferiores del
mundo fuera de la plantilla.

El NBT adaptado contiene 89.888 posiciones explícitas, incluido aire. Cada chunk
coloca solo su parte; no genera anticipadamente los chunks de alrededor. El tipo
`space_fabric:martian_sanctum` y su pieza guardan rotación, origen y límites.
`/locate` calcula un inicio válido sin excavar la cámara durante la búsqueda.

## Jefe y guardaespaldas

| Entidad | Vida | Combate |
| --- | ---: | --- |
| `space_fabric:basalt_ember_golem` | 1.200 | Altura 8,1; golpe de 24 puntos con radio 9, línea de visión, animación de preparación y barra de jefe |
| `space_fabric:basalt_bosswarden` | 80 | Altura 3; daño 14, armadura 14, escudo frontal y aceleración durante 160 ticks cuando atacan al jefe |

La barra del jefe solo se muestra cerca de su cámara, con un máximo de 24 bloques
de diferencia vertical, para no revelar el santuario desde la superficie habitual.

Los ataques se resuelven en el servidor una vez por animación. El impulso vertical
se reduce para la gravedad marciana. Los escudos bloquean ataques frontales
ordinarios; las hachas pueden interrumpir el bloqueo. Los ataques que atraviesan
escudos conservan esa propiedad. No se destruye el escenario al golpear.

Ambos tienen soporte vital, inmunidad al fuego y gravedad marciana del sistema
global. Conservan su hogar al guardar/cargar y regresan a él si se alejan. Los
guardias reconocen únicamente al Basalt Ember Golem cercano, no a cualquier mob
grande. No se atacan entre ellos ni al jefe. En Pacífico permanecen sin atacar.

El jefe entrega un núcleo de obsidiana al morir, sujeto a las reglas de botín;
se mantiene su textura importada. El núcleo todavía no tiene recetas. El jefe
aporta 250 puntos de experiencia y cada guardia 30 según las reglas vanilla de XP.

Son residentes de plantilla, sin spawns naturales en el bioma ni bucle de reposición.
No desaparecen por distancia y no se duplican al cargar chunks. Si mueren, no se
recrean automáticamente. El diseño es una cámara oculta, sin entrada automática
desde la superficie: se descubre explorando el subsuelo o usando comandos.

## Archivos y reproducción

```text
src/main/java/dev/vic17usd/spacefabric/
  entity/sanctum/                   # Registro, soporte vital, IA, escudos y barra de jefe
  client/sanctum/                   # Modelos, animaciones y renderers importados
  worldgen/structure/MarsSanctumStructure.java
  worldgen/structure/MarsSanctumPiece.java
src/main/resources/data/space_fabric/
  structure/soulfire_netherite_sanctum.nbt
  worldgen/structure/soulfire_netherite_sanctum.json
  worldgen/structure_set/soulfire_netherite_sanctum.json
  tags/worldgen/biome/has_structure/soulfire_netherite_sanctum.json
```

Se usa un tipo de estructura Java propio, con generación en `underground_structures`;
no necesita un `template_pool` jigsaw. Los identificadores del tipo y entidades se
registran al iniciar el mod; los registros dinámicos se cargan desde los JSON.

```sh
python3 tools/prepare_martian_sanctum.py
python3 tools/validate_resources.py
./gradlew build --console=plain
```

La fuente y sus recursos están documentados en
[la procedencia de los tres JAR](../third_party/martian_sanctum/README.md).

## Instalación y comandos

Sustituye el JAR anterior por `space-fabric-0.13.0.jar` en cliente y servidor,
con Fabric API para Minecraft 26.1. Esta versión incluye también la corrección de
UV del cohete para optimizadores gráficos; el ítem mantiene el modelo confirmado.

La generación requiere **chunks nuevos**. No se insertan estructuras automáticamente
en terreno ya generado, para conservar las construcciones del jugador. Si la zona
inicial de Marte ya se generó, el santuario de esa región no aparecerá retroactivamente:
usa un mundo de pruebas nuevo para comprobar la primera región.

```mcfunction
/execute in space_fabric:mars run locate structure space_fabric:soulfire_netherite_sanctum
/give @s space_fabric:basalt_ember_golem_spawn_egg
/give @s space_fabric:basalt_bosswarden_spawn_egg
```

Para colocar una copia manual bajo la posición X/Z elegida en un área de pruebas:

```mcfunction
/execute in space_fabric:mars positioned 1000 0 1000 run place structure space_fabric:soulfire_netherite_sanctum
```

La altura la determina el tipo de estructura, no el Y del comando. Colocar otra
copia manual puede añadir otro grupo de residentes; la carga normal no lo hace.

Validación: 226 pruebas JUnit, generación nativa con varias semillas y regiones,
colocación real en un volumen de prueba, separación y recursos verificados. El
combate y la apariencia todavía requieren inspección dentro de una partida real;
no se presenta la validación de codecs como una sesión de juego.
