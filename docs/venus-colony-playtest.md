# Comprobaciones de la colonia en partida — 0.42.1

Estas comprobaciones están preparadas para ejecución dentro de Minecraft; no
se presentan como resultados observados. Usa un mundo de pruebas con cliente y
servidor 0.42.1. El perfil planetario de exposición mantiene la escala 1:72.

1. Construye un hábitat válido de Venus y equipa el traje antes de entrar. Abastece
   el controlador. Comprueba el diagnóstico de presión, oxígeno y refrigeración.
2. Instala invernadero y lámpara. Ejecuta trigo y algas con las recetas del visor K.
   Retira luz, abre una fuga y llena una salida, una condición cada vez. Esperado:
   pausa sin gasto de ingredientes; recuperación al restablecer la condición.
3. Conecta agua, CO₂, O₂ y energía mediante redes separadas y pasamuros. Instala
   recuperador con dos envases, batería y filtro. Con ocupantes reales espera
   residuos de respiración; compáralos con el gasto de O₂. Esperado: dos
   recuperadores no procesan el mismo lote de residuos y los filtros se gastan.
4. Activa una estación de servicio y aparca el rover desocupado. Comprueba la
   recarga, reparación y descarga. Llena las salidas y verifica que los objetos
   permanecen en sus inventarios de origen. Las tolvas no deben retirar repuestos
   de la novena casilla. Con estación apagada u ocupante no debe atenderlo.
5. Prueba el aerostato en primera persona y F5, con y sin Sodium. Comprueba visor,
   giro, ascenso, descenso, colisiones, consumo suspendido y aterrizaje en cubierta.
   Compara `/planetinfo` a Y=160, 260, 300 y 340. La cabina sin suministros no protege.
6. Genera las tres expediciones en chunks nuevos mediante las localizaciones de
   `/estructuras`. Repara y opera el equipo, aporta los objetos y reclama. Esperado:
   repetir la interacción, reconectar o cambiar de instalación no repite premio.
7. En multijugador, dos jugadores intentan reclamar en la misma consola. Cada uno
   debe aportar sus propios insumos; cada UUID obtiene un premio por tipo. Repetir
   la petición del mismo jugador no vuelve a entregarlo.
8. Guarda y reconecta con máquinas a mitad de lote, rover cargado y aerostato
   aparcado. Comprueba inventarios, calor, residuos, reservas y estado de estación.
   Aleja a todos los jugadores para descargar chunks y vuelve: no hay producción
   retroactiva ni pérdidas por un supuesto tránsito de recursos en las tuberías.
9. Mantén la colonia funcionando una noche y registra reservas iniciales/finales,
   duración, número de jugadores, TPS y averías. Para una prueba de cuatro horas
   del mundo completo, prepara depósitos y filtros suficientes: la prueba automática
   de componentes repone sus entradas y no certifica esa autonomía en partida.

10. Fabrica un navegador, guarda dos balizas y cambia el destino con Shift + uso
    en aire. Acércate a una consola natural: debe registrarse una vez sin buscar
    instalaciones remotas. Guarda/reconecta y confirma dimensiones y selección.
11. Usa el navegador sobre el controlador para abrir el panel nativo de libro.
    Cambia suministros y reabre: los datos se actualizan. Comprueba que la autonomía
    se describe como reserva local y que una fuga muestra estado inválido.
12. Recupera caja negra con tarjeta, toma una muestra en la estación y entrega
    cada objeto a un controlador operativo. Repite, reconecta y prueba con otro
    jugador: cada UUID recibe una recompensa por proyecto. Los envases se conservan.
13. Comprueba luces, indicador de vuelo y sonido del aerostato con y sin Sodium.
    Observa fuentes volcánicas y dos especies pacíficas cerca de hábitats corrosivos;
    verifica que huir del jugador conserva prioridad y que los pulsos no dañan terreno.

14. Antes de viajar, fabrica el navegador con tarjeta de datos. Usa `/venus guia`
    y `/venus recursos 15 2 75`: debe indicar una bombona de O₂, seis baterías y
    dos de agua para soporte local. Anota además los costes de industria y traje.
15. Llega a Venus protegido y comprueba el aviso de bienvenida. Completa las
    observaciones de refugio, generación, carga térmica, interfaz y trigo. Revisa
    el diario y los avances; reconecta y vuelve desde otra dimensión. El progreso
    permanece, no repite la bienvenida ni concede objetos. Con otro UUID empieza
    una guía independiente. Comprueba también el caso de observar etapas en otro orden.
16. Genera los tres emplazamientos nuevos y recorre su pasillo central. Prueba
    los cuatro botones del vestíbulo: abre solo la puerta exterior con la interior
    cerrada y comprueba que la sala mantiene el sellado. Abre ambas: debe perderlo.
    No retires el traje en el vestíbulo. Revisa cofres, literas y acceso a máquinas.
17. Sobre lava, automatiza un generador con un depósito de agua y acumulador.
    Recircula una misma bombona: cada lote devuelve agua y batería cargada. Llena
    el depósito de salida y después el acumulador, por separado: debe detenerse
    sin perder envases. Retira la lava y comprueba la pausa.
18. Fabrica placas con dos lingotes de hierro, cerámica y batería; compara con
    un trozo de hierro denso. Tiempos esperados: 20 y 15 segundos. Guarda a mitad
    de lote y reconecta; no debe reiniciarse, duplicar productos ni perder batería.

19. En Venus, verifica las misiones arriba a la izquierda sin un rectángulo de
    fondo y sin llevar el navegador. Compara con la referencia: título blanco,
    línea fina y círculo cian hueco con un único objetivo. Completa una etapa y
    comprueba el cambio de título y objetivo. Prueba etapas posteriores ya completadas, cambia
    de planeta, reconecta y comprueba otro UUID. Abre inventario y pulsa F1:
    debe ocultarse. Revisa escalas de interfaz, idiomas y el aerostato: sus datos
    deben aparecer debajo de la guía. Al completar las seis etapas, muestra fin.
