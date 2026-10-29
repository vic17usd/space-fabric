# Navegación y operaciones de Venus — 0.40.0

Instala 0.40.0 en cliente y servidor con Fabric/Fabric API para Minecraft 26.1
y Java 25. No necesita terreno nuevo para sus objetos, indicadores o ambientes.
Las instalaciones de 0.39.0 siguen conservando sus identificadores y objetivos.

## Navegador de supervivencia

Fabrica el **navegador de expediciones** con cobre, redstone, papel, brújula e
informe venusiano. La receta aparece en K. Usa la interfaz nativa de libro para
el diario y el panel de diagnóstico; no requiere otra biblioteca de pantallas.

- Clic en aire: abrir el diario con coordenadas, rumbo y distancia al objetivo.
- Shift + clic en aire: seleccionar el siguiente destino guardado y abrir el diario.
- Shift + clic sobre un bloque: guardar una baliza en esa posición. La baliza es
  una coordenada del dispositivo; puedes marcar un bloque de magnetita o una
  construcción identificable. No se coloca ni destruye un bloque del mundo.
- Al llevarlo en la mano principal, descubre las consolas naturales cargadas
  de las tres expediciones a **32 bloques**. No localiza instalaciones remotas ni
  genera chunks para buscarlas. Muestra el destino seleccionado en el indicador.
- Conserva un máximo de **32 destinos**, cada uno con su dimensión. Guardar la
  misma posición vuelve a seleccionarla; las observaciones automáticas no reordenan
  destinos ya descubiertos ni cambian la selección. Al llenarse, el guardado
  manual elimina el más antiguo; la detección automática conserva los existentes.

Los datos pertenecen al objeto y se conservan al guardar o transferirlo. El diario
es una lista de destinos descubiertos, no un mapa topográfico ni un teletransporte.
Los rumbos usan N/NE/E/SE/S/SO/O/NO. La distancia incluye diferencia de altura.

## Panel de la colonia

Con el navegador en la mano, **clic normal en el controlador** abre el informe de
la habitación. La mano vacía sigue abriendo el inventario normal del controlador.
El informe contiene:

- Estado del hábitat y cantidad de equipos con aviso de integridad o calor.
- Reservas internas de O₂, energía y refrigerante, residuos y calor.
- Consumo del soporte vital por segundo según ocupantes y planeta.
- Autonomía estimada hasta agotar la primera reserva local, sin contar depósitos
  conectados, futuros suministros ni consumo de otros procesadores.
- Hasta 32 páginas de máquinas vinculadas a la sala: posición, integridad, filtro,
  lotes terminados y progreso de producción.

Es una **instantánea**: reabre para actualizar. Una habitación sin regulación
muestra autonomía cero. Sin ocupantes, el cálculo no inventa consumo de oxígeno.
Los lotes indican producción acumulada, no una tasa medida de toda la fábrica.
El panel no carga equipos remotos ni concede protección por sí mismo.

## Nuevos proyectos de campo

Se suman dos proyectos a las tres expediciones existentes. El diario resume sus
instrucciones; las recompensas son una por jugador y mundo para cada proyecto.

1. **Recuperación de caja negra:** lleva una tarjeta de datos y haz Shift + clic
   con el navegador sobre la consola natural del **refugio de recuperación**.
   Consume la tarjeta y recibes datos de caja negra.
2. **Muestreo atmosférico:** lleva una bombona vacía y batería cargada. Haz Shift
   + clic con el navegador en la consola natural de la **estación atmosférica**,
   estando entre Y=280 y Y=320. Consume ambos insumos y devuelve una batería
   descargada junto con la muestra sellada.
3. **Entrega científica:** lleva los datos/muestra en tu inventario principal y
   haz Shift + clic con el navegador sobre un controlador operativo de Venus.
   Cada entrega válida concede **cuatro placas de compuesto**. La muestra devuelve
   además su bombona vacía. Las muestras se vinculan al UUID de quien las obtuvo.

Si pierdes los datos o una muestra, puedes repetir su obtención pagando otra vez
los insumos. Una nueva obtención no reinicia la recompensa ya entregada. No se
consumen objetos de otro jugador ni se aceptan copias sin los datos de propietario.
Las consolas y tarjetas son las existentes: no se añaden construcciones nuevas
ni NPC humanos. Estas tareas recuperan información y transportan muestras.

## Aerostato

El indicador específico muestra altura, velocidad vertical calculada del movimiento
observado y contacto con el suelo. Reservas bajas o casco muy dañado cambian su
color. Conserva los avisos de cabina sin protección y sus controles de 0.39.0.

Las dos lentes delanteras son emisivas y visibles durante el aterrizaje. No
introducen una dependencia de iluminación dinámica ni iluminan bloques del mundo.
Durante el descenso se añaden partículas de guía; el vehículo ocupado tiene un
sonido suave de sistemas activos. Siguen siendo necesarias energía, O₂,
refrigerante y una cubierta despejada para aparcar.

## Ambientes y fauna

En las zonas bajas de Venus se muestrean posiciones ya cargadas cerca de jugadores.
Lava y fuentes corrosivas descubiertas pueden emitir pulsos de humo y sonidos
locales. Los campos de fumarolas tienen un ambiente de lava diferente del resto
del terreno. Se reutilizan sonidos nativos de Minecraft; no se afirman grabaciones
ambientales originales. Los pulsos no destruyen terreno ni generan daño nuevo.

Acidstone Slime y Ashplume Emberwalker incorporan una preferencia de movimiento
hacia fuentes corrosivas próximas. El objetivo tiene menor prioridad que huir
del jugador y otros comportamientos existentes; no teletransporta animales ni
cambia los límites de aparición. La navegación decide si el lugar es accesible.
La fauna agresiva conserva sus reglas nocturnas anteriores.

## Comprobaciones y límites

Las pruebas verifican persistencia y límite del navegador, posiciones duplicadas,
dimensiones distintas, rumbos, cálculo de autonomía, tipo de libro nativo, recetas
y persistencia de recompensas extendidas. Las comprobaciones de geometría incluyen
las lentes emisivas del aerostato. Continúan pasando las pruebas de recursos de
cuatro horas y generación de expediciones de la entrega anterior.

No se ha ejecutado una partida gráfica ni multijugador en esta entrega. La apertura
real del libro, sus páginas/traducciones, el indicador, los sonidos, la selección
de hábitat de los mobs y la obtención/entrega por interacción necesitan comprobación
en juego. La validación nativa y de componentes no equivale a esa observación.
Consulta [el plan de pruebas en partida](venus-colony-playtest.md).
