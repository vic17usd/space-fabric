# Consola de entrenamiento de Marte — 0.60.0

## Dirección y guion

Una consola de expedición sobria, con el material de Minecraft como protagonista.
Fondo azul carbón, texto marfil, acento cálido de Marte; verde para oxígeno,
ámbar para energía y azul para agua. Sin partículas, vídeo, fuentes externas ni
marcos luminosos. Las pestañas, el estado y los controles quedan anclados mientras
la explicación dirige la mirada. La fuente nativa conserva su tamaño al reducir
la resolución: cambia la distribución, no la escala de todo el contenido.

| Capítulo | Acción y resultado visual |
|---|---|
| 1. Equipo | Iconos reales, cantidades y reserva separada del traje personal. |
| 2. Construcción | Suelo, paredes y techo; pausa con envolvente completa antes del corte. |
| 3. Controlador | Colocación sobre el suelo; contorno hueco sobre la toma; clic derecho. |
| 4. Acceso | Personaje atraviesa exterior, espera con ambas cerradas y pasa al interior. |
| 5. Soporte | O₂ a la casilla 1, batería a la 2; reservas de ejemplo y devolución a 7/6. |
| 6. Agua | Dos bombonas a la casilla 3 y 2000 unidades almacenadas. |
| 7. Fabricación | Ingredientes, productos, estación y duración del visor del servidor. |
| 8. Validación | Personaje dentro; requisitos simultáneos y anuncio del asentamiento. |

Los movimientos usan interpolación con velocidad cero en los extremos. La cámara
orbita discretamente cinco grados; los capítulos tienen fases de acción y lectura,
duran 16 segundos y conservan el resultado final hasta que el jugador continúe.
La barra permite buscar un instante. Una transición de 220 ms revela el capítulo
seleccionado sin alterar su progreso ni el estado de la misión.

El corte elimina superficies de la demostración; no afirma que una habitación
abierta esté sellada. La escena de construcción enseña primero la envolvente
cerrada. Las etiquetas de corte y las instrucciones recuerdan la diferencia.

## Ruta gráfica 26.1

`GuiGraphicsExtractor` produce estados de render; no es un contexto de dibujo 3D
inmediato. Fabric ofrece `PictureInPictureRendererRegistry`: `MarsGuideScene`
registra un renderer con textura de color y profundidad propias, reutilizadas por
la implementación nativa. Un accessor pequeño añade el estado al `GuiRenderState`,
porque esta versión no expone una llamada pública del extractor para PiP propios.

`BlockModelResolver` y `BlockModelRenderState` usan los modelos reales de panel,
controlador y puerta, con suelo de regolito de óxido de hierro para distinguir
la planta de las paredes; el jugador usa `AvatarRenderState`. Se envían mediante
`SubmitNodeCollector` y se procesan con `FeatureRenderDispatcher`, como el renderer
nativo de entidades de GUI. No se simula un mundo ni se crean entidades en él.
El bloque de aire señalado es un contorno de líneas con prueba de profundidad.
Los iconos y recetas usan el renderer de objetos de Minecraft.

La cámara ortográfica y sus límites incluyen construcción y cortes. El PiP se
recorta al área de escena; los textos se dibujan después en otro estrato. Los
modelos se cachean por estado y se invalidan cuando cambia el conjunto de modelos
por recarga de recursos. No se reconstruye la geometría de 170 bloques cada frame
ni se vuelven a leer texturas del disco. La textura PiP se libera por el ciclo de
vida nativo del renderer. No se añaden dependencias.

## Separación de responsabilidades

- `MarsGuideAnimation`: cronología determinista, esclusas, geometría y distribución.
- `MarsGuideScene`: extracción y render 3D; ninguna escritura en mundo o inventario.
- `MarsGuideScreen`: composición, controles, iconos, explicaciones y receta seleccionada.
- `MarsGuideRecipeRequest`: acepta únicamente la respuesta vigente antes de 10 segundos;
  descarta duplicados, respuestas atrasadas y resultados de una selección anterior.
- `MarsGuideNetwork.State`: única fuente del pie **SERVIDOR**. La animación no puede
  escribir objetivos, reservas ni recompensas. No cambia el protocolo de 0.59.

Los ejemplos de soporte conservan 2251 O₂ y 2395 energía: una carga de 2400,
74 celdas de aire y un ocupante después de un ciclo. Son valores explicativos,
no telemetría. El servidor sigue exigiendo 600/1500/2000 y todas las condiciones
simultáneas. Guardado, recetas, atmósferas, trajes, Venus y prototipo esférico
conservan su implementación anterior.

## Reproducir la inspección visual

Con Java 25 y las dependencias de Fabric del proyecto:

```sh
./gradlew runClient -PmarsGuideSmoke --console=plain
```

El fixture de desarrollo abre la pantalla real al entrar en un mundo de prueba.
Usa un mundo desechable; no contiene lógica que cambie sus bloques o inventarios.
El fixture vive en `src/guideSmoke`, se activa solo con esa propiedad y no entra
en el JAR instalable. Iniciar sin la propiedad conserva el arranque habitual.

Revisar pestañas 1–8, flechas, Tab/Enter, pausa, repetición y barra; alternar recetas
rápidamente; probar GUI 320×240, 427×240 y una distribución ancha. En una partida
local la guía pausa el mundo, pero la animación continúa hasta que se la pausa.
Cerrar y reabrir por `/marte guia` y desde el inventario. Comprobar el contorno de
la toma, la oclusión del personaje, que no se corten modelos ni iconos y que la
recarga F3+T vuelva a resolver los modelos.

Los resultados ejecutados y los límites de la inspección se registran en
[validation.md](validation.md). Las pruebas CPU de encuadres no sustituyen
capturas del cliente ni una partida completa de supervivencia.

## Vista de la entrega

[Captura real de la consola](assets/mars-guide-0.60.0.png) ·
[Distribución compacta real](assets/mars-guide-0.60.0-compact.png) ·
[Vídeo real de la construcción (17 s)](assets/mars-guide-0.60.0.mp4).

Estas vistas se obtuvieron en Minecraft 26.1, con el renderizador final y un
mundo de prueba. El vídeo enseña el montaje, la envolvente cerrada y el corte
posterior. El JAR está en `dist/space-fabric-0.60.0.jar`, junto a su `.sha256`.
