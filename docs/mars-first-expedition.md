# Primera expedición de Marte — 0.60.0

La etapa empieza cuando el cohete termina físicamente su descenso en Marte con
el jugador a bordo. El servidor comprueba destino, origen, fase de descenso,
colisión con el suelo, piloto vivo y casco operativo antes de registrar la llegada.
Entrar por comandos, cargar una partida en Marte, colocar un cohete o abortar un
despegue desde Marte no inicia la expedición. En un mundo antiguo ya situado en
Marte, realiza un nuevo viaje de llegada; puedes reutilizar después tu base.

La guía aparece **arriba a la izquierda, sin panel de fondo**, con cuatro objetivos.
`/marte` muestra estado, reservas y coordenadas; `/marte guia` abre un tutorial
visual en español. `/marte texto` conserva las instrucciones largas en el chat.
Son comandos normales, sin permisos de operador.
El visor de recetas existente muestra las cuadrículas y los procesos exactos.

## Guía visual animada

Abre `/marte guia` o pulsa **Guía de Marte** en la esquina superior izquierda del
inventario. También puedes pulsar el aviso del primer aterrizaje. La guía puede
consultarse antes de aterrizar para preparar la expedición; abrirla no inicia ni
completa objetivos.

Los ocho pasos muestran el equipo inicial, el montaje del refugio, la toma de
ventilación, el uso de dos esclusas, la carga de O₂ y batería, la reserva de agua,
las recetas y la validación final dentro de la base. El dibujo del refugio es un
**corte didáctico** con techo y paredes cercanas retirados después de mostrar
la envolvente cerrada: en la
construcción real debes cerrar la envolvente completa. Los ejemplos de inventario
no mueven ni consumen tus objetos. El pie muestra por separado los requisitos
reales enviados por el servidor.

- **Anterior / Siguiente** o **flechas del teclado** cambian de paso.
- Los **números 1–8** del teclado o las pestañas de arriba permiten saltar directamente a cualquier paso.
- **Pausa / Reproducir** o **Espacio** detienen/reanudan la animación.
- **Repetir**, **R** o **Inicio** vuelven al comienzo; la barra permite buscar otro instante.
- **Tab / Mayús+Tab** recorren los botones; **Enter** activa el seleccionado.
- En la distribución compacta, si una explicación ocupa más de tres líneas,
  **↑ / ↓** o un clic sobre ella cambian de página. No se recorta el contenido.
- **Esc** cierra la guía y vuelve al juego.
- En el paso **7**, elige un objeto con los iconos superiores. Las cuadrículas y
  productos proceden del visor de recetas existente del servidor, incluidas las
  modificaciones de datapacks. Las flechas cambian de receta cuando hay varias;
  al pasar el ratón sobre los objetos se muestran sus nombres.

Cada capítulo dura dieciséis segundos y se detiene al final para dejar tiempo de
lectura. Repetir vuelve al inicio y reanuda; cambiar de capítulo conserva una
pausa manual. Al cambiar desde una escena terminada, la siguiente se reproduce. Pausa la partida local; en multijugador el servidor sigue funcionando, por
lo que conviene abrirla desde un lugar seguro. El aviso también aparece al pasar
el ratón sobre el pie de la pantalla. Las misiones conservan sus requisitos y su
persistencia de 0.58.0. Si el cliente no admite la nueva pantalla, el comando
informa de la actualización necesaria y ofrece el estado breve.

### Presentación y límites de la demostración

Consola oscura, acento marciano cálido, O₂ verde, energía ámbar y agua azul.
La maqueta usa modelos y texturas reales con profundidad, iluminación de GUI y
cámara ortográfica suave. El espacio de aire sobre el controlador se señala con
un contorno hueco, sin mostrar un bloque sólido en la toma. Un personaje con la
apariencia del jugador atraviesa el vestíbulo cuando la puerta correspondiente
está abierta. Los inventarios explicativos conservan los iconos a tamaño nativo.

A partir de 600 píxeles GUI y 290 de alto se separan maqueta y explicación; en
320×240 se coloca la explicación debajo. Se redistribuye el contenido sin reducir
la fuente. La barra permite buscar un instante y los controles quedan fijos abajo.
El encabezado identifica el entrenamiento; el pie **SERVIDOR** muestra únicamente
el estado real recibido. Las reservas animadas son un ejemplo de un ciclo y no
las reservas del jugador. La segunda etapa solo se menciona, no se implementa.

El kit recomienda 192 bloques macizos (tres stacks) para la envolvente y margen
para el vestíbulo, dos esclusas y cuatro palancas, accesibles a ambos lados. No
se exigen paneles presurizados en Marte: se muestran como material del mod.
Se conservan los mínimos y suministros de 0.58–0.59; no hay recetas nuevas.

[Dirección artística, guion y pruebas del visor](mars-guide-design.md).

## Preparación en supervivencia

