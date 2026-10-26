# Presión, atmósferas y trajes — Fabric 26.1, 0.16.0

La exposición usa presión en kPa, composición y rangos térmicos de referencia.
La escala elegida es **72 segundos reales por segundo de juego**, tomando como
base el día terrestre estándar de Minecraft: 24 horas / 20 minutos = 72.
Los relojes solares particulares de los planetas no alteran esta escala fisiológica.

## Cifras físicas y calibración del daño

| Cuerpo | Presión de referencia | Composición principal | Temperatura de referencia | Límite nominal sin traje, a 20 TPS |
| --- | --- | --- | --- | --- |
| Mercurio | ~10⁻¹² kPa, exosfera | Trazas; no respirable | Suelo ~−180 a 430 °C | 34 ticks ≈1,70 s |
| Venus | ~9.200 kPa =92 bar | CO₂ 96,5%; N₂ 3,5% | Superficie ~464 °C | 3 ticks ≈0,15 s |
| Tierra | 101,325 kPa | N₂ 78%; O₂ 20,95% | Tierra habitable, reglas vanilla | Sin daño atmosférico |
| Luna | ~3×10⁻¹³ kPa, exosfera | Trazas; no respirable | Suelo ~−173 a 127 °C | 34 ticks ≈1,70 s |
| Marte | ~0,636 kPa | CO₂ ~95%; O₂ ~0,13% | Superficie ~−125 a 20 °C | 34 ticks ≈1,70 s |
| Júpiter | 100 kPa en el nivel de 1 bar | H₂ ~90%; He ~10% | ~−110 °C en ese nivel | 34 ticks ≈1,70 s allí |
| Saturno | 100 kPa en el nivel de 1 bar | H₂ ~96%; He ~3% | ~−140 °C en ese nivel | 34 ticks ≈1,70 s allí |
| Urano | 100 kPa en el nivel de 1 bar | H₂/He; CH₄ ~2% | ~−195 °C en ese nivel | 34 ticks ≈1,70 s allí |
| Neptuno | 100 kPa en el nivel de 1 bar | H₂/He; CH₄ ~1–2% | ~−200 °C en ese nivel | 34 ticks ≈1,70 s allí |

Las cifras físicas son aproximaciones de referencia, no mapas atmosféricos de cada
coordenada. Mercurio y la Luna no tienen una atmósfera densa cuya temperatura sea
la del suelo. Los gigantes no tienen una superficie sólida: **1 bar es un nivel
convencional de referencia**, no su presión en todas las profundidades.

Los tiempos letales de la última columna son **calibración de juego**, no mediciones
clínicas ni tiempos durante los cuales sería seguro permanecer en esos ambientes.
La pérdida de conciencia por falta de oxígeno puede producirse en unos 10–15 segundos
reales, mucho antes de 1–3 minutos. El mod toma 120 segundos como presupuesto nominal
para hipoxia/descompresión, 10 segundos para presión extrema y 60 para fallo térmico.
No inventa un tiempo médico exacto distinto para cada planeta.

`ceil(segundos_nominales × 20 / 72)` da 34, 3 o 17 ticks respectivamente. Por ello,
1–3 minutos reales equivaldrían a ~0,83–2,50 segundos de juego. La exposición actual
usa el punto de referencia de dos minutos para los ambientes sin oxígeno a baja
presión o a 1 bar. Un personaje ya herido puede morir antes. Un servidor con menos
de 20 TPS tarda más tiempo de reloj en simular esos ticks.

## Los gigantes: presión y calor al descender

El nivel de 1 bar corresponde a **Y=240**. La presión sigue esta aproximación:

```text
P(Y) = 100 kPa × exp((240 − Y) / H)
H: Júpiter 27; Saturno 59,5; Urano 27,7; Neptuno 19,1
```

