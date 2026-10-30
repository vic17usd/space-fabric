# Resultado de validación de esta base

Última entrega: **0.44.0**, 596 pruebas JUnit aprobadas, once comprobaciones por
CPU y 1.205 JSON válidos. Los apartados siguientes conservan el historial de
la base y sus ampliaciones; la validación del atlas planetario está al final del documento.

Verificado con JDK 25, Gradle 9.8.2, Loom 1.19.3, Minecraft 26.1,
Fabric Loader 0.20.1 y Fabric API 0.145.1+26.1.

- `./gradlew build --console=plain`: compilación Java, recursos, JAR y pruebas correctos.
- JUnit: **248 ejecutadas, 248 aprobadas, 0 omitidas**.
- `ExposureTest`: 29 casos de atmósfera, inmunidad y protección por sellado.
- `WorldgenCodecTest`: 171 casos con los codecs reales de Minecraft; nueve tipos
  de dimensión, nueve mundos con sus generadores y cuatro tipos de daño aceptados.
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
- `python3 tools/validate_resources.py`: 389 JSON y referencias internas coherentes.
- Comprobaciones de materiales: 24 loot tables y recetas aceptadas por los codecs;
  etiquetas de botas térmicas, componentes de armadura y requisitos de herramientas.
- Modificaciones reales de Fabric: vetas marcianas presentes y decoración orbital
  conservada por el generador vacío.
- Assets: modelos e inventario, traducciones y CRC/contenido de 60 texturas PNG validados,
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

Artefacto generado: `build/libs/space-fabric-0.16.0.jar`.
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

Corrección del cohete incluida en 0.13.0, tras confirmar el uso de un optimizador gráfico:

- El modelo colocado emite directamente las 288 caras con sus UV por vértice
  mediante `submitCustomGeometry`. No usa el renderizado de cubos de `ModelPart`
  ni `Cube.compile`, que puede sustituirse por una caché con UV estándar.
- Conserva geometría, escala, atlas, luz y orientación. El ítem mantiene su JSON
  y textura, confirmados visualmente por el usuario en 0.12.1.
- Compilación y recursos validados; la corrección del cohete colocado aún requiere
  confirmación en la instalación del usuario con su optimizador gráfico.

Comprobaciones añadidas en 0.13.0:

- La carga conjunta acepta el santuario y restringe su único bioma a Marte.
- `RandomSpreadStructurePlacement` nativo: 25 regiones positivas/negativas y tres
  puntos por región, con tres semillas, devuelven exactamente un candidato y
  20.000 bloques entre centros vecinos; separación 1249 elimina el desplazamiento aleatorio.
- Nueve inicios reales válidos: tres regiones por semilla, base Y=-60 y límites
  hasta Y=-13 con la cubierta. Guardado, carga, rotación y movimiento conservan el localizador.
- NBT nativo de 53×32×53: 89.888 posiciones explícitas, incluido aire; un jefe
  central y cuatro guardias persistentes, con volumen libre para sus cuerpos.
- Colocación real sobre roca y sobre un cañón vacío: interior excavado, cubierta
  sólida de 16 capas, sin escrituras fuera del chunk ni por encima de Y=-13.
- Registro de ambos mobs, atributos y huevos; ausencia de apariciones salvajes
  en todos los biomas. Recursos importados comparados por SHA-256.
- `./gradlew verifyRocketMesh`: emisor real de 1.152 vértices, 288 caras con UV
  comparadas con Blockbench, escala, color y luz. Usa las clases cliente de Minecraft
  sin GPU ni sesión de juego; no simula la ejecución de Sodium. Se ejecuta también con `build`.

La colocación de entidades en una partida, el combate, la barra de jefe y la
inspección visual con el optimizador del usuario siguen pendientes de prueba en juego.

Correcciones visuales de 0.13.1:

- El snapshot del cohete distingue primera persona dentro de su propia cabina
  del resto de observadores y modos de cámara. Solo en ese caso omite el exterior.
- Dos Mixins cliente marcan el snapshot del jugador montado y omiten su envío
  completo al renderizador: cuerpo, armadura y capas no sobresalen de la cabina.
  La marca vuelve a false cuando el jugador ya no ocupa un cohete.
- Los puntos de inyección `EntityRenderDispatcher.extractEntity` y `submit` se
  comprueban contra el bytecode real de Minecraft 26.1 en la prueba existente de
  descriptores. No equivale a ejecutar los Mixins ni a comprobar la cámara en juego.
- Se conservan las 226 pruebas JUnit y la verificación de 1.152 vértices del cohete;
  la inspección visual en primera/tercera persona y multijugador sigue pendiente.

Cabina interior de 0.14.0:

- `verifyRocketCockpit` ejecuta el emisor real de geometría: 772 vértices con
  coordenadas y normales finitas, UV válidas, materiales opacos, luz y overlay conservados.
- Una prueba de intersección contra las caras emitidas confirma nueve rayos
  libres por el visor, y seis direcciones cerradas por paredes, marco, techo o suelo.
  Detectaría tanto una cabina vacía como una carcasa que bloquee la ventana.
- Se conserva `verifyRocketMesh` para los 1.152 vértices del exterior, junto con
  las 226 pruebas JUnit. La selección del modo de cámara, la orientación al
  montar y el resultado visual durante el vuelo requieren prueba en partida.
