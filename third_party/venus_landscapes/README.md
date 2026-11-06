# Paisajes venusianos aportados por el usuario

Recursos de **vic17usd using CreativeMode.net**:

- **Venus Volcan.jar**: [Sulfur Crown Caldera](https://creativemode.net/mod/sulfur-crown-caldera-dpjre4dw).
- **Venus acido.jar**: [Ashen Acid Vents](https://creativemode.net/mod/ashen-acid-vents-j6czsz6r).
- **Venus lagos.jar**: [Obsidian Sulfur Wastes](https://creativemode.net/mod/obsidian-sulfur-wastes-wcwkrsde).

`manifest.json` conserva la autoría, las páginas y el SHA-256 de los JAR recibidos.
Las licencias originales están en las tres subcarpetas y se empaquetan en
`META-INF/licenses/venus_landscapes` del mod final. No se conceden permisos nuevos.

Los nueve PNG de bloques se conservan byte por byte. Sus modelos mantienen la
geometría y las UV, cambiando las referencias al namespace `space_fabric`. Los
originales, las configuraciones de bioma y el JSON de la caldera se guardan en
`tools/import_sources/venus_landscapes/`.

La adaptación reimplementa generación, contacto corrosivo y fumarolas con las
APIs nativas de Fabric/Minecraft 26.1. Se reutilizan los materiales de azufre y
condensado ya existentes. No se ejecutó código de los JAR, ni se incorporaron sus
clases, mixins, varitas, proyectiles, telemetría o dependencias anidadas. Los dos
biomas se integran en Venus sin la dependencia TerraBlender que pedían sus originales.

La caldera conserva su silueta de 120×94×123. Sustituye lana, metales valiosos y
luces por geología local; rellena la roca bajo la piel del modelo y añade una
pequeña cuenca de lava contenida en el hueco central. Los canales verdes se
representan con depósitos corrosivos sólidos, sin miles de fuentes de fluido.
`caldera.json` documenta la paleta y los recuentos. La colocación usa 56 plantillas
por chunks y una base irregular, sin la operación rectangular de los asentamientos.
