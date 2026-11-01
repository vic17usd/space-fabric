# Copia del chat y mapa de cuevas — 0.55.2

Abre el chat y pasa el mouse sobre un mensaje. Aparece un icono pequeño de
dos hojas al final de esa línea del chat: un clic izquierdo copia el mensaje
completo, aunque ocupe varias líneas. Funciona también al desplazar el historial
y cambiar la escala o el espaciado. No se abre otra pantalla ni se modifica
el texto que estabas escribiendo. La pantalla de selección de 0.55.0 se eliminó.

El botón «Captura» está al final del campo de entrada, con espacio propio.

«Captura» junto a su zona inferior. «Copiar»
abre una lista de mensajes completos. Puedes copiar uno o usar «Copiar todo»
para obtener el historial en orden cronológico; no envía mensajes al servidor.
«Volver al chat» recupera la pantalla que estabas usando.

«Captura» toma la pantalla actual, guarda el PNG en `screenshots` y copia la
imagen al portapapeles del sistema para pegarla fuera del juego. Solo se permite
una operación de imagen simultánea. Si falla el portapapeles, se informa del
problema y se conserva el PNG, sin copiar texto o rutas como sustituto.

Desde 0.55.2, el mismo icono de copiar que aparece junto al aviso de una captura
hecha con F2 copia su imagen original. Reconoce el aviso de Minecraft en cualquier
idioma y usa el archivo enlazado; no hace una captura nueva ni copia la frase
«Captura de pantalla guardada…». Si ese archivo fue borrado, muestra un aviso.

La flecha del jugador, reducida aproximadamente un 19 % en 0.55.2, usa una silueta fija que gira de forma uniforme, sin
recalcular sus píxeles por ángulo. Sus estratos están separados del terreno,
los marcadores y su propio contorno. El minimapa conserva tamaño y posición arriba a la derecha.

Al estar bajo una cubierta superior a tres bloques y sin vista al cielo, el
minimapa y el mapa grande del planeta actual muestran el subsuelo cercano a
tu altura. El título del minimapa indica «Subsuelo · Y …». Las capas se separan
en bandas de ocho bloques, por mundo y dimensión. Se conservan los marcadores
de la superficie y sus coordenadas completas. Al salir se recupera la vista de
superficie; al consultar otro planeta se muestra su atlas de superficie.

Cada muestra busca el suelo transitable más cercano, con espacio para el jugador,
en una franja vertical acotada. Las paredes aparecen oscuras; las regiones no
muestreadas no usan la superficie como sustituto. La resolución se mantiene en
cuatro bloques por celda y la exploración se actualiza gradualmente. Solo se
leen chunks cargados en el cliente; no se solicitan chunks remotos adicionales.

Los datos de cuevas se guardan en `config/space-fabric/maps/<sesión>/caves/`.
Se mantienen hasta cuatro capas abiertas y se guardan al salir de la partida.
La apariencia y el portapapeles deben comprobarse también dentro del juego.
