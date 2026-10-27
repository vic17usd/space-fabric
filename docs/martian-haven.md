# Pueblo de Marte y aldeano del JAR — Fabric 26.1, versión 0.11.0

Nombre visible actualizado en 0.30.1. [Catálogo y búsquedas en español](structure-names.md).

Haven retira tres capas de pedestal y el terreno importado del nuevo plano Y=0;
la plantilla de superficie conserva 20.406 bloques, mide 124 × 81 × 125 y mantiene
sus edificios y seis habitantes. Se genera en Marte sobre una parcela plana preparada
por `space_fabric:martian_surface`, con los pisos al nivel del terreno. El NBT original
de 45.380 bloques y 124 × 84 × 125 se conserva para piezas guardadas anteriormente.
Consulta [el generador de parcelas](martian-surface-plots.md).

## Reemplazo del aldeano

El personaje utiliza la textura original, el icono y el saludo de `Aldeano Marciano.jar`,
con el modelo adulto/bebé de aldeano vanilla que usa ese JAR. Se reemplaza el atlas
interpretado y el modelo humano de 0.9.0. Los archivos se copian byte por byte:
`third_party/martian_copper_trader/manifest.json` conserva sus SHA-256 y procedencia.
La licencia original se conserva allí y dentro del JAR compilado.

Se integran sus intercambios de amatista/cobre y la reacción defensiva: al sufrir
un daño real obtiene Resistencia II durante 50 ticks y Velocidad II durante 100.
Busca un gólem de hierro cercano o huye del atacante. Al interactuar reproduce
el saludo original. Mantiene las adaptaciones al entorno del mod: soporte vital,
gravedad marciana y seis bloques de caída segura.

La implementación conserva el tipo `space_fabric:martian_copper_trader` y su huevo.
Los habitantes ya guardados reciben el nuevo aspecto sin cambiar UUID ni duplicarse.
Al cargar un registro anterior se actualizan sus ofertas y velocidad una vez;
`TraderRevision` evita que volver a cargar renueve continuamente las existencias.
No hace falta instalar el JAR externo junto al de Space Fabric. Se importan sus
recursos y comportamiento; el inicializador externo y su JAR de telemetría no se cargan.

## Aparición y persistencia

Cada Haven incluye seis residentes en su plantilla NBT. Los puntos locales son
`(30,2,30)`, `(90,1,30)`, `(30,1,90)`, `(90,2,90)`, `(30,1,62)` y `(90,1,62)`.
El conversor comprueba suelo —incluido el regolito implícito— y tres bloques libres.
El generador prepara el terreno
antes de colocar la plantilla y Minecraft transforma las posiciones con su rotación.

Son adultos persistentes que recuerdan una zona de 24 bloques alrededor de su
posición. Se guardan su hogar, ofertas, existencias y fecha de reposición.
No se añaden apariciones salvajes ni un bucle que duplique habitantes al cargar
chunks. Si mueren por combate u otros peligros, no se reponen automáticamente.

## Oxígeno, gravedad y comercio

El soporte vital rechaza `space_fabric:hypoxia` y `space_fabric:decompression` y
permite respirar bajo el agua. Combate, fuego y presión profunda conservan sus
reglas. Los jugadores siguen necesitando su propio equipo fuera del cohete.

El sistema global aplica la gravedad relativa 0,38 de Marte: `0.08 × 0.38 = 0.0304`.
El modificador no se acumula al recargar y se actualiza al cambiar de dimensión.

| Entrega del jugador | Recibe | Existencias |
| --- | --- | ---: |
| 8 fragmentos de amatista + 4 lingotes de cobre | 1 catalejo | 12 |
| 16 fragmentos de amatista + 8 lingotes de cobre | 1 brújula de recuperación | 6 |

Estas son las dos ofertas del JAR aportado. Se reponen cada 24.000 ticks de tiempo
del mundo, unos 20 minutos a 20 TPS, esperando a terminar las transacciones activas.

## Instalación y comandos

Instala Space Fabric 0.11.0 en cliente y servidor con Fabric API para 26.1 y sustituye
la versión anterior; deja un solo JAR de Space Fabric en `mods/`. Las estructuras
ya colocadas no se reconstruyen: comprueba las parcelas en un mundo de pruebas
nuevo o en chunks nuevos.

```mcfunction
/execute in space_fabric:mars run locate structure space_fabric:martian_haven_settlement
/give @s space_fabric:martian_copper_trader_spawn_egg
```

Para colocar una copia en un área de pruebas:

```mcfunction
/execute in space_fabric:mars run place structure space_fabric:martian_haven_settlement ~ ~ ~
```

Este comando modifica el terreno y añade un nuevo grupo. La carga normal de un
chunk ya generado no vuelve a ejecutar la plantilla.

## Reproducir los recursos

La estructura original sigue en `tools/structure_sources/`. Para reconstruir el NBT:

```sh
python3 tools/convert_creativemode_structure.py tools/structure_sources/martian_haven_settlement.json src/main/resources/data/space_fabric/structure/martian_haven_settlement.nbt --entities tools/structure_sources/martian_haven_residents.json
python3 tools/prepare_martian_templates.py
```

Para volver a importar los recursos originales, sin ejecutar el JAR:

```sh
python3 tools/import_martian_trader.py "/ruta/Aldeano Marciano.jar"
python3 tools/generate_haven_resources.py
python3 tools/validate_resources.py
./gradlew build --console=plain
```

El segundo script actualiza modelos y traducciones y conserva los PNG importados.
Las pruebas cubren los recursos originales, intercambios, NBT, atributos y generación
sobre parcelas. La apariencia, el sonido y la IA requieren además comprobación en
una partida real; no se afirma haber ejecutado esa sesión gráfica.
