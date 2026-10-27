# Cohetes y viajes — Fabric 26.1, versión 0.7.0

Implementación integrada en el mod, con mappings oficiales y Java 25. En estas
API, `AbstractContainerMenu` equivale al `ScreenHandler` de los tutoriales Yarn.
La GUI usa `AbstractContainerScreen` y `GuiGraphicsExtractor`; el traslado usa
`TeleportTransition`. No se necesita el antiguo `FabricDimensions.teleport`.

## Arquitectura

Todos los paquetes parten de `dev.vic17usd.spacefabric`.

| Archivo | Responsabilidad |
| --- | --- |
| `space/RocketEntity.java` | Ocupante, tanque, carga, interacción, ascenso y guardado |
| `space/RocketItem.java` | Colocación y combustible conservado en componentes del ítem |
| `space/ModSpaceEntities.java` | Registro de `EntityType`, tamaño y punto del pasajero |
| `space/TravelDestination.java` | Lista cerrada del servidor: Overworld más nueve dimensiones |
| `space/RocketRules.java` | Capacidad, consumo y validación de despegue |
| `space/SpaceTravel.java` | Búsqueda de llegada, plataforma y traslado de raíz/pasajero |
| `space/LandingPlanner.java` | Selección acotada sin sustituir bloques ajenos |
| `space/RocketCabin.java` | Protección atmosférica integrada con los sistemas existentes |
| `machine/FuelRefineryBlock.java` | Apertura, ticker y codec del bloque |
| `machine/FuelRefineryBlockEntity.java` | Inventario, NBT y automatización con tolvas |
| `machine/RefineryProcess.java` | Dos fórmulas y consumo al terminar el procesamiento |
| `machine/ModMachines.java` | Registro del tipo de BlockEntity |
| `menu/ModMenus.java` | `ExtendedMenuType` de Fabric y datos de apertura |
| `menu/RocketMenu.java` | Slots, datos sincronizados y botones validados en servidor |
| `menu/RefineryMenu.java` | Slots filtrados, progreso y transferencia rápida |
| `client/menu/*` | Mapa orbital, destinos, combustible y pantalla de refinería |
| `client/rocket/RocketRenderer.java` | Modelo original de cuerpo, cono, aletas y ventanilla |

`SpaceFabric` registra entidades, máquinas y menús después de los bloques.
`SpaceFabricClient` registra las dos pantallas y el renderer. Los assets, recetas,
loot y traducciones ES/EN se incluyen en `src/main/resources`.

## Construcción y combustible

Las cuatro recetas usan ingredientes vanilla. Los espacios son casillas vacías.

| Resultado | Patrón | Ingredientes |
| --- | --- | --- |
| 1 cohete | ` I ` / `ICI` / `RFR` | I: bloque de hierro; C: bloque de cobre; R: bloque de redstone; F: horno |
| 1 refinería | `III` / `IFI` / `IRI` | I: lingote de hierro; F: horno; R: redstone |
| 4 celdas vacías | ` I ` / `IGI` / ` I ` | I: lingote de hierro; G: vidrio |
| 6 plataformas | `III` / `SSS` | I: lingote de hierro; S: piedra |

La refinería tiene cuatro slots, de izquierda a derecha: **base, reactivo, celda
vacía y salida**. Trabaja mientras su chunk está cargado, sin otro sistema de energía.

| Fórmula | Base | Reactivo | Envase | Tiempo a 20 TPS | Salida |
| --- | --- | --- | --- | --- | --- |
| Inicial | 1 carbón o carbón vegetal | 1 polvo de blaze | 1 celda vacía | 200 ticks, 10 s | 1 celda llena |
| Planetaria | 1 cristal de azufre | 1 condensado ácido o polvo de sulfato | 1 celda vacía | 160 ticks, 8 s | 1 celda llena |

Son fórmulas de jugabilidad, no instrucciones de química real. La primera permite
iniciar los viajes sin depender de recursos extraterrestres. Los ingredientes se
consumen juntos al terminar. Una salida ocupada por otro ítem o una pila completa
pausa el progreso sin consumir recursos. Cambiar de fórmula reinicia el progreso.
Inventario, fórmula y progreso se guardan con `ValueInput`/`ValueOutput`.

Las tolvas introducen base desde arriba, reactivo/celdas por los lados y extraen
el resultado por abajo. Al romper la máquina, el comportamiento nativo de
`BaseContainerBlockEntity` deja caer su contenido una vez; no hay drops duplicados.

## Pilotaje

1. Coloca el cohete sobre la cara superior de un bloque, con espacio libre
   de al menos cuatro bloques de altura y suficiente anchura para sus aletas.
2. Haz clic derecho con una celda llena para cargar **1.000 unidades** y recibir
   una vacía. El tanque admite **3.000**. En creativo no se consume el ítem.
3. Haz clic derecho con otra mano/ítem para montar y abrir el mapa. Agacharse
   al hacer clic abre el inventario sin montar; el botón **Pilotar** permite subir.
4. Selecciona otro destino y pulsa **Despegar** o espacio en la GUI. Con el menú
   cerrado, espacio también inicia el vuelo usando el destino ya seleccionado.
