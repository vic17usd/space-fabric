# Coloso de Azufre y sus dos lugares de exploración — 0.49.0

Minecraft Java **26.1**, Fabric y Java 25. Los tres archivos aportados están
integrados en el propio mod; se instala únicamente el JAR de Space Fabric.

## Dónde vive el boss

Elegí el **Búnker del Coloso** (`space_fabric:venus_colossus_bunker`), adaptado de
`Venus estructura 10.jar`. Su sala principal ancha y techada permite esquivar
los ataques y encaja con un coloso de basalto y azufre que habita bajo Venus.

- Conserva el diseño de 116 × 47 × 119 bloques, con una arena central despejada
  de 45 × 22 × 45. El interior tiene aire explícito para excavar la roca natural.
- Base en **Y −56**, piso de la arena en **Y −52** y pies del boss en **Y −51**.
  La cubierta llega hasta Y 6 y se comprueba que quede enterrada.
- Hay **un Coloso por búnker**, persistente, sin aparición natural adicional en
  los biomas. Al derrotarlo, el búnker permanece como lugar explorado.
- Un cofre lateral contiene suministros limitados: azufre y dos células de combustible.
- Usa una cuadrícula de selección de **256 chunks / 4.096 bloques**, con separación
  de 128 chunks. Es una estructura rara; la cuadrícula no garantiza una distancia
  exacta entre centros ni una aparición en cada región si el terreno no es válido.

Se accede excavando o explorando las capas profundas. Lleva pico, iluminación,
recursos y traje completo de Venus o abisal: el aire de la cámara es un espacio
vacío de bloques, no un hábitat presurizado operativo.

## Nido de Azufre

`space_fabric:venus_sulfurfire_nest` adapta `sulfurfire-broodlands-wrlm5742.jar`
como una formación orgánica superficial de 113 × 87 × 112 bloques. Conserva la
silueta alta del original. Las raíces bajas llegan al terreno; se añadieron
uniones cortas para las partes principales y se retiraron 413 bloques de pequeñas
chispas decorativas que estaban separados de la construcción.

La parcela usa el mismo sistema de integración en terreno que las estructuras
superficiales de Venus: prospección acotada, rechazo de pendientes excesivas,
suelo natural y transición en el perímetro. La base queda apoyada en Venus.
La altura de las torres forma parte del diseño del nido.

Su selección usa regiones de **120 chunks / 1.920 bloques**, separación 64 chunks.
El boss se encuentra en el búnker. El nido conserva la fauna natural de su bioma.
Ambos lugares pueden aparecer en cualquiera de los seis biomas venusianos.

## Combate

El Coloso conserva su modelo original de **124 cubos**, textura 2048 × 2048,
sonido de ventilación y ocho animaciones: reposo, caminar, barrido, golpe al suelo,
ventilación, erupción, tambaleo y muerte.

| Comportamiento | Funcionamiento |
|---|---|
| Vida y tamaño | 400 puntos; unos 6 bloques de ancho y 5 de alto |
| Barrido de garra | Aviso de 1 s; hasta 22 puntos de daño de combate |
| Golpe al suelo | Aviso de 2 s; impacto cercano y empuje |
| Ventilación | Aviso de 3 s; gas con veneno y lentitud cerca del boss |
| Erupción | Tres marcas en el **suelo de la cámara**, aviso de 3 s y explosión visual |
| Núcleo expuesto | Tras un ataque, abre el pecho durante 2,5 s y recibe 35 % más daño |
| Segunda fase | Al 50 % de vida, camina más deprisa y reduce la recuperación de 5 a 3,75 s |
| Derrota | Animación de 4 s; 120 de experiencia y, con baja atribuida a un jugador, un Núcleo del Coloso y 5–8 cristales de azufre |

