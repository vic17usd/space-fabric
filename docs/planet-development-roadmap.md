# Diseño de la expansión de Marte y Venus

## Entrega 1: soporte vital e industria inicial — implementada en 0.29.0

[Guía de construcción y uso](colonies.md). Cinco máquinas funcionales, nueve
bloques, nueve materiales, hielo marciano, gas/agua/baterías, primeras muestras
analizables e informes usados en los generadores. Avances y visor de recetas.
Marte comienza una colonia basada en agua y energía solar; Venus necesita un
cerramiento certificado, refrigeración y energía geotérmica.

El diagnóstico y el interior visual de los hábitats se amplían en **0.31.0**:
[panel, indicadores y niebla de interior](habitat-interiors.md).

La industria de Venus se amplía en **0.34.0** con captura y separación de gases,
refinado de azufre, cerámica y compuestos para bases, mantenimiento, trajes y
la mejora de cohete existente. [Cadena implementada](venus-industry.md).

## Entrega 2: invernaderos, mantenimiento y almacenamiento — Venus implementado en 0.39.0

- Depósitos conectados y pasamuros certificados **implementados en 0.37.0**:
  transporte de agua, gases y energía sin perforar el cierre de la habitación,
  con almacenamiento persistente, envases reciclados, redes acotadas y prioridad
  para el soporte vital. [Guía de automatización](industrial-automation.md).
- Invernadero hidropónico y cultivo de algas **implementados en 0.39.0**: luz,
  agua, energía, habitación regulada y entradas reales de CO₂. Recuperación parcial
  de respiración con filtros y depósito de residuos por controlador.
  [Colonia y circuitos](venus-colony-expansion.md).
- Venus: filtros reemplazables, revestimientos y reparación de máquinas expuestas
  implementados en **0.33.0**, con avisos y protección del recinto certificado.
  [Guía y límites](venus-maintenance.md). Las redes industriales se incorporan en
  0.37.0; los intercambiadores y el balance térmico industrial quedan implementados
  en **0.38.0**. [Refrigeración industrial](industrial-cooling.md).
- Marte: acumuladores nocturnos y limpieza de paneles tras tormentas de polvo.
  Registrar duración de tormentas y consumo; no dañar estructuras sin avisos de juego.

Criterio de entrega: una base mantiene cultivos y dos jugadores, tolera una noche
con reservas y permite reparar una avería sin perder inventarios por duplicación.

## Entrega 3: rover marciano — diseño y recurso 3D pendiente

El **rover venusiano está implementado en 0.36.0**, usando el modelo aportado,
conductor único, nueve espacios de carga, energía, soporte vital, refrigeración,
cabina con visor y seis ruedas animadas. También funciona en Marte dentro de sus
límites ambientales. [Controles y recuperación](venus-rover-and-recovery.md).
La variante marciana con acompañante y modelo específico descrita abajo queda
como ampliación, sin requerirse para usar el vehículo venusiano actual.

Vehículo terrestre con conductor, acompañante y carga. Aceleración y frenado
controlados por el servidor, ruedas visuales, gravedad local, colisiones y
persistencia. Batería y reserva de oxígeno para una cabina sellada; al agotarse,
el traje vuelve a ser necesario. Una mejora venusiana requerirá refrigeración y
carcasa de mayor resistencia. El rover no se obtiene solo con un cambio de textura.

### Especificación del modelo solicitado

- Archivo `.bbmodel` de Blockbench con texturas PNG incluidas, preferentemente
  formato genérico y cubos; sin depender de GeckoLib ni de otro mod.
- Referencia: 16 unidades = 1 bloque. Aproximadamente 3,5 bloques de largo,
  2,2 de ancho y 2,4 de alto. Frente hacia −Z, centro del vehículo en X=0/Z=0,
  contacto de las ruedas con el suelo en Y=0.
- Grupos independientes: `body`, `wheel_fl`, `wheel_fr`, `wheel_rl`, `wheel_rr`.
  El pivote de cada rueda debe coincidir con su eje. Se puede añadir `steering_wheel`.
- Localizadores `seat_driver` y `seat_passenger` dentro de la cabina para ajustar
  ocupantes y cámara. Cabina hueca y parabrisas transparente, con espacio para mirar.
- Los soportes exteriores, ruedas, cabina y carga deben estar en el modelo.
  La integración no añadirá accesorios decorativos ajenos al diseño aprobado.

Validación: mirar desde dentro y con F5, subir/bajar, guardar/reconectar, recargar,
transportar carga y atravesar pendientes marcianas sin atravesar bloques.

## Entrega 4: expediciones y contratos — tres expediciones venusianas implementadas en 0.39.0

Las consolas naturales del laboratorio, refugio de recuperación y estación
atmosférica admiten objetivos de reparación/producción y recompensa única por
jugador. Los contratos generales de la tabla y los rescates de NPC quedan como
ampliaciones. La estación de servicio y el aerostato también están implementados.

| Marte | Venus | Recompensa propuesta |
|---|---|---|
| Cartografiar un depósito de hielo | Instalar una estación geotérmica | Mejoras de almacenamiento |
| Entregar agua y muestras al asentamiento | Recuperar filtros de una instalación abandonada | Recetas de mantenimiento |
| Rescatar una expedición entre cañones | Reparar una instalación durante una tormenta | Componentes de vehículo |
| Investigar el santuario y su jefe existente | Analizar fauna y compuestos atmosféricos | Tecnología para explorar destinos posteriores |

Cada contrato debe tener objetivos medibles en el servidor, progreso por jugador
guardado, entrega de inventario atómica y recompensa única. No sustituir los
intercambios existentes del comerciante marciano. Los contratos nuevos convivirán
con los avances; no se retirará el acceso a tecnología ya poseída por partidas antiguas.

## Entrega 5: grandes expediciones geográficas — diseñada

Marte: cañones extensos, campos de dunas, regiones polares y tubos de lava enfriada,
con depósitos y puntos científicos reconocibles. Volcanes inspirados en Olympus
Mons, adaptados a la altura y distancias de Minecraft. Conservar parcelas de
asentamientos, santuario y aterrizajes seguros; medir generación y `/locate`.

Venus: terrenos volcánicos diferenciados, zonas de recolección de gases y estaciones
elevadas. Las nubes de 50–60 km de altura física requieren una equivalencia de
altitud dentro del mundo del juego y nuevos perfiles de presión/temperatura;
una plataforma a Y=240 no se tratará automáticamente como ese ambiente real.

Cada cambio de terreno tendrá pruebas de varias semillas y límites de coste por
chunk. Las construcciones antiguas conservarán sus plantillas e identificadores,
como ya ocurre con los conjuntos venusianos anteriores a 0.28.

## Operaciones de Venus — 0.40.0

Navegador con destinos descubiertos, panel de diagnóstico por habitación,
recuperación de caja negra y entrega de muestras, instrumentos/luces del aerostato,
actividad local de fumarolas y preferencia de hábitat de las especies pacíficas.
[Guía](venus-operations.md). La validación gráfica y multijugador continúa pendiente;
[lista de pruebas](venus-colony-playtest.md).