Para comprimir la atmósfera en la altura disponible, un bloque vertical de este
modelo representa un kilómetro; esta conversión solo se usa en los gigantes.
Bajo 1 bar, la temperatura aumenta mediante una aproximación adiabática de H₂/He:
`T(K) = T₁bar × (P/100)^(2/7)`. Por encima se conserva la temperatura de referencia.
Es una extrapolación sencilla, no una simulación del núcleo ni de fluidos no ideales.

Por ejemplo, en Júpiter a Y=160 hay ~19,4 bar: el traje criogénico de 10 bar ya no
sirve. A mayor profundidad puede excederse tanto la presión como la temperatura
admitida por el traje abisal. No existe un traje que proteja a profundidad infinita.
La antigua barrera atmosférica uniforme de Y=160 deja de decidir el daño; las reglas
de navegación y aterrizaje conservan sus límites de seguridad.

## Cuatro conjuntos reales de ítems del mod

| Prefijo de ítem | Presión máxima | Intervalo térmico admitido | Uso y capacidades |
| --- | --- | --- | --- |
| `vacuum_suit_` | 200 kPa =2 bar | −200..450 °C | Mercurio, Luna, Marte y capas superiores de gigantes; O₂ y presurización |
| `venus_suit_` | 10.000 kPa =100 bar | −150..550 °C | Venus, calor y corrosión; también otros ambientes dentro de sus límites |
| `cryo_suit_` | 1.000 kPa =10 bar | −230..100 °C | Frío intenso y mayor margen de presión en los gigantes |
| `abyssal_suit_` | 100.000 kPa =1.000 bar | −230..550 °C | Exploración profunda, corrosión y protección más amplia, con límites |

Estos límites son **tecnología ficticia de jugabilidad**. Un traje espacial actual
no permite caminar en la superficie de Venus ni explorar gigantes de esta manera.
Para los planetas rocosos se comprueba el intervalo térmico completo de referencia;
no se calcula transferencia de calor, enfriamiento ni radiación en tiempo real.

Cada conjunto tiene `helmet`, `chestplate`, `leggings` y `boots`. **Las cuatro piezas
deben ser de la misma categoría**. Faltar una pieza o mezclar categorías rompe la
protección. Hierro, diamante, netherita, casco de tortuga o botas térmicas comunes
no sustituyen una escafandra. Las fichas de los ítems muestran sus especificaciones.
Los iconos y el equipo visible reutilizan de momento los recursos vanilla de hierro;
se distinguen por identificador, nombre y descripción, no por una textura nueva.

El O₂ integrado todavía es ilimitado: no hay consumo de botella ni de energía.
También mantiene el suministro de aire bajo el agua. El combate ordinario puede
romper una pieza y, por tanto, abrir el traje. Las pociones de resistencia, la
armadura y los tótems no sustituyen la protección ambiental.

## Fabricación y acceso antes de viajar

Son recetas sin forma, una por pieza:

- Vacío: pieza de hierro correspondiente + vidrio + celda de combustible vacía.
  Todo se puede obtener antes del primer viaje espacial.
- Venusiano: pieza del traje de vacío + lingote de netherita + obsidiana + polvo de sulfato.
- Criogénico: pieza del traje de vacío + hielo compacto + diamante + polvo de sulfato.
- Abisal: pieza del traje criogénico + núcleo de obsidiana + lingote de netherita.

Estas recetas de fabricación producen piezas nuevas y no conservan los encantamientos
de los ingredientes. Se pueden consultar en los JSON `recipe/<prefijo><pieza>.json`.
Los sulfatos se obtienen en Marte con el traje de vacío; no se obliga a entrar sin
protección en Venus para fabricar su equipo.

Ejemplo completo de equipo de vacío en creativo, con permisos de operador:

```mcfunction
/give @s space_fabric:vacuum_suit_helmet
/give @s space_fabric:vacuum_suit_chestplate
/give @s space_fabric:vacuum_suit_leggings
/give @s space_fabric:vacuum_suit_boots
```

Cambia `vacuum` por `venus`, `cryo` o `abyssal` para los demás conjuntos.

## Cabina, clima y síntomas

