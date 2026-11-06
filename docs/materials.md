Desde 0.16.0, las botas térmicas solas no forman un traje: se necesitan las cuatro
piezas correspondientes de un conjunto especial. [Guía actual de presión y trajes](pressure-and-suits.md).

# Bloques y materiales — versión 0.6.0, Minecraft 26.1

El mod registra 11 bloques con BlockItem, tres ítems adicionales, una pestaña
creativa, texturas originales de 16×16, modelos, traducciones, recetas y loot.
Las clases están completas en `src/main/java/dev/vic17usd/spacefabric/`.

## Catálogo de bloques

Todos los IDs usan el prefijo `space_fabric:`.

| ID | Nombre | Obtención y comportamiento |
| --- | --- | --- |
| `mercury_regolith` | Regolito de Mercurio | Cae como arena al perder soporte. Pala recomendada; admite mano. Dureza 0.6. |
| `lunar_regolith` | Regolito lunar | Cae como arena, textura granular propia y dureza 0.5. Pala recomendada. |
| `venus_hot_rock` | Roca volcánica ardiente | Emite luz 5; al pisarla sin botas térmicas, daño por calor y lentitud. Pico de piedra o mejor. |
| `martian_stone` | Piedra marciana | Roca roja de dureza 3 que constituye el terreno marciano. Pico de piedra o mejor. |
| `ares_crystal_ore` | Mena de cristal de Ares | Pico de diamante o mejor: cristal de Ares y 3–7 XP. |
| `phobosite_ore` | Mena de fobosita | Pico de diamante o mejor: fobosita en bruto y 4–8 XP. |
| `uranian_methane_ice` | Hielo de metano de Urano | Fricción interna 0.995; más resbaladizo que el hielo azul. No se derrite en agua. |
| `neptunian_methane_ice` | Hielo de metano de Neptuno | Mismas propiedades físicas, con textura azul propia. |
| `terrestrial_basalt` | Basalto terrestre | Depósitos profundos exclusivos de `space_fabric:earth`. Pico de piedra o mejor. |
| `jovian_meteorite` | Meteorito joviano | Material de pequeños cuerpos orbitales alrededor de Júpiter. Pico de piedra o mejor. |
| `saturn_ring_ice` | Hielo de los anillos de Saturno | Fragmentos de hielo orbitales, con deslizamiento de hielo azul. |

Los materiales de los gigantes son una interpretación jugable de cuerpos
orbitales. No forman una corteza ni un subsuelo del gigante gaseoso. El hielo de
metano es sólido estable en este prototipo: no simula sublimación o combustión.

## Clases Java funcionales

- [ModBlocks.java](../src/main/java/dev/vic17usd/spacefabric/block/ModBlocks.java)
  registra todos los bloques, sus BlockItem y los codecs de sus tipos especiales.
  Asigna `ResourceKey` mediante `Properties.setId`, requerido por esta versión.
- [PlanetRegolithBlock.java](../src/main/java/dev/vic17usd/spacefabric/block/PlanetRegolithBlock.java)
  hereda `FallingBlock`. Minecraft programa la caída al colocar o actualizar el
  soporte y gestiona la entidad de bloque y su aterrizaje. No hay un tick global
  que recorra todos los bloques del planeta. Los bloques en caída conservan su
  gravedad vanilla, distinta del atributo de gravedad de entidades vivas.
- [HotVolcanicRockBlock.java](../src/main/java/dev/vic17usd/spacefabric/block/HotVolcanicRockBlock.java)
  implementa `stepOn` en el servidor. Cada 10 ticks de la entidad aplica 1 punto
  de daño mediante `hotFloor` y 40 ticks de lentitud I. Respeta los intervalos de
  invulnerabilidad y la protección contra fuego de Minecraft; no fuerza daño que
  el motor haya rechazado. Creativo y espectador quedan exentos.
- [PlanetOreBlock.java](../src/main/java/dev/vic17usd/spacefabric/block/PlanetOreBlock.java)
  hereda `DropExperienceBlock`, el nombre oficial de lo que otros mappings llaman
  `ExperienceDroppingBlock`. Usa los proveedores de XP y la lógica vanilla.
