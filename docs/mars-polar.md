# Casquetes polares y subsuelo helado de Marte — 0.51.0

Adaptación del archivo aportado **Glacial Frontier** a Minecraft Java 26.1/Fabric,
integrada en Space Fabric. Instala el JAR de Space Fabric actualizado.

## Geografía

Marte tiene ahora dos biomas: su terreno rojo habitual y el **Casquete polar
marciano** (`space_fabric:mars_polar_ice_cap`). Una fuente de biomas geográfica
mantiene la región central y sitúa los casquetes hacia el norte y el sur:

- Comienzan aproximadamente en **Z −8.192 y Z +8.192**.
- El borde varía suavemente hasta unos 288 bloques según X.
- En **Z ±9.000** ya estás dentro del casquete, en terreno nuevo.
- El mismo bioma continúa en profundidad; las cuevas y vetas corresponden a su zona.

La dimensión de Minecraft es infinita: estas regiones continúan hacia ±Z. Esta
aproximación permite distinguir norte/sur y planificar una expedición desde la
zona de llegada. El terreno conserva el relieve de Marte y recibe una cubierta
superficial de CO₂ congelado sobre varias capas de hielo glaciar.

La atmósfera y gravedad siguen siendo marcianas. El cielo conserva su tono rojizo,
con una niebla de tono más frío. El bioma carece de precipitación terrestre y no
introduce fauna terrestre. Las tres criaturas marcianas existentes mantienen sus
hábitats habituales; las estructuras existentes conservan su selección de bioma.

## Materiales aportados

| Recurso | Obtención y uso |
| --- | --- |
| Hielo glaciar marciano | Superficie polar, vetas y paredes de cámaras; permite obtener agua envasada |
| CO₂ congelado marciano | Cubierta polar y vetas; permite llenar recipientes de CO₂ |
| Mena subglacial marciana | Vetas profundas; requiere pico de piedra o superior, da cristales y experiencia |
| Nódulo de cristal de escarcha | Vetas y cámaras; da cristales y experiencia |
| Cristal de escarcha marciano | Material de fabricación de la piedra térmica |
| Piedra térmica marciana | Consumible de la receta de fusión sellada del hielo |

Se conservan los **seis PNG originales** del JAR: cuatro bloques y dos materiales.
Los nombres e identificadores se adaptan al namespace de Space Fabric. Las loot
tables mantienen las cantidades originales: 1–3 cristales por mena y 2–4 por nódulo.
Los bloques tienen sus ítems, traducciones, herramientas y modelos registrados.

El hielo y CO₂ son sólidos estables de juego: la luz común no los derrite y al
romperlos no aparece agua. No se simula sublimación termodinámica del CO₂ en esta
entrega. La piedra térmica se utiliza para fabricar agua envasada; no sustituye
al traje ni concede inmunidad a la exposición planetaria.

## Cuevas y vetas

Las cuatro vetas originales usan ahora roca marciana y hielo como anfitriones,
y se colocan exclusivamente en el bioma polar. Los minerales habituales de Marte
siguen disponibles bajo los casquetes.

Las cámaras heladas se intentan generar en uno de cada ocho chunks polares,
entre **Y −46 y −16**. La cámara tiene envoltura de hielo y un nódulo en su piso.
Cada intento verifica primero todo su volumen y puede descartarse: agua, cuevas
abiertas, roca no compatible, o referencias a estructuras impiden su colocación.
La frecuencia de cámaras efectivas es por tanto menor que la de intentos.

Se generan antes de las vetas, para que estas puedan aparecer en sus paredes.
La excavación queda dentro del chunk correspondiente, conserva el fondo del mundo
y evita estructuras. Para descubrirlas se exploran las cuevas existentes o se
excava el subsuelo polar.

## Recursos útiles para una base

Las siguientes recetas aparecen en el visor de recetas existente:

