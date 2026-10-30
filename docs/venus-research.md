# Proyecto Aurora: expediciones encadenadas — 0.46.0

Instala 0.46.0 en cliente y servidor, sustituyendo el JAR anterior. La cadena utiliza
el **Refugio de recuperación**, **Laboratorio de Venus** y **Estación atmosférica**
que ya se generan. Funciona en estructuras existentes que conservan sus consolas
naturales; no modifica edificios colocados ni necesita generar una estación nueva.
Las expediciones anteriores y sus recompensas siguen funcionando por separado.

## Comenzar

Fabrica el **Diario del Proyecto Aurora** con un libro, una tarjeta de datos vacía
y un lingote de cobre. La receta aparece en **K**. Usa el diario para leer los
objetivos; **Shift + clic con el diario sobre una consola natural de expediciones**
valida una entrega. Los materiales salen del inventario principal, no de los
huecos de la consola, la mano secundaria ni los depósitos.

También puedes consultar **`/venus investigacion`**, sin permisos de administrador,
o las páginas añadidas al navegador. El indicador transparente de misiones muestra
la cadena cuando has terminado los objetivos iniciales de Venus. Puedes empezar
la investigación antes de completar esa guía: solo cambia la prioridad del HUD.

Los sitios se descubren explorando y pueden marcarse en el atlas. Este sistema no
utiliza `/locate`, carga chunks distantes ni entrega coordenadas de lugares desconocidos.

## Las cuatro entregas, en orden

| Etapa | Lugar y prueba | Materiales entregados |
|---|---|---|
| 1. Archivos | Consola natural del Refugio de recuperación con integridad ≥70 % | Una tarjeta y una batería cargada |
| 2. Restauración | Consola natural del laboratorio y sistemas indicados debajo | Un informe venusiano y una batería cargada |
| 3. Atmósfera | Consola natural de la Estación atmosférica; consola y jugador a Y 280–320; colector adyacente reparado y un lote completado | Una bombona vacía y una batería cargada |
| 4. Calibración | Volver a **la misma consola del laboratorio**, con la estación operativa | Dos olivinos venusianos, dos piroxenos, dos cristales de azufre, un informe, una tarjeta y una batería cargada |

La batería se devuelve vacía en cada entrega. La bombona de la tercera etapa
representa la muestra del proyecto y se devuelve vacía al completar la calibración.
La muestra está registrada en el progreso del servidor; no hay un objeto de misión
que pueda perderse o intercambiarse para saltar etapas.

Al restaurar el laboratorio se guardan las coordenadas de su consola. La
calibración y la fabricación posterior deben hacerse allí. No puedes cambiar el
laboratorio vinculado simplemente visitando otro. Conserva esa consola natural:
romperla y colocar una consola fabricada elimina su condición de sitio natural.

## Qué exige la restauración

La consola debe tener al menos el 70 % de integridad y no estar en parada térmica.
En un radio de doce bloques de ella, la comprobación busca un único hábitat que
cumpla todo esto:

- Controlador con integridad ≥70 %, sala sellada y soporte vital activo. La consola
  debe estar protegida por esa sala.
- Intercambiador cargado, adyacente al controlador y protegido por la misma sala.
- Analizador protegido por esa sala cuyo último lote sea un análisis venusiano.
- Invernadero protegido por esa sala cuyo último lote sea trigo hidropónico.
- Componentes con integridad ≥70 % y sin parada térmica.

Se admiten los lotes ya completados por equipos de esa estación; no hace falta
fabricar máquinas nuevas. Pueden reconstruirla varios jugadores. El avance y las
entregas son personales, mientras que la estación restaurada puede ser compartida.
La etapa atmosférica exige también integridad ≥70 % y un lote real de captación
en el colector pegado a su consola. Una consola fabricada no sirve como sitio natural.

Si faltan pruebas, el diario describe el objetivo. Si faltan materiales, el chat
muestra el coste completo. Un intento rechazado no consume materiales ni avanza.

## Mejoras desbloqueadas

