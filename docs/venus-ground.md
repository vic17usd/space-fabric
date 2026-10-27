# Suelo y afloramientos de Venus — 0.30.0

Los cuatro diseños aportados se incorporan sin instalar sus JAR por separado.
Instala únicamente la nueva versión de Space Fabric junto con sus dependencias.

| Diseño | Bloque del mod |
|---|---|
| Venus suelo 1 | `space_fabric:vulcanized_basalt`, roca principal |
| venus suelo 2 | `space_fabric:venus_hot_rock`, superficie caliente |
| venus suelo | `space_fabric:sulfur_deposit`, depósitos de azufre |
| venus suelo 4 | `space_fabric:sulfur_cluster`, formación 3D nueva |

Los tres bloques existentes conservan sus IDs, dureza, herramientas, daño térmico,
generación y drops. Sus menas y objetos de inventario usan las imágenes nuevas.
No se modifican las gemas de olivino, piroxeno ni el depósito de ácido. El nuevo
aspecto aparece también en chunks antiguos, al volver a cargar los recursos.

El cuarto archivo es una formación de hasta 3×3×3 bloques, con 21 piezas ocupadas.
Se conservan sus modelos y UV originales, atlas y cajas de colisión por pieza.
Cada fragmento se puede minar por separado con un pico de piedra o mejor y suelta
un cristal de azufre. Los fragmentos restantes permanecen, permitiendo excavar
la formación; no se recrean ni se obtiene el objeto completo al romper uno.

Los afloramientos naturales aparecen solamente en **chunks nuevos de Venus**:
un intento por cada 12 chunks de media, antes de comprobar el terreno. La tasa
real será menor. Exigen una base plana de 3×3 bloques de roca venusiana seca y
un volumen libre de 3×3×3; se rechazan pendientes, lava, paredes y plantas. No se
aplana terreno ni se colocan plataformas. La colocación no sale del chunk.

En creativo, el objeto **Afloramiento de azufre venusiano** coloca las 21 piezas
juntas sobre suelo plano. Requiere espacio libre y respeta permisos y colisiones
en todas las piezas. También puede obtenerse con:

```mcfunction
give @s space_fabric:sulfur_cluster
```

El comando `/place feature space_fabric:sulfur_cluster` permite probar la generación
estando en Venus; puede fallar si el punto elegido no tiene una superficie apta.
No se coloca un afloramiento en cada bloque ni se reemplaza toda la corteza por
geometría 3D, para conservar el terreno navegable y limitar el coste de renderizado.

`python3 tools/import_venus_ground.py` regenera la adaptación usando los originales
guardados. Se ejecuta después de generadores generales de materiales que puedan
reescribir las texturas antiguas. [Atribución y licencias](../third_party/venus_ground/README.md).

Validación: registros y generación nativos, colocación de las 21 piezas en varias
semillas, rechazo de obstáculos/lava/bordes descargados, colisiones y herramientas,
loot y decodificación de modelos con el cliente 26.1 por CPU. La inspección visual
en una partida y con optimizadores gráficos sigue pendiente.
