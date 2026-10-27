# Traje y armadura a la vez — 0.21.0 / Fabric 26.1

Los trajes espaciales disponen de **cuatro espacios propios**, independientes de
los espacios de armadura de Minecraft. Puedes llevar, por ejemplo, armadura de
netherita y un traje venusiano completo simultáneamente.

## Equipar y quitar

1. Pulsa **J**, o abre el inventario y pulsa **Traje espacial**, debajo de Recetas.
2. Coloca casco, pechera, pantalones y botas en sus espacios correspondientes.
3. Deja tu armadura habitual en los espacios normales del inventario.
4. Cierra con Escape. El traje sigue equipado y guardado aunque cierres la pantalla.

El clic derecho con una pieza en la mano la equipa en su espacio de traje si está
vacío, sin reemplazar la armadura. Si está ocupado, retira primero la pieza desde
la pantalla. Shift-clic envía las piezas del inventario a su espacio válido y
permite devolverlas al inventario. Cada espacio acepta una única pieza de traje
de su categoría corporal; no acepta armadura corriente, minerales u otros objetos.
La tecla se puede cambiar en Opciones → Controles → Inventario.

El estado de la pantalla indica si el conjunto está completo o mezclado. Siguen
siendo necesarias las cuatro piezas de la misma familia para aportar protección
ambiental. Un conjunto incompleto o combinado no se sella.

## Armadura, ambiente y apariencia

- La **armadura normal** aporta defensa y sus efectos/encantamientos habituales.
- El **traje independiente** aporta oxígeno y su protección contra presión,
  temperatura y corrosión, según la familia. No añade una segunda barra de defensa.
- Las botas de traje del espacio independiente conservan la protección térmica
  al pisar las rocas calientes del mod.
- La corrosión ambiental puede desgastar el traje si no resiste el ácido.
- No se aplican los atributos o encantamientos de combate del traje a través de
  estos espacios adicionales. Se conserva su reparación normal mediante materiales.

Se mantienen las texturas de 0.20.0. El traje se dibuja como capa más ajustada al
cuerpo y la armadura normal queda por encima: esta puede cubrir parte del diseño
del traje. No se intenta dibujar dos armaduras en la misma superficie. La capa
se sincroniza para otros jugadores y sigue los movimientos del modelo humanoide.
La ocultación del piloto dentro del cohete se conserva.

## Actualizar una partida existente

Instala **space-fabric-0.21.0.jar en cliente y servidor** y reinicia. No hace falta
regenerar terreno ni volver a fabricar las piezas.

Por compatibilidad, un conjunto completo colocado en los espacios normales de
armadura sigue proporcionando su protección ambiental mientras permanezca allí.
Al abrir la pantalla del traje, si sus cuatro espacios están vacíos y detecta un
conjunto completo en la armadura, lo traslada entero a los nuevos espacios. Así
se evita una transferencia manual que deje al personaje temporalmente expuesto.
Aparece un aviso al realizar el traslado.

La migración no traslada armadura corriente, no reemplaza piezas ya guardadas en
los espacios de traje y no mezcla piezas entre ambas ubicaciones para completar
un conjunto. Si hay dos conjuntos completos, se prioriza el del espacio de traje.

## Guardado y muerte

Las cuatro piezas, con daño y componentes, se guardan por jugador mediante un
attachment persistente de Fabric. Se conservan al salir, reconectar y cambiar de
dimensión. El menú es una vista de ese mismo almacenamiento, no una copia aparte.

| Situación | Resultado |
|---|---|
| Muerte con `keepInventory=true` | Conserva el traje |
| Muerte con `keepInventory=false` | Las piezas caen y los espacios quedan vacíos |
| Maldición de desaparición | La pieza no cae al morir con pérdida de inventario |
| Maldición de ligamiento | Impide retirarla en supervivencia; creativo permite retirarla |
| Traslado de dimensión/recreación sin muerte | Conserva el traje |

Las reglas de muerte también se aplican cuando la pantalla estaba abierta. El
menú consulta siempre el almacenamiento actual para evitar conservar una copia
obsoleta que pueda duplicarse al cerrar.

## Código y comprobaciones

- `equipment/SuitLoadout`: cuatro piezas, snapshots, codec persistente y de red.
- `equipment/SuitEquipment`: attachment, migración, apertura, muerte y corrosión.
- `equipment/SuitContainer`: vista del almacenamiento y límites de los espacios.
- `equipment/SuitItem`: equipamiento con clic derecho.
- `equipment/SuitProtection`: prioridad del traje independiente y compatibilidad.
- `menu/SuitMenu` y `client/menu/SuitScreen`: contenedor y pantalla.
- `client/equipment/SuitEquipmentClient`: tecla y botón.
- `client/equipment/SuitLayer` y `mixin/client/SuitRendererMixin`: captura del traje
  sincronizado y renderizado nativo de la capa bajo la armadura.
- `space_fabric:suit_inventory`: identificador del attachment y del menú.

Las pruebas usan los registros, tags, contenedor, encantamientos y codecs nativos:
protección con armadura de netherita, conjuntos incompletos y compatibilidad,
guardado NBT y red, conteo al retirar, rutas de Shift-clic, tipos de espacios,
política de muerte, desaparición y migración sin reemplazar armadura normal.
También se comprueban los descriptores exactos del renderer al que se inyecta la
capa. La compilación y las pruebas no arrancan una partida: la comprobación visual
con armadura, multijugador, reconexión real y muerte dentro del juego sigue pendiente.
