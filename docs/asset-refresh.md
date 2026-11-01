# Actualización de diseños 0.54.4

| Archivo aportado | Destino |
| --- | --- |
| Mena de bauxita.jar | Mena de bauxita existente |
| Mena de ares.jar | Mena de cristal de Ares existente |
| Traje espacial nivel 1.jar | Traje de vacío: equipamiento y cuatro iconos |
| Traje espacial para venus.jar | Traje venusiano: equipamiento y cuatro iconos; mismos iconos para Venus reforzado |

El regolito de óxido de hierro recupera su textura anterior al lote 0.54.0.
El minimapa se sitúa arriba a la derecha, conservando tamaño y controles.
Se conservan las propiedades, recetas, protecciones y generación existentes.

`tools/import_asset_refresh.py` aplica los recursos desde copias de origen.
`--source-root /ruta/de/archivos` vuelve a extraer los cuatro JAR. Solo se extraen
recursos, metadatos y licencias; no se ejecuta ni se incluye código de los mods
originales. Los nombres, autores, licencias y hashes de entrada constan en
`third_party/asset_refresh/manifest.json`, y las licencias originales se incluyen
en `assets/space_fabric/licenses/asset_refresh`. Los importadores del lote y de
los iconos reaplican estos diseños para no volver a sobrescribirlos.

Los modelos finales usan rutas del propio mod y las imágenes se copian sin
repintarlas. Se conservan las capas de forro selladas. Los originales incluyen
imágenes alternativas que permanecen en la caché de importación; se usan las
imágenes que los modelos de origen referencian.

Para instalar, sustituye el JAR anterior por 0.54.4 y reinicia el juego.
