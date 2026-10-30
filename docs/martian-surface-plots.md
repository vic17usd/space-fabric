# Estructuras integradas en el terreno de Marte — 0.43.0

Breathforge y Haven usan `space_fabric:martian_surface`. El generador elige una
altura común y prepara una parcela antes de colocar el NBT. Así los huecos que
el editor omitió quedan despejados, en lugar de conservar roca dentro del edificio.
El relieve de Marte fuera de las parcelas mantiene sus cañones y cráteres.
Desde 0.11.0 se recortan también los pedestales inferiores: el piso arquitectónico
queda a la misma altura que el suelo de la parcela, sin la losa de terreno importada.

Desde **0.43.0**, la altura deja de ser el punto más alto de la parcela. Se aplica
el mismo criterio de integración de Venus: rechazar emplazamientos con demasiado
desnivel, elegir una cota baja y enlazar los bordes con el terreno natural. Corrige
las grandes mesetas que podían dejar el asentamiento decenas de bloques por
encima del paisaje. Se conserva el santuario subterráneo, sus jefes y su profundidad.

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
  "padding": 8,
  "max_terrain_relief": 16
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
2. Se consultan esquinas, centro y una cuadrícula cada 16 bloques, incluidos los
   bordes: como máximo 126 consultas con los tamaños admitidos. Se resta uno al
   primer bloque libre para obtener suelo sólido. Se busca el grupo de alturas
   más compacto que contiene al menos **el 75 % de las muestras**, y se rechaza
   el candidato si ese grupo abarca más de 16 bloques de desnivel. Esto descarta
   laderas y cañones extensos sin confundir grietas estrechas con el suelo general.
   El piso usa la mediana de ese grupo, limitada a cuatro bloques sobre su cota
   inferior. Ni un pico aislado lo levanta ni una grieta aislada lo hunde. Y=0 de
   la plantilla es el piso. Marte necesita este ajuste frente a Venus por sus
   grandes plantillas y sus fisuras profundas y estrechas.
3. Se comprueba que todo el edificio cabe en la altura del mundo. La altura,
   rotación, posición y transición de bordes se guardan en la pieza, para que todos
   sus chunks usen el mismo plano.
4. Durante la decoración, cada chunk limpia las columnas de su parte de la parcela
   desde el plano hasta la superficie real de cada columna. Esto retira también
   picos que hayan quedado entre muestras y despeja los futuros interiores.
5. Debajo se rellena hasta encontrar terreno sólido, con un mínimo de cuatro
   capas de soporte. La capa superior es regolito de óxido de hierro; debajo hay
   piedra marciana. El margen de ocho bloques pasa gradualmente a la altura
   natural; su borde exterior se conserva intacto. El centro queda plano y apoyado.
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

La parcela es una zona plana local con un margen de transición; no aplana todo el
planeta. Puede recortar pequeñas laderas o rellenar huecos. El límite de cuatro
bloques se refiere al grupo predominante del ruido: una grieta minoritaria o un
cráter entre muestras puede necesitar más relleno localizado. Cada operación se recorta a
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
| `MarsTerrain` | Cota del terreno predominante; rechazo de pendientes extensas y de picos aislados |

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
`TerrainBlend` guarda el margen de transición de 0.43. Las piezas que no tienen
esa marca mantienen su colocación anterior, sin mover edificios ni inventarios.
La tolerancia `max_terrain_relief` admite 0–32; el valor distribuido es 16.

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
