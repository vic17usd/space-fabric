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

Los encargos de producción de agua, oxígeno y energía, sus reservas y la prueba
nocturna todavía no están conectados. No se completa la expedición ni se otorga
ninguna recompensa en esta entrega.
