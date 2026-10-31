# Nuevos biomas de Venus — 0.48.0

Se adaptan los tres JAR de Minecraft 1.21.5 al sistema nativo de **Fabric 26.1**.
Instala únicamente el nuevo Space Fabric y Fabric API en cliente y servidor;
no instales estos tres JAR de origen ni TerraBlender junto al mod.

## Las tres regiones añadidas

| Nombre | Identificador | Terreno y recursos |
|---|---|---|
| Grietas de obsidiana | `space_fabric:venus_obsidian_riftlands` | Superficie oscura, crestas bajas y grietas con fondo incandescente. Menas de hierro de grieta |
| Mesetas de azufre | `space_fabric:venus_sulfur_shardlands` | Roca de tesela, relieves escalonados y bandas de fisuras luminosas |
| Yermos de ceniza basáltica | `space_fabric:venus_basalt_ashlands` | Lajas de basalto, roca carbonizada, capas finas de ceniza y pequeños pozos contenidos de lava |

Se suman a Llanuras volcánicas de Venus, Campos de fumarolas y Cuencas de obsidiana:
**Venus tiene ahora seis biomas**. No reemplazan a esos tres biomas ni añaden regiones
al Overworld u otros planetas. Las regiones se distribuyen con ruido climático
nativo en dos ejes; esos ejes son selectores de regiones, no humedad o temperatura
terrestre simuladas.

El cielo, color de niebla y agua parten de los originales. Ceniza ambiental, sonidos
y música de los JAR se trasladan a los atributos ambientales de 26.1. La niebla de
las tormentas, las capas atmosféricas altas y el interior de los hábitats conservan
sus ajustes existentes y pueden modificar el aspecto final del bioma.

## Nueve bloques y dos materiales

- **Obsidiana de grieta:** roca resistente; requiere pico de diamante o mejor.
- **Mena de hierro de grieta:** requiere pico de hierro; minería manual entrega
  uno o dos fragmentos y experiencia. Se detecta con el escáner y admite extracción
  finita con la perforadora, a una unidad por mena.
- **Costra de grieta incandescente:** roca luminosa y caliente.
- **Roca de tesela venusiana:** superficie de las nuevas mesetas.
- **Roca de fisura incandescente:** bandas luminosas y calientes entre las teselas.
- **Lámpara de azufre:** bloque decorativo de luz 15, fabricado con roca de tesela,
  roca de fisura y el núcleo de azufre que ya existía en el mod.
- **Laja de basalto:** roca superficial de los yermos.
- **Roca carbonizada:** roca dura pero rompible. La textura procede del bloque
  llamado `charred_bedrock`; no se convierte la superficie en bedrock indestructible.
- **Ceniza de azufre:** capa de 1/16 de bloque, sin colisión, que se retira con pala
  o a mano y entrega de uno a tres polvos de azufre.

Las once texturas —nueve de bloques y dos de materiales— conservan los PNG de los
JAR sin recolorearlos. Los modelos mantienen sus geometrías y UV; se adaptan las
referencias al namespace de Space Fabric. Los bloques aparecen en la pestaña del mod.

Los **fragmentos de hierro de grieta** se refinan en lingotes de hierro en horno
o alto horno. Cuatro unidades de **polvo de azufre venusiano** producen un cristal
de azufre del sistema industrial existente. Recetas disponibles en **K**, incluido
el uso de la perforadora para el hierro nuevo.

## Integración con Venus

Los seis biomas conservan olivino venusiano, piroxeno, azufre y condensado ácido,
además de las cuevas del generador planetario. El hierro de grieta añade vetas
específicas de Grietas de obsidiana, con seis intentos por chunk y tamaño cinco
entre Y −48 y 80, sujetos a roca reemplazable y al bioma; no se garantizan vetas
por chunk.

La fauna es **la que ya tiene Venus**: dos especies pacíficas y cuatro hostiles,
con los límites compartidos de dos pacíficos y cuatro hostiles por chunk y la
preferencia nocturna anterior. No se importan los mobs, amuletos ni zombis,
esqueletos y otros spawns vanilla incluidos en los JAR originales.

Los laboratorios, refugios y estaciones atmosféricas admiten los seis biomas.
Las ruinas mantienen sus afinidades: los sitios de ceniza se amplían a su región
relacionada, los de azufre a las mesetas y los basálticos a las grietas. Las
misiones de Aurora siguen usando sus consolas naturales.

