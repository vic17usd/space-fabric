# Colonias de Marte y Venus — 0.29.0, actualizada en 0.31.0

Esta entrega construye el ciclo jugable inicial: hielo → agua → oxígeno e
hidrógeno, baterías, habitaciones presurizadas y análisis de muestras. Incluye
recetas, texturas originales, inventarios persistentes y avances. El diseño de
las siguientes entregas está en [el plan de desarrollo](planet-development-roadmap.md).

Instala `space-fabric-0.31.0.jar` en cliente y servidor con Fabric API para 26.1.
Los bloques se pueden fabricar en partidas existentes. El nuevo hielo subterráneo
aparece en chunks nuevos de Marte; también se admite hielo vanilla y agua traída
mediante cubos, así que el ciclo no depende de regenerar una dimensión.

## Construir el primer hábitat

1. Prepara baterías cargadas, bombonas de oxígeno y un controlador. Fabrica primero
   el analizador y el electrolizador; se pueden operar con traje o en la Tierra.
2. Construye una habitación de 7×7×5 bloques exteriores: interior de 5×5×3.
   Cierra piso, paredes y techo. Usa paneles y ventanas presurizadas y una puerta
   de esclusa; se abre con señal de redstone, como una puerta de hierro.
3. Coloca el controlador dentro, sobre el piso. **Deja libre el bloque inmediatamente
   encima:** es su toma de aire. La protección cubre la habitación conectada a esa toma.
4. Abre su inventario y carga oxígeno en la primera casilla, batería cargada en la
   segunda y, en Venus, agua refrigerante en la tercera. Cierra todas las puertas.
5. Usa **Shift + clic derecho con la mano vacía** para consultar el estado. Cuando
   diga **Presurizado y protegido**, el personaje puede estar sin traje dentro.
6. Para entrar y salir sin ventear la habitación principal, construye una pequeña
   cámara con dos puertas. Cierra la puerta exterior antes de abrir la interior.

En Marte se admiten cerramientos de bloques completos comunes. En Venus la
**envolvente exterior** debe usar bloques del tag `space_fabric:pressure_rated_shell`:
paneles, ventanas, puertas de esclusa cerradas y carcasas de las máquinas coloniales.
Los muebles normales pueden quedar dentro de esa envolvente certificada.
Una losa, rejilla, puerta común o conducto abierto no equivale a una pared sellada.
Mantén las tolvas dentro del cerramiento; todavía no existen pasamuros de tuberías.

El escaneo tiene límites de 512 bloques de aire y 12 bloques por eje desde la
entrada. Para Venus, la envolvente certificada también debe contener como máximo
512 posiciones interiores, incluidos muebles. Divide las instalaciones grandes
con paredes y controladores independientes. Un límite excedido, una zona no
cargada o una fuga impiden la protección. No se cargan chunks para buscar un cierre.

El cierre se comprueba otra vez al consultar la protección ambiental. Abrir una
puerta al exterior, romper una pared o cambiar una celda del cerramiento invalida
la habitación. Una fuga pierde la reserva de oxígeno; energía y refrigerante se
conservan. Al volver a cerrar se necesita oxígeno para presurizar otra vez.

## Inventario de las máquinas

El controlador tiene su propio panel de diagnóstico desde 0.31.0, conservando
las nueve casillas. Las demás máquinas mantienen su interfaz nativa. Las posiciones
se cuentan de izquierda a derecha por filas:

| Posición | Función |
|---|---|
| 1, 2, 3 | Material, batería y recipiente/reactivo |
| 4, 5, 6 | Producto principal, coproducto y batería descargada |
| 7 | Recipientes devueltos por controlador/generador |
| 8, 9 | Espacios auxiliares |

En el **controlador**, la fila superior es oxígeno / batería / agua. Devuelve
baterías descargadas en la casilla 6 y bombonas vacías en la 7.

En el **cargador solar**, coloca baterías descargadas en la casilla 1; las cargadas
aparecen en la 5. En el **geotérmico**, usa además agua en la casilla 3.

Las tolvas insertan material por arriba, batería/reactivo por los lados y extraen
productos por debajo. Las salidas bloqueadas detienen el proceso sin consumir
los ingredientes. Cada lote consume sus entradas al completarse, de forma atómica.
Cambiar la fórmula reinicia el progreso; perder sol o calor pausa el proceso.
Al romper la máquina se suelta su inventario mediante el comportamiento nativo
de los contenedores. Los suministros ya convertidos en reservas internas no se
recuperan como objetos.

