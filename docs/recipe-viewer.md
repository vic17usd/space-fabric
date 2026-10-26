# Visor de recetas y usos — 0.19.0 / Fabric 26.1

El mod incluye una lista de ítems y un visor de recetas de estilo JEI, integrado
en el propio JAR. Muestra los ítems registrados de Minecraft y del mod, con sus
nombres y texturas, una búsqueda y una ficha de fabricación o procesamiento.

## Cómo abrirlo

- Pulsa **K** mientras estás jugando. Se puede cambiar en Opciones → Controles → Inventario.
- O pulsa **Recetas**, arriba a la derecha del inventario de supervivencia o creativo.
- Usa **Volver** o Escape para regresar.

El botón abre una pantalla propia. Al salir del inventario se cierra su sesión de
fabricación con el comportamiento normal de Minecraft; los ingredientes de la
cuadrícula se devuelven de la forma habitual. La consulta de recetas no fabrica
objetos ni mueve ingredientes.

## Buscar y consultar

El catálogo da prioridad a los ítems de Space Fabric y después muestra el resto.
Se puede buscar por nombre traducido, ID o namespace, combinando palabras:

| Búsqueda | Resultado |
|---|---|
| `cohete` | Cohetes y kits con ese nombre |
| `níquel` o `niquel` | Recursos de níquel, ignorando tildes y mayúsculas |
| `titanium_ingot` | Lingote de titanio por su ID |
| `@space_fabric` | Ítems del mod |
| `@minecraft iron` | Ítems vanilla cuyo nombre o ID incluye `iron` |

Hay botones para recorrer las páginas del catálogo y, por separado, las recetas
del ítem seleccionado.

Dentro del visor, sobre un ítem del catálogo o un ingrediente de la receta:

- **Clic izquierdo o R:** cómo se obtiene mediante recetas.
- **Clic derecho o U:** en qué recetas se utiliza.

Si el campo de búsqueda tiene el foco, las letras se escriben en él. Haz clic
en un ítem para consultar y dejar de escribir. También se puede cambiar entre
**Recetas [R]** y **Usos [U]** con los botones de la ficha.

Cada ingrediente alterna entre sus opciones válidas, por ejemplo carbón/carbón
vegetal o distintas maderas de un tag. Se muestran las cantidades del resultado,
la estación necesaria y, cuando corresponde, tiempo de procesamiento y XP.
**Recargar** repite la consulta al servidor.

## Recetas incluidas

Se usan las recetas reales cargadas por el servidor, incluidas las de datapacks.
No hace falta haberlas desbloqueado previamente en el libro vanilla para consultarlas.

- Fabricación con forma y sin forma.
- Horno, alto horno, ahumador y hoguera.
- Cortapiedras y herrería con un display estándar.
- Cohetes básicos, kits planetarios, trajes, minerales y almacenamiento del mod.
- Las dos fórmulas de la **refinería de combustible**, incluidas sus alternativas:
  carbón/carbón vegetal + polvo de blaze + celda vacía; o cristal de azufre +
  condensado ácido/polvo de sulfato + celda vacía.

Las fórmulas de la refinería se comparten con el motor de la máquina en
`RefineryRecipes`; no hay una copia independiente que pueda quedar desactualizada.

Un ítem que solo se obtiene por minería o como drop puede no tener recetas de
obtención; **Usos** permite encontrar sus aplicaciones. Este visor no es una lista
de botín ni un simulador de generación de minerales. Las recetas especiales sin
un display estático estándar y la elaboración de pociones no forman parte de esta
primera versión. El catálogo muestra la instancia base de cada ítem, sin enumerar
todas las variantes posibles de componentes/NBT. Se muestran hasta nueve espacios
y hasta 64 alternativas por espacio; recetas de otros mods que superen esos límites
necesitan una integración específica.

## Servidores y actualizaciones

Instala **space-fabric-0.19.0.jar en cliente y servidor**, manteniendo Fabric API.
El servidor recibe el ítem, el tipo de consulta y la página, y devuelve una sola
ficha. El cliente no decide ingredientes, resultados ni acciones de inventario.
Los índices se invalidan al recargar datapacks y al detener el servidor. Las
respuestas antiguas de otra selección se descartan mediante un identificador
de consulta. Si no llega respuesta, se ofrece Recargar.

No requiere regenerar el mundo, fabricar un objeto de guía ni instalar otro visor.
Los cohetes y texturas de las versiones anteriores se conservan.

## Archivos y verificación

- `recipe/RecipeCard`: resolución de displays y alternativas de ingredientes.
- `recipe/RecipeCatalog`: índices de obtención y usos.
- `recipe/RecipeViewerNetwork`: consultas y respuestas con codec de Minecraft.
- `recipe/RecipeSearch`: búsqueda por nombre, ID y namespace.
- `client/recipe/RecipeViewerClient`: tecla, botón de inventario y receptor de cliente.
- `client/recipe/RecipeViewerScreen`: catálogo, búsqueda, fichas y navegación.
- `machine/RefineryRecipes`: fórmulas compartidas con la máquina.

La compilación comprueba la interfaz contra las APIs reales de 26.1. Las pruebas
cargan los JSON del mod, construyen índices con los displays y tags nativos,
consultan recetas y usos, comprueban las alternativas de la refinería contra la
máquina real y codifican/decodifican las fichas con `RegistryFriendlyByteBuf`.
También se comprueban búsquedas con tildes, IDs y filtros de namespace.
La inspección visual del visor y la prueba interactiva en multijugador siguen
pendientes; las pruebas automáticas no arrancan una GPU ni una partida.
