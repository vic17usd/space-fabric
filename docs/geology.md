# Geología de Mercurio, Venus y Marte — 0.6.0, Fabric 26.1

Implementación integrada con bloques, BlockItem, materiales, texturas originales,
loot tables, traducciones españolas/inglesas, recetas y worldgen. Se conservan los
IDs de versiones anteriores. Los nuevos bloques aparecen en la pestaña creativa
«Materiales del sistema solar» y mediante `/give @s space_fabric:<id>`.

## Alcance geológico

La lista implementada es la especificada para estos tres planetas, no un catálogo
exhaustivo de todos sus minerales. Se distingue evidencia geológica de diseño:

- Mercurio tiene corteza de silicatos, un núcleo rico en hierro y hielo polar en
  regiones permanentemente sombreadas. Una veta de hierro denso extraíble a escala
  Minecraft representa ese interior; no demuestra la existencia de vetas minables
  de hierro líquido en la corteza real. Esta versión implementa **hierro denso sólido**.
- El basalto y silicatos como olivino/piroxeno son una interpretación plausible de
  rocas de Venus. Los cristales de azufre y el depósito sólido de ácido condensado
  son recursos jugables; no se presentan como depósitos superficiales confirmados.
  El ácido sulfúrico de las nubes no es estable de la misma forma en la superficie
  caliente de Venus.
- En Marte hay evidencia de óxidos de hierro, olivino y sulfatos. Los bloques y
  recetas simplifican sus mezclas y procesos. El horno aporta una abstracción del
  refinado: no se modela toda la química de reducción.

Los materiales ficticios de Ares y fobosita anteriores se conservan; no pasan a
considerarse minerales reales por añadir este catálogo.

