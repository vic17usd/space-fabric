# Tierra única: Overworld — 0.52.0

La Tierra del juego es ahora **`minecraft:overworld`**. Se retira la dimensión
adicional `space_fabric:earth` de la generación, del selector de viajes y de la
lista de mapas visitables. Quedan ocho dimensiones adicionales: Mercurio, Venus,
Luna, Marte, Júpiter, Saturno, Urano y Neptuno.

## Viajes y recursos

El menú del cohete muestra un único destino terrestre: **Overworld**. Todos los
cohetes que podían regresar a casa conservan la compatibilidad con ese destino,
con las restricciones de rango, combustible y casco existentes. El mapa orbital
usa la cantidad real de destinos y mantiene sus colores alineados.

Los minerales, recetas, objetos y vehículos con identificadores `earth_*` siguen
registrados para conservar las partidas y sus inventarios. Los minerales terrestres
continúan en el Overworld; el filtro de basalto terrestre también apunta a él.
`Planet.EARTH` permanece como perfil físico para gravedad, atmósfera y cálculo de
viajes. Sus claves de mundo, tipo y stem apuntan a los registros vanilla.
El Overworld conserva sus reglas normales de cielo, gravedad, reloj y clima.

## Actualizar una partida

Reinicia Minecraft después de sustituir el JAR. En multijugador, actualiza también
el servidor. El menú usa una revisión de protocolo nueva para evitar que clientes
con una lista antigua activen sus botones con índices distintos.

La compatibilidad se aplica al leer los datos:

- El mapa de dimensiones guardado en `level.dat` se filtra durante su preparación,
  evitando que vuelva a crear la Tierra retirada.
- Los índices antiguos de destinos y orígenes de viaje se traducen a la nueva
  lista. El destino antiguo Earth se convierte en Overworld; los demás conservan
  su planeta. Los guardados nuevos incluyen identificadores estables.
- Un jugador que hubiera cerrado sesión en `space_fabric:earth` reaparece cerca
  del punto de aparición del Overworld, conservando inventario, experiencia y
  datos personales. Funciona tanto con el propietario de una partida individual
  como con los datos de jugadores de un servidor.
- Si estaba montado, el vehículo guardado y sus pasajeros se trasladan con sus
  desplazamientos relativos. Los cohetes conservan su carga y combustible y pasan
  a descenso para apoyarse sobre terreno real.
- Los jugadores guardados en otras dimensiones mantienen sus datos de posición.

Las carpetas antiguas de regiones, entidades y mapas de `space_fabric:earth` se
conservan como archivo. Sus construcciones permanecen en esos archivos y no se
copian al terreno del Overworld. La migración transforma copias de los datos en
memoria; el guardado posterior utiliza los mecanismos normales de Minecraft.

Se mantiene un **tipo de dimensión y sus recursos de reloj de compatibilidad**
para poder interpretar archivos antiguos. Ese tipo no crea un mundo ni aparece
como destino. Un datapack externo que vuelva a definir la dimensión tendría que
retirarse por separado para mantener esta configuración.

## Implementación y verificación

`PlanetDimensions` resuelve la Tierra al Overworld y conserva el comportamiento
vanilla de este al evaluar efectos planetarios. `TravelDestination` elimina el
duplicado y migra índices. Los cohetes guardan `DestinationId`, `JourneyOriginId`
y `DestinationVersion`. `EarthRetirement` filtra dimensiones y adapta datos de
jugadores; dos Mixins lo aplican al preparar mundos y cargar jugadores.

Las pruebas nativas verifican la ausencia del mundo retirado, la lectura y
preparación de un mapa antiguo de dimensiones, los diez índices originales,
serialización de cohetes, conservación de inventario/vehículo y descenso de
cohetes trasladados. Se mantienen las comprobaciones de física, minerales,
industria, recetas, modelos y generación planetaria.

Pendiente comprobar en cliente y servidor reales una copia de partida anterior:
menú, viaje de ida y vuelta, reconexión desde la dimensión retirada, vehículo,
inventario y atlas. Las pruebas de codecs y NBT no sustituyen esa revisión manual.