- El interior reutiliza el atlas importado: no se añaden texturas ni cambia el
  modelo del ítem. La validación sigue cubriendo 303 JSON y 52 PNG.

Fauna de Venus, versión 0.15.0:

- Cuatro mobs y sus huevos cargados en el bootstrap nativo; pruebas de presencia
  exclusiva en el bioma de Venus. Modelos y animaciones importados compilan.
- Base VenusMob para presión, hipoxia, veneno y calor; roca caliente sin lentitud.
- Acidstone cambia de WaterAnimal a navegación terrestre; Sulfurwing obtiene
  FlyingPathNavigation, FlyingMoveControl, sustentación y protección contra caídas.
- Pesos/grupos: Acidstone 18/1–3, Lavafang 10/1–2, Emberwalker 16/1–3,
  Sulfurwing 4/1. Se habilita generación inicial de criaturas en Venus.
- Frecuencia efectiva, movimiento y renderizado dentro del juego siguen pendientes.

Recursos actuales: 320 JSON y 60 PNG, validados con el comprobador de recursos.

Presión y equipo de 0.16.0:

- Nueve perfiles: presión, O₂ y rangos térmicos; 1 bar de referencia en gigantes.
- Presión y temperatura crecientes al descender; la cabina no protege por encima de 100 bar.
- Doce casos puros de referencia, tiempos 1:72, dosis acumulada y fallos de trajes.
- Dieciséis piezas de equipo: componentes, lore, tags, conjuntos completos, incompletos
  y mezclados; las 16 recetas se ensamblan con los ingredientes reales de sus JSON.
- Los cuatro DamageType cargan; se verifica que sus tags atraviesen armadura,
  escudo, resistencia, encantamientos, cooldown e invulnerabilidad temporal.
- Venus carga sin precipitación de superficie. El equipo apropiado resiste ácido.
- 248 pruebas JUnit aprobadas; se conservan las dos comprobaciones de mallas del cohete.
- Pendiente: observación del daño, interfaz, protección y sincronización en partida real.

Las cifras físicas son aproximadas; los tiempos letales y las capacidades de los
trajes son calibración de juego y equipo futurista, no resultados clínicos.

Colonias de Marte y Venus, versión 0.29.0:

- `./gradlew build --console=plain`: 417 pruebas JUnit, sin fallos, errores ni
  pruebas omitidas, y ocho comprobaciones nativas de recursos/renderizado por CPU.
- `python3 tools/validate_resources.py`: 867 JSON válidos. La regeneración de los
  recursos coloniales produce los mismos archivos sin modificar los anteriores.
- Se comprueban habitaciones cerradas, puertas abiertas, roturas de piso/techo,
  zonas descargadas y límites del escaneo. Una mesa de trabajo dentro de la
  envolvente certificada de Venus no invalida su cierre exterior.
- Las reservas requieren oxígeno, energía y, en Venus, refrigeración. Las pruebas
  cubren consumo, pérdida de oxígeno por fuga, límites y carga de reservas.
- Las 13 fórmulas procesan entradas y salidas de forma atómica, devuelven baterías
  o recipientes y aparecen en el visor. Las 15 recetas y los cinco avances se
  decodifican con los registros nativos de Minecraft 26.1.
- Se verifica el guardado y la carga NBT de las cinco máquinas, el rechazo de
  inserciones automáticas en salidas y la receta de combustible de hidrógeno con
  devolución de bombona. El hielo subterráneo se asigna solamente a Marte.
- La ventana usa el material translúcido nativo de 26.1; se comprueban su alfa,
  los modelos coloniales y las ocho variantes de la puerta sin usar una GPU.

No se ha ejecutado una partida interactiva ni una sesión multijugador para esta
entrega. La inspección visual, el recorrido por esclusas y la automatización con
tolvas requieren comprobación en juego. Las pruebas no sustituyen esa observación.

Suelo de Venus, versión 0.30.0:

- Compilación completa correcta: 422 pruebas JUnit sin fallos, errores ni omisiones,
  y nueve comprobaciones de recursos/renderizado por CPU. 896 JSON válidos.
- Los cuatro PNG importados coinciden byte por byte con los originales. La
  regeneración desde las fuentes guardadas produce los mismos recursos.
- Los modelos de las tres rocas, las 21 piezas del afloramiento y su modelo
  completo de inventario se decodifican con el cliente nativo 26.1, sin GPU.
- Colocación real sobre un volumen de prueba en 16 semillas: 21 estados distintos,
  entre una y tres capas sobre el terreno, sin escrituras fuera del chunk.
- Se rechazan huecos en la base, lava, obstáculos en el volumen y chunks
  descargados. Se comprueban cajas de colisión, herramienta mínima, loot y
  asignación de la característica exclusivamente al bioma de Venus.
- Los bloques existentes mantienen sus IDs y comportamientos. Los afloramientos
  naturales requieren chunks nuevos. Inspección visual y multijugador pendientes.

Nombres de estructuras, versión 0.30.1:

- Compilación correcta: 422 pruebas JUnit y nueve comprobaciones por CPU;
  901 JSON válidos. Los cinco alias apuntan a las estructuras existentes.
- El JAR contiene el catálogo `/estructuras`, las traducciones y las etiquetas
  para `/locate structure`. Los JSON de registro de estructuras permanecen
  idénticos a 0.30.0; no cambian generación, habitantes ni plantillas.