- **Piedra térmica:** patrón original, tres lingotes de hierro, tres carbones y
  un cristal de escarcha; produce una piedra.
- **Agua envasada:** un hielo glaciar + un recipiente vacío + una piedra térmica
  → un recipiente de agua. Los tres ingredientes se consumen.
- **CO₂ envasado:** un CO₂ congelado + un recipiente vacío → un recipiente de CO₂.

El agua y el gas utilizan los recipientes industriales existentes. No se crean
recipientes adicionales al fabricar estas salidas. Se trata de una preparación
manual y sellada para esta fase; la futura industria marciana puede automatizarla.

## Actualización y visita

Se aplica a **chunks nuevos**. Los bloques, biomas guardados y construcciones de
zonas ya exploradas se conservan. En el borde entre terreno antiguo y nuevo puede
apreciarse el cambio de cubierta y bioma. Los cohetes reconocen hielo y CO₂ como
suelo natural de aterrizaje.

Para una inspección con permisos de operador:

```mcfunction
/gamemode spectator
/execute in space_fabric:mars run tp @s 0 160 9000
```

Revisa la superficie y baja al subsuelo. Para jugar en supervivencia, lleva equipo
planetario completo, herramientas y suministros. El hielo no hace respirable la
atmósfera ni elimina la descompresión.

## Fuentes y validación

- `mars/MarsPolarBiomeSource`: selección geográfica, codec nativo y conservación
  de ambos biomas al serializar.
- `mars/MarsPolarContent`: cuatro bloques, dos materiales, fuente de biomas y feature.
- `mars/MarsGlacialCaveFeature`: prevalidación y excavación acotada.
- `tools/import_mars_glacial.py`: importación reproducible desde recursos guardados;
  acepta `--jar` para importar el archivo original.
- `tools/import_sources/mars_glacial/`: recursos fuente.
- `third_party/mars_glacial/`: licencia, procedencia, hash y adaptación.

Se prueban codecs, muestreo en ambos polos y el ecuador con tres semillas,
exclusividad del bioma, minerales compartidos, loot, herramientas, texturas
idénticas, aterrizaje, excavación real de cámaras en un volumen acotado, rechazo de
agua/bloques protegidos y fabricación de recipientes con sus ingredientes.

Pendiente revisión en cliente de la cubierta y sus bordes, cuevas en chunks
completos, densidad de vetas, aspecto del cielo, navegación, aterrizaje y juego
multijugador. Las pruebas de generación no equivalen a una partida visual.

Diseño original: **holasoynuevo / CreativeMode.net**,
[Glacial Frontier](https://creativemode.net/mod/glacial-frontier-kzj32637).
Las licencias se incluyen en el JAR. Se importan los recursos como datos; las clases,
TerraBlender, los inicializadores y la telemetría del archivo original quedan fuera
del mod integrado.

## Apariencia del hielo desde 0.55.4

El hielo glaciar azul usa la textura limpia en todas sus caras, sin franja
de tierra. La cubierta blanca de CO₂ y las posiciones de los bloques se
conservan. El cambio visual se aplica también a terreno existente al actualizar
el JAR y reiniciar Minecraft; no requiere generar chunks nuevos.

## Cubierta física completa desde 0.56.0

El hielo azul expuesto se sustituye por bloques blancos de CO₂ en todas sus
caras accesibles: techo, suelo y paredes. El azul queda dentro del depósito;
para llegar a él desde una superficie natural hay que romper la cubierta.
No se mueve el terreno ni se agregan bloques por fuera del relieve original.

El acabado se aplica gradualmente al cargar cada zona, tanto nueva como ya
explorada. Se registra una sola vez por interior y borde de chunk. Los bordes
se completan cuando carga el vecino, sin cargar chunks adicionales. Después
puedes excavar y dejar azul visible: no se vuelve a tapar al regresar ni al
reiniciar. Mantén la misma versión en cliente y servidor.
