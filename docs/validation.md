# Resultado de validación de esta base

Verificado con JDK 25, Gradle 9.8.2, Loom 1.19.3, Minecraft 26.1,
Fabric Loader 0.20.1 y Fabric API 0.145.1+26.1.

- `./gradlew build --console=plain`: compilación Java, recursos, JAR y pruebas correctos.
- JUnit: **214 ejecutadas, 214 aprobadas, 0 omitidas**.
- `ExposureTest`: 29 casos de atmósfera, inmunidad y protección por sellado.
- `WorldgenCodecTest`: 149 casos con los codecs reales de Minecraft; nueve tipos
  de dimensión, nueve mundos con sus generadores y tres tipos de daño aceptados.
- `EnvironmentRulesTest`: 12 casos de velocidades solares, niebla y corrosión.
- Cielos y ciclos: las nueve timelines nativas producen distinta luz de día/noche
  y los cielos sin atmósfera conservan RGB negro.
- Mixins: el de clima se aplica a `ServerLevel` y los métodos gráficos coinciden
  con las firmas reales de 26.1; no se abrió un cliente para validar su renderizado.
- Cavernas: se ejecuta la feature real en un volumen de prueba, comprobando
  cámara, entrada, hielo y límites de escritura.
- `PlanetHazardsTest`: 10 casos de presión por altitud y exposición a lluvia ácida.
- La carga conjunta usa `RegistryDataLoader` con los recursos vanilla y del mod,
  incluidos biomas, features, carvers, tags, fluidos y ruido.
- `python3 tools/validate_resources.py`: 292 JSON y referencias internas coherentes.
- Comprobaciones de materiales: 24 loot tables y recetas aceptadas por los codecs;
  etiquetas de botas térmicas, componentes de armadura y requisitos de herramientas.
- Modificaciones reales de Fabric: vetas marcianas presentes y decoración orbital
  conservada por el generador vacío.
- Assets: modelos e inventario, traducciones y CRC/contenido de 47 texturas PNG validados,
  incluido el atlas de 512×512 importado de `space-rocket.bbmodel`.
  También se comprueban las rutas de texturas declaradas con `Identifier.fromNamespaceAndPath`
  en los renderers Java para detectar referencias al namespace de los mods fuente.
- Geología: extracción con netherita; recetas ensambladas de hierro y pigmentos;
  nueve features restringidas a sus biomas y hielo con techo opaco en ambas bandas polares.
- Transporte: 8 casos de reglas de vuelo y 6 de llegada: ocupación, obstáculos,
  borde, plataformas reutilizables, presión profunda y límite de búsqueda.
- Refinería: fórmulas inicial/planetaria con pilas reales, consumo al terminar,
  pausa de salida bloqueada, cambio de fórmula y filtrado de automatización.
- Persistencia: se guarda y restaura una BlockEntity real con inventario y progreso;
  tanque, destino y carga del cohete restaurados con sus métodos reales de NBT;
  combustible parcial del ítem empaquetado validado con `ItemStack.CODEC`.
- Recetas de cohete, refinería, celda vacía y plataforma realmente ensambladas
  desde sus JSON usando ingredientes vanilla.
- Martian Breathforge: `structure`, `template_pool`, `structure_set` y tag de bioma
  aceptados en la carga conjunta; el único bioma válido es Marte.
- Plantilla NBT original: `NbtIo` y `StructureTemplate` cargan 89 × 73 × 89, 22 materiales y
  30.608 bloques, comparados posición e índice de paleta con el archivo original.
- Los cráteres marcianos aparecen en la fase anterior a las estructuras de superficie.
- Regresión de `/locate`: `Structure.generate` real con tres semillas reproduce
  el inicio inválido de `size: 0` y obtiene una pieza válida con `size: 1`, usando
  el generador de ruido de Marte y la plantilla cargada por `StructureTemplateManager`.
  Esto comprueba generación de inicios, sin arrancar una partida ni ejecutar una
  búsqueda completa de `/locate` en un servidor de juego.
- Haven: sus registros cargan correctamente; 45.380 bloques y seis entidades
  persistentes se conservan en la plantilla nativa con posiciones iniciales despejadas.
- Haven también genera un inicio válido de una pieza con tres semillas. La plantilla
  de superficie mide 124 × 81 × 125 bajo sus rotaciones; la original conserva 124 × 84 × 125.
- Comerciantes: tipo de entidad, atributos y huevo funcional registrados; dos ofertas originales
  probadas con consumo real de materiales y serialización nativa de existencias.
- Gravedad del comerciante: 0.0304 en Marte sin acumulación; conservación de
  modificadores ajenos al volver a gravedad normal. Hipoxia/descompresión protegidas,
  con daño de combate y presión profunda todavía permitidos por la regla de protección.
- Gradle Wrapper y distribución: SHA-256 verificado con los checksums oficiales.
- JDK descargado para este entorno: SHA-256 verificado con el checksum oficial.

Artefacto generado: `build/libs/space-fabric-0.12.1.jar`.
Los informes de la ejecución están en `build/test-results/test/` y
`build/reports/tests/test/`; son salidas locales ignoradas por Git.