- La presentación del chat y sus enlaces requiere comprobación en una partida.

Interiores de hábitats, versión 0.31.0:

- `./gradlew build --console=plain`: 429 pruebas JUnit aprobadas, sin errores ni
  omisiones, y diez comprobaciones por CPU. 901 JSON válidos.
- Se distinguen fallos por oxígeno, energía y refrigerante. Las lecturas reguladas
  muestran objetivos de 101,3 kPa y 22 °C; todos los estados de fallo los retiran.
- Paquetes completos, incrementales y de salida conservan sus campos y geometría.
  Se rechazan enumeraciones y tamaños de geometría inválidos antes de leer posiciones.
- El menú nativo conserva nueve slots, muestra las reservas sincronizadas, filtra
  entradas y permite transferir suministros/productos con Shift sin duplicarlos.
- `verifyHabitatView` usa el estado real del cliente y `FogData`: comprueba cámara
  exterior, jugador exterior, fuga local, zona descargada, cambio de dimensión o
  controlador, estados de fallo, mensajes caducados y limpieza de sesión.
- El ajuste interior conserva los campos nativos de distancia de renderizado,
  cielo y nubes. Agua/lava y efectos de ceguera/oscuridad se excluyen en la ruta
  del Mixin. Estos caminos no se han observado en una partida interactiva.
- La geometría cambia solamente al cambiar sala/cierre y se valida localmente
  una vez por tick; no se solicitan acciones de protección al cliente.

Pendientes: aspecto del panel/HUD, transición de niebla con ventanas, recorrido
por esclusas y sincronización multijugador en una partida real. Los objetivos
regulados no equivalen a una simulación termodinámica ni a presión volumétrica.

Paisajes de Venus, versión 0.32.0:

- Compilación completa: 436 pruebas JUnit y diez comprobaciones por CPU, sin
  fallos. 949 JSON válidos.
- La fuente multi_noise selecciona los tres biomas con tres semillas. Todos
  mantienen minerales, seis especies, límites de grupos y ausencia de precipitación.
- Caldera: generación nativa de inicio válido, 56 piezas sin parcela rectangular,
  serialización de la marca Natural y colocación de sus 295.678 posiciones
  por chunks, comparadas con sus plantillas, despeje del terreno que las enterraría
  y apoyo sobre terreno más bajo. Se conservan las pruebas
  de los dos asentamientos, sus apoyos y plantillas heredadas.
- Fumarolas, lagunas y grietas se colocan sobre terreno de prueba con escrituras
  limitadas al chunk. Lagunas/grietas rechazan relieve excesivo sin cambios parciales.
- Se verifican registros, tablas de loot, herramientas, altura del depósito ácido,
  colisión vacía y admisión de las rocas nuevas como suelo natural de aterrizaje.
- Los nueve modelos nuevos se decodifican con el cliente nativo por CPU. Los PNG
  y los modelos proceden de los JAR; la adaptación conserva los primeros sin cambios.

Pendientes de partida real: aspecto del volcán y las transiciones de bioma,
frecuencia efectiva, daño de contacto, partículas, recorrido de fauna y multijugador.
Las comprobaciones no equivalen a observar el juego con GPU u optimizadores.

Corrosión y mantenimiento, versión 0.33.0:

- Compilación completa: 451 pruebas JUnit sin fallos y diez comprobaciones por
  CPU. 963 JSON válidos. Cuatro recetas nuevas y un avance nativo.
- Modelo de desgaste: tiempo cargado, fases, daño absorbido por capa, reducción
  por filtro, saturación, límites, reparación reversible y fracciones de segundo
  conservadas al mover la máquina. Un recinto abierto o una cara exterior libre
  impiden el aislamiento; las pruebas también invalidan un cierre modificado.
- Las seis máquinas guardan inventario y mantenimiento en NBT. La operación
  nativa CopyCustomDataFunction conserva solamente Maintenance en el objeto,
  sin copiar Items. Se decodifican los seis loot tables con los registros reales.
- La refinería conserva un lote parcial al guardar/cargar, permanece detenida
  durante la avería y termina el lote después de reparar a velocidad reducida.
- Se sincronizan estado de protección e integridad mediante datos nativos de
  menú y telemetría del hábitat. Las pruebas cubren slots, Shift y paquetes con
  campos nuevos. La condición de controlador averiado retira la vista despejada
  en la comprobación del cliente existente.

Pendientes en partida real: uso de los materiales con clic derecho, recogida y
colocación, recepción de avisos, lectura visual de paneles y multijugador. Los
ensayos por CPU no sustituyen la observación de esos recorridos.

Industria exclusiva de Venus, versión 0.34.0:

- Compilación completa: **479 pruebas JUnit, sin fallos, errores ni omisiones**,
  diez comprobaciones por CPU y **1.024 JSON** válidos con referencias internas.
- Cuatro máquinas, doce recetas y cuatro avances nuevos se interpretan con
  registros/codecs nativos de 26.1. Las siete fórmulas industriales usan el mismo
  motor que las trece anteriores, con entradas y salidas atómicas.