Referencias generales: [Mercurio](https://science.nasa.gov/mercury/facts/),
[Venus](https://science.nasa.gov/venus/facts/) y
[Marte](https://science.nasa.gov/mars/facts/), NASA Science.

## Bloques y drops

Todos los IDs usan `space_fabric:`. Las cantidades son antes de Fortuna.

| Planeta | ID | Nombre español | Drop sin Toque de Seda | XP | Herramienta para obtener drop |
| --- | --- | --- | --- | ---: | --- |
| Mercurio | `mercury_silicate` | Silicatos de Mercurio | El bloque | 0 | Pico de piedra o mejor |
| Mercurio | `dense_iron_ore` | Mena de hierro denso | 1–3 `dense_iron_chunk` | 2–5 | **Netherita** |
| Mercurio | `crater_ice` | Hielo eterno de cráter | 2–4 `crater_ice_shard` | 0 | Mano o herramienta |
| Venus | `vulcanized_basalt` | Basalto vulcanizado | El bloque | 0 | Pico de piedra o mejor |
| Venus | `venusian_olivine_ore` | Mena de olivino venusiano | `venusian_olivine` | 1–3 | Pico de piedra o mejor |
| Venus | `pyroxene_ore` | Mena de piroxeno | `pyroxene` | 1–3 | Pico de piedra o mejor |
| Venus | `sulfur_deposit` | Depósito de azufre | 1–3 `sulfur_crystal` | 0 | Pico de piedra o mejor |
| Venus | `condensed_sulfuric_acid` | Ácido sulfúrico condensado | `acid_condensate` | 0 | Pico de piedra o mejor |
| Marte | `iron_oxide_regolith` | Regolito de óxido de hierro | `iron_oxide_dust` | 0 | Mano; pala recomendada |
| Marte | `martian_olivine_stone` | Piedra con olivino marciano | `martian_olivine` | 1–3 | Pico de piedra o mejor |
| Marte | `martian_sulfate_deposit` | Depósito de sulfatos marcianos | 1–3 `sulfate_dust` | 0–2 | Pico de piedra o mejor |

Toque de Seda recupera el bloque de los depósitos que normalmente producen un
material, sin XP mediante la lógica vanilla. Fortuna afecta hierro denso, gemas,
azufre y sulfatos; no multiplica el polvo de regolito, el hielo ni el condensado
ácido. No se generan ítems adicionales desde `playerDestroy`, evitando duplicados.

## Java y API de 26.1

[ModBlocks.java](../src/main/java/dev/vic17usd/spacefabric/block/ModBlocks.java)
registra bloques y BlockItem con `Registry.register`, `ResourceKey` y
`BlockBehaviour.Properties.setId`. Se usan los nombres oficiales, no Yarn.

`FabricBlockSettings` no es la API usada en esta versión. La forma vigente es:

```java
BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .strength(10.0F, 18.0F)
        .requiresCorrectToolForDrops()
        .setId(ResourceKey.create(Registries.BLOCK, SpaceFabric.id("dense_iron_ore")));
```

`strength(dureza, resistencia)` configura dos propiedades distintas. El nivel de
herramienta lo aplican las etiquetas:

```text
data/minecraft/tags/block/mineable/pickaxe.json
data/minecraft/tags/block/needs_stone_tool.json
data/minecraft/tags/block/needs_diamond_tool.json
data/minecraft/tags/block/incorrect_for_diamond_tool.json
```

El hierro denso está en `needs_diamond_tool` y además en
`incorrect_for_diamond_tool`. Así quedan excluidos hierro, diamante y niveles
inferiores, mientras la etiqueta distinta de netherita lo permite. No se borran
valores vanilla: todos los tags usan `replace: false`. Una herramienta incorrecta
puede destruir el bloque lentamente, pero no obtiene su drop, según Minecraft.

[ModItems.java](../src/main/java/dev/vic17usd/spacefabric/item/ModItems.java) registra
los nueve materiales nuevos y los expone al inventario creativo mediante `all()`.
Son materiales de crafteo almacenables. El polvo de sulfato no se registra todavía
como combustible: los sulfatos no son automáticamente combustibles y el sistema
de química/combustión queda para una fase posterior.

## Comportamientos especiales

- [PlanetOreBlock.java](../src/main/java/dev/vic17usd/spacefabric/block/PlanetOreBlock.java)
  extiende `DropExperienceBlock`, el nombre oficial de la clase conocida como
  `ExperienceDroppingBlock` en otros mappings. Los nuevos minerales reutilizan
  esa implementación y proveedores de XP diferentes.
- [CraterIceBlock.java](../src/main/java/dev/vic17usd/spacefabric/block/CraterIceBlock.java)
  extiende `Block`, no `IceBlock`: no tiene el tick de derretimiento por luz.
  Dureza 0.25, resistencia a explosiones 0.4 y sonido de vidrio. Retirar el techo o
  colocar antorchas no lo derrite; el estado «eterno» es una propiedad jugable.
- [HotVolcanicRockBlock.java](../src/main/java/dev/vic17usd/spacefabric/block/HotVolcanicRockBlock.java)
  se instancia para basalto vulcanizado y azufre: daño térmico de 1 punto y
  lentitud I cada 10 ticks de la entidad al caminar sin botas térmicas. Respeta
  inmunidad al fuego, invulnerabilidad, creativo y espectador.
- [CondensedAcidBlock.java](../src/main/java/dev/vic17usd/spacefabric/block/CondensedAcidBlock.java)
  aplica veneno por contacto al pisarlo sin sellado y desgasta botas equipadas
  una vez por segundo. Una escafandra completa evita el veneno, pero sus botas
  siguen corroyéndose. Es independiente de que esté lloviendo.
- [PlanetRegolithBlock.java](../src/main/java/dev/vic17usd/spacefabric/block/PlanetRegolithBlock.java)
  añade caída al regolito oxidado. El polvo de caída usa su color de mapa rojo/naranja.

## Generación por planeta

[OreGen.java](../src/main/java/dev/vic17usd/spacefabric/worldgen/OreGen.java)
registra el nuevo tipo de feature y añade las `PlacedFeature` solo a sus biomas:

```java
BiomeModifications.addFeature(
        BiomeSelectors.includeByKey(PlanetBiomes.key(Planet.MERCURY)),
        GenerationStep.Decoration.UNDERGROUND_ORES,
        OreGen.DENSE_IRON
);
```

Los JSON de los registros dinámicos están en `data/space_fabric/worldgen/`:

| Configured/placed feature | Máximo de bloques/veta | Intentos/chunk | Altura de origen |
| --- | ---: | ---: | --- |
| `dense_iron` | 4 | 3 | −56 a −16 |
| `venusian_olivine` | 7 | 6 | −32 a 100 |
| `pyroxene` | 9 | 8 | −32 a 128 |
| `sulfur` | 8 | 6 | 16 a 120 |
| `condensed_acid` | 3 | 2 | 24 a 120 |
| `iron_oxide` | 24 | 10 | 0 a 112 |
| `martian_olivine` | 8 | 6 | −48 a 80 |
| `martian_sulfate` | 10 | 8 | −16 a 100 |

Son intentos, no cantidades garantizadas. Los objetivos usan tags propios
`mercury_host_rocks`, `venus_host_rocks` y `mars_host_rocks`. Estos aceptan las rocas
nuevas y los materiales anteriores donde corresponde. Las features previas de
regolito y magma también se actualizaron para no dejar de funcionar.

Mercurio utiliza silicatos propios como roca base; Venus utiliza basalto
vulcanizado. Marte mantiene piedra marciana bajo una capa superficial de regolito
oxidado. Las reglas de superficie son distintas de los depósitos subterráneos.

Ejemplo del configured feature de hierro:

```json
{
  "type": "minecraft:ore",
  "config": {
    "size": 4,
    "discard_chance_on_air_exposure": 0.0,
    "targets": [{
      "target": {"predicate_type": "minecraft:tag_match", "tag": "space_fabric:mercury_host_rocks"},
      "state": {"Name": "space_fabric:dense_iron_ore"}
    }]
  }
}
```

## Hielo en sombra permanente

[ShadowCraterIceFeature.java](../src/main/java/dev/vic17usd/spacefabric/worldgen/ShadowCraterIceFeature.java)
construye un cráter pequeño con una cornisa opaca y hielo bajo ella. El techo deja
dos bloques de aire sobre el depósito: no es hielo enterrado sin acceso.

La banda con `|Z| >= 4096` representa regiones polares del mapa plano. No es una
latitud astronómica calculada: Minecraft no tiene esfera planetaria. Se intenta
la feature en 1/16 de sus chunks y únicamente en Mercurio. Todas las escrituras
quedan en su chunk, y el hielo solo se coloca tras comprobar el bloque de techo.
No se usa «es de noche» como sustituto de sombra permanente.

Los archivos `configured_feature/crater_ice.json` y
`placed_feature/crater_ice.json` eligen el bloque y la distribución; se añaden en
`TOP_LAYER_MODIFICATION`, después de la decoración base. La feature no reemplaza
la generación de todos los cráteres ni impone luz artificial al planeta.

## Refinado y pigmentos

| Material | Procesamiento | Resultado |
| --- | --- | --- |
| Trozo de hierro denso | Horno: 400 ticks / alto horno: 200 ticks | 1 lingote de hierro, 0.7 XP de receta |
| Polvo de óxido de hierro | Horno: 200 ticks / alto horno: 100 ticks | 1 pepita de hierro, 0.1 XP de receta |
| 9 pepitas de hierro | Receta vanilla | 1 lingote de hierro |
| Polvo de óxido de hierro | Crafteo sin forma | 1 tinte rojo |

Las cinco recetas nuevas están en `data/space_fabric/recipe/`. El refinado del
regolito común produce pepitas para evitar que cada bloque superficial equivalga
a un lingote entero. Olivino, piroxeno, azufre, sulfatos y condensado ácido quedan
como recursos para recetas posteriores; no se inventa aquí una química completa.

## Assets, traducciones y validación

Los nombres españoles completos están en
[es_es.json](../src/main/resources/assets/space_fabric/lang/es_es.json). Hay modelos
de bloque e inventario, loot por bloque y 20 texturas originales adicionales de
16×16. El catálogo total queda en **22 bloques**, **12 ítems de materiales/equipo**
y el cubo de lava viscosa que ya existía.

`tools/generate_geology_resources.py` genera estos datos de forma determinista.
Si también regeneras los materiales de fases anteriores, ejecútalo después de
`generate_material_resources.py`. No se necesita regenerar para compilar los
archivos ya incluidos. Los generadores reescriben sus archivos: conserva primero
las personalizaciones manuales.

La compilación y las 140 pruebas pasaron. Entre ellas: tags de netherita, recetas
que realmente aceptan ingredientes y producen hierro/pigmento, loot tables,
aislamiento de features por bioma y ejecución de la feature de hielo sobre un
volumen de bloques con altura/visibilidad de cielo. Cada bloque de hielo generado
en esa prueba tiene techo y sombra en ambas bandas polares.

No se ejecutó una partida gráfica ni un servidor de juego: quedan pendientes la
revisión visual, el minado real con encantamientos y la exploración de vetas en
chunks completos. Usa un mundo de pruebas nuevo; los chunks antiguos conservan su
terreno y bloques existentes.