Las rocas de suelo nuevas son compatibles con el aterrizaje; la capa de ceniza
no sustituye un suelo sólido certificado. La gravedad, presión, gases, temperatura,
trajes, refrigeración y corrosión de Venus siguen usando los sistemas anteriores.
Los pozos abiertos de lava entre estas rocas pueden activar las microerupciones
de 0.47.0. Las rocas incandescentes conservan el daño de contacto y la protección
de botas térmicas existentes.

## Adaptación del terreno y protección de estructuras

Los programas originales de terreno de 1.21.5 no se ejecutan. Uno imponía un suelo
fijo a Y 72 y otro excavaba o elevaba decenas de bloques, incluso en fases tardías.
Se reescriben como features nativas que se ejecutan antes de las estructuras:

- Teselas elevadas hasta diez bloques sobre el suelo local.
- Crestas de obsidiana hasta seis bloques y grietas de diez a catorce de profundidad,
  con curvas continuas basadas en coordenadas del mundo y la semilla.
- Yermos sobre la altura nativa, con manchas carbonizadas y ceniza; pequeños pozos
  con fondo y cuatro paredes sólidas, sin pozas colgantes a una cota fija.
- Escrituras limitadas al chunk decorado y a las columnas del bioma correspondiente.
  Se respetan límites verticales, fluidos y bloques que no pertenecen a la geología.
- Los chunks que tienen inicios o referencias de estructuras se reservan: no reciben
  estas elevaciones o excavaciones adicionales. Sus superficies mantienen las
  texturas regionales, pero se prioriza el apoyo de edificios y sus parcelas.

No se recalcula la altura de las construcciones ya colocadas ni se desplazan sus
plantillas. Esta adaptación evita aplicarles los reescritos de terreno de los JAR
originales. La inspección visual de transiciones entre parcelas y relieve sigue
pendiente de una partida real.

## Partidas existentes

**Los biomas nuevos aparecen en chunks sin generar.** Los chunks que ya exploraste
conservan sus biomas, bloques, estructuras y mapas registrados. No se fuerza una
regeneración ni se modifica tu base. Explora zonas nuevas de Venus o prueba otra
semilla en una copia de partida.

Al cambiar la distribución de biomas, una frontera entre terreno antiguo y nuevo
puede ser visible. Los relieves de features están acotados; no se garantiza una
transición idéntica entre generaciones de versiones distintas.

## Fuentes y reproducción

Diseños y texturas originales de **CreativeMode.net**, aportados por el usuario:

- [Obsidian Riftlands](https://creativemode.net/mod/obsidian-riftlands-fu2ghb3x)
- [Sulfur Shardlands](https://creativemode.net/mod/sulfur-shardlands-36x5fjm7)
- [Basalt Ashlands](https://creativemode.net/mod/basalt-ashlands-iktgmxrq)

Hashes, autores y licencias en `third_party/venus_regions`; los datos fuente y PNG
seleccionados en `tools/import_sources/venus_regions`. Se incluyen créditos y
licencias en el JAR bajo `assets/space_fabric/licenses/venus_regions`.

`python3 tools/import_venus_regions.py` reproduce los recursos desde esos datos.
Con `--jars` permite volver a importar los tres archivos en el orden anterior.
Si regeneras recursos con los generadores de Venus anteriores, ejecuta este
importador al final para conservar las seis regiones y sus etiquetas.
No se copian clases, entrypoints, mixins, código de TerraBlender ni JAR anidados.

## Comprobaciones en partida pendientes

Los codecs, seis biomas alcanzables en tres semillas, orden de features, terreno
acotado, parcelas reservadas, recursos y refinado se comprueban automáticamente.
Falta comprobar dentro de Minecraft:

1. Explorar terreno nuevo y revisar los tres paisajes, colores, música y ceniza.
2. Comprobar suelos, cuevas, menas y el paso entre regiones con distintas semillas.
3. Revisar el apoyo y acceso a edificios en los límites de las parcelas reservadas.
4. Minar y refinar ambos materiales, usar el escáner y ejecutar una extracción de hierro.
5. Probar aterrizaje, apariciones de fauna, iluminación y volcanismo en las regiones nuevas.
6. Revisar un mundo anterior, dos jugadores, el mapa y el renderizado con Sodium.
