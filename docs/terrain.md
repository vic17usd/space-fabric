# Terreno planetario y biomas — Minecraft 26.1

| Destino | Generación implementada |
| --- | --- |
| Mercurio | Roca de basalto liso y toba; sin agua, lluvia ni vegetación. Cráteres de 8–12 bloques de diámetro con cuencas de hasta cuatro bloques de profundidad. |
| Luna | Comparte la generación seca y los cráteres, con su propio bioma, dimensión y gravedad. |
| Venus | Basalto, depósitos de magma y cuencas de lava viscosa. Lluvia que aplica veneno a jugadores expuestos. |
| Marte | Terracota roja en todo el subsuelo, canales profundos en la densidad, carvers de cuevas y cañones, vetas de hierro. |
| Júpiter, Saturno, Urano, Neptuno | Mundos vacíos, sin capas sólidas ni decoración automática. Niebla corta y zona de presión extrema bajo Y=160. |
| Tierra | Conserva generación y biomas del Overworld en una dimensión independiente. |

Es una interpretación jugable. La terracota roja representa visualmente el suelo
oxidado; no es un modelo geológico. Los cráteres son pequeños y frecuentes, sin
macrocuencas planetarias. Venus no tiene todavía volcanes cónicos ni erupciones.

## Cómo se registran los biomas

Los biomas son entradas de un registro dinámico. Fabric carga automáticamente
los JSON incluidos en el mod, por ejemplo:

```text
src/main/resources/data/space_fabric/worldgen/biome/mercury.json
src/main/resources/data/space_fabric/worldgen/biome/venus.json
src/main/resources/data/space_fabric/worldgen/biome/moon.json
src/main/resources/data/space_fabric/worldgen/biome/mars.json
src/main/resources/data/space_fabric/worldgen/biome/jupiter.json
src/main/resources/data/space_fabric/worldgen/biome/saturn.json
src/main/resources/data/space_fabric/worldgen/biome/uranus.json
src/main/resources/data/space_fabric/worldgen/biome/neptune.json
```

`PlanetBiomes.key(Planet.MARS)` devuelve la referencia Java:

```java
ResourceKey<Biome> key = ResourceKey.create(
        Registries.BIOME, SpaceFabric.id("mars"));
```

No se usa `Registry.register` en `onInitialize()` para biomas dinámicos. El mundo
elige el bioma mediante su generador:

```json
{
  "type": "space_fabric:mars",
  "generator": {
    "type": "minecraft:noise",
    "biome_source": {
      "type": "minecraft:fixed",
      "biome": "space_fabric:mars"
    },
    "settings": "space_fabric:mars"
  }
}
```

El bioma define clima, colores, carvers, features y spawns. El material principal
del terreno se define en `worldgen/noise_settings/mars.json`, mediante
`default_block` y `surface_rule`; cambiar el color de un bioma no convierte piedra
en suelo marciano. La función de densidad marciana resta canales estrechos de
la masa de terreno usando el ruido `minecraft:ridge`. Los carvers usan un tag
propio que permite excavar terracota e hierro; los tags vanilla de piedra no bastan.

## Cráteres de Mercurio

`CraterFeature` excava una cuenca parabólica y reviste el fondo con toba.
`PlanetFeatures` registra **el tipo de feature**, que sí pertenece a un registro
estático:

```java
Registry.register(BuiltInRegistries.FEATURE,
        SpaceFabric.id("crater"), new CraterFeature());
```

El configured feature `worldgen/configured_feature/crater.json` elige ese tipo:

```json
{ "type": "space_fabric:crater", "config": {} }
```

El placed feature aplica `rarity_filter` con `chance: 2` y comprobación de bioma.
El bioma lo referencia en el paso `TOP_LAYER_MODIFICATION`, índice 10 de `features`.
Usa el RNG del worldgen y solo escribe dentro de su chunk: no depende de un tick
que modifique chunks ya explorados. No es un algoritmo de cráteres gigantes.

Mercurio tiene cielo negro, ninguna precipitación, nubes transparentes y niebla
alejada. El vacío y el daño por falta de protección los gestiona `AtmosphereSystem`.
Un bioma seco por sí solo no elimina el oxígeno del jugador.

