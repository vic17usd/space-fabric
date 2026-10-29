# Consola, recuperación de la fundición y rover — 0.36.0

Los dos modelos Blockbench aportados están integrados en el JAR principal.
Requiere Minecraft 26.1, Java 25, Fabric y Fabric API; sustituye el JAR anterior
en cliente y servidor por `space-fabric-0.36.0.jar`.

## Recuperar una fundición

Busca **Fundición abandonada de Venus** en `/estructuras`. Las fundiciones
generadas desde 0.36.0 tienen una consola y una fila de tres equipos averiados
en una zona despejada de su patio. Se conservan los edificios y plantillas de
fundiciones ya guardadas; la ampliación aparece al generar instalaciones nuevas.

1. Lleva el traje venusiano completo, al menos nueve kits de reparación y tres
   filtros contra gases corrosivos. Cada equipo comienza con integridad cero.
2. Consulta la consola. Indica si falta un equipo, su chunk está descargado,
   necesita reparación, filtro o completar su primera operación.
3. Repara cada equipo hasta **al menos 70 % de integridad**. Tres kits recuperan
   el 75 % desde una avería total. La corrosión sigue activa: mantener margen,
   revestimiento y filtros ayuda durante la puesta en marcha.
4. Instala un filtro activo en cada equipo y completa un lote en cada uno:

| Equipo | Entradas | Condición | Productos |
|---|---|---|---|
| Generador geotérmico | Batería descargada en primera entrada; agua en tercera | Lava debajo, ya preparada en la instalación | Batería cargada y agua devuelta desde 0.41 |
| Unidad de refrigeración | Agua y batería cargada | Equipo reparado | Refrigerante enfriado y batería descargada |
| Procesador de azufre | Dos cristales de azufre, batería cargada y nitrógeno | Equipo reparado | Dos azufres refinados, batería y bombona vacías |

También sirve la fórmula existente de recuperación de ácido en el procesador.
Las recetas aparecen con **K**. Los suministros se consumen al terminar el lote;
las salidas llenas pausan el procesamiento. Las baterías producidas por el
generador pueden alimentar los otros dos equipos.

5. Cuando los tres equipos estén preparados, pulsa **Reactivar instalación y
   recoger recompensa**. Recibes **un rover sin suministros y cuatro placas de
   compuesto venusiano**. Las máquinas reparadas quedan disponibles para
   producción normal, que continúa necesitando materiales y mantenimiento.

La recompensa es **una por instalación**, compartida entre jugadores. Se registra
en datos del servidor antes de la entrega. Repetir el clic, volver a entrar o
sustituir la consola no reinicia ese registro. Si falta espacio en el inventario,
la entrega utiliza el comportamiento nativo de devolución de objetos.

Una consola fabricada puede volver a vincularse a una instalación registrada a
menos de 16 bloques de su consola original. No crea una instalación premiable
por sí sola. Conserva las tres máquinas en sus posiciones; si falta alguna,
puedes sustituirla por otra del mismo tipo y completar su operación.

## Preparar el rover

Puedes conseguirlo recuperando una fundición o fabricarlo con cuatro placas de
compuesto, una unidad de refrigeración, dos bombonas vacías, un informe venusiano
y una batería descargada. La receta fabrica el vehículo sin recursos cargados.

Colócalo con clic derecho sobre el suelo, con espacio libre para su volumen de
colisión de aproximadamente **4 × 4 × 3 bloques**. El modelo conserva seis
ruedas, radiadores y compartimento de carga; se adapta la carrocería maciza a
una cabina hueca con visor, asiento y mandos. Las baterías superiores acompañan
la elevación del techo. Ambos atlas PNG originales se conservan sin cambios.

Usa **Shift + clic derecho** para abrir su inventario antes de subir. Tiene tres
entradas de suministros, dos salidas de envases y nueve espacios de carga.

