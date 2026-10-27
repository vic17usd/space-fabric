# Paisajes de Venus — 0.32.0

Se integran los tres JAR aportados dentro de Space Fabric. Instala la versión
0.32.0 en cliente y servidor, junto con Fabric API para 26.1. No necesitas instalar
los JAR de origen ni TerraBlender para este contenido.

## Regiones

| Región | Identificador | Aspecto y contenido |
|---|---|---|
| Llanuras volcánicas de Venus | `space_fabric:venus` | Conservan el suelo anterior, magma y lava; pueden aparecer grietas incandescentes |
| Campos de fumarolas | `space_fabric:venus_fumaroles` | Sustrato basáltico, costras calientes, respiraderos de humo y pequeños depósitos corrosivos |
| Cuencas de obsidiana | `space_fabric:venus_obsidian_basins` | Escoria oscura y lagunas corrosivas superficiales con fondo y bordes rocosos |

El generador nativo `multi_noise` distribuye las tres regiones de forma
determinista según la semilla. Se usa un eje de ruido climático como selector de
regiones; no se simula humedad terrestre ni lluvia de agua en Venus. No cambian
la presión, temperatura extrema, gravedad, día solar ni requisitos de los trajes.

Las tres regiones mantienen los minerales anteriores y las seis criaturas de
Venus. Se conservan los grupos y el límite compartido de dos pacíficos por chunk,
y las preferencias nocturnas de los hostiles. Los dos asentamientos existentes
pueden generarse en cualquiera de las tres regiones.

## Bloques nuevos

| Bloque | Uso y comportamiento |
|---|---|
| Basalto de fumarola | Respiradero caliente; humo y pequeñas llamas cuando tiene aire encima |
| Costra de azufre | Superficie caliente; al minarla suelta el cristal de azufre ya usado por el mod |
| Costra incandescente | Emite luz y aplica el comportamiento térmico existente |
| Depósito corrosivo | Capa baja alrededor de las fumarolas; suelta condensado ácido |
| Sustrato basáltico venusiano | Roca de los campos de fumarolas y fondo de las cuencas |
| Roca volcánica agrietada | Roca caliente de la caldera |
| Escoria de obsidiana | Roca resistente de las cuencas; requiere pico de diamante o mejor |
| Cristales de azufre | Depósitos luminosos de la caldera; sueltan cristales de azufre |
| Superficie de laguna corrosiva | Capa baja sobre fondo rocoso; suelta condensado ácido |

Los bloques usan los nueve PNG originales, sin recolorearlos ni sustituir las
texturas aportadas anteriormente. Todos se encuentran en la pestaña de materiales.
Excepto la escoria, los nuevos materiales requieren pico de piedra o mejor.

Las dos capas corrosivas tienen el aspecto original de 2/16 de bloque y carecen
de colisión: se camina sobre el fondo. Necesitan un bloque completo y seco debajo;
heredan la retirada al perder su soporte. El contacto bajo aplica veneno y daño
leve periódico a personajes sin protección contra ácido. Las criaturas nativas de
Venus y el equipo resistente al ácido conservan su protección. Son depósitos
corrosivos jugables del diseño aportado, no un modelo de ácido líquido estable
en la superficie real de Venus. No permiten nadar ni fluyen como agua.

## Generación pequeña

- Campos de fumarolas: un intento de grupo por chunk del bioma correspondiente,
  con hasta tres respiraderos y costras o depósitos a su alrededor.
- Lagunas: un intento por cada seis chunks de cuencas, de media. Radio de 4–6
  bloques, fondo basáltico y borde de escoria. Rechazan desniveles mayores de dos
  bloques y superficies ocupadas o mojadas.
- Grietas de lava: un intento por cada doce chunks de cualquiera de las regiones,
  de media. Trincheras cortas con lava viscosa, fondo y contención lateral.

Las probabilidades indican intentos, no garantizan un accidente por chunk. Las
operaciones están limitadas al chunk que se está decorando y validan el terreno
antes de excavar lagunas o grietas. Se ejecutan antes de colocar los asentamientos,
para que sus parcelas y edificios no resulten recortados por estas excavaciones.
Las rocas naturales nuevas también admiten aterrizajes, con las comprobaciones
anteriores de suelo firme y espacio libre; las capas corrosivas no sirven de suelo.

## Caldera de azufre de Venus

El volcán aportado se integra como **Caldera de azufre de Venus**, con su silueta
de 120×94×123 bloques. Se rellena roca bajo su piel para apoyar las laderas y se
conserva el hueco central con una pequeña cuenca de lava contenida. La lana, los
metales preciosos y las luces del editor se sustituyen por materiales geológicos.
No se incorporan miles de fuentes de lava en los antiguos canales verdes.

Son 56 plantillas de hasta 16×94×16, con 295.678 posiciones no vacías, sin almacenar
el volumen de aire. Su huella irregular ocupa 10.461 columnas.
Las piezas apoyan esa huella sobre el terreno; no generan una plataforma
rectangular. Se eligen emplazamientos de relieve suave, hasta ocho bloques en el
sondeo, sin modificar las plantillas anteriores de los pueblos.

El conjunto tiene un candidato por región de 96×96 chunks (1.536×1.536 bloques),
sujeto al terreno y una exclusión de 16 chunks alrededor de candidatos del conjunto
de asentamientos. Por ello no hay una caldera garantizada en cada región.
La caldera se registra como estructura localizable y aparece en `/estructuras`:

```mcfunction
/execute in space_fabric:venus run locate structure #space_fabric:caldera_venusiana
```

Para buscar las regiones:

```mcfunction
/execute in space_fabric:venus run locate biome space_fabric:venus_fumaroles
/execute in space_fabric:venus run locate biome space_fabric:venus_obsidian_basins
```

## Mundos existentes y límites

Los paisajes aparecen en **chunks nuevos**. Las construcciones, biomas y bloques
ya generados se conservan; busca una zona sin explorar para ver una caldera completa.
La dimensión mantiene su identificador. El cielo y la niebla del interior de los
hábitats siguen las reglas de 0.31, también en los biomas nuevos.

Esta entrega añade geografía, fumarolas visibles y contacto corrosivo; todavía no
hay erupciones dinámicas, terremotos, corrosión de máquinas ni simulación de fluidos
químicos. El calor de contacto reutiliza las botas térmicas y trajes existentes.

Para regenerar recursos desde las fuentes guardadas:

```sh
python3 tools/import_venus_landscapes.py
python3 tools/generate_venus_landscapes.py
```

Ejecuta estos pasos después de generadores generales de terreno o estructuras que
reescriban las etiquetas de Venus. [Procedencia y licencias](../third_party/venus_landscapes/README.md).

La validación usa registros y codecs nativos, tres semillas para la selección de
biomas, colocación completa de la caldera por chunks y su guardado/carga, rechazo
de pendientes sin escrituras parciales, herramientas, loot, colisiones y modelos
cliente por CPU. La frecuencia observada, los efectos de contacto y la inspección
visual/multijugador necesitan también comprobación en una partida real.