## Lava viscosa y lluvia ácida de Venus

`ViscousLavaFluid` hereda el comportamiento de lava, pero usa sus propios estados
fuente/flujo y bloque. Espera 80 ticks por propagación y tiene un alcance horizontal
reducido. El tiempo sigue ticks de simulación y puede variar con el lag.
Los fluidos se incorporan a `minecraft:lava` para conservar los peligros de lava.
El cliente registra un `FluidModel.Unbaked` con los materiales vanilla de lava
mediante la API de renderizado de fluidos de Fabric **26.1**.

Los lagos se generan con `default_fluid: space_fabric:viscous_lava` y `sea_level: 40`.
No es un líquido que pueda añadirse exclusivamente con datapack: necesita las
clases Java y los registros del mod. Para obtenerlo durante desarrollo:

```mcfunction
give @s space_fabric:viscous_lava_bucket
```

Venus tiene `has_precipitation: true`, `temperature: 2.0` y `downfall: 1.0`.
`downfall` no significa lluvia permanente: el clima debe estar activo. Para probar:

```mcfunction
execute in space_fabric:venus run weather thunder 300
```

Cada 20 ticks, si llueve a la altura de la cabeza, el jugador está sin escafandra
completa y no es creativo/espectador, se aplica `POISON` durante 60 ticks.
Un techo o entrar en una zona sin lluvia impide renovar el veneno; el efecto ya
aplicado puede durar tres segundos más. La presión atmosférica continúa siendo
un peligro independiente: un techo normal no es una habitación presurizada.

La lluvia conserva las partículas y el sonido vanilla. La composición sulfúrica
se representa con veneno; no hay todavía partículas verdes ni química del ácido.
El veneno vanilla no mata por sí solo; presión, lava y exposición sí pueden matar.

## Gigantes sin suelo y alternativa orbital

Cada gigante tiene un bioma propio con niebla de alcance 32 bloques y este
generador, cambiando únicamente `biome` para cada destino:

```json
{
  "type": "minecraft:flat",
  "settings": {
    "biome": "space_fabric:jupiter",
    "layers": [],
    "lakes": false,
    "features": false,
    "structure_overrides": []
  }
}
```

No hay islas del End, bedrock ni suelo natural. El gas se representa por ambiente
y reglas de juego, no mediante millones de bloques de fluido. Por debajo de
Y=160 la presión inflige cuatro puntos de salud por intervalo de 20 ticks,
incluso con el traje básico. No se aplican dos golpes de atmósfera en ese intervalo.
Creativo y espectador siguen exentos. Continuar descendiendo termina en el vacío
vanilla. La capa mortal no ralentiza físicamente la caída.

Por encima de Y=160, la escafandra completa evita la exposición normal. Puedes
crear una plataforma orbital durante desarrollo; estos comandos **colocan bloques**
en las coordenadas indicadas, así que úsalos en un mundo de pruebas vacío:

```mcfunction
execute in space_fabric:saturn run fill -4 239 -4 4 239 4 minecraft:smooth_quartz
execute in space_fabric:saturn run tp @s 0.5 240 0.5
```

Primero usa creativo y equipa el traje antes de pasar a supervivencia. No se crea
una plataforma automáticamente al teletransportarse. Un anillo de Saturno, una
estación con estructuras propias o un punto de llegada seguro automatizado serían
una extensión; no se afirma que este prototipo los genere.

## Validación

El test de worldgen carga los recursos vanilla y del mod mediante
`RegistryDataLoader`, incluyendo tags, biomas, features, carvers y ajustes de ruido.
Después valida los nueve generadores y tipos de dimensión con sus codecs reales.
También prueba que los gigantes no tengan capas sólidas, que la lava use su bloque
y tag propios, el límite de presión Y=160 y las condiciones de exposición ácida.

No se ejecutó un cliente ni la generación completa de chunks: quedan pendientes
la apariencia de los cráteres/cañones, el renderizado del fluido y el comportamiento
de los eventos dentro de una partida. Usa **un mundo nuevo**; los chunks existentes
no cambian al reemplazar estos JSON y los antiguos gigantes podían contener islas.