- El procesamiento del colector se pausa con planeta incorrecto, toma bloqueada
  o salida ocupada; conserva suministros y progreso. El horno requiere calor,
  guarda un lote parcial mediante NBT de una BlockEntity real y lo termina al
  restaurar. El servidor obtiene las condiciones del mundo y su heightmap;
  estas pruebas inyectan una lectura ambiental, sin construir un ServerLevel.
- Las siete fórmulas conservan la cantidad de recipientes y devuelven la batería
  descargada. La receta alternativa de cohete realmente se ensambla y devuelve
  ambas bombonas de nitrógeno vacías. Se mantiene la receta original.
- La herrería nativa produce las cuatro piezas reforzadas con doble durabilidad
  y conserva daño, nombre y encantamientos. Tags, slots independientes y mezcla
  de piezas venusianas originales/reforzadas mantienen la categoría Venus.
- La mejora de carcasa reduce el ataque previo al filtro, evita la reaplicación,
  conserva el ritmo de saturación y no repara daño. Los guardados anteriores sin
  el campo nuevo siguen cargando. Las diez máquinas conservan mantenimiento y
  refuerzo en NBT y en la copia nativa al objeto, sin copiar su inventario.
- El visor conserva las entradas vacías y lista todos los productos del proceso.
  Los cinco modelos de bloque nuevos se decodifican con el cliente nativo por
  CPU, con texturas presentes; el validador comprueba modelos, PNG y traducciones.

Pendientes en partida real: toma bajo cristal/hojas/techos, colocación del horno,
recorrido completo de producción, interacción y aspecto de menús/objetos,
uso de la herrería con otros mods y sincronización multijugador. No se abrió un
cliente con GPU ni se ejecutó un mundo interactivo para esta entrega.


Construcciones de Venus, versión 0.35.0:

- Compilación completa: **483 pruebas JUnit**, cero fallos, errores u omisiones,
  diez comprobaciones por CPU y **1.037 JSON** válidos.
- Se auditan catorce JAR: cuatro diseños nuevos seleccionados, dos torres
  aplazadas y ocho diseños ya integrados. Se conservan fuentes, hashes y licencias.
- Cuatro registros de estructura, cuatro tags de bioma, cuatro alias españoles
  y un conjunto con exclusión de candidatos cercanos a asentamientos anteriores.
- Las 113 plantillas suman 72.020 bloques. El cliente/servidor nativo carga sus
  paletas y tamaños; las pruebas detectan posiciones duplicadas, materiales
  inapropiados y componentes desconectadas del suelo.
- La prueba de generación existente ahora incluye los siete diseños de Venus.
  Obtiene un inicio válido en sus biomas permitidos, conserva cota y rotación al
  serializar, y ejecuta la colocación real por chunks sobre terreno simulado,
  comparando cada bloque con la plantilla y verificando apoyo de las bases.
- Se conservan los registros y plantillas de las versiones anteriores.

Pendientes en partida real: lectura visual de la arquitectura y las parcelas,
frecuencia efectiva, búsqueda completa de /locate y multijugador. No se abrió un
cliente con GPU. Los nuevos edificios requieren chunks aún no generados.


Consola, recuperación y rover, versión 0.36.0:

- Compilación completa: **498 pruebas JUnit**, cero fallos, errores u omisiones,
  once comprobaciones por CPU y **1.061 JSON** válidos.
- Los marcadores NBT de la fundición nueva crean una consola natural y tres
  BlockEntities reales con mantenimiento agotado. Las pruebas comprueban pausa
  durante avería, reparación, filtro, producción y contador persistente de lotes.
- Se conservan las plantillas `ruins_v1`. Los inicios nuevos usan `ruins_v2`, con
  cuatro bloques funcionales añadidos y lava bajo el generador. La generación y
  colocación por chunks siguen comprobándose con el motor nativo.
- El registro de recompensas conserva la activación tras serializar/cargar y al
  volver a registrar la misma ubicación. Se rechazan activaciones repetidas y
  consolas sin instalación; cada ubicación natural conserva su propio estado.
- El rover guarda casco, tres reservas e inventario mediante NBT nativo. El
  objeto recogido conserva reservas y daño con ItemStack.CODEC; la carga no se
  copia dentro del objeto. Se valida la fábrica real de colocación y se rechazan
  ítems de otro tipo.
- La recarga devuelve recipientes, rechaza cargas parciales y salidas bloqueadas.
  Se prueban límites de energía/oxígeno/refrigerante, consumo ocupado y pausa
  vacía, frenado, aceleración, marcha atrás y límites térmicos/de presión.
- El menú filtra suministros, separa carga y envases, conserva sus 50 slots y
  permite Shift sin duplicación. El asiento conserva su posición al rotar con
  el vehículo mediante el contrato nativo de anclajes.
- Los atlas PNG de ambos modelos coinciden byte por byte con los originales.
  La comprobación nueva interpreta los modelos nativos de consola e inventario,
  emite 3.624 vértices del rover, comprueba seis ruedas en tres ángulos y nueve
  rayos de visión libres por el visor con suelo/techo/paredes interiores presentes.

Pendientes en partida real: recuperación de principio a fin con inventarios de
jugadores, entrega con inventario lleno, sustitución de consola, conducción en
pendientes, colisiones, confort/interpolación de cámara, tecla V, aspecto con GPU
u optimizadores y sincronización multijugador. Las pruebas de registro y menús
no sustituyen esos recorridos interactivos.


