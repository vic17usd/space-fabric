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

### Reservas de la base

`/marte autonomia` consulta sin transferir recursos. Las entradas del controlador
cuentan solo si hay sitio para los envases de retorno. La red se descubre a través
de interfaces adyacentes dirigidas al controlador: no hace falta vincular depósitos
por coordenadas. Al desconectar, cerrar o sustituir tuberías e instalaciones se
recalcula la conexión real. Las interfaces deshabilitadas y redes mezcladas no
aportan reservas; faltas de chunks o redes mayores de 128 nodos se informan como
datos incompletos. No se cargan chunks ni se reservan recursos de la red.

La interfaz necesita una bombona o batería vacía y espacio en la salida del
controlador. El cálculo cuenta únicamente redes con envases y la entrada correspondiente vacía o con una sola unidad;
es conservador cuando un envase podría llegar más tarde. Deduplica depósitos por
posición y calcula bombonas completas para cada conjunto de redes alcanzables por
una interfaz. Una bombona representa 1.000 unidades de oxígeno en la red y 2.400
en el soporte vital; una batería representa 2.400 unidades en ambos. Otros
consumidores pueden reducir los suministros compartidos tras la consulta.

El agua de la tercera entrada y depósitos de agua integrados en la habitación sigue
la misma regla de la primera expedición: hasta dos bombonas (2.000 unidades). Es
almacenamiento accesible manualmente, no un consumo del soporte vital en Marte.
No se cuenta agua de redes sin esa pertenencia. Los objetos en el inventario del
jugador no cuentan. Las acreditaciones de producción son independientes.

Se calcula una noche de 12.000 ticks, 600 ciclos de soporte vital, 25 % de margen
sobre el consumo y una presurización conservadora de 2 unidades por bloque de aire.
Cada ocupante consume una unidad de oxígeno por ciclo y la energía consume cinco.
Ejemplo: un recinto de 100 bloques y dos ocupantes requiere 1.700 unidades de
oxígeno, 3.750 de energía y 2.000 de agua almacenada; sin ocupantes requiere 200,
3.750 y 2.000 respectivamente. La capacidad interna de 9.600 admite estos ejemplos;
para grupos mayores pueden requerirse entradas o redes adicionales. Una demanda
inicial que supera la capacidad impide marcar reservas suficientes.

La suficiencia es una instantánea: requiere controlador operativo, recinto sellado,
datos completos y reservas suficientes. No garantiza una noche de funcionamiento:
otros consumidores, cambios de ocupantes o averías pueden alterar el resultado. La
prueba nocturna y la recompensa permanecen pendientes.

### Solicitud explícita de la prueba nocturna

`/marte autonomia noche iniciar` comprueba en el servidor la aceptación, las tres
producciones acreditadas, los permisos y el estado actual del refugio seleccionado
(incluidas sus reservas reales). Si todo cumple, guarda una solicitud personal
vinculada a la identidad del controlador. Repetirla no duplica progreso; consultar
`/marte autonomia` no la activa. Cambiar el refugio elimina la solicitud anterior.

Esta entrega registra la preparación explícita, **no observa todavía una noche**.
La solicitud sobrevive a un guardado normal, pero no asegura que el refugio siga
válido tras reconectar: el futuro observador deberá volver a comprobar las
condiciones antes del inicio real. No se conecta MarsNightTrial, no se cambia el
reloj y no hay recompensas. El comando lo indica expresamente.
