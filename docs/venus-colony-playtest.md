# Comprobaciones de la colonia en partida — 0.40.0

Estas comprobaciones están preparadas para ejecución dentro de Minecraft; no
se presentan como resultados observados. Usa un mundo de pruebas con cliente y
servidor 0.40.0. El perfil planetario de exposición mantiene la escala 1:72.

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
