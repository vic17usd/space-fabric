# Rediseños de trajes — 0.50.1

Los cuatro ZIP aportados sustituyen la apariencia equipada de los trajes existentes:

| Archivo/descripción | Traje del mod |
| --- | --- |
| Acid-proof Venusian | Venusiano; también su variante reforzada |
| Heavy vacuum | Vacío |
| Deep-sea abyssal | Abisal |
| Cryogenic | Criogénico |

Cada ZIP aporta `humanoid.png` y `humanoid_leggings.png`, ambos RGBA de 128 × 64.
Se conservan los ocho PNG **sin modificar sus bytes**, escala, colores o transparencia.
Las definiciones nativas `equipment/*.json` asignan cada diseño a su material de
armadura. Se aplica tanto en las ranuras dedicadas de traje como al equipar sus
piezas en las ranuras normales.

Los dibujos tienen zonas transparentes dentro de algunas caras del casco y las
piernas. Se conserva una capa interior opaca con el material anterior y el dibujo
nuevo encima, usando el sistema nativo de capas de equipo. Así se ven los detalles
aportados y las zonas de material transparente tienen un forro visible. La misma
cobertura funciona al llevar el traje debajo de la armadura normal.

Esta entrega cambia las texturas equipadas. Los iconos de inventario conservan su
arte anterior; los ZIP aportan exclusivamente las dos capas para el personaje.
Recetas, propiedades, resistencia ambiental, corrosión y durabilidad mantienen sus
valores. Venus reforzado comparte la apariencia equipada venusiana y conserva sus
iconos de inventario diferenciados.

## Fuentes y regeneración

`tools/texture_sources/suit_redesigns/` contiene los PNG originales, la asignación
y los hashes de los ZIP y sus imágenes. Ejecutar:

```sh
python3 tools/import_suit_redesigns.py
```

restaura las texturas desde esas fuentes. El importador general de equipo también
las reaplica al terminar, para que regenerar iconos, trajes o cohetes conserve el
rediseño. Las copias del forro están en `textures/entity/equipment/*/*_lining.png`.

## Comprobación

`verifyEquipmentTextures` interpreta las definiciones con el codec nativo de 26.1,
verifica las dos capas de cada material, igualdad exacta con los ocho PNG aportados,
proporción UV, transparencia del margen y cobertura combinada de casco, torso,
brazos, piernas y botas. Conserva las comprobaciones de los iconos existentes.

Pendiente inspección en cliente: cuatro conjuntos completos, cada pieza por
separado, inventario, tercera persona, trajes dedicados con armadura encima y la
variante venusiana reforzada. Las comprobaciones por CPU no equivalen a esa revisión
visual ni a una prueba con Sodium.