Completar la calibración entrega **una unidad de cada mejora**, una sola vez por
jugador y mundo. No desbloquea ni altera las capacidades de los cohetes o los trajes.
Las mejoras se pueden comerciar; la investigación limita su fabricación, no quién
puede instalar un módulo obtenido de otro jugador.

### Módulo de eficiencia del rover

Haz clic con el módulo sobre un **rover terrestre aparcado y sin ocupantes**.
Se consume al instalarlo. No se instala en aerostatos ni se puede acumular.

- Reduce un **50 % el coste de energía de tracción**.
- Reduce un **50 % el consumo de refrigerante** cuando el rover ocupado está en Venus.
- Mantiene el consumo eléctrico auxiliar, oxígeno, capacidades de depósitos,
  integridad y límites de presión y temperatura.
- No rellena reservas ni repara el casco.
- Se guarda en la entidad y en el rover recogido como ítem. Sobrevive a la recarga,
  reparación y reposición de suministros. El tooltip del rover recogido lo indica.

### Lente de prospección profunda

Pon el **escáner geológico en la mano secundaria** y la lente en la principal.
Úsala en el aire. Se consume al instalarse y no admite otra lente encima.

Amplía el sondeo de **9×9 y 64 capas** a **13×13 y 96 capas**. Conserva los puntos
anteriores y la importación al atlas. El coste sigue siendo una batería por pulso,
con diez segundos de enfriamiento entre pulsos.

El presupuesto sigue limitado a 512 celdas por tick compartidas entre dos trabajos;
la zona más grande tarda más en examinarse. Solo observa chunks ya cargados. La
lente no amplía el área de la perforadora ni garantiza detectar una veta.

## Fabricar más módulos

Tras completar la cadena, usa **Shift + diario en el laboratorio vinculado**, aún
operativo, para fabricar un nuevo par. Cada entrega consume:

- Cuatro placas compuestas venusianas.
- Dos cerámicas de piroxeno.
- Una tarjeta de datos.
- Una batería cargada, cuya celda vacía se devuelve.

Entrega ambos módulos. Es una fabricación manual validada por el servidor;
no es una receta de mesa ni una fórmula ejecutable por tuberías. El visor K
muestra sus ingredientes y aclara la condición del proyecto. Repetir la interacción
con materiales suficientes fabrica otro par y paga de nuevo.

## Guardado y límites

El servidor guarda la etapa y el laboratorio por UUID de jugador, en SavedData
`space_fabric:venus_research`. Cambiar de dimensión, desconectarse o morir no
reinicia la investigación. Los avances se validan en el servidor, sin un mensaje
cliente que permita declararlos completados. Se registra cada avance antes de
entregar la recompensa para impedir cobros repetidos por una segunda interacción.

No se alteran los registros de expediciones anteriores, las siete observaciones
de la guía inicial, sus recompensas, ni las recetas existentes. Los guardados
anteriores del rover cargan con la mejora desactivada.

Si el inventario no admite una recompensa, Minecraft la entrega mediante su
mecanismo habitual de retorno al inventario, que puede dejar objetos en el suelo.
Reclama en un lugar seguro y con espacio disponible.

## Comprobaciones en partida pendientes

Las pruebas automáticas cubren las reglas, pagos, progresión, paquetes, recursos,
mejoras, inventarios y serialización nativa. Falta recorrer la cadena en una partida:

1. Crear el diario, leerlo y comprobar su interacción con una consola y con la búsqueda K.
2. Intentar etapas desordenadas y sin materiales; confirmar que no consume nada.
3. Restaurar un laboratorio real, abrir una puerta o cortar el soporte y comprobar
   que la validación rechaza una estación que ya no funciona.
4. Visitar la estación atmosférica, volver al laboratorio vinculado y calibrar.
5. Reclamar una vez, repetir con y sin materiales y comprobar el coste de fabricación.
6. Instalar los módulos, guardar, recoger el rover y reconectar; revisar efecto y tooltip.
7. Probar dos jugadores, persistencia tras muerte y el HUD con distintas escalas de interfaz.