Instala `space-fabric-0.60.0.jar` en cliente y servidor, sustituyendo la versión
anterior, con Minecraft Java 26.1, Java 25, Fabric Loader 0.20.1 y Fabric API
0.145.1+26.1. No hace falta generar otro mundo ni reconstruir una base válida.

1. Prepara un cohete de Marte (nivel 3) o superior, combustible de ida y vuelta,
   traje con protección para Marte, oxígeno personal y alimentos. Consulta la
   [flota](rocket-fleet.md) y el coste vigente mostrado por la pantalla de viaje.
2. Lleva un controlador de hábitat, bloques completos para suelo/paredes/techo,
   puerta de esclusa y palancas. Una segunda esclusa permite entrar sin ventear.
3. Para la base, prepara **2 bombonas de oxígeno, 2 baterías cargadas y 2 bombonas
   de agua**. El mínimo para validar una base recién cerrada es un O₂, una batería
   y dos aguas; el segundo O₂ y batería son margen para volver a presurizar tras
   construir o entrar. Esto no incluye el consumo del traje, del viaje ni de
   los procesos de fabricación.
4. Fabrica el O₂ con el electrolizador antes de partir. Puedes llevar también
   electrolizador, hielo y baterías para reponer allí. El cargador solar es opcional:
   exige un informe de Marte; las baterías iniciales se cargan en la mesa.

### Recetas existentes

Las filas separadas por `/` describen la cuadrícula de la mesa, de arriba abajo.

| Componente | Receta |
|---|---|
| 8 paneles presurizados | `ICI / CIC`: I = hierro, C = cobre |
| Controlador | `HCH / ROG / HCH`: H = panel, C = cobre, R = redstone, O = observador, G = vidrio |
| 4 bombonas vacías | ` I  / IGI /  I `: hierro en cruz alrededor de vidrio |
| 2 baterías descargadas | `ICI / IRI`: I = hierro, C = cobre, R = redstone |
| Batería cargada | Batería descargada + 2 redstone + 1 cobre, sin forma |
| Bombona de agua | Bombona vacía + cubo de agua; devuelve el cubo |
| Electrolizador | `HCH / WRW / HCH`: H = panel, C = cobre, W = cubo de agua, R = redstone |
| Oxígeno e hidrógeno | Electrolizador: agua + batería cargada + bombona vacía, 10 segundos |
| Agua a partir de hielo | Electrolizador: hielo admitido + batería cargada + bombona vacía, 5 segundos |

En el electrolizador las entradas son las tres primeras casillas. Oxígeno e
hidrógeno salen en las casillas 4 y 5, la batería descargada en la 6. Retira los
productos para permitir el siguiente lote. No se añaden máquinas, recursos ni
recetas, y ninguna receta obligatoria de esta etapa depende de tecnología de Venus.

## Recorrido y condiciones reales

1. **Aterrizar.** Selecciona Marte, despega y espera a tocar suelo y recuperar el
   estado detenido. Aparece el aviso de expedición y la guía. Baja con tu traje.
2. **Establecer o restaurar un refugio.** Construye, por ejemplo, una envolvente
   de 7×7×5, interior de 5×5×3. Coloca el controlador dentro, con aire inmediatamente
   encima de su toma. Ábrelo con clic derecho para elegirlo como tu refugio.
   Puedes abrir otro controlador para cambiar de base mientras la etapa esté
   pendiente. Elegir el controlador solo marca este primer paso.
3. **Sellar.** Cierra suelo, paredes, techo y puertas. En Marte sirven bloques
   completos comunes. La esclusa necesita redstone; usa dos puertas en serie para
   acceder sin exponer la sala principal. Una losa, una tubería en la pared, una
   puerta abierta, la toma tapada, exceder 512 bloques de aire/radio 12 o alcanzar
   un chunk descargado impiden reconocer un recinto sellado.
4. **Oxígeno y energía.** Fila superior del controlador: bombona de O₂ a la
   izquierda, batería cargada en el centro. Espera su ciclo de funcionamiento
   (hasta un segundo) y el estado **Presurizado y protegido**. El servidor requiere
   que haya consumido suministros, presurizado la habitación y siga funcionando,
   con **al menos 600 O₂ y 1500 de energía internos**. Llevarlos encima o dejarlos
   sin procesar en entradas bloqueadas no cumple este paso.
5. **Agua almacenada.** Pon **2 bombonas llenas en la tercera casilla de la fila
   superior del mismo controlador**. En Marte esta casilla conserva agua; no se
   consume como refrigerante. También se acepta agua procesada en depósitos de
   líquidos completamente dentro de esa habitación: se suman ambas reservas
   hasta 2000 unidades. Los depósitos son opcionales porque su receta usa una
   placa de Venus. No cuentan otro cuarto, tanques expuestos al exterior, envases
   en espera en un depósito, refrigerante enfriado ni el inventario del jugador.
6. **Validar dentro.** Entra en la sala y mantén todas las condiciones a la vez.
   La revisión ocurre una vez por segundo. Un refugio válido puede completar
   varios pasos simultáneamente al restaurarlo. Estar al lado, encima de él,
   montado en un vehículo, muerto o en espectador no completa la etapa.

