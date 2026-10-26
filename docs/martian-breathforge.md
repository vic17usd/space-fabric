# Martian Breathforge — Fabric 26.1, versión 0.11.0

La plantilla de superficie contiene 28.147 bloques y mide 89 × 72 × 89: retira
la capa de pedestal inferior y el terreno importado al nivel del piso. Conserva
la construcción sobre el suelo marciano. El JSON fuente y la plantilla original
de 30.608 bloques, 89 × 73 × 89, siguen disponibles para compatibilidad.
La plantilla actual es `data/space_fabric/structure/martian_breathforge_surface.nbt`;
se reproduce con `python3 tools/prepare_martian_templates.py`.

## Colocación actual

Desde 0.10.0 utiliza `space_fabric:martian_surface`, que prepara una parcela plana
y despeja el terreno antes de colocar el NBT. Consulta la [guía de parcelas](martian-surface-plots.md)
para el algoritmo, límites y pruebas de interiores despejados.

`data/space_fabric/worldgen/structure/martian_breathforge.json`:

```json
{
  "type": "space_fabric:martian_surface",
  "biomes": "#space_fabric:has_structure/martian_breathforge",
  "step": "surface_structures",
  "spawn_overrides": {},
  "terrain_adaptation": "none",
  "template": "space_fabric:martian_breathforge_surface",
  "padding": 8
}
```

El tag conserva `space_fabric:mars` como único bioma. El `structure_set` sigue
usando regiones de 40 × 40 chunks y separación 16; la sal actual es 836172107.
Los recursos son registros dinámicos de Minecraft. El tipo personalizado requiere
la clase y el registro Java incluidos en Space Fabric.

Los `template_pool` antiguos se conservan para compatibilidad, pero la definición
actual coloca el NBT directamente. El ZIP de integración 0.8.1 de `dist/` contiene
la antigua configuración jigsaw y no incluye esta corrección de terreno: usa el
JAR actual o los recursos y clases actuales del repositorio.

## Comandos

```mcfunction
/execute in space_fabric:mars run locate structure space_fabric:martian_breathforge
```

Prueba en chunks nuevos, con generación de estructuras activada. La actualización
no desplaza ni reconstruye edificios ya colocados. Para generar una copia en una
zona de pruebas puedes usar:

```mcfunction
/execute in space_fabric:mars run place structure space_fabric:martian_breathforge ~ ~ ~
```

Ese comando prepara terreno y coloca bloques. `/place template` solo coloca el
NBT y, por tanto, no prepara una parcela ni corrige el relieve.

## Validación e historial del fallo de búsqueda

La configuración 0.8.0 usaba `minecraft:jigsaw` con `size: 0`, que produce un inicio
vacío en 26.1 y puede hacer que `/locate` recorra una gran cantidad de candidatos.
0.8.1 lo corrigió a uno. La implementación actual crea explícitamente una pieza
de superficie; las pruebas conservan la reproducción del fallo jigsaw anterior.

También ejecutan la colocación nativa de esta plantilla sobre terreno elevado y
comprueban sus bloques, pisos a ras del regolito y huecos despejados. La inspección
gráfica del resultado queda pendiente.