Automatización industrial, versión 0.37.0:

- Compilación completa: **514 pruebas JUnit**, cero fallos, errores u omisiones,
  once comprobaciones por CPU y **1.136 JSON** válidos.
- Diez bloques con recetas y loot nativos, tres canales, depósitos con tipo y
  cantidades persistentes, válvulas axiales y una interfaz para las máquinas
  coloniales. Se comprueban etiquetas, herramientas y modelos de seis brazos.
- Los pasamuros sustituyen paneles certificados sin un estado intermedio de
  aire. La prueba de cierre usa los estados reales y la función de sustitución:
  cerrar o alimentar la válvula con redstone mantiene el recinto; sustituirla
  por una tubería estrecha o aire invalida inmediatamente el cierre guardado.
- El grafo se prueba con ramas y ciclos, depósitos alcanzados por caminos
  repetidos, tipos incompatibles incluso vacíos, válvulas cerradas, canales
  distintos, límites de 128 nodos y áreas descargadas que rechazan toda lectura.
  Una transferencia fallida no cambia las cantidades.
- El presupuesto de 64 interfaces rota por grupos y atiende primero el soporte
  vital seleccionado. La prueba nativa de escasez entrega la única batería
  disponible al controlador antes que al refrigerador industrial.
- Un ciclo con BlockEntities reales alimenta un electrolizador desde depósitos,
  procesa su receta, almacena oxígeno/hidrógeno y abastece soporte vital venusiano.
  Conserva cuatro bombonas y dos baterías físicas, con los consumos de agua y
  energía esperados. Tener suministros nunca concede protección sin sala válida.
- Los depósitos convierten cubos, bombonas y baterías con devolución de sus
  recipientes. Capacidad, contenido incompatible, salidas bloqueadas y envases
  faltantes impiden consumos indebidos. Los productos terminados esperan dentro
  de la máquina mientras su depósito no tenga espacio.
- El NBT nativo conserva contenido, filtros y slots. La función de loot real
  copia solamente TransportTank al objeto: el inventario no se duplica. Los
  menús filtran entradas/salidas, sincronizan cantidades y permiten Shift.

Pendientes en partida real: sustitución de panel con devolución al jugador,
montaje completo de una fábrica/base, controles de válvulas y redstone, aspecto
con GPU, descarga/recarga de chunks durante una sesión y multijugador. Los
ensayos por CPU usan un mundo de topología de prueba; no arrancan ServerLevel
ni equivalen a una sesión interactiva con jugadores.


Refrigeración industrial, versión 0.38.0:

- `./gradlew build --console=plain`: **525 pruebas JUnit**, cero fallos, errores
  u omisiones; once comprobaciones de modelos/renderizado por CPU aprobadas.
- `python3 tools/validate_resources.py`: **1.142 JSON** válidos, referencias de
  modelos, PNG, loot y traducciones coherentes.
- Nuevo bloque de intercambiador registrado en el tipo de entidad colonial,
  inventario creativo, receta, avance, visor y etiquetas de herramienta/cerramiento.
- Balance térmico por segundo: generación solo si hubo trabajo, disipación básica,
  agrupación mediante la geometría de habitaciones verificadas y destinos adyacentes.
  El servicio usa el registro de entidades cargadas y `getChunkNow`; no genera chunks.
- Pruebas nuevas: umbrales e histéresis, saturación de entradas, reserva de frío,
  salida bloqueada, parada sin consumo de ingredientes, recuperación, presupuesto
  compartido sin doble débito, avería del intercambiador y ausencia de destino.
- Ciclo nativo probado: depósito de agua → interfaz → unidad de refrigeración →
  depósito de refrigerante → interfaz → intercambiador → retorno al depósito de
  agua; conserva bombonas y baterías, y consume energía de ambas operaciones.
- Guardado/carga NBT de calor, disparo térmico y frío; copia escalar de datos de
  minería y restauración del objeto sin copiar inventarios. Menús y telemetría
  incluyen lecturas térmicas con límites de tamaño.
- La parada térmica afecta a los procesadores coloniales de Venus, exceptuando
  el intercambiador para permitir la recuperación. La protección de hábitat sigue
  dependiendo de la habitación, sus suministros y el mantenimiento.

No se ha ejecutado una partida gráfica ni un servidor multijugador. La selección
espacial de propietarios, los mensajes de transición, el montaje físico completo,
los controles de inventario y la apariencia necesitan también comprobación en juego;
las pruebas con el presupuesto compartido inyectan explícitamente la pertenencia
al recinto. Los valores térmicos son unidades de juego, no temperaturas físicas.


Ampliación de la colonia de Venus, versión 0.39.0:

- `./gradlew build --console=plain`: **541 pruebas JUnit**, cero fallos, errores
  u omisiones. Once comprobaciones de modelos y renderizado por CPU aprobadas.
- `python3 tools/validate_resources.py`: **1.186 JSON** válidos, referencias de
  modelos, texturas, tablas de loot y traducciones coherentes.
- Invernadero: receta de trigo y algas, dependencia de recinto regulado y luz,
  pausa sin consumo y conservación de semillas/recipientes. El contexto de recinto
  e iluminación se inyecta en las pruebas de producción; no es una partida real.
