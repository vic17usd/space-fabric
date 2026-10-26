# Parcelas planas para Marte — 0.11.0

Breathforge y Haven usan `space_fabric:martian_surface`. El generador elige una
altura común y prepara una parcela antes de colocar el NBT. Así los huecos que
el editor omitió quedan despejados, en lugar de conservar roca dentro del edificio.
El relieve de Marte fuera de las parcelas mantiene sus cañones y cráteres.
Desde 0.11.0 se recortan también los pedestales inferiores: el piso arquitectónico
queda a la misma altura que el suelo de la parcela, sin la losa de terreno importada.

## Configuración actual

Por ejemplo, `data/space_fabric/worldgen/structure/martian_haven_settlement.json`:

```json
{
  "type": "space_fabric:martian_surface",
  "biomes": "#space_fabric:has_structure/martian_haven_settlement",
  "step": "surface_structures",
  "spawn_overrides": {},
  "terrain_adaptation": "none",
  "template": "space_fabric:martian_haven_settlement_surface",
  "padding": 8
}
```

La plantilla actual está en `data/space_fabric/structure/martian_haven_settlement_surface.nbt`.
Breathforge usa el mismo tipo con su propia plantilla y etiqueta de bioma.
Los `structure_set`, sus sales de 0.10.0 y los nombres utilizados en `/locate` se conservan.

Este JSON requiere el registro Java de Space Fabric; no es un datapack vanilla
independiente. Los antiguos `template_pool` se mantienen para compatibilidad y
pruebas de la configuración anterior. La colocación actual utiliza el NBT directamente.

## Cómo se prepara el suelo

1. Se carga la plantilla, se elige una rotación y se centra su huella en el chunk
   de inicio. La parcela añade ocho bloques de margen a cada lado.
2. Se consulta el ruido de superficie en una cuadrícula de 16 bloques, incluidos
   los bordes. Para las dimensiones admitidas son como máximo 121 muestras.
   Las muestras devuelven el primer bloque libre; se resta uno para obtener la
   altura del suelo sólido. El piso local Y=0 se sitúa exactamente en ese plano.
3. Se comprueba que todo el edificio cabe en la altura del mundo. La altura,
   rotación y posición se guardan en la pieza, para que todos sus chunks usen el mismo plano.
4. Durante la decoración, cada chunk limpia las columnas de su parte de la parcela
   desde el plano hasta la superficie real de cada columna. Esto retira también
   picos que hayan quedado entre muestras y despeja los futuros interiores.
5. Debajo se rellena hasta encontrar terreno sólido, con un mínimo de cuatro
   capas de soporte. La capa superior es regolito de óxido de hierro; debajo hay
   piedra marciana. Los cañones no dejan el edificio sobre una plataforma suspendida.
6. Después se colocan los bloques del NBT recortado y sus habitantes. Las posiciones
   vacías de la plantilla permanecen como aire porque la preparación ya retiró el terreno.

Las plantillas de superficie se reproducen con:

```sh
python3 tools/prepare_martian_templates.py
```

| Plantilla actual | Capas inferiores retiradas | Bloques conservados | Dimensiones |
| --- | ---: | ---: | --- |
| `martian_breathforge_surface.nbt` | 1 | 28.147 | 89 × 72 × 89 |
| `martian_haven_settlement_surface.nbt` | 3 | 20.406 | 124 × 81 × 125 |

En el nuevo plano Y=0 se retiran granito, arenisca roja, arenisca lisa y terracota
del terreno importado. Sus posiciones las ocupa el regolito de la parcela. Se
conservan los pisos arquitectónicos de otros materiales y todos los bloques por
encima del recorte. Haven conserva sus seis habitantes con las posiciones rebajadas
tres bloques; la validación acepta el suelo marciano implícito bajo ellos.

La parcela es una meseta artificial con bordes de contención; no aplana todo el
planeta. Puede cortar laderas o elevar terreno bajo. Cada operación se recorta a
la máscara del chunk de decoración: no escribe anticipadamente en chunks vecinos.
El centro y el margen permanecen dentro del alcance de referencias de estructuras
de Minecraft, incluso al rotar Haven.

`terrain_adaptation: none` evita sumar un segundo ajuste de ruido a este trabajo.
Los cráteres marcianos se excavan antes, en `LOCAL_MODIFICATIONS`. Los minerales
mantienen sus reglas habituales; no se modifican las estructuras de otros planetas.

## Código y guardado

| Clase | Función |
| --- | --- |
| `MarsSurfaceStructures` | Registro del tipo de estructura y de su pieza persistente |
| `MarsSurfaceStructure` | Codec, rotación, muestras y creación de un inicio válido |
| `MarsSurfacePiece` | Guardado de origen/rotación/margen, preparación y colocación del NBT |
| `SurfacePlot` | Recorte, relleno y muestreo acotados |

Las plantillas de este tipo admiten entre 1 y 128 bloques por eje. Se falla con
un mensaje concreto si el recurso falta o excede ese tamaño. Las plantillas más
grandes necesitan dividirse en piezas para mantener los límites de generación.
`/locate` solo calcula el inicio y sus muestras; el movimiento de bloques se hace
al decorar los chunks y no durante la búsqueda.

## Partidas y comprobación

La corrección se aplica a **nuevas generaciones**. Los edificios ya colocados no
se reconstruyen ni se desplazan al actualizar; sus cambios del jugador permanecen.
Usa un mundo de pruebas nuevo o chunks nuevos para revisar la corrección.
Las plantillas originales sin sufijo se mantienen para piezas antiguas guardadas.
La marca persistente `FloorAligned` distingue el plano actual del de 0.10.0;
una pieza antigua conserva su NBT y su alineación al terminar chunks pendientes.

```mcfunction
/execute in space_fabric:mars run locate structure space_fabric:martian_breathforge
/execute in space_fabric:mars run locate structure space_fabric:martian_haven_settlement
```

Para una colocación manual, en un área de pruebas:

```mcfunction
/execute in space_fabric:mars run place structure space_fabric:martian_haven_settlement ~ ~ ~
```

Este comando prepara terreno y coloca bloques y habitantes. No se ejecuta
automáticamente sobre las construcciones antiguas.

Las pruebas ejecutan la preparación real sobre picos y cañones, rechazan escrituras
fuera del chunk y colocan las dos plantillas recortadas sobre un volumen de roca.
Comprueban pisos, regolito en los huecos del suelo y aire dentro de las habitaciones.
Ambas estructuras generan inicios válidos con tres semillas y sus piezas conservan
el plano al serializarse y moverse. La inspección gráfica de una partida real sigue pendiente.