## Agua, oxígeno, combustible e investigación

| Máquina | Entradas | Productos | Duración a 20 TPS |
|---|---|---|---:|
| Electrolizador, fusión | Hielo + batería cargada + bombona vacía | Agua + batería descargada | 5 s |
| Electrolizador, electrólisis | Agua + batería cargada + bombona vacía | Oxígeno + hidrógeno + batería descargada | 10 s |
| Analizador | Mineral + batería cargada + tarjeta de datos | Informe + batería descargada | 10 s |
| Cargador solar | Batería descargada, sol directo | Batería cargada | 20 s |
| Generador geotérmico | Batería descargada + agua, lava debajo | Batería cargada + bombona vacía | 5 s |

El hielo subterráneo marciano forma vetas entre Y=8 y Y=48, exclusivamente en
Marte. El electrolizador también acepta hielo, hielo compacto, hielo azul y
fragmentos de hielo de cráter. Una bombona de agua y un cubo permiten recuperar
un cubo de agua, devolviendo la bombona: sirve para cultivar dentro de la base.
El hidrógeno, polvo de sulfato y una celda vacía producen combustible de cohete,
devuelven la bombona y conectan esta industria con el sistema de viajes existente.

El analizador obtiene informes de Marte de olivino marciano o sulfatos; de Venus,
de olivino venusiano, piroxeno o azufre. El informe marciano es ingrediente del
cargador solar; el venusiano, del generador geotérmico. Son objetos comerciables
y consumibles al fabricar. Se pueden producir nuevos informes analizando muestras.
Las recetas de emergencia permiten cargar una batería con cobre y redstone antes
de disponer de generadores. Los avances del menú **L** orientan esta primera cadena.
El visor **K** muestra tanto los crafteos como las fórmulas de las máquinas.

## Condiciones y balance

- El cargador solar requiere cielo abierto, fase diurna del reloj local y ausencia
  de lluvia en su posición. En Venus no produce, debido a su cubierta de nubes.
- El geotérmico requiere lava directamente debajo, incluida la lava espesa del
  mod, y consume agua por lote. Una roca caliente común no basta.
- Cada bombona/batería aporta 2.400 unidades de reserva; capacidad máxima 9.600.
  La puesta en presión consume dos unidades de oxígeno por bloque de habitación.
  Después se consume una unidad por ocupante vivo cada segundo de juego.
- El mantenimiento consume 5 unidades de energía por segundo en Tierra/Marte,
  o 15 de energía y 5 de refrigerante en Venus. Un suministro aislado de energía
  dura hasta 8 minutos en Marte o 2 min 40 s en Venus. Estos son balances de juego,
  no cálculos de ingeniería de recipientes, electrólisis ni refrigeración real.
- La protección incluye hipoxia, presión, exposición térmica y lluvia ácida del
  sistema ambiental del mod. No elimina la gravedad marciana, el daño físico de
  lava/bloques ardientes, el ahogamiento bajo agua ni ataques de criaturas.
- Esta versión certifica hábitats en Tierra, Marte y Venus. No concede protección
  para gigantes gaseosos ni otros cuerpos con esta carcasa.

## Alcance y verificación

Implementado: construcción, cierre de habitaciones, recursos y automatización
por tolvas, generación de hielo, investigación inicial, recetas y avances.
Los cultivos usan la mecánica vanilla en un espacio protegido e iluminado.
Todavía no hay irrigación automática, fotosíntesis conectada al depósito, rovers,
contratos de aldeanos ni simulación de corrosión de máquinas. Desde 0.31.0, la niebla planetaria de suelo se retira cuando el jugador y la
cámara están dentro de una sala regulada y verificada. El controlador y un
indicador en pantalla muestran reservas, presión/temperatura reguladas y fallos.
Consulta [el comportamiento y sus límites](habitat-interiors.md).

Las pruebas verifican fugas y puertas, muestreo acotado, mobiliario interior de
Venus, reservas y presión, transacciones de todas las fórmulas, recipientes
retornados, guardado NBT, recetas y avances mediante los codecs de 26.1, y la
asignación del hielo al bioma marciano. El chequeo nativo de materiales verifica
el vidrio translúcido sin GPU. La partida real, las tolvas y el recorrido por
esclusas requieren también comprobación interactiva; no se han observado aquí.
