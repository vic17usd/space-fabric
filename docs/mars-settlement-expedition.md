# Investigación del asentamiento de Marte — 0.61.0

La segunda expedición está disponible cuando el servidor ya ha completado tu
refugio de la primera etapa. Los jugadores de 0.58–0.60 que cumplieron esa etapa
pueden continuar directamente. El progreso es personal y se guarda en el mismo
registro `space_fabric:mars_expedition`; los campos anteriores conservan su formato.

Instala `space-fabric-0.61.0.jar` en cliente y servidor, sustituyendo el anterior,
con Minecraft 26.1, Java 25, Fabric Loader 0.20.1 y Fabric API 0.145.1+26.1.

## Recorrido en supervivencia

1. **Prepara la salida.** Completa el refugio de la primera expedición. Mantén
   traje y oxígeno durante la exploración. Lleva un analizador de muestras,
   una batería cargada y una tarjeta de datos; puedes usar una máquina ya existente.
2. **Busca un Pueblo de Marte.** `/marte buscar` comprueba una zona de 17×17 chunks
   alrededor del jugador usando solamente chunks cargados. Si hay un pueblo natural
   confirmado, muestra sus coordenadas. En caso contrario ofrece una **zona potencial
   sin confirmar**, calculada a partir de la semilla y la distribución de estructuras.
   Viaja allí y repite la búsqueda desde la nueva zona; otra ejecución tras cinco
   segundos de juego ofrece la siguiente zona. Las exclusiones, el terreno y los
   chunks antiguos pueden impedir un pueblo en una zona potencial. No se garantiza
   que exista allí y esa sugerencia no completa ningún objetivo.
3. **Entra en el asentamiento.** El servidor detecta el inicio natural y los límites
   de la plantilla del pueblo. La plataforma de terreno exterior no cuenta. Colocar
   una plantilla manualmente sin su registro de estructura natural tampoco cuenta.
4. **Investiga un punto.** Dentro del pueblo, mira un bloque sólido a menos de cinco
   bloques y ejecuta `/marte investigar`. Se guarda ese bloque como punto concreto
   de registro. No se modifica el edificio, no se coloca una consola y no se necesita
   ningún NPC vivo. Si el bloque se rompe, repite el comando mirando otro bloque
   sólido del mismo pueblo; también puedes colocar expresamente un bloque allí.
5. **Encarga el análisis.** En Marte, mira un analizador detenido, reparado y con
   las tres entradas vacías. Ejecuta `/marte analizar` **antes** de cargarlo. La
   máquina queda vinculada a tu encargo. Puedes hacerlo dentro de tu base segura.
6. **Carga la receta real.** Fila superior: una muestra de **olivino marciano o
   polvo de sulfato**, una batería cargada y una tarjeta de datos. Espera diez
   segundos de procesamiento con las salidas libres. El analizador produce el
   informe marciano y devuelve la batería descargada. El servidor acredita ese
   ciclo a quien encargó el trabajo, incluso si se desconecta mientras sigue cargado.
7. **Registra el resultado.** Regresa al pueblo, mira el bloque registrado y usa
   `/marte entregar`. Recibirás **dos baterías cargadas, una sola vez**. Conservas
   el informe para sus usos tecnológicos habituales. Deja un espacio normal del
   inventario o una pila con capacidad para dos baterías; si no cabe, la entrega
   queda pendiente y puedes reintentar sin perder el progreso.

`/marte` muestra el objetivo y las coordenadas, `/marte texto` incluye las
instrucciones escritas y `/marte expedicion` abre la sección dedicada. También se
accede mediante **Expedición II** en `/marte guia`, conservando los ocho capítulos
del entrenamiento de la primera etapa y el acceso desde el inventario.

## Componentes y recetas

Se reutilizan las recetas existentes del mod; el visor muestra las cuadrículas
y cantidades del servidor. En la pantalla de la expedición, pulsa **Recetas** y
busca *Analizador de muestras*, *Tarjeta de datos* y *Batería cargada*.
La tarjeta de datos se fabrica sin forma con papel, cobre y redstone: da cuatro.
El analizador usa `HGH / CAR / HCH`: H = panel presurizado, G = vidrio,
C = cobre, A = fragmento de amatista y R = redstone.
No se exige tecnología nueva de Venus. La recompensa devuelve dos baterías
cargadas por el coste de una batería durante el análisis: permite continuar la
exploración sin saltarse una cadena tecnológica.

Los informes actuales son objetos genéricos y **no certifican quién los produjo**.
Tener uno encima, introducir uno en las salidas o analizar antes de vincular la
máquina no acredita este encargo. La prueba procede de la finalización real de
la receta, guardada por el servidor, y no del contenido del informe.

## Máquinas compartidas, averías y guardado

Cada analizador admite un encargo activo. Otros jugadores pueden aportar los
materiales y usar sus inventarios, pero ese ciclo pertenece a quien lo encargó;
para su propio progreso deberán encargar otro ciclo, después o en otra máquina.
El pueblo y el punto pueden compartirse. La misión no consulta ni modifica el
comercio, el inventario o la supervivencia de los aldeanos.

Si la máquina se rompe, queda bloqueada o necesitas cambiarla, usa `/marte cancelar`.
Se conserva la investigación del pueblo y puedes vincular otro analizador vacío.
El encargo viejo pierde su validez. Reemplazar la máquina en las mismas coordenadas,
copiar informes o completar un encargo cancelado no acredita el nuevo trabajo.
Las reservas, el progreso parcial y la identidad del encargo sobreviven al guardado
y la reconexión. Las máquinas descargadas se detienen según las reglas habituales.

La entrega comprueba el punto, el asentamiento y el espacio disponible antes de
marcar la recompensa y añadirla al inventario en la misma acción del servidor.
No suelta objetos al suelo y no cobra otra muestra ni otro informe. Los reintentos
posteriores se rechazan. Se conserva el comportamiento normal de guardado de Minecraft;
no se ofrece una transacción de disco entre archivos ante apagados abruptos.

Al completar se anuncia **hacer autosuficiente la base**. Esa tercera etapa queda
como objetivo narrativo, sin nuevas condiciones ni recompensas en esta entrega.

## Prueba reproducible

En supervivencia, completa la primera etapa, realiza los siete pasos anteriores,
reconecta después de vincular la máquina y antes de entregar. Comprueba el mensaje
con inventario lleno, libera un hueco y registra. Repite `/marte entregar`: la
recompensa no debe aumentar. Un segundo jugador debe realizar su propio encargo.
En un pueblo antiguo, comprueba también el registro sin aldeanos y el cambio del
punto tras romper el bloque elegido. No es necesario regenerar el mundo.

Las comprobaciones automáticas y la inspección real del cliente se detallan en
[validation.md](validation.md).

### Fixture de desarrollo

Para repetir el recorrido automático en un **mundo desechable**, puede usarse:

```sh
JAVA_TOOL_OPTIONS="-Dspacefabric.marsSettlementCheck=true" ./gradlew runClient -PmarsGuideSmoke
```

El fixture prepara explícitamente la primera etapa, cambia al jugador de prueba
a creativo, genera un pueblo mediante la generación normal del mundo, coloca
un analizador y ejecuta las acciones reales de la segunda expedición. Espera sus
ticks de procesamiento, prueba inventario lleno y entrega repetida, y abre la
pantalla al completar. Modifica el inventario del jugador de prueba: úsalo solo
en un mundo desechable. Sus clases y metadatos están en `src/guideSmoke` y quedan
fuera del JAR instalable. La búsqueda de producción nunca ejecuta esa generación.
