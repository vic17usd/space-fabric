# Nombres de estructuras — 0.30.1, ampliados en 0.35.0

| Planeta | Nombre visible | Diseño original / ID conservado | Alias para buscar |
|---|---|---|---|
| Marte | Base de Marte | Martian Breathforge / `martian_breathforge` | `#space_fabric:base_marciana` |
| Marte | Pueblo de Marte | Martian Haven / `martian_haven_settlement` | `#space_fabric:pueblo_marciano` |
| Marte | Santuario subterráneo de Marte | Soulfire Netherite Sanctum / `soulfire_netherite_sanctum` | `#space_fabric:santuario_marciano` |
| Venus | Pueblo de Venus | Copper Crown Village / `copper_crown_village` | `#space_fabric:pueblo_venusiano` |
| Venus | Pirámide de Venus | Sunstone Pyramid Bazaar / `sunstone_pyramid_bazaar` | `#space_fabric:piramide_venusiana` |
| Venus | Caldera de azufre de Venus | Sulfur Crown Caldera / `venus_sulfur_caldera` | `#space_fabric:caldera_venusiana` |
| Venus | Puesto de ceniza de Venus | Venusian Ash Outpost / `venusian_ash_outpost` | `#space_fabric:puesto_ceniza_venusiano` |
| Venus | Bóveda de azufre de Venus | Venusian Sulfur Vault / `venusian_sulfur_vault` | `#space_fabric:boveda_venusiana` |
| Venus | Refugio de basalto de Venus | Venus Basalt Haven / `venus_basalt_haven` | `#space_fabric:refugio_venusiano` |
| Venus | Fundición abandonada de Venus | Rustfall Foundry / `rustfall_foundry` | `#space_fabric:fundicion_venusiana` |

Escribe **`/estructuras`** para ver el catálogo en el chat. Al pulsar un nombre se
prepara el comando de búsqueda con la dimensión correcta; tú decides ejecutarlo.
El catálogo es de consulta; el comando vanilla de localización conserva sus
permisos habituales de operador y no se ejecuta automáticamente.

También puedes usar estos comandos, estando en el planeta correspondiente:

```mcfunction
locate structure #space_fabric:base_marciana
locate structure #space_fabric:pueblo_marciano
locate structure #space_fabric:santuario_marciano
locate structure #space_fabric:pueblo_venusiano
locate structure #space_fabric:piramide_venusiana
locate structure #space_fabric:caldera_venusiana
```

El carácter **`#`** es necesario: son etiquetas españolas que apuntan a los
registros existentes, no estructuras adicionales. Se sugieren al escribir el
argumento de `/locate structure`. Los nombres propios del catálogo se mantienen
en español incluso si el cliente usa inglés.

Las claves de registro, plantillas, generación, frecuencia, habitantes y jefes
se conservan. Los comandos antiguos siguen funcionando y las construcciones ya
generadas no necesitan regenerarse. Minecraft vanilla muestra identificadores en
`/locate`; estos alias permiten reconocerlos en español sin renombrar los datos
guardados. El catálogo muestra los nombres con espacios y tildes.
