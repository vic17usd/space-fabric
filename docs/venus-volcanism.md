# Actividad volcánica de Venus — 0.47.0

Instala el JAR en cliente y servidor, sustituyendo el anterior. Usa los respiraderos
y las fuentes de lava existentes: **funciona en terreno ya generado**, sin rehacer
biomas, estructuras ni volcanes. Es una ampliación de los paisajes y peligros previos.

## Qué sucede

Los respiraderos cercanos recorren un ciclo de reposo, aviso, actividad y enfriamiento.
Durante el aviso aumenta el humo y se oyen silbidos o burbujeos. A menos de doce
bloques aparece un mensaje breve con el tiempo restante y la distancia al foco.
Al entrar en una zona ya activa puedes encontrar un chorro en curso: observa las
partículas y busca una ruta alternativa o cobertura.

| Fenómeno | Foco | Aviso | Actividad | Alcance del núcleo peligroso |
|---|---|---|---|---|
| Fumarola | Basalto de fumarola expuesto | 6 segundos | 4 segundos | Radio 2,5; hasta 5 bloques sobre el foco |
| Microerupción | Fuente de lava expuesta junto a roca venusiana | 8 segundos | 5 segundos | Radio 4; hasta 6 bloques sobre el foco |

Los tiempos suponen 20 TPS; el lag alarga el tiempo real disponible. La primera
activación espera entre cinco y quince segundos desde que el sistema descubre
el foco y después realiza el aviso completo. La fumarola descansa de 60 a 90
segundos tras su actividad; la microerupción, de 120 a 150 segundos.

Las fumarolas expulsan humo y una nube amarillenta. Las erupciones muestran lava,
llamas, humo y ceniza. Son **proyecciones visuales con un área de calor**, sin
entidades de proyectil, explosiones ni lava colocada. No generan bloques o botín,
ni cambian el relieve, las bases, los minerales o las estructuras.

## Cómo protegerse

La exposición se evalúa una vez por segundo, solo en el núcleo del chorro y con
línea directa hasta el jugador. Las partículas pueden verse fuera de ese núcleo.
Las paredes, cristales y puertas cerradas bloquean el alcance según sus formas
de colisión; una puerta abierta puede dejar pasar el chorro.

- Un **hábitat operativo** o una **cabina protegida de cohete/rover** evita los efectos
  nuevos sobre el ocupante. Conservan sus requisitos habituales de suministros,
  integridad y protección atmosférica.
- Un **traje completo de Venus o abisal** evita el daño y debilitamiento del gas,
  pero recibe un punto de desgaste por pieza y segundo de exposición.
- En una microerupción, ese traje reduce el daño nominal a **un punto de salud por
  segundo** —medio corazón— y recibe dos puntos de desgaste por pieza. Un chorro
  volcánico puntual supera la protección continua del traje: apártate del núcleo.
- Sin traje adecuado, el gas causa dos puntos nominales de daño por segundo,
  debilidad y lentitud breves. La erupción causa cuatro puntos y lentitud.
- La mejora de eficiencia del rover no cambia su blindaje ni la protección del traje.

Los efectos usan la resistencia e invulnerabilidad habituales de Minecraft;
los daños nominales pueden reducirse o quedar bloqueados por ellas. El daño de la
erupción es de fuego. La armadura de combate y el escudo no sustituyen un traje
planetario. Si coinciden varios focos, se aplica la mayor exposición, **sin sumar
el daño o desgaste de cada uno**.

Se desgasta el traje dedicado si hay un conjunto completo; en su defecto se usa
el equipo válido de los huecos de armadura. Los jugadores creativos y espectadores
no reciben los efectos nuevos. Este sistema afecta a jugadores; no daña a las
criaturas nativas ni introduce cambios en su inmunidad, apariciones o gravedad.

La presión, falta de oxígeno y temperatura ambiental siguen actuando como antes.
El aviso volcánico no concede tiempo adicional para sobrevivir sin traje. También
se conserva el peligro previo de pisar roca caliente o tocar lava directamente.

## Dónde aparecen y cómo cambian las rutas

Se descubren focos de superficie cerca de jugadores vivos por debajo de Y 260.
Los campos de fumarolas son la zona más habitual para los chorros de gas; las
fuentes de lava de grietas y calderas pueden producir microerupciones.

Un foco necesita dos bloques de aire encima. La lava debe ser una fuente y tener
al menos dos vecinos horizontales de las rocas de `space_fabric:venus_host_rocks`.
Un generador geotérmico colocado encima obstruye el foco. La lava o los
respiraderos colocados por jugadores también pueden activarse si cumplen las mismas
condiciones en Venus: no se guarda una etiqueta de origen natural por bloque.

Puedes rodear el núcleo, esperar su enfriamiento o construir cobertura. Tapar el
respiradero, quitarlo o cambiar la fuente invalida el evento. No se inventan focos
en aire ni se producen erupciones de este sistema en otros planetas.

## Rendimiento y carga de chunks

Los ciclos son compartidos por los jugadores de Venus: un foco no crea una copia
por cada observador. El servidor mantiene como máximo **24 focos**, separados al
menos diez bloques, y **cuatro en aviso o actividad** a la vez.

La búsqueda reparte **48 columnas cada cinco ticks** entre los jugadores cercanos,
en un área de 33×33 alrededor de cada uno. Lee alturas de chunks ya cargados.
La comprobación de focos y de cobertura consulta únicamente posiciones cargadas;
si encuentra una posición descargada no intenta obtener su estado.

No se fuerza carga ni generación de chunks, no se ejecuta `/locate` y no se realiza
trabajo de terreno. Al descargar un foco o alejarse más de 48 bloques todos los
jugadores observadores, se descarta su ciclo. Al volver empieza un reposo nuevo
antes del aviso. No hay avance offline ni una erupción instantánea por recuperar
un temporizador antiguo al entrar en la partida.

## Opciones del servidor

Al arrancar se crea `config/space-fabric-volcanism.json`:

```json
{
  "enabled": true,
  "damage": true
}
```

- `enabled: false` desactiva los nuevos ciclos, avisos y efectos del sistema.
- `damage: false` conserva el espectáculo y los avisos, pero elimina el daño,
  desgaste y efectos de estado de estos eventos.

Reinicia el servidor o la partida individual para aplicar cambios. No desactiva
la atmósfera, el contacto con lava, la roca caliente ni el humo decorativo anterior
del bloque de fumarola. La configuración del servidor es la que determina los efectos.

## Comprobaciones en partida pendientes

Las pruebas automáticas cubren tiempos, límites compartidos, daño calculado,
configuración, detección y colisiones nativas. Falta observar el sistema en juego:

1. Recorrer un campo de fumarolas y una grieta/caldera con equipo adecuado.
2. Revisar humo, sonidos, cuenta atrás, duración, núcleo y enfriamiento a distintas escalas gráficas.
3. Comparar exposición con traje, armadura normal, cabina operativa y hábitat sellado.
4. Probar paredes y las dos posiciones de una esclusa, sin perder de vista la presión exterior.
5. Tapar un foco durante el aviso, descargar su chunk y volver; confirmar la nueva espera.
6. Observar un mismo foco con dos jugadores y comprobar que no duplica daño ni efectos.
7. Probar `damage: false` y `enabled: false` después de reiniciar en una copia de prueba.