- Recuperador: gasto único de 4.800 unidades de respiración compartidas, filtros,
  salida bloqueada, persistencia de residuos, límite de recuperación y ausencia
  de producción de residuos cuando el soporte vital falla.
- Estación: apagada de fábrica, servicio mediante recipientes y reservas nativas
  del rover, conservación de carga/componentes, reparación, salidas bloqueadas,
  integración con las redes y persistencia del estado/repuestos. La selección
  espacial del vehículo aparcado y las interacciones necesitan comprobación en juego.
- Aerostato: tipo de entidad y estado empaquetado, control vertical acotado,
  presión/temperatura por altura sin oxígeno respirable. Emisión de vértices
  nativos, límites de la caja de colisión, nueve rayos libres por el visor y
  superficies interiores opacas comprobados por CPU. Vuelo no observado con GPU.
- Expediciones: registro persistente de premio por UUID/tipo, sin repetición tras
  recargar el ledger; recetas, estructura, paleta y entidad de consola naturales.
  Generación nativa de laboratorio/refugio/estación en semillas 42, 1729 y 8128;
  la estación atmosférica produce una pieza con cota inferior Y=300. El pool
  jigsaw usa tamaño 1 y altura inicial 301 para compensar su desplazamiento nativo.
- Dos pruebas largas de cuatro horas simuladas: una avanza LifeSupport con dos
  ocupantes, cuentas exactas de consumo y restauraciones periódicas; otra ejecuta
  soporte vital, recuperador, algas e interfaces sobre depósitos reales de la
  capa de transporte. Esta última conserva seis bombonas y tres baterías, termina
  cinco ciclos de recuperación/fotosíntesis y mantiene suministros disponibles.
  Ambas pruebas reponen recursos desde sus fuentes preparadas; no equivalen a
  cuatro horas ejecutando todos los sistemas del mundo ni certifican autonomía
  con calor, corrosión, entidades y chunks en una partida.

No se ha realizado una sesión gráfica ni multijugador. Se incluye una lista
reproducible de comprobaciones pendientes en `docs/venus-colony-playtest.md`.
No se afirma haber validado entrada de jugador, vuelo, mensajes, recompensas por
clic, toda la instalación física o rendimiento de una colonia real con GPU.


Navegación y operaciones de Venus, versión 0.40.0:

- `./gradlew cleanTest build --console=plain`: **548 pruebas JUnit**, cero fallos,
  errores u omisiones, y once comprobaciones por CPU. Se ejecutó secuencialmente
  tras detectar un conflicto de archivos temporales entre comprobaciones solapadas.
- `python3 tools/validate_resources.py`: **1.194 JSON** válidos y referencias
  internas, PNG, modelos y traducciones coherentes.
- Navegador: componente de libro nativo, receta, lista NBT de hasta 32 puntos,
  dimensiones separadas, deduplicación, persistencia, selección sin reordenar el
  historial y descubrimiento sin cambiar el destino elegido. Rumbos cardinales
  y cálculo de autonomía local comprobados con los métodos usados por el servidor.
- Proyectos adicionales: ampliación compatible del ledger a cinco recompensas,
  gasto único y persistencia de las dos nuevas entradas por UUID. La obtención y
  entrega por interacción dependen de jugador/posición/propietario y no se han
  observado en un servidor vivo; las pruebas del ledger no equivalen a ese recorrido.
- Revisión del camino nativo del libro: el servidor admite el componente
  WRITTEN_BOOK_CONTENT, se difunde el inventario antes de abrirlo y la interacción
  de máquina enruta el navegador antes de abrir el menú normal del controlador.
- Renderizado: el comprobador del aerostato también emite las lentes delanteras
  y verifica límites de la malla. Son emisivas; no generan luz dinámica en el mundo.
- El indicador de vuelo mide desplazamiento vertical observado por tick de cliente.
  Sonidos y emisiones locales consultan posiciones cargadas. El objetivo de hábitat
  de fauna pacífica conserva prioridad inferior a sus comportamientos existentes.

Siguen pendientes las comprobaciones gráficas, de sonidos, comportamiento completo
de fauna, vuelo y multijugador de `docs/venus-colony-playtest.md`. No se afirma haber
certificado rendimiento de una colonia real ni cuatro horas de mundo ejecutándose;
las dos pruebas largas previas son simulaciones de componentes/recursos.

Progresión e interiores de Venus, versión 0.41.0:

- `./gradlew test --console=plain`: **571 pruebas JUnit**, cero fallos, errores
  u omisiones. `./gradlew build --console=plain` completa compilación, JAR y las
  **once comprobaciones por CPU** de modelos, recursos y cámaras.
- `python3 tools/validate_resources.py`: **1.205 JSON** válidos y referencias
  internas coherentes. El esquema SVG de interiores también es XML válido.
- 23 casos nuevos: cálculo de reservas contrastado con 900 segundos de soporte
  real de componentes para dos ocupantes; límites y redondeo; 100 ciclos
  geotérmicos recirculando una bombona y una batería física, con extracción de
  energía producida; ausencia de lava y retorno bloqueado; compuesto con hierro
  común guardado a mitad de lote y su presencia en el visor.
- Progreso personal: observaciones fuera de orden, llegada única, separación por
  UUID, persistencia y rechazo de máscaras inválidas. Los siete avances usan el
  codec nativo, criterio imposible concedido por el servidor y ningún premio de
  objetos. Receta de navegador fabricable antes de viajar y desbloqueo válidos.