- [MethaneIceBlock.java](../src/main/java/dev/vic17usd/spacefabric/block/MethaneIceBlock.java)
  fija `friction(0.995F)`. En Minecraft esta propiedad actúa como conservación de
  movimiento: un valor mayor implica más deslizamiento, no más agarre físico.
- [ModItems.java](../src/main/java/dev/vic17usd/spacefabric/item/ModItems.java)
  registra los dos materiales y las botas térmicas con componentes de armadura
  funcionales. [ModCreativeTabs.java](../src/main/java/dev/vic17usd/spacefabric/item/ModCreativeTabs.java)
  expone todos los materiales en «Materiales del sistema solar».

`SpaceFabric.onInitialize()` ejecuta esos registros antes de cargar los datapacks
y registra `OreGen`. No hace falta ningún Mixin para estas mecánicas.

## Botas térmicas

Las botas usan estadísticas y apariencia equipada de netherita, nombre propio y
un ítem resistente al fuego. La etiqueta `space_fabric:thermal_boots` es la que
concede protección frente a la roca caliente: las botas de hierro o netherita
normales no están incluidas. Un datapack puede añadir otras botas a esa etiqueta.

También están en `space_fabric:space_suit/boots`: pueden reemplazar las botas de
hierro manteniendo el sellado de una escafandra completa. Las botas por sí solas
no protegen del vacío, la presión o el ácido de Venus.

Receta sin forma: botas de netherita + cristal de Ares + crema de magma.
Para pruebas:

```mcfunction
give @s space_fabric:thermal_boots
give @s space_fabric:ares_crystal
give @s space_fabric:raw_phobosite
```

La resistencia al fuego de un jugador puede impedir el daño de calor; seguirá
recibiendo lentitud si no usa las botas térmicas. El efecto existente puede durar
hasta dos segundos después de equiparse o abandonar la roca. El calor se aplica
también a otras entidades vivas que caminen sobre ella y no tengan botas válidas.

## Drops, experiencia y herramientas

Los datos están en `data/space_fabric/loot_table/blocks/`. Cada bloque tiene su
propia loot table. Las dos menas utilizan alternativas:

1. Con Toque de Seda: entregan el bloque de mena, sin XP mediante la lógica vanilla.
2. Sin Toque de Seda: entregan su ítem exclusivo y aplican `minecraft:ore_drops`
   para Fortuna; el bloque entrega la experiencia configurada.

No se sobreescribe `playerDestroy` ni se generan ítems con código adicional, para
evitar duplicar drops. `requiresCorrectToolForDrops` y los tags
`minecraft:mineable/pickaxe`, `needs_stone_tool` y `needs_diamond_tool` hacen cumplir
la herramienta mínima. Un pico de hierro no obtiene las menas raras; diamante y
netherita sí. La experiencia no aumenta con Fortuna. Creativo conserva sus reglas
vanilla de ausencia de drops/XP normales.

Las loot tables y su receta se validan con los codecs reales del juego. La
comprobación dentro de una partida de Fortuna, Toque de Seda y aparición de
orbes de experiencia sigue pendiente; no se afirma haber ejecutado esa partida.

## Generación con Fabric y datos

[OreGen.java](../src/main/java/dev/vic17usd/spacefabric/worldgen/OreGen.java) guarda
las claves de `PlacedFeature` y añade cada una al bioma correspondiente:

```java
BiomeModifications.addFeature(
        BiomeSelectors.includeByKey(PlanetBiomes.key(Planet.MARS)),
        GenerationStep.Decoration.UNDERGROUND_ORES,
        OreGen.ARES_CRYSTAL
);
```

Los JSON de `worldgen/configured_feature/` definen objetivo, tamaño y bloque de
salida. Los de `worldgen/placed_feature/` definen intentos por chunk y altura:

| Feature | Objetivo | Bloques por veta (máximo) | Intentos/chunk | Rango Y de origen |
| --- | --- | ---: | ---: | --- |
| `mercury_regolith` | Basalto liso | 24 | 8 | −48 a 96 |
| `lunar_regolith` | Basalto liso | 24 | 8 | −48 a 96 |
| `venus_hot_rock` | Basalto | 24 | 12 | −48 a 128 |
| `ares_crystal` | Piedra marciana | 5 | 3 | −48 a 24 |
| `phobosite` | Piedra marciana | 3 | 2 | −56 a −8 |
| `terrestrial_basalt` | Roca base del Overworld, solo en Tierra | 32 | 6 | −48 a 0 |

