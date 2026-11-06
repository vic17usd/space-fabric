# Refugios presurizados en Mercurio

El controlador de hábitat existente funciona en Mercurio con el mismo escáner
que en Marte. Construye una habitación completamente cerrada con bloques completos,
incluidos suelo y techo. Coloca un controlador con un bloque de aire encima y
mantén cerradas las puertas; una esclusa con dos puertas reduce las fugas. El
escáner admite hasta 512 bloques de aire y un radio de 12 desde la toma. Los bloques
de presión son compatibles, pero Mercurio no usa el requisito de paredes reforzadas
de Venus. Si falta un chunk, la habitación no se considera verificada.

Pon bombonas de oxígeno en la primera entrada y baterías cargadas en la segunda;
el controlador devuelve bombonas vacías en la casilla 7 y baterías vacías en la 6.
Deja espacio en ambas salidas. Al presurizar se gastan 2 unidades de oxígeno por
bloque de aire; cada segundo posterior gasta 1 por ocupante y 5 de energía, también
con cero ocupantes para la energía. Cada bombona o batería aporta 2.400 unidades y
la capacidad interna de cada reserva es 9.600. La habitación se despresuriza si
faltan suministros, se abre una fuga o cambia su volumen. Estos costes templados
son una regla provisional, no un modelo del calor de Mercurio.

**La presión no sustituye al traje térmico en Mercurio.** Dentro del recinto activo,
la ruta atmosférica elimina el riesgo de descompresión e hipoxia y repone el aire.
La comprobación térmica exterior sigue activa: el perfil actual usa el intervalo
−180 a 430 °C para el planeta, sin diferenciar día, sombra ni aislamiento del
refugio. Un traje de vacío completo (−200 a 450 °C) cubre ese intervalo. El panel
muestra temperatura desconocida y advierte de la falta de regulación térmica. No se
ha añadido blindaje contra radiación ni resistencia especial a impactos de meteoros;
el techo físico sigue sujeto a las reglas normales del mundo.

Para fabricar el controlador se usa la mesa con `HCH / ROG / HCH`, donde H es
panel presurizado, C cobre, R redstone, O observador y G vidrio. Los paneles se
fabrican con `ICI / CIC` (I hierro y C cobre), obteniendo ocho. El electrolizador
`HCH / WRW / HCH` (W cubo de agua) procesa bombona de agua, batería cargada y
bombona vacía en sus tres entradas para producir oxígeno e hidrógeno. Consulta el
visor de recetas del juego para cantidades y casillas actualizadas. En la mesa se
puede cargar una batería descargada con dos redstone y un cobre.

El cargador solar usa la receta `GGG / CRC / HDH`, donde D es un informe de
investigación marciana: requiere haberlo obtenido o transportado antes. Su receta
es la existente; carga una batería vacía en 400 ticks efectivos con cielo visible
y sin lluvia cuando la fase del reloj de Mercurio está entre 0 y 11.999. El reloj
avanza a 1/175,94 de la velocidad normal: el día completo equivale a unos 58,65 h
reales a 20 TPS. No se acelera el tiempo para esta entrega, así que hay que prever
energía para la larga noche. El equipo, las máquinas o los suministros pueden
transportarse al llegar; la receta solar no se ha alterado.

## Validación y límites

Los tests automáticos comprueban los destinos admitidos, las condiciones solares,
la distinción entre toma bloqueada y zona descargada y la selección de riesgos de
presión y temperatura. Los tests existentes de hábitats prueban el escáner,
recintos, fugas y presupuestos de soporte vital mediante fixtures. Ninguno de estos
fixtures representa una partida completa en supervivencia. La protección dentro y
fuera, la interfaz en 320×240, la carga de chunks y el recorrido de suministros en
Mercurio deben comprobarse también en un cliente real; no se ha hecho en esta
entrega. Una futura primera expedición necesita objetivos, progresión persistente,
instrucciones y recompensas; no se añaden aquí.