Desde 0.17.0, la cabina proporciona aire y aislamiento dentro de los
[límites de su variante](planet-rockets.md), siempre que el casco no esté destruido.
Para Venus hace falta un cohete resistente al ácido y a sus condiciones de presión
y calor; para los gigantes, la variante debe soportar el ambiente a la altura local.
Si la cabina falla, solo un traje adecuado puede proteger al ocupante. Al salir se
evalúa inmediatamente el equipo del jugador.

La dosis avanza cada tick, con aviso de presión y riesgo, debilidad y ceguera tempranas,
daño periódico y daño letal al alcanzar el límite. Cambiar entre planetas peligrosos
sin protección no reinicia la dosis. Estar protegido o volver a un ambiente seguro
la elimina, sin curar automáticamente la salud perdida. Creativo y espectador están exentos.

Venus deja de tener lluvia en el bioma de superficie: las gotas de ácido de las nubes
reales no llegan al suelo caliente. El sistema antiguo de lluvia ácida se conserva
para compatibilidad con datapacks que la reactiven. Los conjuntos venusiano y abisal
evitan el veneno y la corrosión de ese sistema y de los depósitos de ácido. Un techo
ordinario no crea un hábitat presurizado.

## Configuración y código

`config/space-fabric-atmosphere.json` se crea al iniciar el mod. La configuración
efectiva de la exposición es la del servidor, incluido el servidor integrado:

```json
{"real_seconds_per_game_second": 72}
```

El valor debe ser mayor que cero y como máximo 3600. Requiere reiniciar; un valor
menor hace más lenta la exposición. No depende de `/time`, del ciclo diurno ni de
los tiempos de rotación propios de los planetas.

- `AtmosphereProfile`: presión, composición y temperaturas de referencia.
- `SuitTier` y `SuitProtection`: capacidades y conjunto completo por tags.
- `PressureExposure`: peligro, dosis y conversión de tiempos, comprobables sin Minecraft.
- `AtmosphereSystem`: daño en el servidor y síntomas; afecta a jugadores.
- `suits/<categoría>/<slot>`: tags para añadir equipo de otros mods mediante datapack.
- `/planetinfo`: presión local, composición, rango térmico y categoría del traje.

Los habitantes marcianos y venusianos conservan las adaptaciones propias ya implementadas.
La evaluación atmosférica del jugador no se aplica a todos los mobs vanilla.

## Actualización y verificación

Instala 0.16.0 en cliente y servidor. **Antes de actualizar una partida con el
personaje en un planeta hostil, déjalo dentro del cohete o en creativo**: el hierro
normal deja de proteger y la exposición comienza inmediatamente al jugar en supervivencia.
No hace falta regenerar el terreno para el daño y los trajes. El cambio de precipitación
se carga con el bioma al reiniciar el mundo.

Se comprueban los perfiles, el aumento de presión/temperatura, dosis y límites,
trajes incompletos y mezclados, recetas ensambladas, propiedades de equipo y tags
reales de los daños. Las comprobaciones en una partida y en multijugador siguen pendientes.

Referencias para las cifras y su contexto:

- [NASA/NSSDCA, fichas planetarias](https://nssdc.gsfc.nasa.gov/planetary/factsheet/).
- [NASA, Marte](https://science.nasa.gov/mars/facts/) y [Venus](https://science.nasa.gov/venus/facts/).
- [NASA, Júpiter](https://science.nasa.gov/jupiter/facts/), [Saturno](https://science.nasa.gov/saturn/facts/),
  [Urano](https://science.nasa.gov/uranus/facts/) y [Neptuno](https://science.nasa.gov/neptune/facts/).

Son referencias de consulta; la descarga automatizada de esos sitios fue bloqueada
por la política de red del entorno durante esta tarea. Los valores aproximados
indicados y las reglas de juego se distinguen arriba; no se afirma una verificación documental en línea.

A partir de 0.17.0 la cabina se evalúa por variante y estado del casco. Consulta los
[límites de cada cohete](planet-rockets.md): no todas las cabinas admiten Venus ni
las mismas profundidades en gigantes gaseosos.
