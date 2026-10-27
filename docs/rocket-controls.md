# Cohetes: identidad, combustible y destinos — 0.21.1

Esta actualización corrige la información y los controles del mapa estelar.

- El encabezado identifica el **modelo real del cohete**: por ejemplo, «Cohete de
  Marte». El destino elegido se muestra por separado como «Destino: Marte».
- El combustible muestra **cantidad actual / capacidad máxima**, con el coste del
  viaje en otra línea. El inventario también muestra el contenido del depósito.
- La barra usa la capacidad recibida del servidor. Todos los modelos actuales
  conservan su depósito de **3000 unidades**; esta corrección no cambia el balance.
- Cada celda aporta 1000 unidades. Se necesita ese espacio libre para consumirla
  completa; el combustible sobrante no se desperdicia. Si no cabe, el clic derecho
  muestra un aviso en vez de fallar silenciosamente.
- Es posible elegir y consultar un destino antes de subir a un cohete vacío y
  detenido. Despegar sigue requiriendo ser su piloto, combustible y casco operativo.

## Por qué puede estar bloqueado un planeta

La pantalla distingue y explica estas situaciones al pasar el cursor:

| Situación | Explicación |
|---|---|
| Todavía no llegaron los datos | Sincronizando; no se presenta una variante supuesta como si fuera definitiva |
| Estás en ese destino | Ya estás en esa dimensión; selecciona otra |
| Otro jugador ocupa el cohete | No puedes cambiar su ruta ni ocupar su asiento |
| El cohete ya vuela | No se permite modificar el vuelo |
| Modelo incompatible | Falta protección térmica, de presión, ácido, polvo, radiación o viento |
| Regla exclusiva del servidor | Solo admite su misión y el regreso a la Tierra |
| Despegue sin piloto | Hay que subir primero |
| Despegue con casco roto o poco combustible | La ficha muestra el motivo correspondiente |

Los controles y el servidor comparten `RocketNavigation`, para que la explicación
corresponda a la condición que bloquea el botón. El servidor conserva las
comprobaciones físicas adicionales de salida y aterrizaje.

## El caso Mercurio / Marte

En las capturas del problema, la etiqueta superior derecha de la interfaz anterior
indicaba **Mercury**: ese campo identificaba la variante del cohete, aunque podía
confundirse con el destino seleccionado. El cohete de Mercurio no lleva filtros
contra el polvo marciano. El cohete de Marte sí admite su propia misión.

La versión nueva pone ambas identidades en encabezados distintos y explica la
falta de filtros. Si el cohete se identifica como Mercurio, instala el **kit de
Marte** sobre el vehículo vacío y detenido; el juego confirmará «Kit instalado:
Cohete de Marte». Conserva combustible y carga. También puedes colocar directamente
`space_fabric:mars_rocket`.

Si ya estás en `space_fabric:mars`, el botón Marte está bloqueado porque ya estás
allí, independientemente de que el cohete sea marciano. Overworld y Tierra siguen
siendo destinos de regreso distintos.

No se convierte automáticamente un cohete de Mercurio en uno de Marte: una
corrección de interfaz no debe cambiar las mejoras que tiene tu vehículo.

## Actualización y pruebas

Sustituye el JAR por **space-fabric-0.21.1.jar en cliente y servidor**, cierra el
juego y reinicia. Los cohetes, mejoras, depósitos y carga guardados se conservan;
no hace falta regenerar terreno. Cambia también la cantidad de datos de la GUI,
por lo que ambos lados deben usar la versión actualizada.

Las pruebas comprueban las diez variantes y sus misiones, la causa polvo en
Mercurio → Marte, la selección antes de subir y el requisito de piloto para el
despegue. Se ejercita la factoría real de colocación de los nueve ítems planetarios,
la conservación al recoger y guardar, las mejoras y los doce campos sincronizados
en un menú nativo receptor. También se verifican las cantidades del tooltip de
combustible, la capacidad de la barra y las causas de bloqueo. La inspección
visual y el vuelo interactivo dentro de una partida siguen pendientes.