Los valores de daño son anteriores a las reducciones de armadura y resistencia.
El traje completo resistente al ácido filtra el gas y recibe un punto de desgaste
por pieza; las cabinas y los hábitats que mantienen su protección también lo filtran.
La armadura sigue siendo importante frente a las garras y los impactos.
El núcleo es un trofeo/material; no añade una nueva receta industrial en esta entrega.

El boss hereda la inmunidad venusiana a presión, hipoxia, calor, lava y veneno,
y el modificador global de gravedad del planeta. Defiende su zona y vuelve al
búnker si se aleja. En pacífico permanece inactivo. Sus ataques generan partículas
y daño; conservan los bloques de la estructura.

## Encontrarlos

Con permisos de operador, `/estructuras` muestra los dos nombres en español y
sugiere los comandos correspondientes:

```mcfunction
/execute in space_fabric:venus run locate structure #space_fabric:bunker_del_coloso
/execute in space_fabric:venus run locate structure #space_fabric:nido_de_azufre
```

El primer comando sitúa el destino en la profundidad del búnker. En creativo
puedes inspeccionar primero con espectador. También existen el huevo de aparición
y `/summon space_fabric:sulfur_colossus` para pruebas en una cámara de tamaño suficiente.

Las estructuras requieren **chunks nuevos**. Los chunks ya explorados mantienen
sus construcciones y las partidas conservan sus bloques, registros y progresión.

## Implementación y reproducción

- `entity/colossus/`: entidad, registro, IA de combate y sincronización de acciones.
- `client/colossus/`: rig y animaciones nativos de 26.1; pose de recuperación del núcleo.
- `worldgen/structure/VenusBunkerStructure` y `VenusBunkerPiece`: generación profunda,
  cubierta, guardado de posición y colocación por chunk.
- `data/space_fabric/worldgen/structure{,_set}/`: selección y configuración de los dos sitios.
- `data/space_fabric/structure/venus_colossus_bunker/`: 64 plantillas nativas.
- `data/space_fabric/structure/venus_sulfurfire_nest/`: 50 plantillas nativas.
- `tools/import_venus_boss_sites.py --stored-sources`: regenera las plantillas y sus configuraciones.
- `tools/import_venus_colossus.py`: importa arte y rig desde los recursos originales.
  La IA adaptada se mantiene en el repositorio.

La ruta de `/locate` del nuevo búnker consulta nueve alturas y deja la carga de
plantillas para la generación efectiva. La colocación escribe únicamente dentro
del chunk procesado. Las marcas de erupción consultan bloques ya cargados junto a
la víctima; no utilizan el mapa de altura de la superficie sobre el techo.

## Comprobaciones y prueba manual

Las pruebas ejecutan los codecs de 26.1, generación con tres semillas, colocación
real de bloques en volúmenes acotados, guardado de piezas, suelo y hueco del boss,
un único residente persistente y conexión de todas las partes del nido al piso.
Una comprobación por CPU ejecuta el rig, UV y todas las animaciones sin GPU.

Queda por comprobar visualmente en cliente la experiencia de combate, iluminación,
sonido y sincronización multijugador. Para la revisión manual: localiza ambos sitios
en terreno nuevo, entra con equipo completo, verifica los cuatro ataques y la
apertura del núcleo, guarda y recarga, derrota al Coloso y confirma sus recompensas.

## Créditos

Diseños originales aportados por el usuario: **holasoynuevo / CreativeMode.net**.
Se conservan licencias, procedencia y hashes en `third_party/venus_boss_sites/` y
`third_party/venus_colossus/`, y las licencias se incluyen entre los recursos del JAR.

- [Basalt Lifebunker](https://creativemode.net/mod/basalt-lifebunker-f2yaojrq)
- [Sulfurfire Broodlands](https://creativemode.net/mod/sulfurfire-broodlands-wrlm5742)
- [Sulfur Colossus](https://creativemode.net/mod/sulfur-colossus-3bbkryhu)

Los archivos originales se leen como datos; Space Fabric no incluye ni ejecuta
los inicializadores, dependencias o telemetría de los mods fuente.