Son intentos, no una garantía de obtener esa cantidad: las cuevas, el aire y los
materiales no reemplazables reducen el resultado. El regolito también cubre las
superficies de Mercurio y la Luna mediante `surface_rule`. La roca caliente aparece
en superficie de Venus y en depósitos interiores. Los carvers marcianos y el
hierro previo se actualizaron para aceptar la nueva piedra marciana.

Tierra comparte biomas con el Overworld. Por eso `TerrestrialBasaltFeature` verifica
`context.level().getLevel().dimension()` antes de colocar bloques: añadir una
feature a un bioma vanilla no debe alterar todas las dimensiones que lo utilizan.

## Depósitos orbitales

Los cuatro gigantes usan `space_fabric:orbital` con `layers: []`.
`OrbitalChunkGenerator` conserva la decoración de su bioma: el flat vanilla
descarta las features cuando está vacío y el bioma no es `minecraft:the_void`.

`OrbitalMaterialFeature` coloca cuerpos pequeños entre Y=222 y Y=234, en una
banda a 512–768 bloques del origen de cada dimensión. Cada chunk de esa banda
tiene una probabilidad de 1/4 de intentarlo. Los cuerpos tienen radio horizontal
de 3–5 y semialtura de 2 bloques, permanecen dentro de su chunk y solo ocupan aire.
No cubren todo el mundo ni crean una plataforma automática de llegada.

- Júpiter: `jovian_meteorite`.
- Saturno: `saturn_ring_ice`.
- Urano: `uranian_methane_ice`.
- Neptuno: `neptunian_methane_ice`.

Desde 0.16.0, la presión y temperatura dependen de la profundidad y los límites
del traje; consulta [Presión y trajes](pressure-and-suits.md). Para recolectar en órbita se necesita
escafandra y una forma segura de desplazamiento. No hay subsuelo donde generar
menas ordinarias en estos gigantes.

## Traducciones, modelos y texturas

Los nombres completos están en
[es_es.json](../src/main/resources/assets/space_fabric/lang/es_es.json), junto con
los ítems y la pestaña creativa. También hay traducción inglesa. Los bloques tienen
sus propios `blockstates`, `models/block`, definiciones `items` y texturas PNG de
16×16 en `assets/space_fabric/`. Las botas reutilizan el aspecto de netherita.

`tools/generate_material_resources.py` genera de forma determinista esos recursos
y las definiciones de vetas. Sus PNG son originales y no necesitan descargas.
Ejecutarlo vuelve a escribir los archivos generados: revisa los cambios si has
personalizado manualmente esos assets. No hace falta ejecutarlo para compilar.

## Comprobación manual pendiente

En un mundo de pruebas nuevo:

1. Comprueba la pestaña creativa, nombres en español y ausencia de texturas faltantes.
2. Coloca regolito suspendido: debe caer y poder recogerse al romperlo.
3. En una zona segura de Tierra, coloca roca caliente. En supervivencia, compara
   caminar sin botas, con botas de hierro y con botas térmicas.
4. Mina cada mena con pico de hierro, diamante, Fortuna y Toque de Seda. Comprueba
   los ítems y la XP, usando menas nuevas para cada combinación.
5. Compara el deslizamiento sobre hielo azul y ambos hielos de metano.
6. Explora chunks nuevos de los planetas sólidos para revisar superficies y vetas.
7. En los gigantes, recorre en creativo la banda orbital alrededor de X=600, Z=0.
   No desactives el vuelo hasta localizar un depósito o disponer de plataforma.

La compilación y pruebas automatizadas están documentadas en `validation.md`.
No sustituyen la prueba física y visual en cliente ni una sesión multijugador.

La versión 0.6.0 añade corrosión de armadura y cavernas dentro de cuerpos de hielo;
consulta [environments.md](environments.md) para los riesgos y efectos visuales.

La ampliación geológica de Mercurio, Venus y Marte está en [geology.md](geology.md).
El catálogo inicial de esta guía se conserva junto a esos materiales nuevos.
