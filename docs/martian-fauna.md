# Fauna de la superficie de Marte — Fabric 26.1, versión 0.11.0

Se conservan los nombres de los JAR aportados: Crystal Hum Crawler, Golden Eye
Sandwraith y Red Dune Ironmaw. Sus recursos y clases adaptadas están integrados en
Space Fabric; no hay que instalar los tres mods externos ni bibliotecas de animación.

## Aparición natural

| Mob / identificador `space_fabric:` | Categoría | Peso | Grupo mín.–máx. | Luz y dificultad |
| --- | --- | ---: | --- | --- |
| Crystal Hum Crawler / `crystal_hum_crawler` | `creature` | 18 | 1–3 | Cualquier luz; también Pacífico |
| Golden Eye Sandwraith / `golden_eye_sandwraith` | `monster` | 10 | 1–2 | Oscuridad vanilla; dificultad superior a Pacífico |
| Red Dune Ironmaw / `red_dune_ironmaw` | `monster` | 2 | 1–1 | Oscuridad vanilla; dificultad superior a Pacífico |

El peso es relativo a los demás candidatos de la **misma categoría**, no un
porcentaje por tick. Crawler usa el cupo de animales, separado del de los hostiles.
Los grupos pueden quedar incompletos si falta espacio o se alcanza el cupo.

Para los tres se exige simultáneamente:

- Dimensión **`space_fabric:mars`** y bioma **`space_fabric:mars`**.
- Suelo en `#space_fabric:mars_surface_spawnable`: `space_fabric:iron_oxide_regolith`
  o `space_fabric:martian_stone`, los bloques reales del terreno de este mod.
- Posición de pies igual a la primera altura libre de `MOTION_BLOCKING_NO_LEAVES`.
- Cielo visible y ausencia de fluido en pies y cabeza; colocación nativa `ON_GROUND`.
- Colisiones, suelo válido, distancia al jugador y límites de población vanilla.

Esto excluye cuevas, interiores techados y plataformas de otro material. No impide
que un mob ya aparecido camine hacia un edificio o sea transportado a otro mundo.
Los huevos y `/summon` permiten colocación manual y no son una prueba del spawn natural.

`data/space_fabric/worldgen/biome/mars.json` contiene estos campos dentro de `spawners`:

```json
{
  "creature": [
    {"type": "space_fabric:crystal_hum_crawler", "weight": 18, "minCount": 1, "maxCount": 3}
  ],
  "monster": [
    {"type": "space_fabric:golden_eye_sandwraith", "weight": 10, "minCount": 1, "maxCount": 2},
    {"type": "space_fabric:red_dune_ironmaw", "weight": 2, "minCount": 1, "maxCount": 1}
  ]
}
```

No reemplaces el bioma completo con ese fragmento: conserva sus efectos,
generación y otros campos. `noise_settings/mars.json` permite también generación
inicial de criaturas con `disable_mob_generation: false`.

## Movimiento, combate y recursos

Todos usan navegación terrestre, colisiones y gravedad normales. El sistema global
del mod aplica una sola vez el factor marciano **0,38**, con atributo de gravedad
resultante **0,0304** y seis bloques de caída segura. Respiran sin oxígeno y resisten
los tipos de daño `hypoxia` y `decompression`; combate y otros peligros conservan
sus reglas. Las adaptaciones no dan soporte vital a los jugadores.

| Mob | Vida / velocidad base | Comportamiento adaptado |
| --- | --- | --- |
| Crawler | 8 / 0,17 | Pasea, consume polvo sin destruir suelo y produce un fragmento de cristal tras 25 ciclos de 240 ticks: cinco minutos a 20 TPS. Al recibir daño se recoge durante 48 ticks; durante ese intervalo se protege de daños ordinarios. |
| Sandwraith | 30 / 0,30 | Busca jugadores de noche o con tormenta y se defiende al recibir daño. Camina hasta entrar en alcance; la animación dispara una ráfaga de 5 puntos de daño, ceguera de 70 ticks e impulso vertical reducido. Requiere línea de visión. |
| Ironmaw | 90 / 0,24 | Detecta jugadores que se mueven y persigue por el suelo; también responde a ataques. Su golpe animado causa daño base 14 en un radio de 5 bloques con línea de visión, fuego e impulso reducido. Tiene 8 de armadura e inmunidad al fuego. |

Sandwraith conserva la animación original, pero su navegación no vuela. Ironmaw
no se entierra, no atraviesa bloques, no se teletransporta y no destruye el terreno.
El impacto se ejecuta una vez por animación, en el servidor. Un objetivo adquirido
puede seguir siendo perseguido al amanecer; la adquisición normal del Sandwraith
queda limitada a noche/tormenta. El caparazón del Crawler se restaura sin dejar
inmunidad permanente al guardar y cargar.