No se ejecutó un cliente gráfico, un servidor de juego ni una sesión multijugador.
Los codecs no demuestran que el terreno se genere correctamente durante una
exploración ni que el movimiento se sincronice en un cliente. El procedimiento
manual del README y de `docs/space-travel.md` cubre esos puntos y queda pendiente.
Tampoco se ha ejecutado un vuelo real ni un traslado con pasajeros en una sesión
de juego. La compilación comprueba el uso de las firmas 26.1; las pruebas del
planificador y de combustible no sustituyen esa verificación multijugador.
La adaptación visual del edificio al relieve marciano también queda pendiente;
la preparación de parcela despeja el terreno antes de colocar los vacíos del NBT.
La IA, la interfaz de comercio y la apariencia del comerciante requieren además
comprobación visual en una partida real. No se simula esa sesión mediante los tests de NBT.
Los nuevos mobs compilan con sus modelos y animaciones originales; su comportamiento,
renderizado y frecuencia efectiva de aparición requieren también esa prueba en juego.

El entorno utiliza un JDK local en `/workspace/.space-fabric-tools/jdk` y caché
Gradle fuera del checkout. Los scripts reutilizables del entorno activan esas
rutas y mantienen el proxy y la confianza TLS de la máquina. No se guardaron
credenciales ni se desactivó su verificación.

La configuración de red guardada como borrador añade los destinos oficiales de
Fabric y Mojang para desarrollo. Guardar ese borrador no publica el entorno ni
confirma que una máquina futura se haya restaurado o validado.

Comprobaciones añadidas en 0.10.0:

- Textura, icono y OGG originales comparados por SHA-256 con el manifiesto del JAR importado.
- Dos ofertas de amatista/cobre comprobadas con consumo de ambos ingredientes y stock persistente.
- Preparación real sobre picos y cañones; suelo continuo y límites del chunk respetados.
- `MarsSurfacePiece.postProcess` coloca el NBT real de Breathforge sobre roca: sus bloques
  se conservan y los huecos de habitación quedan como aire en el volumen VoxelWorld de prueba.
- Ambas piezas conservan plano, rotación y límites al guardar/cargar y moverse;
  sus parcelas quedan dentro del alcance de referencias de Minecraft.
- El muestreo de altura es acotado e incluye los bordes de la parcela.

Comprobaciones añadidas en 0.11.0:

- Tres tipos de fauna, atributos, huevos y cuatro materiales registrados en el bootstrap real.
- `SpawnPlacements` devuelve `ON_GROUND` y `MOTION_BLOCKING_NO_LEAVES` para cada especie;
  el Mixin de acceso al registro privado se aplica durante las pruebas.
- Tablas de bioma nativas: pesos y grupos exactos, presentes solo en Marte; ruido
  marciano con generación inicial de criaturas habilitada.
- Regla de hábitat: rechaza otra dimensión, otro bioma, suelo ajeno, agua, techo,
  cuevas y posiciones suspendidas; acepta suelo marciano exterior a ras del heightmap.
- PNG y OGG de los tres JAR fuente comparados por SHA-256 con el manifiesto importado.
- Recorte reproducible de pedestales: 28.147 bloques de Breathforge y 20.406 de Haven,
  sin alterar los bloques arquitectónicos por encima del recorte; seis residentes válidos.
- Generación nativa con tres semillas por estructura: origen del piso en el suelo sólido.
  Guardado/carga conserva la alineación nueva y la de piezas antiguas sin `FloorAligned`.
- `MarsSurfacePiece.postProcess` coloca ambas plantillas recortadas sobre roca en
  VoxelWorld, verificando pisos, huecos con regolito y habitaciones despejadas.

Cambios visuales de 0.12.0:

- Cohete: 48 cubos y 288 caras importados del archivo Blockbench del usuario.
- Atlas PNG 512×512 extraído sin modificaciones; UV por cara conservadas.
- Modelo de entidad ajustado uniformemente a 1,6 bloques de ancho y unos 3,28 de
  alto, apoyado en Y=0 dentro de la colisión existente de 1,6 × 3,5.
- El ítem de inventario usa las mismas 48 piezas y el mismo atlas mediante JSON vanilla.
- Compilación y validación de recursos correctas; renderizado en partida pendiente.

Corrección 0.12.1 tras la captura del usuario:

- Se elimina el contorno blanco forzado: en la sobrecarga corta de `submitModel`,
  el último entero representa `outlineColor`, no el tinte. Se usa la sobrecarga
  explícita con tinte -1 y contorno de `EntityRenderState`.
- El ítem utiliza `item/rocket`, descubierto por el atlas vanilla. El PNG se copia
  sin modificar a `textures/item/rocket.png`; el modelo colocado conserva su textura
  de entidad. La validación comprueba que ambos PNG coincidan con el original y
  que las referencias del modelo de ítem pertenezcan a las carpetas del atlas.
- El resultado gráfico de esta corrección aún requiere prueba dentro del juego.