5. El cohete asciende verticalmente hasta `level.getMaxY() - 8`. Entonces se
   traslada con su ocupante y carga; vuelve al estado de reposo al aterrizar.

Los dos primeros slots del cohete son celda llena de entrada y envase vacío de
salida. Acepta una celda completa solo cuando caben sus 1.000 unidades y el envase
puede guardarse. Tiene otros nueve slots de carga, con transferencia por shift.
El mapa es esquemático; las posiciones no son una simulación de efemérides.

Solo el ocupante puede cambiar destino o despegar. El coste se muestra antes
del despegue y se cobra **una vez** al iniciar el ascenso. Es
`200 + 100 × abs(nivelOrigen - nivelDestino)`, con niveles jugables:
Tierra/Overworld/Luna 0, Venus 1, Mercurio/Marte 2, Júpiter 4, Saturno 5, Urano 6,
Neptuno 7. Elegir la misma dimensión no permite volar. El coste máximo es 900.
No se habilita despegue desde Nether, End ni dimensiones de otros mods.

Si encuentra un techo, pierde el piloto, excede 1.200 ticks de vuelo o no hay
llegada válida, el cohete se detiene flotando. El combustible cobrado no se
reembolsa. Puede reiniciar el vuelo con el combustible restante y condiciones
válidas. Desmontar con shift durante el ascenso conserva el comportamiento
vanilla: el jugador puede caer; conviene desmontar en la plataforma.

Un cohete desocupado e inmóvil puede recogerse golpeándolo. Deja caer el ítem
con todo el combustible restante, incluso cantidades parciales, y la carga por
separado. No convierte el tanque en nuevas celdas. Un golpe en creativo lo retira
sin drops. Un cohete ocupado o en vuelo no puede desmontarse a golpes.
Al recargar el mundo se conservan inventario, tanque y destino, pero se detiene
el ascenso para evitar traslados automáticos desde un estado guardado.

## Llegada y cambio de dimensión

El cliente envía un **índice de botón**, mediante el paquete vanilla del menú.
El servidor comprueba el menú abierto, dimensión, distancia, piloto, estado,
combustible, lista de destinos y existencia del `ServerLevel` de llegada. El
cliente no envía coordenadas ni una clave arbitraria de dimensión. La entidad
mantiene `getControllingPassenger()` sin controlador para que los paquetes de
movimiento de vehículo del cliente no decidan la trayectoria. El servidor lee
`ServerPlayer.getLastClientInput().jump()` y calcula la velocidad de ascenso.

`LandingPlanner` busca hasta 16 candidatos de 5×5 cerca del origen (centros
X/Z entre 8 y 56). Se carga el chunk, se comprueban borde y entidades, y se exige
un volumen de aire de cinco bloques de alto. El suelo solo puede ser aire o
plataforma del mod. Las rocas, líquidos y construcciones no se eliminan. En
mundos rocosos intenta primero la superficie; si no cabe, intenta otros 16 puntos
en altura. Una plataforma propia despejada se reutiliza. Si todo está bloqueado,
se cancela el traslado; el jugador no aparece atrapado por la fuerza.

En Júpiter, Saturno, Urano y Neptuno la plataforma se prepara a Y=239 y el cohete
llega a Y=240, por encima de la zona de presión profunda. La plataforma se crea
en el momento de la llegada y permanece en el mundo. No crea terreno sólido
planetario. **Overworld** es el mundo inicial; **Tierra** es la dimensión separada
`space_fabric:earth`. El regreso se hace a la zona de plataformas del Overworld,
no a la cama ni a la posición original de lanzamiento.

El paso que ejecuta el traslado es la API nativa de 26.1:

```java
Entity arrived = rocket.teleport(new TeleportTransition(
        target,
        Vec3.atBottomCenterOf(floor.above()),
        Vec3.ZERO,
        rocket.getYRot(), 0,
        TeleportTransition.PLACE_PORTAL_TICKET));
if (arrived instanceof RocketEntity transferred) {
    transferred.finishLanding();
}
```

Minecraft cambia de dimensión a los pasajeros, crea la entidad destino desde
su tipo registrado, copia su estado y vuelve a montar al jugador. Se usa el
objeto devuelto: la instancia antigua queda retirada. No se crean pasajeros ni
cohetes adicionales manualmente.

Desde 0.16.0 la cabina protege hasta 100 bar, no a cualquier profundidad.
Consulta [los nuevos trajes y exposición](pressure-and-suits.md). Protege de hipoxia, descompresión, presión y
lluvia ácida de este mod. Al salir vuelven las reglas del traje y del ambiente.
No elimina daños externos como impactos de meteoritos ni sustituye reglas de otros mods.

## Comprobación

`./gradlew build --console=plain` compila el código común/cliente y ejecuta JUnit.
`python3 tools/validate_resources.py` valida los JSON, las referencias y PNG.
Las pruebas cubren las fórmulas con pilas reales, salida bloqueada, cambio de
fórmula, NBT de la BlockEntity, componentes del cohete empaquetado, recetas,
selección de llegada y rechazo de despegues inválidos.

