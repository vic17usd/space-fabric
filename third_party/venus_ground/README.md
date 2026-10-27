# Diseños de suelo venusiano aportados por el usuario

Recursos originales de **vic17usd using CreativeMode.net**, adaptados a Fabric 26.1:

| Archivo recibido | Diseño original | Uso en Space Fabric |
|---|---|---|
| Venus suelo 1.jar | [Sulfur Veinstone v1](https://creativemode.net/mod/sulfur-veinstone-r7gz6mnf) | Basalto vulcanizado, PNG de 128×128 |
| venus suelo 2.jar | [Sulfur Veinstone v2](https://creativemode.net/mod/sulfur-veinstone-r7gz6mnf) | Roca volcánica ardiente, PNG de 16×16 |
| venus suelo.jar | [Venus Sulfurstone](https://creativemode.net/mod/venus-sulfurstone-gtdukli6) | Depósito de azufre, PNG de 128×128 |
| venus suelo 4.jar | [Sulfurstone Cluster](https://creativemode.net/mod/sulfurstone-cluster-spcbdqbx) | Afloramiento 3D de 21 piezas, atlas de 512×512 |

Los PNG se conservan byte por byte. El primer JAR referenciaba gilded_blackstone
vanilla en su modelo: se usa su PNG propio incluido, según el diseño solicitado.
Los dos primeros JAR comparten namespace y nombre de bloque, pero sus recursos
se asignan a IDs distintos dentro de Space Fabric, evitando colisiones.

Las licencias originales se guardan en las cuatro subcarpetas y se incluyen en
`META-INF/licenses/venus_ground` del JAR final. No se conceden permisos nuevos.
El manifiesto registra autoría y SHA-256 de cada JAR. Modelos, textura, configuración
y metadatos de origen se conservan en `tools/import_sources/venus_ground/`.

Los JAR se leyeron como archivos de datos. No se ejecutaron sus inicializadores,
no se incluyeron clases originales, mixins, dependencias anidadas ni telemetría.
