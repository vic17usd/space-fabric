# Interiores y diagnóstico de hábitats — 0.31.0

Esta entrega implementa el primer punto de la mejora de Venus: una base que pueda
verse despejada por dentro y cuyo soporte vital sea fácil de comprobar. También
se aplica a los hábitats ya construidos en Marte y Tierra. No requiere modelos
3D nuevos ni reconstruir las habitaciones existentes.

## Cómo usarlo

1. Instala `space-fabric-0.31.0.jar` en cliente y servidor, con Fabric API para 26.1.
2. [Construye y sella una habitación](colonies.md), con la toma del controlador
   libre. En Venus usa una envolvente certificada, incluida la puerta de esclusa.
3. Abre el controlador. La primera fila recibe **oxígeno / batería / agua**.
   Las baterías descargadas salen en la sexta casilla; las bombonas, en la séptima.
   Pasar el cursor por una casilla vacía explica su función.
4. Con el soporte vital activo aparece **Interior regulado**. El panel y el HUD
   muestran presión, temperatura y reservas. Dentro puedes estar sin traje según
   las reglas de protección existentes; lleva uno para atravesar una esclusa.

El inventario conserva las mismas nueve posiciones guardadas. Shift + clic mueve
suministros compatibles a la entrada correcta; las salidas permiten retirar los
objetos guardados y no admiten inserciones manuales. Las tolvas conservan las
reglas de automatización de 0.29. Las demás máquinas mantienen su menú anterior.

## Qué indican los instrumentos

| Indicador | Significado |
|---|---|
| Presión 101,3 kPa | Objetivo de presión regulada mientras el soporte vital protege la sala |
| Temperatura 22 °C | Objetivo térmico regulado, con refrigeración operativa cuando es necesaria |
| Oxígeno | Reserva interna consumible, hasta 9.600 unidades |
| Energía | Reserva eléctrica del controlador, hasta 9.600 unidades |
| Refrigerante | Agua disponible para refrigeración en Venus, hasta 9.600 unidades |
| Volumen | Número de celdas de aire verificadas en la habitación |

Las barras representan las **reservas ya cargadas**, no los objetos que todavía
esperan en las entradas. Cada bombona o batería aporta 2.400 unidades según las
reglas anteriores. Las reservas de recipientes pendientes pueden permitir nuevos
rellenados; mantener libres las salidas evita que se detengan.

A un 15 % de reserva o menos aparece un aviso amarillo para el recurso más bajo.
Cuando el soporte vital falla, el aviso rojo identifica falta de oxígeno, energía
o refrigerante, fuga, volumen excesivo, cerramiento débil o toma bloqueada.
Presión y temperatura pasan a **—**, indicando ausencia de regulación verificada.
No se presentan como una medición física calculada de una descompresión real.
Los objetivos y balances son una abstracción del juego; todavía no hay gradientes
térmicos, gas por tuberías ni una simulación progresiva de presión entre salas.

El HUD aparece dentro de la sala o al acercarse al controlador para diagnosticarlo
(hasta seis bloques de distancia Manhattan). Que un controlador cercano indique
una sala regulada **no protege a quien está fuera de ella**. Se oculta al abrir
pantallas y con F1, y desaparece al alejarse, morir o desconectarse.

## Niebla interior

La niebla planetaria de suelo se despeja solamente si:

- el servidor informa que el soporte vital está regulado;
- los ojos del jugador y la cámara pertenecen al volumen de aire verificado;
- el cerramiento y el interior siguen coincidiendo con la comprobación recibida.

En tercera persona, sacar la cámara fuera de la sala restaura la niebla exterior.
Una puerta abierta al exterior o una pared rota invalida la vista despejada. Una
esclusa con ambas puertas cerradas puede mantener la sala principal regulada; al
abrir la puerta interior cambia el volumen que debe comprobarse y presurizarse.

Se conservan la distancia de renderizado, el cielo, las nubes y la iluminación.
No se eliminan los efectos de niebla de agua, lava, ceguera u oscuridad, ni se
sustituye la escena de viaje espacial del cohete.

El ajuste de niebla sigue la cámara y usa la niebla global de Minecraft. La escena
lejana vista por las ventanas recibe ese mismo ajuste; esta entrega no separa
niebla interior/exterior por cada rayo ni añade una simulación volumétrica.

## Sincronización y verificación

Las lecturas proceden del servidor. Los recursos y la protección no se modifican
desde el HUD ni desde paquetes enviados por el cliente. La geometría acotada de
la sala se envía al entrar o cambiar el cierre; las lecturas cambian al actualizarse
las reservas o el estado, con un mensaje de continuidad cada segundo de juego.
La comprobación local del cierre visual se limita a una vez por tick.

Cambiar de dimensión o controlador invalida la geometría anterior. Sin mensajes
válidos durante dos segundos de ticks del cliente, se retiran el HUD y la vista
despejada. Una pausa local no consume ese plazo. Morir o desconectarse limpia el
estado; el servidor renueva la geometría al reemplazar al jugador al reaparecer.
Las posiciones no cargadas impiden despejar la niebla y no fuerzan cargas de chunks.

Las pruebas cubren reservas y fallos, paquetes completos e incrementales, límites
antes de leer geometría, conservación de slots y transferencias con Shift, cámara
fuera de la sala, fugas, cambios de dimensión/controlador, expiración y los campos
nativos de `FogData`. La inspección visual, el menú y la sincronización multijugador
en una partida real siguen pendientes; las comprobaciones por CPU no los sustituyen.
