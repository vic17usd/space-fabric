# Nueve destinos en Fabric 26.1

En los nombres oficiales de 26.1 la clase se llama `ResourceKey<T>`.
`RegistryKey<T>` es el nombre equivalente habitual en tutoriales con Yarn.
Este proyecto usa exclusivamente los nombres oficiales.

| ID (`space_fabric:`) | Cielo base | Niebla base | min_y | height | Y máximo incluido |
| --- | --- | --- | ---: | ---: | ---: |
| mercury | #000000 | #272727 | -64 | 384 | 319 |
| venus | #cf8b40 | #b48236 | -64 | 512 | 447 |
| earth | #78a7ff | #c0d8ff | -64 | 384 | 319 |
| moon | #000000 | #676b73 | -64 | 384 | 319 |
| mars | #b96540 | #a35d40 | -64 | 384 | 319 |
| jupiter | #cba27a | #9e7758 | -64 | 768 | 703 |
| saturn | #dfc38d | #bca776 | -64 | 640 | 575 |
| uranus | #93dfdf | #7ac2c2 | -64 | 512 | 447 |
| neptune | #355cbb | #2c4596 | -64 | 512 | 447 |

Estos son límites verticales de construcción. Aumentar `height` no estira el
terreno ni cambia la altura del ruido: los generadores todavía son provisionales
y usan ajustes vanilla. El área horizontal conserva los límites normales de
Minecraft; un radio planetario o un borde propio requiere otra configuración.
Los colores son valores base ambientales; hora, clima y efectos visuales pueden
alterar su aspecto. No se comprobó todavía el renderizado en un cliente gráfico.

`Planet.java` contiene la lista Java con los nueve perfiles y sus parámetros.
`PlanetDimensions.java` expone las tres clases de claves para cada perfil:

```java
ResourceKey<Level> world = PlanetDimensions.key(Planet.MOON);
ResourceKey<DimensionType> type = PlanetDimensions.typeKey(Planet.MOON);
ResourceKey<LevelStem> definition = PlanetDimensions.stemKey(Planet.MOON);
```

Las tres comparten `space_fabric:moon`, pero pertenecen a registros distintos:
`Registries.DIMENSION`, `Registries.DIMENSION_TYPE` y `Registries.LEVEL_STEM`.

Las claves **no crean mundos**. Durante el arranque, Minecraft carga el tipo de
`data/space_fabric/dimension_type/moon.json` y el generador de
`data/space_fabric/dimension/moon.json`. No se usa `Registry.register` sobre estos
registros dinámicos en `onInitialize()`. La comprobación de arranque del mod
confirma que existe un `ServerLevel` para cada destino.

Cada destino tiene dos archivos propios dentro del datapack integrado del mod:

```text
src/main/resources/data/space_fabric/
├── dimension/mercury.json
├── dimension_type/mercury.json
├── dimension/moon.json
├── dimension_type/moon.json
└── ... los siete destinos restantes
```

No necesitas nueve namespaces ni nueve carpetas de datapack independientes.
Minecraft separa automáticamente los datos persistentes por dimensión:

```text
<mundo>/dimensions/space_fabric/mercury/
<mundo>/dimensions/space_fabric/venus/
<mundo>/dimensions/space_fabric/earth/
<mundo>/dimensions/space_fabric/moon/
<mundo>/dimensions/space_fabric/mars/
<mundo>/dimensions/space_fabric/jupiter/
<mundo>/dimensions/space_fabric/saturn/
<mundo>/dimensions/space_fabric/uranus/
<mundo>/dimensions/space_fabric/neptune/
```

Los subdirectorios de chunks se materializan cuando el juego guarda datos de esos
mundos. No crees esas carpetas manualmente: por sí solas no registran dimensiones.
Tierra es independiente del Overworld; la Luna es un satélite incluido como
destino adicional. Los gigantes mantienen islas como abstracción de exploración.

Para visitar la Luna con permisos de operador, primero activa espectador y usa:

```mcfunction
execute in space_fabric:moon run tp @s 0 180 0
```

Reinicia el mundo después de editar los registros dinámicos. Cambiar los límites
de dimensiones en partidas existentes puede requerir migración: prueba la versión
0.2.0 en un mundo nuevo. `/reload` no regenera terreno ni es un registro de mundos.
