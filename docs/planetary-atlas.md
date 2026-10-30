# Minimapa y atlas de los planetas — 0.44.0

Instala 0.44.0 en **cliente y servidor**, sustituyendo el JAR anterior. Requiere
Minecraft 26.1, Java 25, Fabric y Fabric API; no añade otra dependencia de mapas.
Funciona en partidas existentes. Cada planeta y la Luna tienen su mapa propio;
la Tierra del mod y el Overworld también se guardan por separado.

## Controles

| Acción | Control predeterminado |
|---|---|
| Abrir/cerrar mapa grande | **M**; Esc también cierra |
| Guardar tu posición | **B** |
| Mostrar/ocultar minimapa | **N** |
| Desplazar el mapa grande | Arrastrar con botón izquierdo |
| Acercar/alejar | Rueda sobre el mapa o botones +/− |
| Crear marcador sobre el mapa | Clic derecho en el punto deseado |
| Volver a tu posición | **Centrar** |
| Consultar otro planeta visitado | Pulsar el botón superior con el nombre del planeta |

Las tres teclas se pueden reasignar en Opciones → Controles. Las pantallas del
mapa y de marcadores mantienen la supervivencia en marcha; ábrelas con el equipo
y soporte vital adecuados al lugar donde estés.

## Minimapa

Se muestra en la **esquina inferior derecha**, separado del indicador de misiones
y del panel de hábitat. El norte permanece arriba. La marca blanca señala al
jugador y el trazo dorado indica hacia dónde mira. Los marcadores usan el color
elegido; el destino seleccionado lleva un borde blanco. Si queda fuera del área
visible, su indicador aparece en el borde. Las coordenadas X/Z están abajo.

Se oculta con N, F1, las pantallas abiertas, al morir y durante el vuelo del cohete.
La preferencia de N se conserva para esa partida y jugador. Mientras el minimapa
esté oculto, el atlas puede seguir registrando la exploración.

## Mapa grande y localizaciones

**M** abre el mapa de la dimensión actual. Puedes ampliar y desplazar la vista,
consultar las coordenadas bajo el cursor y revisar los marcadores del panel derecho.
El botón con el nombre del planeta recorre los mapas ya visitados: consultar uno
no cambia tu dimensión. Centrar vuelve a tu posición actual o a la última posición
guardada en el planeta que estás consultando.

Con **B** o **Aquí +** guardas la posición del jugador. El clic derecho sobre el
mapa propone X/Z del lugar marcado y una altura de superficie aproximada; puedes
editar las tres coordenadas. En la pantalla de edición:

- Escribe un nombre de hasta 48 caracteres y elige uno de seis colores.
- Cambia **Visible/Oculto** para controlar su indicador.
- Guarda la localización, cancela la edición o elimina un marcador existente.
- Hay hasta **128 localizaciones por dimensión**, con selección persistente.

Al pulsar un marcador de la lista se selecciona y se centra el mapa en él.
**Editar destino** permite cambiarlo. Debajo se muestran sus coordenadas y, si
pertenece al planeta actual, la distancia al jugador. Los botones inferiores
de la lista recorren las páginas cuando hay muchas localizaciones.

El botón **Navegador** importa las localizaciones de ese planeta desde los
navegadores de expedición de tu inventario o mano secundaria. Omite coordenadas
ya registradas y respeta el límite de 128. Los datos originales del navegador
se conservan. Puedes importarlos de nuevo para incorporar destinos nuevos.

## Terreno explorado

El atlas usa los colores cartográficos de los bloques de superficie y sombreado
de relieve. Se actualiza con los chunks que el cliente ya tiene cargados alrededor
del jugador. Se registra una muestra cada **cuatro bloques**: ampliar la imagen
no aumenta esa resolución. Las zonas desconocidas permanecen oscuras.

La vista representa la superficie, con la altura conservada en sus marcadores.
En los gigantes gaseosos registra las plataformas u otros bloques presentes y
las zonas vacías observadas. No inventa un terreno sólido en esos planetas.

El muestreo está limitado a dos chunks por tick de cliente y a un radio de ocho
chunks. No busca estructuras con `/locate` ni solicita chunks remotos para
completar el mapa. Las regiones guardadas se cargan con un presupuesto por tick;
una vista grande puede completarse progresivamente al consultar datos antiguos.

## Guardado por mundo, jugador y planeta

Los datos personales se guardan en el cliente:

```text
config/space-fabric/maps/
  <identificador de mundo/servidor y jugador>/
    settings.json
    <identificador de dimensión>/
      markers.json
      regions/
        <región X>_<región Z>.sfa
```

Los nombres de carpeta se calculan con SHA-256. El servidor proporciona un UUID
persistente del mundo, que también permite separar los mapas cuando se reinicia
un mundo en la misma dirección de servidor. Se combinan esa identidad, la partida
o dirección de conexión y el UUID del jugador. Cada archivo de marcadores conserva
su dimensión y se comprueba al cargarlo.

Los marcadores son **personales**: se guardan localmente. El servidor transmite
la identidad del mundo; no recibe las coordenadas ni los nombres de tus marcadores.
Para llevarte el atlas a otro ordenador, conserva la carpeta `maps` junto con la
misma cuenta y contexto de partida/servidor.

Los marcadores se guardan al confirmar sus cambios. El terreno se guarda en lotes
y al desconectar o cerrar el cliente. Los archivos se sustituyen mediante un
archivo temporal. Si hay datos dañados, se informa en el registro y se conservan
los archivos afectados. La caché mantiene hasta 3.072 regiones en memoria por
mapa y dos mapas abiertos; el resto permanece en disco.

## Validación y comprobaciones en juego

Las pruebas automáticas verifican separación de dimensiones, mundos y jugadores,
las diez dimensiones principales, coordenadas negativas, límites de regiones,
selección/edición/ocultación/borrado, persistencia, presupuesto de lectura y escritura,
límites de marcadores y conservación de archivos dañados. La identidad del mundo
se prueba con su codec de SavedData y el paquete nativo de Minecraft.

La compilación valida las APIs de pantalla, textura dinámica y controles de 26.1.
No se ha abierto un cliente gráfico ni realizado una sesión multijugador para
observar este atlas. Quedan por ejecutar estas comprobaciones:

1. Entrar en una partida existente, abrir M y recorrer terreno: el área conocida
   crece; las zonas remotas continúan oscuras. Revisar colores y orientación.
2. Crear, editar, ocultar y eliminar puntos con B y clic derecho. Probar rueda,
   arrastre, controles reasignados, páginas de la lista y retorno desde el editor.
3. Viajar entre los nueve cuerpos y el Overworld. Confirmar que cada mapa conserva
   sus puntos y que el selector permite consultar mapas anteriores sin viajar.
4. Reconectar y comparar posiciones, selección y terreno. Probar dos jugadores,
   dos mundos y un servidor cuyo mundo se haya reiniciado, usando copias de prueba.
5. Importar un navegador y repetir: no duplica coordenadas. Con 128 puntos, la
   edición sigue disponible y se impide añadir otro hasta borrar uno.
6. Revisar minimapa, hábitat, misiones, aerostato y vuelo del cohete con distintas
   escalas de interfaz y con Sodium. Confirmar F1, N, idiomas y cierre del cliente.
