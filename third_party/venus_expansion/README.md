# Contenido venusiano aportado por el usuario

Recursos y código generado de CreativeMode.net, adaptados a Fabric/Minecraft 26.1:

- [Copper Acidstinger Drone](https://creativemode.net/mod/copper-acidstinger-drone-qvloegth), autor acreditado `vic17usd using CreativeMode.net` — Venus mob.jar.
- [Sulfurwing Copperstinger](https://creativemode.net/mod/sulfurwing-copperstinger-qxq3li2s), autor acreditado `vic17usd using CreativeMode.net` — Venus mob 2.jar.
- [Copper Crown Village](https://creativemode.net/mod/copper-crown-village-zvhfngoo), autor acreditado `17usdd using CreativeMode.net` — Venus build 2.jar.
- [Sunstone Pyramid Bazaar](https://creativemode.net/mod/sunstone-pyramid-bazaar-m4ezrsqc), autor acreditado `17usdd using CreativeMode.net` — Venus build.jar.

Las licencias originales se conservan en cada subcarpeta y se incluyen en el JAR
final bajo `META-INF/licenses/venus_expansion`. La adaptación conserva la titularidad
de los recursos originales; no concede permisos adicionales sobre ellos.

`manifest.json` registra los hashes de los dos JAR de fauna y sus recursos.
`structures.json` registra los JAR de estructuras, autoría, dimensiones, número de
bloques, división en plantillas y sustituciones de materiales. Los JSON originales
de construcciones se conservan en `tools/structure_sources/venus/`; los modelos y
metadatos originales de fauna, en `tools/import_sources/venus_fauna/`.

Los dos JAR de mobs indicaban Minecraft 1.21.5. Se adaptaron sus clases de entidad,
rig, modelo, animación y renderer a las APIs nativas de 26.1. Se conservaron las
texturas y huevos originales. Se reutiliza el ítem sulfur_core ya existente.
La importación leyó los archivos como datos y adaptó sus clases; no ejecutó sus
inicializadores ni incorporó su telemetría, varitas o paquetes JAR anidados.
