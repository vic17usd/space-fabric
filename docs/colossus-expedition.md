# La señal del Coloso — 0.50.0

Una expedición personal une las instalaciones abandonadas de Venus, el Nido de
Azufre y el Búnker del Coloso. Comparte el diario de Proyecto Aurora y conserva
su propio progreso; puedes explorarla sin terminar Aurora.

## Empezar

Fabrica el **diario de investigación** con un libro, una tarjeta de datos en blanco
y un lingote de cobre (receta disponible en el visor). Clic derecho al aire abre
el diario: después de Aurora aparecen los registros de esta expedición.
`/venus coloso` muestra el objetivo y permite reanudar la triangulación.

Para recuperar una pista, permanece de pie sobre el suelo y usa **Mayús + clic
derecho con el diario sobre un bloque sólido de la estructura natural**. Sirven
los pisos y paredes completos; las piezas decorativas sin colisión completa no
son puntos de lectura. Un edificio construido por el jugador no aporta pruebas.
Acércate al centro del lugar si su chunk de origen aún no está cargado.

## Recorrido

1. **Fundición Abandonada de Venus** (`rustfall_foundry`): recupera el Registro de
   emergencia. Describe una señal térmica ajena a las máquinas.
2. **Puesto de Ceniza de Venus** (`venusian_ash_outpost`): descifra los archivos.
   Identifican un organismo de basalto vinculado al nido.
3. **Nido de Azufre** (`venus_sulfurfire_nest`): examina sus raíces o bloques bajos.
   Los pulsos permiten triangular la ubicación de un búnker.
4. **Búnker del Coloso**: consulta las coordenadas guardadas en el diario, prepara
   equipo y llega a su cámara profunda. Examina el piso con el diario cerca del
   centro de la arena. Debe ser el búnker asignado a tu expedición.
5. **Derrota al Coloso** en ese búnker. La baja debe atribuirse a ti, también si
   utilizas un proyectil. La recompensa normal del boss se mantiene.

Las pistas requieren ese orden. Puedes volver a leer el diario, fabricar otro si
lo pierdes y consultar el comando sin perder progreso. El registro se guarda por
UUID en la partida; cambiar de dimensión, morir o reconectar conserva las pruebas
y las coordenadas. Otros jugadores tienen su propia expedición y deben recibir
su propia baja del boss para completarla.

Los registros son páginas del diario, no objetos transferibles que permitan
saltarse la exploración. Esta entrega desarrolla la expedición; el Núcleo del
Coloso conserva sus usos anteriores.

## Guía y búsqueda

La guía existente mantiene el fondo transparente. Tras recuperar la primera
pista, muestra el siguiente objetivo cuando se ha completado el tutorial inicial
de supervivencia. También sugiere el primer paso al completar Aurora. El diario contiene la historia, los pasos y las coordenadas.
Al finalizar la expedición vuelve a mostrarse la guía de investigación.

La triangulación prospecta hasta **81 regiones**, empezando cerca del nido. El
servidor procesa globalmente **un candidato por segundo** y mantiene como máximo
**una comprobación asíncrona de chunk** pendiente. No espera un futuro bloqueando
el hilo del juego ni ejecuta un `/locate` extensivo.

Primero verifica el emplazamiento previsto usando semilla y configuración nativa;
después consulta/genera el chunk hasta `STRUCTURE_STARTS` para confirmar el inicio
real. Descarta además los chunks que ya estaban completamente generados para
seleccionar un búnker nuevo cuyo boss aún no se haya generado. Así evita enviar
la expedición a una estructura antigua que pueda estar saqueada. Las
coordenadas son de un emplazamiento válido cercano dentro de la búsqueda acotada;
no se garantiza que sea el más próximo de todo el planeta.

La búsqueda continúa mientras estás en Venus. Si sales, desconectas o reinicias
antes de recibir coordenadas, usa `/venus coloso` al volver. Si no encuentra un
sitio o están desactivadas las estructuras, muestra el motivo; puedes desplazarte
a otra región y volver a intentarlo. Los datapacks deben conservar el conjunto
de estructuras y el tipo de búnker de Space Fabric.

Los archivos pueden recuperarse en estructuras existentes que conserven sus
inicios naturales. Los búnkeres nuevos requieren terreno no generado previamente.
La expedición busca un destino nuevo; los búnkeres antiguos conservan sus bloques
y no regeneran bosses ya derrotados.

## Validación

Se comprueban orden, rechazo de repeticiones y saltos, aislamiento entre jugadores,
guardado de coordenadas, rechazo de etapas inválidas y presupuesto finito de
búsqueda. Los codecs nativos verifican candidatos con tres semillas sin cargar
plantillas durante la prospección. La compilación incluye los controles anteriores
de estructuras, modelo, industria y supervivencia.

Pendiente revisar en cliente la lectura de las pistas, la guía, la búsqueda en una
partida antigua, desconexión durante una búsqueda, y el combate hasta completar
la expedición. Las comprobaciones automáticas no sustituyen esa partida manual.
