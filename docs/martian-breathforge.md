# Martian Breathforge — estructura de superficie en Fabric 26.1

El adjunto original usa `creativemode.structure`, versión 1. Es una lista de
30.608 entradas `[x, y, z, índiceDePaleta]`, con 22 materiales vanilla y tamaño
89 × 73 × 89. `grid_size: 256` describe la cuadrícula del editor, no el tamaño
de la construcción. El anclaje del editor es `[44, 0, 44]`.

Minecraft no puede usar ese JSON como una plantilla de estructura. Se conserva
el original en `tools/structure_sources/martian_breathforge.json` y se convierte
al NBT binario comprimido que Minecraft carga. La conversión mantiene exactamente
las posiciones, la paleta y los bloques; no sustituye materiales por otros del mod.
No hay entidades, block entities, loot ni conectores jigsaw en el archivo original.

## Archivos integrados

Todas estas rutas son relativas a `src/main/resources/data/space_fabric/`:

```text
structure/martian_breathforge.nbt
worldgen/structure/martian_breathforge.json
worldgen/template_pool/martian_breathforge/start.json
worldgen/structure_set/martian_breathforge.json
tags/worldgen/biome/has_structure/martian_breathforge.json
```

En 26.1 se usa **`structure/` en singular** para la plantilla NBT.
`worldgen/structure/` contiene su definición de generación y tiene otra función.
El `location` del pool, `space_fabric:martian_breathforge`, referencia el NBT;
`start_pool` referencia el pool `space_fabric:martian_breathforge/start`.
El tag de bioma limita la selección a `space_fabric:mars`, que el proyecto
utiliza en la dimensión `space_fabric:mars`.

Se usan los registros dinámicos nativos: Fabric incluye estos recursos en el
datapack del mod y Minecraft los carga al abrir el mundo. No se necesita añadir
`Registry.register`, eventos de biomas ni una clase de estructura Java para
este edificio. La dimensión ya usa un generador de ruido con estructuras.

## Decisiones de generación

- `type: minecraft:jigsaw` permite usar el motor de estructuras de vanilla.
- `size: 0` coloca solo la pieza inicial. Es la profundidad de expansión, no
  las dimensiones del edificio. El adjunto no tiene conectores para piezas hijas.
- No se establece `start_jigsaw_name`: exigiría un conector inexistente.
- `start_height: {absolute: 0}` se combina con `WORLD_SURFACE_WG`: el cero es
  un desplazamiento sobre la altura calculada del terreno, no una colocación fija en Y=0.
- `projection: rigid` mantiene alineados pisos, paredes y máquinas decorativas.
  `terrain_matching` deformaría la construcción al seguir las columnas del suelo.
- `terrain_adaptation: beard_thin` integra la base con el ruido del terreno.
  No garantiza una parcela plana de 89 × 89 ni que todos los interiores queden despejados.
- `max_distance_from_center: 116` deja los 12 bloques de adaptación dentro
  del límite de 128 que valida el codec. La pieza inicial es de 89 × 73 × 89;
  no se limita aquí a los 48 bloques de la interfaz de bloques de estructura.
- La lista de procesadores está vacía para conservar los materiales originales.
- `spacing: 40` y `separation: 16` se miden en **chunks**. Se intenta un
  candidato por región de 40 × 40 chunks, aproximadamente 640 × 640 bloques.
  No significa exactamente un edificio cada 640 bloques ni garantiza cada intento.
- `salt: 836172105` proporciona una distribución propia a partir de la semilla.

Las estructuras se restringen por **bioma**, no por una clave de dimensión en
el JSON. En este mod el bioma marciano es exclusivo de Marte. Si otro datapack
lo usa en otra dimensión, también podrá generar allí esta estructura.

Los cráteres marcianos se ejecutan ahora en `LOCAL_MODIFICATIONS`, antes de
`SURFACE_STRUCTURES`, para que su excavación no elimine partes bajas del edificio.
Las cuevas exteriores mantienen su fase anterior.

## Reproducir y comprobar

Desde la raíz:

```sh
python3 tools/convert_creativemode_structure.py
python3 tools/validate_resources.py
./gradlew build --console=plain
```

El conversor usa solamente la biblioteca estándar de Python. Comprueba formato,
límites, coordenadas duplicadas e índices de paleta. Emite `DataVersion: 4786`,
la versión de datos oficial de Minecraft Java 26.1. Los tests leen el archivo
con `NbtIo` y `StructureTemplate` reales y comparan cada bloque con el adjunto.

El original omite todas las posiciones vacías. El NBT respeta esa omisión:
esas posiciones no borran bloques existentes. Esto evita vaciar todo el prisma
de 89 × 73 × 89, pero requiere inspeccionar la adaptación en pendientes y cañones.
Si una ladera invade habitaciones, edita la plantilla añadiendo aire explícito
solo en esos interiores o prepara una base modular; `beard_thin` no sustituye
ese trabajo de diseño. No se inventa un volumen de aire que no aparece en el adjunto.

Para localizarla en una partida de pruebas con comandos habilitados:

```mcfunction
/execute in space_fabric:mars run locate structure space_fabric:martian_breathforge
```

Para probar la definición sin esperar su distribución natural:

```mcfunction
/execute in space_fabric:mars run place structure space_fabric:martian_breathforge ~ ~ ~
```

Este último comando coloca bloques: úsalo en un área de pruebas. Para comprobar
solo la conversión se puede usar `/place template space_fabric:martian_breathforge`
en una ubicación despejada; la posición es la esquina de la plantilla, no el
anclaje del editor original.

Cierra y vuelve a abrir el mundo tras añadir registros de worldgen. Prueba un
mundo nuevo o chunks nuevos; estos archivos no añaden edificios retroactivamente
a chunks ya generados. Activa la generación de estructuras al crear el mundo.
El verificado de codecs/NBT no equivale a una inspección visual de terreno real;
esa comprobación queda pendiente.
