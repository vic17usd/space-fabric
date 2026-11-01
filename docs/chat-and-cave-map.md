# Copia del chat y mapa de cuevas — 0.55.0

Abre el chat: aparecen «Copiar» y «Captura» junto a su zona inferior. «Copiar»
abre una lista de mensajes completos. Puedes copiar uno o usar «Copiar todo»
para obtener el historial en orden cronológico; no envía mensajes al servidor.
«Volver al chat» recupera la pantalla que estabas usando.

«Captura» toma la pantalla actual, guarda el PNG en `screenshots` y copia la
imagen al portapapeles del sistema para pegarla fuera del juego. Solo se permite
una captura simultánea. Si el sistema no admite copiar imágenes, se guarda el
PNG y se copia su ruta, con un aviso que distingue ambos resultados. No se
suben archivos automáticamente.

La flecha del jugador usa estratos separados del terreno, los marcadores y su
propio contorno. El minimapa conserva tamaño y posición arriba a la derecha.

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
