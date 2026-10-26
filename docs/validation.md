# Resultado de validación de esta base

Verificado con JDK 25, Gradle 9.8.2, Loom 1.19.3, Minecraft 26.1,
Fabric Loader 0.20.1 y Fabric API 0.145.1+26.1.

- `./gradlew build --console=plain`: compilación Java, recursos, JAR y pruebas correctos.
- JUnit: **109 ejecutadas, 109 aprobadas, 0 omitidas**.
- `ExposureTest`: 29 casos de atmósfera, inmunidad y protección por sellado.
- `WorldgenCodecTest`: 58 casos con los codecs reales de Minecraft; nueve tipos
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
- `python3 tools/validate_resources.py`: 155 JSON y referencias internas coherentes.
- Comprobaciones de materiales: 11 loot tables y receta aceptadas por los codecs;
  etiquetas de botas térmicas, componentes de armadura y requisitos de herramientas.
- Modificaciones reales de Fabric: vetas marcianas presentes y decoración orbital
  conservada por el generador vacío.
- Assets: modelos e inventario, traducciones y CRC/contenido de 13 texturas PNG validados.
- Gradle Wrapper y distribución: SHA-256 verificado con los checksums oficiales.
- JDK descargado para este entorno: SHA-256 verificado con el checksum oficial.

Artefacto generado: `build/libs/space-fabric-0.5.0.jar`.
Los informes de la ejecución están en `build/test-results/test/` y
`build/reports/tests/test/`; son salidas locales ignoradas por Git.

No se ejecutó un cliente gráfico, un servidor de juego ni una sesión multijugador.
Los codecs no demuestran que el terreno se genere correctamente durante una
exploración ni que el movimiento se sincronice en un cliente. El procedimiento
manual del README cubre esos puntos y queda pendiente.

El entorno utiliza un JDK local en `/workspace/.space-fabric-tools/jdk` y caché
Gradle fuera del checkout. Los scripts reutilizables del entorno activan esas
rutas y mantienen el proxy y la confianza TLS de la máquina. No se guardaron
credenciales ni se desactivó su verificación.

La configuración de red guardada como borrador añade los destinos oficiales de
Fabric y Mojang para desarrollo. Guardar ese borrador no publica el entorno ni
confirma que una máquina futura se haya restaurado o validado.
