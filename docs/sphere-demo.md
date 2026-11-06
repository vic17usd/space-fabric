# Prueba de planeta esférico — 0.57.0

Instala el JAR actualizado y entra con:

```mcfunction
/planetaprueba entrar
```

La dimensión `space_fabric:sphere_demo` contiene una esfera real de bloques de
radio 24 y diámetro de 49 bloques, centrada en X 0,5; Y 96,5; Z 0,5. Sus polos
son blancos, el ecuador amarillo y un meridiano rojo. No necesitas traje.
Baja de cualquier vehículo antes de entrar.

- WASD: desplazamiento por la superficie.
- Mouse: mirar y cambiar la dirección de marcha.
- Espacio: salto con gravedad radial.
- Ctrl o la tecla de sprint configurada en Minecraft: correr.
- Mayús o la tecla de agacharse: volver.
- `/planetaprueba salir`: regresar al punto exacto de entrada, con su dimensión
  y orientación originales.

Puedes cruzar los laterales, caminar por debajo y volver al comienzo. Seguir el
meridiano permite recorrer ambos polos. La cámara se mantiene orientada respecto
a la superficie; se usa primera persona y se restaura tu vista al salir.

El movimiento emplea un arnés invisible controlado por el servidor, con adhesión
a la superficie y salto radial. Es una demostración jugable de locomoción, no
un motor completo de gravedad para todas las mecánicas de Minecraft. No se
permite construir, romper bloques o usar objetos dentro de la prueba. La rotación
de modelos de terceros y las colisiones generales no están adaptadas. El minimapa
plano se oculta. La dimensión no aparece como destino de los cohetes.

Los operadores pueden usar `/gamemode spectator` para observar la esfera completa
desde el exterior y volver a su modo anterior para caminar. El comando de regreso
sigue disponible. No se cambia automáticamente el modo de juego.

El punto de regreso se guarda como attachment persistente del jugador. Si sales
y vuelves a entrar a la partida estando en la prueba, el arnés se recrea en el
polo de inicio. Una entrada directa por teleportación de administrador no guarda
un regreso: usa el comando de entrada normal.

Las pruebas cubren generación nativa, codecs, transporte del marco local, una
vuelta completa, alturas de los bloques, cámara y datos de regreso. El recorrido
visual, la suavidad y los controles requieren comprobación en un cliente real.
