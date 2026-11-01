# Diseños del lote de recursos

La versión 0.54.0 adapta 30 diseños de bloques y 21 imágenes de objetos a sus
identificadores existentes. El detalle de cada archivo, destino, procedencia y
SHA-256 está en `third_party/mod_batch/manifest.json`. Los identificadores,
recetas, generación, propiedades y funcionamiento de las máquinas se conservan.

El archivo llamado «Deposito de sulfuto marciano» se usa como diseño del depósito
de sulfatos ya existente; sigue soltando los mismos materiales. Glacial Frontier
coincide exactamente con el archivo ya incorporado anteriormente y no se duplica.

Los modelos de la perforadora de sondeo y del separador de gases se convierten a
modelos de bloque nativos con las 219 y 129 piezas, respectivamente, y su atlas
original. Se reducen para encajar en un bloque, sin cambiar colisiones ni lógica.
Algunos modelos de máquinas del lote emplean materiales vanilla en sus modelos
originales y se conservan así. Se mantienen los PNG de origen en los recursos de
importación para facilitar su revisión.

Los JAR aportados se usan como fuentes de recursos: sus clases y telemetría no se
ejecutan ni se incluyen. Los créditos, enlaces y licencias se conservan en el
manifiesto y en `assets/space_fabric/licenses/mod_batch/`.

`python3 tools/import_mod_batch.py` regenera los recursos desde las copias de
entrada incluidas. Para volver a extraerlos del ZIP original se puede usar
`python3 tools/import_mod_batch.py --zip /ruta/Mod.zip`. Otros generadores de
recursos antiguos pueden sobrescribir estos modelos; vuelve a ejecutar este
importador después de utilizarlos.

Para actualizar desde 0.52.0, sustituye el JAR anterior en la carpeta `mods` y
reinicia el juego. En multijugador usa la misma versión en cliente y servidor. La
compilación comprueba los modelos y texturas sin GPU; la apariencia y orientación
deben revisarse también dentro de Minecraft.
