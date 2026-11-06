# Tercera expedición: selección de refugio

Tras registrar la investigación del asentamiento y su recompensa, `/marte autonomia`
muestra que la expedición está disponible. `/marte autonomia iniciar` la acepta una
sola vez por jugador. Los mundos anteriores conservan su progreso.

En Marte, vivo, sin montar y con permiso para interactuar, mira a un controlador
de hábitat a cinco bloques o menos y ejecuta `/marte autonomia refugio`. Puedes
repetirlo o mirar a otro controlador para cambiar de refugio. No hace falta estar
dentro ni tener suministros. Cada jugador mantiene su propia selección y varios
pueden elegir el mismo controlador.

`/marte autonomia` muestra las coordenadas y el estado actual, volumen de aire y
ocupantes de la habitación cuando está sellada. Se usa el mismo escáner y la misma
regla del soporte vital: jugadores vivos no espectadores con los ojos dentro del
aire detectado. Una habitación vacía puede estar sellada. La selección no asegura
presurización ni protección; comprueba también el soporte vital normal. Si la toma
superior está tapada, hay una fuga, se supera el límite o faltan datos, la consulta
explica el problema. La inspección reutiliza la caché del escáner, no fuerza la carga
de chunks y puede esperar al siguiente escaneo tras una reparación. Un controlador
sustituido en la misma posición debe seleccionarse de nuevo; una descarga temporal
no borra la selección. Consultar el estado no selecciona ni modifica la base.

Las reservas y la prueba nocturna todavía no están conectadas. No se completa la expedición ni se otorga
ninguna recompensa en esta entrega.

## Encargos de producción local

Después de aceptar y seleccionar refugio, se puede acreditar **una tanda nueva** de
cada proceso. Esto demuestra producción, no la suficiencia de las reservas.

1. Vacía las entradas del electrolizador y espera a que el progreso sea cero. Mira
   la máquina y usa `/marte autonomia produccion agua`. Después introduce hielo
   marciano, hielo normal/compactado/azul o fragmento de hielo en la casilla 1,
   batería cargada en la 2 y bombona vacía en la 3. La receta existente produce una
   bombona de agua en 100 ticks efectivos de trabajo y devuelve la batería vacía.
2. Registra `/marte autonomia produccion oxigeno` con el electrolizador vacío y
   detenido. Introduce una bombona de agua, batería cargada y bombona vacía en las
   mismas entradas. En 200 ticks produce oxígeno e hidrógeno y devuelve la batería.
3. Mira un cargador solar vacío y usa `/marte autonomia produccion solar`. Introduce
   una batería vacía en su primera casilla. Necesita las condiciones reales del
   cargador: luz solar, cielo visible y ausencia de lluvia; no carga de noche.
   Completa la batería en 400 ticks efectivos. La carga en mesa o geotérmica no cuenta.

`/marte autonomia` muestra las tres acreditaciones y el encargo activo. Hay un
encargo por jugador y una máquina no admite dos simultáneos. La instalación puede
compartirse por turnos. `/marte autonomia produccion cancelar` permite volver a
encargar tras una avería, destrucción o cambio de plan; no destruye materiales ni
borra los procesos acreditados. Al cambiar el refugio se reinicia esta demostración
para que la producción de la instalación anterior no se traslade a otra base.

Se reutiliza MarsCommission: UUID del jugador, ticket nuevo, identidad persistente
de la máquina, coordenadas y contador inicial. Solo el callback posterior a una
transacción real de receta puede acreditar. Repeticiones, ciclos anteriores,
tickets cancelados y máquinas sustituidas se rechazan. Los trabajos sobreviven a
reconexiones y guardados normales y pueden terminar sin el jugador conectado.

Esta entrega aún no comprueba redes o ubicación de máquinas respecto a la habitación,
no contabiliza reservas ni conecta MarsNightTrial. La producción es local a Marte;
la procedencia geológica del bloque de hielo no se rastrea. No hay recompensas ni
finalización provisional de la tercera expedición.

### Validación de esta entrega

`bash ./gradlew build --console=plain`: compilación, pruebas JUnit y verificadores
del proyecto. Las pruebas nuevas cubren guardado/restauración del encargo,
baselines antiguos, tickets incorrectos/cancelados, identidad reemplazada, repetición
de resultados y los tres procesos con el procesador e inventarios nativos. La
prueba solar usa un entorno de máquina preparado con y sin luz solar: es un fixture,
no una partida en supervivencia. No se ha realizado una prueba del cliente real ni
una sesión multijugador; deben comprobarse los comandos, las reconexiones y el
recorrido anterior dentro de Minecraft. No se han modificado las recetas ni las
capacidades para esta demostración.
