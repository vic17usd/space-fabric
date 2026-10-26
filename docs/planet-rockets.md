# Cohetes planetarios — 0.17.0 / Fabric 26.1

Hay nueve variantes además del cohete básico. Se conserva el fuselaje, textura,
visor y cabina 3D del modelo `space-rocket.bbmodel`; cada variante incorpora equipo
exterior diferente, también visible en su ítem 3D. Los componentes exteriores no
se dibujan dentro del habitáculo, para mantener abierta la escotilla del piloto.

| Cohete / ID del ítem | Equipo exterior | Presión máxima | Temperatura admitida |
|---|---|---:|---:|
| Mercurio / `mercury_rocket` | Radiadores y protección reflectante | 2 bar | −220 a 600 °C |
| Venus / `venus_rocket` | Collares blindados y refrigeración, protección ácida | 200 bar | −180 a 700 °C |
| Tierra / `earth_rocket` | Depósitos de servicio y antena | 2 bar | −100 a 150 °C |
| Luna / `moon_rocket` | Depósitos de soporte vital y patas ampliadas | 5 bar | −230 a 250 °C |
| Marte / `mars_rocket` | Filtros de polvo y módulos laterales | 2 bar | −160 a 150 °C |
| Júpiter / `jupiter_rocket` | Jaula de blindaje contra radiación y tormentas | 200 bar | −200 a 700 °C |
| Saturno / `saturn_rocket` | Refuerzos en anillo y estabilización | 100 bar | −210 a 600 °C |
| Urano / `uranus_rocket` | Paneles de aislamiento criogénico | 100 bar | −240 a 300 °C |
| Neptuno / `neptune_rocket` | Collares y refuerzos para vientos extremos | 150 bar | −250 a 550 °C |

Estos son límites **ficticios de tecnología del mod**, evaluados contra los perfiles
ambientales existentes. No son capacidades de cohetes reales. Polvo, ácido,
radiación y viento se representan mediante certificaciones de diseño, no mediante
una simulación aerodinámica. Los motores mantienen el vuelo vertical automatizado
con independencia de la gravedad planetaria; se conservan combustible y controles.

## Fabricación y conversión

1. Fabrica el cohete básico con su receta habitual.
2. Fabrica el kit del destino: `space_fabric:<planeta>_rocket_kit`.
3. Coloca el cohete, baja de él y haz clic derecho con el kit.
4. El modelo cambia, el casco queda al 100 % y se selecciona la misión del kit.
   Se conservan entidad, combustible y los once espacios de inventario.

Todos los kits usan esta receta; `P` cambia según la tabla:

```text
IPI   I = lingote de hierro
RCR   R = redstone
IPI   C = bloque de cobre
```

| Kit | P (dos unidades) |
|---|---|
| Mercurio | Cuarzo |
| Venus | Obsidiana |
| Tierra | Lingote de cobre |
| Luna | Vidrio |
| Marte | Lingote de hierro |
| Júpiter | Fragmento de netherita |
| Saturno | Lingote de oro |
| Urano | Hielo azul |
| Neptuno | Cristales de prismarina |

También están los nueve cohetes listos en la pestaña creativa del mod o mediante
`/give @s space_fabric:venus_rocket`, por ejemplo. Los kits se consumen en
supervivencia. Recoger un cohete conserva su variante, combustible y estado del
casco; la carga cae como ítems, igual que antes.

## Destinos por capacidad y seguridad

Por defecto un cohete puede visitar **todos los destinos que soporte**, no solo el
que le da nombre. Todos permiten regresar a la Tierra/Overworld. Por ejemplo,
Mercurio también admite la Luna; Júpiter admite Marte, pero no los vientos de
Neptuno. Venus exige protección ácida; Marte, filtros; Mercurio y Júpiter,
blindaje contra radiación; los cuatro gigantes, refuerzos de distinto nivel.

El mapa estelar desactiva los destinos incompatibles y muestra el nombre de la
variante, casco y explicación al pasar por el destino. El servidor vuelve a
comprobar la selección, el despegue y las condiciones a la altura exacta del
aterrizaje: manipular un botón del cliente no elimina estas restricciones.

Los gigantes se visitan en las plataformas orbitales a Y≈240. La presión y
la temperatura aumentan al descender; **ningún cohete permite bajar sin límite**.
Fuera de sus capacidades, la cabina deja de proteger inmediatamente y el casco
pierde 10 puntos por segundo. A cero, el vuelo se interrumpe. Un lingote de hierro
aplicado con clic derecho repara 25 puntos mientras el cohete está detenido.
El traje adecuado sigue siendo necesario para salir de la cabina.

`config/space-fabric-rockets.json` se crea al iniciar:

```json
{"exclusive_destinations": false}
```

El valor predeterminado cumple la selección por capacidades. Opcionalmente `true`
limita cada variante a su misión y el regreso a la Tierra, manteniendo las
comprobaciones ambientales. Cambiarlo requiere reiniciar el servidor; la GUI usa
la política recibida del servidor.

## Partidas existentes, archivos y pruebas

Los cohetes anteriores guardados sin variante se convierten al modelo de su
planeta al cargar (en Tierra/Overworld conservan el básico). No se borran carga ni
combustible. Los ítems básicos antiguos siguen siendo básicos: instala un kit
antes de viajar a un planeta cuya protección exceda sus capacidades.

- `space/RocketVariant`: capacidades y destinos.
- `space/RocketEntity`, `RocketCabin`, `SpaceTravel`: persistencia, deterioro y seguridad.
- `space/RocketUpgradeItem`, `item/ModItems`: kits y cohetes.
- `client/rocket/RocketModules`: módulos 3D, usando emisión directa compatible con la ruta de render de Sodium.
- `tools/generate_rocket_variants.py`: fuente común para módulos e ítems 3D.
- `data/space_fabric/recipe/*_rocket_kit.json`: recetas editables.

La compilación comprueba recetas con los codecs nativos y su ensamblado, conversión
sin pérdida, guardado de variante/casco/carga/combustible, rutas compatibles y
límites profundos. También emite y valida los vértices de los nueve modelos e ítems,
el fuselaje original y la abertura real de la cabina, sin GPU. La inspección visual
en una partida con Sodium y las pruebas de vuelos multijugador siguen pendientes.
