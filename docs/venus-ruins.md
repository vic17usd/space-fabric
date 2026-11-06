# Nuevas construcciones de Venus — 0.35.0

Desde **0.36.0**, las fundiciones nuevas tienen consola y tres equipos averiados
para [recuperar la instalación y conseguir un rover](venus-rover-and-recovery.md).
La arquitectura de fundiciones ya guardadas se conserva.

Se revisaron los catorce JAR aportados. Se eligieron cuatro construcciones nuevas
para complementar los paisajes, el pueblo, la pirámide y la caldera existentes.
Instala `space-fabric-0.35.0.jar` en cliente y servidor, sustituyendo el anterior.
No instales los JAR de origen como dependencias: se incorporan sus diseños como
datos nativos; no se ejecutan sus clases ni sus inicializadores.

| Archivo aportado | Nombre en el juego | Biomas | Tamaño adaptado X × Y × Z |
|---|---|---|---|
| Venus estructura.jar | Puesto de ceniza de Venus | Llanuras volcánicas y cuencas de obsidiana | 116 × 61 × 120 |
| Venus estructura 3.jar | Bóveda de azufre de Venus | Campos de fumarolas | 69 × 50 × 80 |
| Venus estructura 4.jar | Refugio de basalto de Venus | Llanuras volcánicas y cuencas de obsidiana | 55 × 45 × 63 |
| Venus instalaciones averiadas.jar | Fundición abandonada de Venus | Llanuras volcánicas y campos de fumarolas | 111 × 66 × 101 |

## Integración con el terreno

- Se retiran los basamentos de exhibición: 17 capas del puesto, 18 del refugio
  y dos de la bóveda y la fundición. Se conserva la arquitectura superior, sin
  sustituir estos basamentos por plataformas en el aire.
- El generador existente busca terreno suave, con relieve máximo de ocho bloques,
  prepara una parcela de suelo venusiano y suaviza sus bordes en 16 bloques.
  Todas las piezas de un edificio comparten la misma cota de suelo.
- Se añaden apoyos cortos de mampostería, de hasta nueve bloques por unión,
  y se retiran dieciséis vóxeles decorativos suspendidos de la fundición.
  Las componentes arquitectónicas quedan conectadas al suelo o apoyadas en él.
- Se cambian madera y lana por materiales minerales, agua por vidrio decorativo
  y bloques decorativos de diamante/escombros ancestrales por geología venusiana.
  Se conserva la variedad de formas, luces y acentos de cobre.
- Los cuatro diseños reúnen **72.020 bloques distribuidos en 113 plantillas** de
  hasta 16 × 16 de superficie. Cada chunk coloca solamente su porción.

Comparten un conjunto de generación con candidatos separados por una cuadrícula
de 96 chunks (1.536 bloques), separación interna de 48 chunks y peso igual para
cada diseño. No significa una estructura garantizada cada 1.536 bloques: el
bioma, relieve y altura pueden descartar un candidato. Una zona de exclusión de
12 chunks evita candidatos próximos a los del conjunto antiguo de asentamientos.

## Cómo encontrarlas

Escribe `/estructuras` para ver sus nombres y preparar la búsqueda. También puedes
usar los alias, con permisos de operador:

```mcfunction
/execute in space_fabric:venus run locate structure #space_fabric:puesto_ceniza_venusiano
/execute in space_fabric:venus run locate structure #space_fabric:boveda_venusiana
/execute in space_fabric:venus run locate structure #space_fabric:refugio_venusiano
/execute in space_fabric:venus run locate structure #space_fabric:fundicion_venusiana
```

**Se generan en zonas nuevas de Venus.** Los chunks explorados conservan su
contenido. No se borran ni se trasladan las construcciones guardadas anteriormente.
En 0.35.0 estos diseños aportaban arquitectura y exploración. La recuperación de
la fundición se añade en 0.36.0 con cuatro bloques funcionales nuevos; la fábrica
reparada sigue consumiendo sus propios suministros.

Explóralos con traje venusiano. Para convertir un edificio en una base habitable,
instala cerramiento certificado, controlador y suministros de soporte vital;
la arquitectura importada por sí sola no regula la presión ni proporciona oxígeno.

## Los otros diez archivos

Las dos torres (`Venus estructura 2.jar` y `Venus estructura 5.jar`) quedan fuera
de esta selección: sus alturas originales de 211 y 146 bloques exceden el límite
de 128 del generador de superficie actual y dominarían el paisaje. No se recortan
sus cimas ni se fuerzan sobre el terreno.

Los otros ocho archivos corresponden a diseños ya integrados: ácido/fumarolas,
lagos, volcán, dos asentamientos y tres diseños de suelo. Conservan sus recursos
y generación anteriores, sin añadir duplicados con otros nombres.

## Fuentes y verificación

[Atribución y licencias](../third_party/venus_ruins/README.md). El manifiesto
registra los SHA-256, autores, páginas originales, tamaños y bloques adaptados.
Los JSON originales se conservan para reproducir la importación:

```sh
python3 tools/import_venus_ruins.py --stored-sources
./gradlew build --console=plain
python3 tools/validate_resources.py
```

Las pruebas usan registros, plantillas y colocación nativos de Minecraft, con
generación de inicios y colocación por chunks en un terreno de prueba. También
comprueban cantidades, ausencia de bloques de recompensa desproporcionada y
conexión al suelo de cada componente. No equivalen a observar las construcciones
en una partida con GPU: quedan pendientes el aspecto final, la frecuencia real
durante exploración, la búsqueda completa de `/locate` y el multijugador.