Queda pendiente la prueba en un cliente y servidor de juego. Para realizarla:

1. En un mundo de pruebas, fabrica cohete/refinería y produce celdas con carbón,
   polvo de blaze y envases. Comprueba tolvas, salida llena y reabrir el mundo.
2. Carga el cohete por mano y por inventario; verifica que cada celda entrega
   un envase y que el tanque nunca supera 3.000.
3. Guarda carga reconocible, elige Luna, despega con el botón y vuelve con
   espacio. Comprueba identidad del piloto, carga, combustible y solo un cohete.
4. Repite Tierra, Venus, Marte y los cuatro gigantes; confirma plataforma Y=239
   y protección dentro de cabina. Lleva escafandra para salir.
5. Construye obstáculos en sitios de llegada y sobre el despegue; comprueba que
   el vuelo se detiene o usa otra plataforma sin borrar bloques.
6. Con dos clientes, intenta controlar el cohete ajeno, cambiar destino sin
   montar, abrir desde lejos y pulsar despegar repetidamente. Comprueba el
   rechazo en servidor y un único cargo de combustible.
7. Recoge/recoloca un cohete con combustible parcial y recarga el mundo durante
   un ascenso. Comprueba conservación de recursos y reposo al volver a cargar.

No se afirma una sesión multijugador ni una validación visual ya realizadas.

## Modelo del cohete desde 0.12.0

Se usa `space-rocket.bbmodel`, aportado por el usuario, con sus 48 piezas y atlas
512×512. Aparece tanto en el cohete colocado como en el ítem 3D del inventario.
La escala es uniforme, con 1,6 bloques de anchura máxima y 3,28 de altura visible;
el origen inferior queda a ras del suelo. El renderer conserva el giro del cohete.
La colisión y el asiento mantienen las medidas del sistema de transporte.

El archivo fuente se conserva en `tools/import_sources/space-rocket.bbmodel`.
`python3 tools/import_rocket_model.py` reproduce `RocketModel.java`,
`textures/entity/rocket.png` y `models/item/rocket.json`. El generador general
de recursos de transporte vuelve a aplicar esta importación al terminar. No
hacen falta Blockbench ni bibliotecas de animación al jugar.

La compilación y las referencias de los recursos se verifican automáticamente;
falta revisar la apariencia y la posición del piloto en una sesión gráfica.

Desde 0.12.1 se evita el contorno blanco forzado en `submitModel`. El atlas original
se copia también a `textures/item/rocket.png` para su inclusión en el atlas vanilla
de objetos; el JSON del ítem referencia `space_fabric:item/rocket`.

Desde 0.13.0 la entidad usa `submitCustomGeometry` y emite las UV por vértice,
para evitar las cachés de cubos vanilla de los optimizadores gráficos. La
geometría y textura del ítem no cambian. Esta ruta no añade un contorno de
resaltado al cohete si se le aplica el efecto de brillo.

## Cabina del piloto desde 0.14.0

El piloto ve un habitáculo 3D con paredes, techo, suelo, respaldo, consola y
mandos. El visor circular frontal es una abertura real en esa geometría: deja
ver el terreno, el cielo y los efectos del planeta sin una imagen de fondo.
El marco tiene relieve y los paneles usan la zona clara del atlas del cohete,
con iluminación interior para que también sean visibles durante la noche.

La cabina reemplaza el exterior macizo únicamente para la cámara que ocupa ese
cohete en primera persona. En tercera persona y para los demás observadores se
conserva el modelo exterior; cuerpo y armadura del piloto permanecen ocultos
para que no sobresalgan de la cápsula. El ítem del inventario no cambia.

La geometría interior acompaña la posición de la cámara durante la interpolación,
pero su orientación sigue al cohete, no a los movimientos de la mirada. Al montar,
la vista se orienta hacia el visor una sola vez. Después puedes mirar libremente
a los lados, al suelo, al techo y hacia atrás. F5 conserva su funcionamiento normal.
No se cambian el asiento, las colisiones ni la física del servidor.

La versión 0.13.1 solo retiraba la carcasa de la vista del piloto; esta versión
sustituye esa vista vacía por el interior. Se aplica a cohetes ya colocados al
reiniciar el cliente con el JAR actualizado.

Código: `client/rocket/RocketCockpitModel.java` define las superficies y el marco;
`RocketCockpitView.java` centra la mirada al montar. `RocketRenderer` selecciona
la cabina o el exterior según la cámara. Ambos modelos emiten vértices directamente
para conservar sus UV en instalaciones con optimizadores de entidades.

Validación sin GPU: `./gradlew verifyRocketCockpit` comprueba las caras emitidas,
sus normales y materiales, nueve rayos de visión libres por el visor y seis
direcciones que deben encontrar paneles, techo, suelo o marco. También se ejecuta
con `build`. La comprobación visual en una partida sigue pendiente.

Prueba manual: montar y cerrar el mapa estelar, mirar por el visor y girar la
cabeza para revisar los paneles; cambiar con F5 y desmontar. Repetir durante
el ascenso y en Marte de noche para revisar la interpolación y la iluminación.
