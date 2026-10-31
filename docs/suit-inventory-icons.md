# Iconos de los trajes rediseñados — 0.52.1

Los veinte iconos de inventario de los trajes utilizan ahora ilustraciones basadas
en los diseños equipados aportados:

- Vacío: placas oscuras, bordes dorados y controles anaranjados.
- Venus: protección amarilla y negra con respirador y paneles oscuros.
- Criogénico: blanco, azul hielo y juntas azul oscuro.
- Abisal: azul marino con líneas y luces cian.
- Venus reforzado: comparte los cuatro dibujos nuevos de Venus, igual que su
  apariencia equipada; conserva sus nombres, propiedades y durabilidad propios.

Cada familia tiene casco, pechera, pantalones y botas. Se ven en inventario,
barra rápida, ranuras de traje y visor de recetas porque mantienen los mismos
identificadores de ítem y referencias de modelo. Las texturas equipadas, las
recetas y la protección ambiental conservan sus valores.

## Arte y reproducción

`tools/texture_sources/suit_inventory_redesign.png` conserva la hoja artística
completa generada a partir de las cuatro texturas superiores aportadas. Su JSON
adjunto registra el SHA-256, orden de filas/columnas y tamaño final.

`python3 tools/import_suit_inventory_icons.py` extrae los dieciséis dibujos con
ImageMagick, usa los separadores transparentes de la hoja, recorta el margen vacío
y centra cada pieza a 128×128 con escalado de píxeles. Copia los cuatro iconos
venusianos a su variante reforzada. Esta operación empaqueta el arte generado;
no repinta los dibujos originales ni las capas equipadas del usuario.

Los importadores de equipo y el generador industrial reaplican estos iconos al
terminar, para evitar que una regeneración restaure los iconos anteriores.
El JAR y su compilación habitual utilizan los PNG ya incluidos y no requieren
ImageMagick.

## Comprobaciones

El control nativo de equipo comprueba ahora 29 iconos: veinte piezas de traje y
nueve kits de cohete. Verifica modelos, referencias del atlas, dimensiones y
transparencia; también conserva los controles de cobertura del traje equipado y
la igualdad de sus ocho capas originales con los PNG aportados.

Pendiente inspección visual dentro de Minecraft con las veinte piezas y en el
visor de recetas. La hoja artística y las comprobaciones por CPU no son una
captura de una partida.
