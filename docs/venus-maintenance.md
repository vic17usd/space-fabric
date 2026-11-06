# Corrosión y mantenimiento — 0.33.0

Instala `space-fabric-0.33.0.jar` en cliente y servidor con Fabric API para 26.1.
Funciona en partidas existentes; no requiere generar terreno nuevo.

En **0.34.0**, el mismo sistema cubre también las cuatro máquinas de
[industria venusiana](venus-industry.md). El refuerzo cerámico permanente reduce
el ataque químico antes del filtro y se conserva al recoger el bloque. El polvo
de carbono industrial ofrece otra receta para reciclar filtros saturados.

Afecta al controlador de hábitat, electrolizador, cargador solar, generador
geotérmico, analizador de muestras y refinería de combustible de Space Fabric.
La corrosión ambiental nueva se produce en Venus, incluso si la máquina está
parada o no tiene ingredientes. El desgaste ya sufrido permanece al trasladarla
a otro planeta y se puede reparar allí.

## Protección y exposición

Un techo abierto por los lados no aísla el equipo de la atmósfera. Una máquina
queda protegida si pertenece a una habitación certificada y sellada de un
controlador y ninguna de sus caras queda abierta al exterior. Los muebles o
bloques completos adyacentes también pueden cubrir caras. Una máquina integrada
en una pared con una cara exterior libre necesita protección química.

El cerramiento sigue aislando las máquinas aunque falte oxígeno o energía para
el soporte vital. Esto **no concede protección al jugador**: para respirar y
regular presión/temperatura el controlador debe funcionar y tener suministros.
Un controlador averiado sigue comprobando el cierre para aislar químicamente
el resto de equipos, pero no mantiene el soporte vital.

El contacto adyacente con una fumarola o depósito corrosivo supera la protección
ambiental del recinto. Aparta esas fuentes del equipo o usa protección química.
Se detectan las seis caras vecinas mediante `space_fabric:corrosive_sources`.
La bruma intensa de Venus acelera el desgaste sin requerir lluvia en superficie.

La exposición se evalúa una vez por segundo de juego, sin cargar chunks para
comprobar habitaciones. Solo avanzan los ticks cargados; no se acumula daño por
el tiempo que la partida está cerrada o la máquina descargada.

## Etapas y avisos

| Integridad | Comportamiento |
|---|---|
| Más del 75 % | Funcionamiento normal |
| 75 % o menos | Aviso de mantenimiento |
| 50 % o menos | Procesamiento a media velocidad |
| 20 % o menos | Aviso crítico |
| 0 % | Avería: se pausa el trabajo hasta reparar |

El **controlador de hábitat mantiene la frecuencia normal de soporte vital hasta
la avería**: no reduce a la mitad el consumo de oxígeno o la refrigeración. Su
integridad aparece en el HUD y en la esquina superior derecha del panel. Al
averiarse se retiran la regulación, la protección atmosférica y la vista despejada.
Las reservas internas se conservan si el cerramiento no tiene una fuga.

Las máquinas de procesamiento tienen un panel con progreso, integridad,
revestimiento y filtro. En la refinería y el controlador, pasa el cursor por la
integridad para ver las protecciones. **Shift + clic derecho con la mano vacía**
muestra el diagnóstico completo en el chat, incluida la exposición.

Al cruzar una etapa o agotar una protección se avisa en el chat a jugadores
situados a 16 bloques o menos, indicando bloque y coordenadas. No se repite el
mismo aviso cada tick. También hay avisos antes de que se saturen filtros o se
agoten revestimientos.

Una avería por corrosión conserva inventario y progreso; no destruye la máquina.
La refinería y los procesadores continúan el lote pendiente al repararse. El
controlador vuelve a intentar el soporte vital en el siguiente tick, sujeto a
cierre válido y suministros. Lleva puesto el traje para intervenir en una base
que haya perdido su protección.

## Materiales y uso

Cierra el menú, sostén el material y haz **clic derecho sobre la máquina**:

| Material | Fabricación sin orden | Efecto |
|---|---|---|
| Filtro contra gases corrosivos | Carbón vegetal + papel + pepita de hierro + lingote de cobre | Reduce un 75 % el ataque químico mientras está activo |
| Revestimiento anticorrosivo | Cuarzo + bola de arcilla + bola de slime + lingote de cobre | Añade 2.400 unidades que absorben desgaste antes que la carcasa |
| Kit de reparación de máquinas | 2 lingotes de hierro + 2 de cobre + redstone | Recupera hasta 25 puntos porcentuales de integridad |
| Recarga de filtro | Filtro saturado + carbón vegetal + papel | Fabrica un filtro nuevo |

Las cuatro recetas aparecen en el visor **K**. Fabricar un kit añade el avance
**Mantenimiento preventivo** y desbloquea las recetas en el libro vanilla.
Los materiales tienen iconos originales y descripciones de uso.

Un filtro activo no se puede reemplazar antes de saturarse. Al instalar un
repuesto se devuelve **un filtro saturado**, reutilizable en la receta de recarga;
si no cabe, la devolución usa el comportamiento normal del inventario del jugador.
El filtro agotado se conserva dentro de la máquina hasta reemplazarlo.

El revestimiento admite hasta 4.800 unidades, equivalentes a dos cargas. Solo
consume una aplicación si cabe la carga completa. Los kits no se consumen cuando
la integridad está completa; reparan hasta el máximo sin modificar filtro o capa.
La protección química se consume únicamente mientras existe exposición.

## Balance de juego

| Entorno sin protección | Desgaste por segundo | Tiempo hasta avería desde 100 % |
|---|---:|---:|
| Atmósfera venusiana | 4 de 10.000 | 41 min 40 s |
| Bruma intensa | 8 de 10.000 | 20 min 50 s |
| Fuente corrosiva adyacente | 16 de 10.000 | 10 min 25 s |

El filtro contiene 1.200 unidades: dura aproximadamente 20, 10 o 5 minutos según
los tres entornos de la tabla. Primero reduce el ataque; después el revestimiento
absorbe el resto. Una carga de revestimiento, sin filtro y con exposición normal,
dura 10 minutos. Las cantidades son calibración jugable, no tasas reales de
reacción química ni certificaciones de materiales para Venus.

## Guardado, recogida y automatización

NBT conserva daño, capa, filtro instalado y fracción del segundo de exposición.
Recoger y colocar una máquina guarda **solo su estado de mantenimiento** en el
objeto, cuyo tooltip muestra la integridad y protecciones. No reinicia el desgaste
ni permite recuperar repetidamente filtros nuevos. Un equipo nuevo fabricado o
procedente de una partida anterior empieza con toda su integridad.

Al romper un contenedor, sus materiales se sueltan mediante la lógica nativa.
El inventario no se copia dentro del objeto de la máquina, evitando duplicación.
El progreso del lote no se transporta al recoger el bloque; sí se conserva durante
una avería y al guardar/cargar la máquina colocada. Los suministros de mantenimiento
se aplican manualmente al bloque; las tolvas mantienen sus entradas y salidas de
producción anteriores, sin casillas nuevas.

Regeneración de recetas, iconos, traducciones y funciones de loot:

```sh
python3 tools/generate_maintenance_resources.py
```

Ejecuta este generador después de generadores generales que reescriban el loot de
las máquinas. La interacción y el desgaste los decide el servidor. Las pruebas
cubren etapas, protección y saturación, aislamiento, guardado de las seis máquinas,
la función nativa de copia de NBT sin inventario, menús, recetas y reparación de
una refinería con un lote pendiente. La interacción visual y el multijugador real
siguen pendientes de comprobar dentro de una partida.
