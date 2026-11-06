# Categorías del inventario creativo — 0.53.0

El contenido de Space Fabric se organiza en ocho pestañas según su utilidad:

| Categoría | Contenido |
| --- | --- |
| Bloques y menas | Terrenos planetarios, hielos, minerales y bloques de almacenamiento |
| Materiales y componentes | Lingotes, gemas, polvos, piezas, cristales y recompensas de criaturas |
| Herramientas y armas | Escáner, brocas, lentes, diarios y navegador; herramientas y armas registradas como tales |
| Trajes y protección | Los cuatro trajes, Venus reforzado y botas térmicas |
| Cohetes y vehículos | Cohetes, kits de mejora, rover, aeróstato, mejora del rover y plataforma de aterrizaje |
| Máquinas y redes | Refinería, máquinas, consolas, módulos de hábitat, conexiones, tuberías y depósitos |
| Suministros y mantenimiento | Combustible, recipientes, células de energía, refrigerantes, filtros, recubrimientos y reparación |
| Criaturas y bosses | Huevos de aparición de los comerciantes, fauna y bosses del mod |

Abre el inventario en creativo para ver las pestañas. Si hay varias páginas de
pestañas, utiliza las flechas para recorrerlas. La búsqueda general conserva los
objetos del mod. Los nombres están traducidos al español y al inglés.

La categoría de herramientas y armas organiza los objetos existentes; esta entrega
no añade armas. Los objetos tienen una sola categoría principal, y los materiales
son el destino predeterminado para recursos que no corresponden a otra familia.

## Actualización

Sustituye el JAR anterior por **0.53.0** en el cliente/perfil que utilizas y reinicia
Minecraft. Esta entrega también contiene los veinte iconos nuevos de los trajes,
añadidos en 0.52.1. La versión 0.52.0 conserva los iconos anteriores.
En multijugador, utiliza la misma versión del mod en cliente y servidor.

Esta organización afecta al inventario creativo. Las recetas, propiedades,
identificadores y objetos guardados conservan su comportamiento. El visor de
recetas existente sigue mostrando sus recetas y usos.

## Implementación y pruebas

`ModCreativeTabs` conserva la clave de registro `space_fabric:materials` y añade
siete pestañas. Los objetos se recogen del registro real de ítems del mod cuando
se construye el contenido de cada pestaña. Se clasifican por tipo, componentes y
función; se ordenan de forma estable por identificador. Esto evita mantener listas
manuales que puedan omitir objetos de los módulos nuevos.

Dos pruebas nativas construyen las ocho pestañas, verifican sus iconos y contenido,
comparan la unión con todos los ítems registrados y rechazan duplicados. Comprueban
además ejemplos de cada categoría y todas las piezas de las redes industriales.

Pendiente revisión visual en Minecraft del orden, páginas, títulos y búsqueda.
Las pruebas de contenido no sustituyen esa comprobación del inventario abierto.