La recompensa es el avance y el desbloqueo personal de **«Investigar un
asentamiento marciano»**. Se muestra como próximo objetivo narrativo; esta
entrega no incluye su búsqueda, coordenadas, encuentros ni segunda misión.
No concede objetos, experiencia ni funciones de recompensa.

## Por qué esas cantidades

- Una bombona de O₂ o batería aporta **2400** unidades internas al controlador;
  cada reserva admite **9600**. Presurizar cuesta **2 × volumen** de aire y luego
  se consume **1 O₂ por ocupante y segundo**, con **5 de energía por segundo**.
- Los mínimos de **600 O₂ y 1500 energía**, medidos después de presurizar, cubren
  **300 segundos para dos ocupantes**, sin fugas. Incluso a volumen máximo de
  512, una bombona y una batería bastan para superar el mínimo inicial.
- Las **2000 unidades de agua** son dos envases de 1000 del transporte existente.
  Son una reserva inicial accesible para dos lotes posteriores de electrólisis;
  cada lote también necesita batería y bombona vacía. No se consumen como pago
  de misión ni representan un sistema nuevo de sed.
- Abrir al exterior ventea el O₂ con las reglas actuales. Trajes, daños ambientales,
  tiempos de exposición, energía, recetas y protección siguen usando sus sistemas
  existentes. Completar una misión no vuelve segura una base averiada.

## Persistencia y compatibilidad

`MarsProgress` usa el mismo patrón de `SavedData` del servidor que la guía de
Venus, con clave nueva `space_fabric:mars_expedition`, por UUID de jugador. Guarda
llegada, controlador elegido, hitos observados y finalización. Se conserva al
morir, cambiar de dimensión, reconectar y reiniciar; un mundo previo empieza sin
misión registrada. El agua, suministros y máquinas conservan sus NBT existentes.

Las marcas de la guía pendiente representan las **condiciones actuales**: una
fuga o una reserva insuficiente vuelve a desmarcar el paso correspondiente. Los
hitos históricos se conservan, pero no permiten combinar condiciones alcanzadas
en momentos o bases diferentes para completar. Los chunks descargados nunca se
cargan a petición de la misión ni se consideran prueba de que la base funciona.
Al volver, se revalidan el cierre y las máquinas.

La finalización solo se registra una vez. Los avances vanilla se reconcilian de
forma idempotente al volver a Marte o consultar `/marte`, sin recompensas
materiales repetibles. Cada jugador debe aterrizar y seleccionar su controlador;
pueden compartir una base operativa, pero sus expediciones son independientes.
La guía de Venus mantiene sus datos, tareas y canales de red. El prototipo
esférico queda fuera de esta progresión.

## Comprobaciones y prueba pendiente en Minecraft

La compilación usa Java 25 y las APIs reales de Minecraft 26.1/Fabric. Las pruebas
automáticas cubren las reglas de aterrizaje, estados por jugador, finalización
simultánea e idempotente, sellado con bloques reales, consumo de suministros,
electrólisis, agua procesada y guardada, límites de reservas, codecs de avances,
paquetes y caducidad del HUD. El progreso se escribe y vuelve a leer como NBT
comprimido en disco, antes y después de completar. Los controladores y depósitos
se guardan/restauran con los métodos nativos de BlockEntity.

**No se ha ejecutado esta expedición dentro de un cliente Minecraft ni en una
sesión multijugador.** Las pruebas de NBT y lógica no sustituyen un reinicio de
un servidor de juego; las comprobaciones de render por CPU no validan el aspecto
del HUD en pantalla. La lista de comprobación manual es:

- Realiza el recorrido anterior en supervivencia, comprobando que la guía empieza
  al tocar suelo y se actualiza al segundo siguiente de cada cambio.
- Antes de añadir agua, desconecta y reinicia el servidor: llegada y coordenadas
  deben conservarse; el cierre y las reservas se deben volver a comprobar.
- Retira O₂/energía, tapa la toma o abre ambas puertas: no debe completarse. Llevar
  agua en el inventario o almacenarla en otro cuarto tampoco debe completar.
- Restaura todo, introduce el agua, entra y espera el aviso final. Reconecta,
  reinicia, muere/reaparece y realiza otra llegada: el desbloqueo sigue guardado
  y no se repite el aviso de recompensa.
- Con otro jugador, confirma que tu progreso no se le atribuye y que él puede
  completar su propia etapa seleccionando el mismo refugio tras aterrizar.
- Comprueba la guía a varias escalas GUI, F1, menús, viaje de vuelta y una visita
  a Venus; confirma que sus misiones siguen mostrando su progreso anterior.

## Continuación

Desde 0.61.0 puedes [investigar un asentamiento marciano](mars-settlement-expedition.md)
tras completar el refugio. Usa `/marte expedicion` o el botón **Expedición II**
en la consola de entrenamiento.