| Suministro | Reserva añadida | Capacidad |
|---|---:|---:|
| Batería cargada | 2.400 de energía | 24.000 |
| Bombona de oxígeno | 4.800 | 9.600 |
| Bombona de agua | 2.400 de refrigeración | 9.600 |
| Bombona de refrigerante enfriado | 4.800 de refrigeración | 9.600 |

La unidad de refrigeración fabrica una bombona enfriada con agua y una batería
cargada en diez segundos de procesamiento. Reutiliza el recipiente del agua.
El rover devuelve baterías descargadas y bombonas vacías. Solo admite una carga
si cabe completa y hay espacio para el recipiente devuelto.

## Conducir y mantener

- Clic derecho o **Subir** en el panel: entra el único conductor.
- **W/S:** avanzar y retroceder. **A/D:** girar. **Espacio:** frenar.
- **V:** abrir el inventario mientras estás montado. Es configurable en controles.
- **F5:** alternar las perspectivas habituales. La primera persona mira desde
  la cabina real por el visor; la mirada puede moverse y acompaña los giros.
- **Shift:** salir. En Venus debes llevar el traje adecuado al abandonar la cabina.
- Clic derecho con una **placa de compuesto**: recuperar hasta 25 puntos de casco.
- Sin ocupantes y detenido, **Shift + golpe**: recoger el rover. Conserva las
  reservas y el daño en el objeto; su carga se suelta aparte una sola vez.

El movimiento lo calcula el servidor a partir de las teclas normales del jugador.
Tiene aceleración y frenado, marcha atrás, colisión con el terreno y un escalón
máximo de un bloque. Su velocidad de avance está limitada a 4,8 bloques/s.
Las seis ruedas giran con el desplazamiento, también al retroceder.

Mientras está ocupado, consume energía para avanzar y mantener los sistemas.
En planetas sin aire respirable consume oxígeno; en Venus también refrigerante.
Las reservas máximas de oxígeno y refrigeración duran aproximadamente ocho
minutos a 20 TPS. Sin ocupantes, los consumibles se conservan. Los chunks
descargados o el tiempo con la partida cerrada no generan consumo retroactivo.

La cabina protege de la atmósfera cuando tiene **casco, energía, oxígeno y
refrigeración suficientes**, dentro de sus límites de 100 bar y −150 a 550 °C.
Es apta para Venus y Marte; los entornos que exceden su certificación dañan el
casco. No es un vehículo de vuelo ni anfibio: utiliza terreno firme y evita
inmersión y lava. El agotamiento de reservas o casco retira la protección; el
traje del conductor sigue siendo una protección independiente.

## Guardado e integración

- Instalaciones y recompensas: `FoundryLedger`, datos guardados del servidor.
- Consola: BlockEntity y menú nativos, con modelo orientable de 33 piezas.
- Equipos: mantenimiento existente y contador persistente de lotes completados.
- Rover: entidad, datos sincronizados, inventario nativo y estado NBT del objeto.
- Modelos originales: `tools/import_sources/`, con hashes y adaptación en
  `tools/venus_expedition_models.json`. No requieren bibliotecas de animación.
- Recursos nuevos: generadores `import_venus_expedition_models.py` y
  `generate_venus_recovery_resources.py`; las fundiciones nuevas usan plantillas
  `ruins_v2`, manteniendo las antiguas `ruins_v1` para partidas anteriores.

Las pruebas comprueban guardado, límites, recetas, envases, menús, recuperación y
recompensa única. Una comprobación gráfica por CPU interpreta los modelos
nativos y emite sus vértices reales: seis ruedas en tres ángulos, nueve rayos de
visión libres y paredes interiores opacas. Quedan pendientes de observar en una
partida real la conducción en pendientes, el confort de cámara, la recuperación
completa con jugadores y la sincronización multijugador.

Desde **0.39.0** existe una estación para recargar, reparar y descargar vehículos
aparcados, además de un aerostato independiente. [Ampliación de la colonia](venus-colony-expansion.md).