- Las tres plantillas contienen estados nativos válidos, mitades de puerta,
  botones y cofres con botín limitado. El escáner real certifica su sala, conserva
  el cierre al abrir solo la puerta exterior y detecta exposición al abrir ambas.
  Se comprueba pasillo central libre, cubierta de aterrizaje despejada de 7×7×8,
  ausencia de camas explosivas y controladores
  inicialmente sin O₂, energía ni agua. Las tres tablas de cofre decodifican con
  el codec nativo; el botín no se ha abierto dentro de una partida.
- Se conservan las pruebas de generación en tres semillas de las instalaciones,
  expediciones y recursos de cuatro horas de la entrega anterior. La simulación
  de los 100 ciclos nuevos aísla inventarios y red: no es una simulación de calor,
  corrosión o un mundo servidor completo.
- No se ha ejecutado cliente gráfico ni sesión multijugador. El clic del chat,
  comandos reales con jugadores, páginas, avances, botones/redstone, decoración,
  recuperación de equipos y ritmo de supervivencia quedan en la lista manual
  actualizada, puntos 14–18. No se atribuye a esta validación una partida observada.

[Balance, guía e interiores](venus-progression.md) ·
[Comprobaciones pendientes en juego](venus-colony-playtest.md).

Guía de Venus en pantalla, versión 0.42.0:

- Compilación completa con `./gradlew build --console=plain`: 574 pruebas JUnit
  y once comprobaciones por CPU. Se mantienen los 1.205 JSON válidos.
- Tres pruebas nuevas verifican el objetivo pendiente cuando hay etapas posteriores
  completadas, limpieza/caducidad de estados, actualización y los 128 valores
  válidos del paquete nativo, rechazando bits desconocidos y máscaras negativas.
- El servidor sincroniza marcas personales de la guía existente, sin canal para
  que el cliente conceda objetivos. Se conserva el registro guardado de 0.41.
- Indicador en la esquina superior izquierda con texto ajustado al ancho, acción
  actual, siguiente paso y contador. El renderer no dibuja paneles de fondo.
  No requiere llevar un objeto. El aerostato se dibuja debajo; el indicador se
  oculta durante el vuelo del cohete, las pantallas, F1 y fuera de Venus.
- No se ha abierto un cliente gráfico. La composición final del HUD, los idiomas,
  escalas de interfaz y convivencia con otros mods necesitan la comprobación
  manual 19 de `docs/venus-colony-playtest.md`. Las pruebas de paquetes y estado
  no equivalen a una observación visual ni una sesión multijugador.

Ajuste visual del indicador, versión 0.42.1:

- Encabezado blanco, separador fino, círculo cian hueco y un objetivo breve con
  texto al 85 %. El fondo sigue transparente. Se mantiene el estado y protocolo
  de progreso de 0.42; no se reinician partidas ni se alteran recompensas.
- Compilación y comprobaciones de recursos/modelos por CPU; 574 pruebas previas
  de reglas, registros, persistencia y red. No se añaden pruebas que reproduzcan
  posiciones decorativas del renderer.
- No se ha observado un cliente gráfico: la comparación visual dentro de Minecraft
  con la referencia, las escalas GUI y el texto reducido siguen pendientes.

Apoyo de estructuras superficiales de Marte, versión 0.43.0:

- `./gradlew build --console=plain`: **582 pruebas JUnit** sin fallos, errores ni
  omisiones, y once comprobaciones por CPU. Se mantienen **1.205 JSON** válidos.
- La Base de Marte y el Pueblo de Marte usan una cota del terreno predominante:
  el intervalo de alturas más compacto que reúne al menos el 75 % de muestras.
  Se rechazan pendientes extensas y se evita que un pico aislado eleve la parcela
  o que una grieta aislada hunda el edificio. La tolerancia distribuida es 16.
- Ocho casos nuevos: seis pruebas de alturas (pico 50 bloques más alto, fisuras
  profundas, bordes de fisura, pendientes, cota limitada y presupuesto de consultas)
  y dos pruebas de preparación real de márgenes sobre terreno alto/bajo. Se verifica
  continuidad, apoyo de columnas y ausencia de escrituras fuera del chunk.
- Las pruebas nativas existentes se actualizan para comprobar el nuevo criterio:
  ambas estructuras encuentran emplazamientos válidos en semillas 0, 42 y 8675309,
  con búsqueda acotada. Las plantillas reales se colocan después de limpiar sus
  interiores y mantienen su suelo y arquitectura.
- Origen, rotación, plano y transición persisten en NBT. Los guardados anteriores
  sin TerrainBlend conservan su comportamiento. No se desplazan edificios ya
  colocados. El santuario subterráneo y sus pruebas siguen sin cambios.
- La generación se ha verificado mediante APIs nativas y volúmenes de prueba;
  no se ha observado un cliente gráfico ni cronometrado /locate en un servidor
  de juego completo. La inspección visual en chunks nuevos continúa pendiente.

[Configuración y comandos de comprobación](martian-surface-plots.md).

Atlas y minimapa planetarios, versión 0.44.0:

- `./gradlew build --console=plain`: **596 pruebas JUnit**, cero fallos, errores
  u omisiones; once comprobaciones por CPU de recursos, modelos y vistas existentes.
  `python3 tools/validate_resources.py`: **1.205 JSON** válidos.
