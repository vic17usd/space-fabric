# Primera expedición de Mercurio

La expedición comienza al aterrizar físicamente en Mercurio desde otro destino,
con un piloto vivo en un cohete intacto. Desembarcar de un cohete ya parado,
teletransportarse o viajar desde Mercurio no registra un aterrizaje nuevo. Los
mundos anteriores no contienen un registro de aterrizaje histórico: un jugador que
ya está en Mercurio debe completar un nuevo viaje y aterrizaje para activarla.

1. Consulta `/mercurio guia` y mantén el traje térmico completo: el refugio actual
   suministra presión y aire, pero no regula la temperatura de Mercurio.
2. Cierra el suelo, paredes y techo de una habitación. Coloca un controlador de
   hábitat con aire encima y cierra las puertas. El límite del escáner existente
   es 512 bloques de aire y radio 12.
3. Mira el controlador y ejecuta `/mercurio refugio`. Puedes elegir otro antes de
   completar la etapa. Un controlador sustituido en la misma posición debe
   seleccionarse otra vez. La selección es personal y varios jugadores pueden usar
   el mismo refugio.
4. Introduce oxígeno y batería cargada en las dos primeras entradas, dejando
   espacio en las salidas para sus envases. Espera a que el controlador presurice.
5. Entra en la habitación. El servidor comprueba una vez por segundo la identidad
   del controlador, el recinto sellado y cargado, la posición de los ojos del
   jugador en su interior y el soporte vital operativo con al menos **600 unidades
   de oxígeno y 1.500 de energía**. Son reservas iniciales de 600 segundos para un
   ocupante y 300 segundos de energía, sin contar gastos adicionales; no cubren la
   larga noche de Mercurio. Solo se cuentan los presupuestos internos reales.

`/mercurio` muestra el objetivo y la instalación elegida; `/mercurio guia` explica
el procedimiento. Consultar el estado no selecciona ni completa la misión. El
progreso se guarda por UUID y sobrevive a guardados y reconexiones. Al completar se
anuncia la futura exploración de hielo polar; todavía no existe esa segunda etapa.
Se concede como recompensa un logro persistente y único; no entrega objetos ni XP. La misión no garantiza combustible para el viaje de vuelta.

El controlador se fabrica con `HCH / ROG / HCH`: panel presurizado (H), cobre (C),
redstone (R), observador (O) y vidrio (G). El panel usa `ICI / CIC` (I hierro,
C cobre), con ocho de resultado. Carga una batería vacía con dos redstone y un
cobre en la mesa. El cargador solar requiere informe marciano y funciona según el
reloj de Mercurio. Consulta [refugios en Mercurio](mercury-habitats.md) para las
recetas, el clima y las limitaciones de protección.

## Validación

Las pruebas automáticas comprueban la regla de aterrizaje, selección, identidad,
progreso personal, rechazo de observaciones falsas y serialización. Las pruebas de
hábitat existentes usan fixtures de escáner y soporte vital. No se ha probado el
recorrido en un cliente real ni en supervivencia: deben verificarse el viaje, los
comandos, los chunks descargados y las fugas en una partida real.