Materiales integrados:

- `energy_crystal_shard`: producción del Crawler, sin modificar bloques.
- `condensed_stardust`: 1–2 al morir Sandwraith.
- `vibration_sensor`: 1–3 al morir Ironmaw.
- `crushed_iron_ore`: 2–5 al morir Ironmaw.

Son materiales para futuras recetas; esta versión no inventa recetas de combustible
para ellos. Los drops de muerte respetan la regla vanilla de botín de criaturas.

## Archivos para ajustar el sistema

Rutas Java relativas a `src/main/java/dev/vic17usd/spacefabric/`:

| Archivo o carpeta | Función |
| --- | --- |
| `entity/fauna/ModMartianFauna.java` | Tipos, atributos, sonido y registro de colocación |
| `entity/fauna/MartianSpawnRules.java` | Predicados comunes de dimensión, bioma, suelo, altura, luz y fluidos |
| `entity/fauna/{CrystalHumCrawler,GoldenEyeSandwraith,RedDuneIronmaw}Entity.java` | IA, ataques, producción y drops |
| `entity/fauna/*RigEntity.java`, `*RigProfile.java` | Estado animado sincronizado y tiempos de impacto originales |
| `mixin/SpawnPlacementsAccessor.java` | Invoker del registro privado de `SpawnPlacements` en Minecraft 26.1 |
| `client/fauna/` | Modelos originales, keyframes, estados de render y renderers |
| `item/ModItems.java` | Cuatro materiales y tres huevos de aparición |

No se usa una API de Forge/MCreator: la implementación es Fabric 26.1 con nombres
oficiales de Mojang. `SpaceFabric` registra el sistema común y `SpaceFabricClient`
registra los renderers exclusivamente en cliente. El invoker está declarado en
`space_fabric.mixins.json`; los codecs y el bootstrap de pruebas cargan ese Mixin.

Datos y recursos relativos a `src/main/resources/`:

```text
data/space_fabric/worldgen/biome/mars.json
data/space_fabric/worldgen/noise_settings/mars.json
data/space_fabric/tags/block/mars_surface_spawnable.json
data/space_fabric/loot_table/entities/{crystal_hum_crawler,golden_eye_sandwraith,red_dune_ironmaw}.json
assets/space_fabric/textures/entity/{crystal_hum_crawler,golden_eye_sandwraith,red_dune_ironmaw}.png
assets/space_fabric/{items,models/item}/<material_o_huevo>.json
assets/space_fabric/sounds/crystal_crawler_hum.ogg
assets/space_fabric/sounds.json
assets/space_fabric/lang/{es_es,en_us}.json
```

Las loot tables de entidades se mantienen vacías porque los drops anteriores se
resuelven en Java. Los PNG y el OGG originales se comprueban por SHA-256 con
`third_party/martian_fauna/manifest.json`; las licencias se incluyen en el JAR.
Se han portado las clases relevantes y los recursos, sin cargar los inicializadores
ni las dependencias de telemetría de los JAR fuente.

## Instalación y prueba en juego

Sustituye la versión anterior por `dist/space-fabric-0.11.0.jar` en cliente y
servidor, con Fabric API para 26.1. Deja un solo JAR de Space Fabric en `mods/`.
Reinicia el mundo: no basta con `/reload` para actualizar registros de worldgen.
No necesitas los tres JAR de fauna externos.

Comandos de prueba en Marte, con permisos de operador:

```mcfunction
/give @s space_fabric:crystal_hum_crawler_spawn_egg
/give @s space_fabric:golden_eye_sandwraith_spawn_egg
/give @s space_fabric:red_dune_ironmaw_spawn_egg
/execute in space_fabric:mars run locate structure space_fabric:martian_haven_settlement
```

Para comprobar aparición natural usa una zona amplia del suelo marciano exterior,
sin techo ni iluminación artificial para los hostiles, dificultad Normal y
generación de criaturas activada. Observa en supervivencia desde más de 24 bloques,
dentro del radio habitual de aparición y de los chunks simulados. El cupo de
criaturas puede retrasar el Crawler; no se fuerza un spawn por temporizador.
El Ironmaw necesita además espacio para su cuerpo de unos 3,8 bloques de altura.

La aparición natural puede ocurrir en chunks de Marte ya existentes tras reiniciar.
Las estructuras sin pedestal de [la guía de parcelas](martian-surface-plots.md)
requieren chunks nuevos; no se reconstruyen edificios ya guardados.

Validación automatizada: registros, atributos, huevos, recursos originales,
tablas de bioma y rechazos de hábitats incorrectos. Compilación y **226 pruebas**
correctas; la inspección de modelos, IA y aparición natural en una partida gráfica
sigue pendiente. Véase [el informe de validación](validation.md).