- Doce pruebas nuevas de archivos y modelo: separación entre planetas, mundos y
  jugadores; nueve cuerpos y Overworld; coordenadas negativas y límites de región;
  terreno desconocido; edición, ocultación, selección y borrado; límite de puntos;
  claves privadas de almacenamiento; metadatos/regiones dañados preservados;
  posición anterior, guardado y lectura con presupuestos; validación de coordenadas.
- Dos pruebas nativas verifican el codec de SavedData del UUID del mundo y su
  paquete de 16 bytes. La conexión del cliente y el envío en JOIN requieren todavía
  comprobación en partida: no se presenta una conexión real como resultado de JUnit.
- El cliente muestrea solo chunks ya cargados, usando getChunk con carga desactivada.
  Las lecturas de altura y color se limitan a dos chunks por tick. La superficie se
  conserva en celdas de cuatro bloques y regiones comprimidas con caché limitada.
  El guardado usa archivos temporales y sustitución atómica cuando está disponible.
- Compilan las pantallas nativas, teclas reasignables y texturas dinámicas de 26.1.
  Se verificó la API nativa para ARGB y el escalado de la textura. Las pruebas de
  modelos por CPU anteriores no ejecutan el renderer GPU del nuevo atlas.
- No se ha ejecutado cliente gráfico ni multijugador. Minimapa, zoom, arrastre,
  marcadores, selector, importación del navegador, colores, teclas y reconexión
  deben verificarse con la lista en docs/planetary-atlas.md. No se afirma que se
  haya observado en juego el muestreo o la composición de la nueva interfaz.

[Controles, guardado y pruebas en partida](planetary-atlas.md).

Minimapa circular, versión 0.44.1:

- `./gradlew build --console=plain`: **599 pruebas JUnit**, cero fallos, errores
  u omisiones; once comprobaciones por CPU existentes. **1.205 JSON** válidos.
- Tres pruebas nuevas comprueban esquinas transparentes, recorte circular y marco,
  proyección radial del destino en las ocho direcciones, símbolos acotados y
  orientación de la flecha con el yaw de Minecraft.
- El minimapa usa una textura dinámica de 104×104 con marco y máscara; los iconos
  tienen contorno oscuro y se recortan dentro del círculo. El mapa grande mantiene
  su formato rectangular y comparte los símbolos de estrella, rombo y flecha.
- Se inspeccionó una previsualización por CPU de la geometría y los símbolos sobre
  terreno sintético; no es una captura de Minecraft ni valida la composición GPU.
- Se mantienen el formato de guardado y los controles. Falta revisar en cliente
  gráfico el marco, las escalas de interfaz, los marcadores y Sodium.

Minimapa compacto, versión 0.44.2:

- Minimapa de 76×76 píxeles de interfaz, antes 104×104, anclado al borde inferior
  derecho con margen horizontal de ocho píxeles. Coordenadas y título se ajustan
  al nuevo ancho; la flecha del personaje usa escala 1/1,6 en ambas vistas.
- `./gradlew build --console=plain`: 599 pruebas JUnit sin fallos, errores
  ni omisiones; once comprobaciones de recursos y modelos por CPU.
- 1.205 JSON válidos; JAR y versión comprobados. Ajuste visual pendiente de
  observar en cliente gráfico y con Sodium. No cambia el formato de guardado.

Prospección y minería industrial, versión 0.45.0:

- `./gradlew build --console=plain`: **617 pruebas JUnit**, cero fallos, errores
  u omisiones. Once comprobaciones por CPU; la de colonia incluye la nueva textura
  y el modelo nativo de la perforadora. **1.217 JSON** válidos.
- Dieciocho casos añadidos: dos volúmenes completos sin celdas repetidas; extracción
  de un bloque real y retorno de recipientes; salidas bloqueadas y calor; pausa
  sin leer celdas descargadas; denegación y revalidación tras callbacks; sustitución
  fallida; guardado del propietario, cursor y extracción parcial; dimensiones y
  producción sin mundo rechazadas; búsqueda acotada y agotamiento; rotura de broca;
  puertos reales de energía/frío/agua; puntos del escáner en ItemStack; tres recetas,
  avance y loot nativos; consultas del visor para los tres minerales.
- El contrato de suministro de la perforadora no puede ejecutarse mediante
  Formula.commit ni el procesador de recetas: la transacción del mundo es obligatoria.
  La prueba general de recetas distingue este contrato de las recetas de fabricación.
- Escáner: 512 celdas por tick compartidas entre un máximo de dos trabajos activos
  de Venus. Perforadora: 16 comprobaciones de búsqueda por tick de trabajo y volumen
  finito de 864 posiciones. Ninguno solicita chunks remotos ni utiliza /locate.
- La capa de servidor consulta propietario, dimensión, permisos, borde, spawn y el
  evento BEFORE de Fabric. Las pruebas usan un mundo en memoria para observar
  denegaciones y cambios; no se ha probado un mod de parcelas concreto.
- No se ha ejecutado cliente gráfico ni partida multijugador. Sondeo interactivo,
  importación visual al atlas, control por redstone, conexión física completa y
  permisos con otros mods quedan pendientes del checklist de docs/venus-mining.md.
