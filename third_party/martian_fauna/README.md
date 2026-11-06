# Procedencia de la fauna marciana

Los tres JAR aportados identifican a **17usdd using CreativeMode.net** como autor.
Los modelos, keyframes, texturas y sonido proceden de esos mods de
[CreativeMode.net](https://creativemode.net), © 2025–2026 Argon, Inc.

| Nombre original | Archivo fuente | Página oficial |
| --- | --- | --- |
| Crystal Hum Crawler | `crystal-hum-crawler-rmo4iakf.jar` | https://creativemode.net/mod/crystal-hum-crawler-rmo4iakf |
| Golden Eye Sandwraith | `golden-eye-sandwraith-6xxxfsvc.jar` | https://creativemode.net/mod/golden-eye-sandwraith-6xxxfsvc |
| Red Dune Ironmaw | `red-dune-ironmaw-4w2qsmth.jar` | https://creativemode.net/mod/red-dune-ironmaw-4w2qsmth |

Licencia original: [CreativeMode Mods License](https://creativemode.net/mod-license),
versión 1.2. Los textos completos se conservan por especie y dentro del JAR compilado.
Son recursos de terceros sujetos a esa licencia, que distingue modificación para
uso privado de distribución de versiones modificadas. La adaptación no cambia
su titularidad ni concede permisos adicionales.

`manifest.json` registra los SHA-256 de los JAR y de cada recurso binario importado.
Se portaron sus modelos Java, animaciones y estado de rig a `client/fauna/` y
`entity/fauna/` con el namespace de Space Fabric. Las nuevas clases `*Entity`
implementan IA terrestre, hábitat y combate para Marte. No se incluye ni ejecuta
el inicializador o la telemetría de los mods fuente.
