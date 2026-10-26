# Martian Haven y comerciantes de cobre — Fabric 26.1

La versión 0.9.0 añade el asentamiento del archivo proporcionado a la superficie
de Marte y lo puebla con seis comerciantes marcianos. Conserva los 45.380 bloques,
los 36 materiales vanilla y las dimensiones 124 × 84 × 125 del original.
Los habitantes forman parte de la plantilla NBT; no se crean desde un bucle que
pueda duplicarlos cada vez que se carga un chunk.

## Los archivos originales

`martian_haven_settlement.json` usa el formato del editor `creativemode.structure`.
Se guarda sin cambios en `tools/structure_sources/` y se convierte a NBT nativo.

El segundo JSON es solamente un modelo de ítem: referencia la textura externa
`martian-copper-trader-envsqju5:item/martian_copper_trader_envsqju5_spawn_egg`.
No contiene definición de entidad, IA, intercambios ni textura binaria. Se
conserva en `tools/import_sources/` como fuente y se reemplaza en el mod por un
huevo funcional de nuestro propio namespace. No hace falta instalar el mod de origen.

La apariencia del comerciante usa un atlas adaptado a partir de la imagen de
referencia y un modelo humano con brazos clásicos. El PNG integrado está en
`assets/space_fabric/textures/entity/martian_copper_trader.png`; no es una copia
binaria del PNG original del otro mod, que no forma parte de los dos JSON.

## Aparición del asentamiento y sus habitantes

Los recursos se encuentran bajo `src/main/resources/data/space_fabric/`:

```text
structure/martian_haven_settlement.nbt
worldgen/structure/martian_haven_settlement.json
worldgen/template_pool/martian_haven_settlement/start.json
worldgen/structure_set/martian_haven_settlement.json
tags/worldgen/biome/has_structure/martian_haven_settlement.json
loot_table/entities/martian_copper_trader.json
```

La estructura usa `minecraft:jigsaw`, **`size: 1`**, `projection: rigid` y
`WORLD_SURFACE_WG`. Así mantiene su forma y produce un inicio válido en 26.1.
Se restringe al bioma `space_fabric:mars`, que en este proyecto es exclusivo de
Marte. El `structure_set` utiliza regiones de 48 × 48 chunks, separación 20 y
una zona de exclusión de 16 chunks respecto a los candidatos de Breathforge.
Estos valores describen intentos de generación, no edificios garantizados a
intervalos fijos. Las dos estructuras pueden coexistir en el planeta.

Cada Haven genera seis entidades `space_fabric:martian_copper_trader`. Sus puntos
locales son `(30,5,30)`, `(90,4,30)`, `(30,4,90)`, `(90,5,90)`, `(30,4,62)` y
`(90,4,62)`, centrados en el bloque. El conversor comprueba suelo y tres bloques
libres en el original. Minecraft transforma las posiciones al rotar la plantilla
y asigna UUID distintos; no se incluyen UUID fijos en el archivo.

Los residentes son persistentes, adultos y no desaparecen por alejarse el jugador.
Al aparecer recuerdan una zona de 24 bloques alrededor de su posición. Su hogar,
ofertas, existencias y fecha de reposición se guardan con el sistema NBT de la entidad.
No se añade una población salvaje al bioma ni se reponen automáticamente los
residentes que mueran por combate u otros peligros.

## Oxígeno y gravedad

`MartianCopperTraderEntity` extiende `AbstractVillager`, con IA de paseo, huida,
mirada y comercio. Tiene 24 puntos de salud y respira bajo el agua gracias a su
soporte vital. Rechaza explícitamente los daños `space_fabric:hypoxia` y
`space_fabric:decompression`. No tiene invulnerabilidad general: sigue expuesto
a combate, fuego y presión profunda de los gigantes.

El sistema atmosférico existente aplica su tick a jugadores. La protección de
la entidad también funciona si otro sistema le aplica directamente esos tipos
de daño. Los jugadores siguen necesitando su propio equipo al salir del cohete.

El mismo evento global de gravedad que afecta a otras entidades vivas aplica
el factor **0,38 de Marte**, dando `0.08 × 0.38 = 0.0304` al atributo de gravedad
del comerciante. El modificador es transitorio y no se acumula al recargar. Al
cambiar de dimensión se actualiza conservando modificadores ajenos. Tiene seis
bloques de caída segura para desplazarse por las pequeñas plataformas del poblado.

## Comercio

Clic derecho abre la interfaz nativa de comercio. No necesita un puesto de trabajo.

| Entrega del jugador | Recibe | Existencias por reposición |
| --- | --- | ---: |
| 12 lingotes de cobre | 1 esmeralda | 16 |
| 2 esmeraldas | 8 lingotes de cobre | 16 |
| 1 esmeralda | 2 polvos de óxido de hierro | 16 |
| 4 esmeraldas | 1 polvo de sulfato | 12 |
| 1 olivino marciano | 3 esmeraldas | 12 |
| 8 esmeraldas | 1 celda de combustible | 4 |

Repone existencias cada 24.000 ticks de tiempo del mundo, unos 20 minutos a
20 TPS, y espera a que termine una transacción antes de reponer. El ciclo solar
particular del planeta no cambia ese intervalo. No usa niveles de profesión.

## Uso y reproducción de recursos

Instala el JAR 0.9.0 en cliente y servidor junto con Fabric API para Minecraft
26.1. Sustituye la versión anterior: no mantengas dos JAR del mismo mod.

Con comandos habilitados:

```mcfunction
/execute in space_fabric:mars run locate structure space_fabric:martian_haven_settlement
/give @s space_fabric:martian_copper_trader_spawn_egg
```

Para una comprobación manual en un área de pruebas:

```mcfunction
/execute in space_fabric:mars run place structure space_fabric:martian_haven_settlement ~ ~ ~
```

También se puede invocar la entidad desde Marte con
`/summon space_fabric:martian_copper_trader ~ ~ ~`.
El comando de colocar escribe bloques y entidades: repetirlo genera otro grupo.
La carga normal de un chunk ya generado no vuelve a ejecutar la plantilla.

El asentamiento aparece de forma natural en **chunks nuevos**, con generación
de estructuras activada. Añadir el JAR no modifica chunks ya explorados. La
plantilla conserva vacíos implícitos del editor; el ajuste con laderas grandes
y la interacción visual de los habitantes requieren revisión dentro del juego.

Para volver a generar el NBT y el huevo desde la raíz:

```sh
python3 tools/convert_creativemode_structure.py tools/structure_sources/martian_haven_settlement.json src/main/resources/data/space_fabric/structure/martian_haven_settlement.nbt --entities tools/structure_sources/martian_haven_residents.json
python3 tools/generate_haven_resources.py
python3 tools/validate_resources.py
./gradlew build --console=plain
```

Las pruebas cargan todos los registros, comparan el NBT con los bloques originales,
comprueban los seis residentes y ejecutan `Structure.generate` con tres semillas
para Haven y Breathforge. Verifican también el huevo, atributos nativos,
protección de daños y consumo/serialización de intercambios. No se afirma una
prueba gráfica ni una sesión de juego con los habitantes ya realizada.
