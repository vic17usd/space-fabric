# Industria de Venus — 0.34.0

Instala `space-fabric-0.34.0.jar` en cliente y servidor, sustituyendo el JAR
anterior. Requiere Minecraft 26.1, Fabric y Fabric API. Funciona en mundos ya
creados: se añaden máquinas y recetas, sin regenerar el terreno.

## Cadena de producción

Las cuatro máquinas nuevas tienen inventario, progreso, automatización,
mantenimiento y menú propios mediante el sistema colonial existente. Los
ingredientes se consumen al completar el lote; una salida llena pausa el trabajo
sin gastar suministros. Los subproductos y los recipientes también necesitan
espacio. Una avería detiene la máquina y permite continuar después de repararla.

| Máquina | Entrada de material | Batería | Tercer ingrediente | Productos | Tiempo |
|---|---|---|---|---|---|
| Colector atmosférico de Venus | Bombona vacía | Cargada | — | Atmósfera venusiana + batería descargada | 10 s |
| Separador de gases | Atmósfera venusiana | Cargada | Bombona vacía | CO₂ + nitrógeno + batería descargada | 10 s |
| Procesador de azufre | 2 cristales de azufre | Cargada | Nitrógeno | 2 azufres refinados + batería descargada + bombona vacía | 10 s |
| Procesador de azufre | Condensado ácido | Cargada | Hidrógeno | Azufre refinado + agua + batería descargada | 10 s |
| Horno de cerámica venusiana | Piroxeno | Cargada | CO₂ | 2 cerámicas de piroxeno + batería descargada + bombona vacía | 15 s |
| Horno de cerámica venusiana | Cerámica de piroxeno | Cargada | Trozo de hierro denso | Placa de compuesto + batería descargada | 15 s |
| Horno de cerámica venusiana, desde 0.41 | Cerámica de piroxeno | Cargada | 2 lingotes de hierro | Placa de compuesto + batería descargada | 20 s |
| Electrolizador existente | CO₂ | Cargada | — | Oxígeno + polvo de carbono + batería descargada | 10 s |

Tiempos a 20 TPS y con integridad superior al 50 %. Las siete fórmulas se añaden
a las trece anteriores. Cada fórmula conserva los recipientes: los gases pueden
consumirse o convertirse, pero las bombonas no desaparecen ni se multiplican.
Las baterías cargadas vuelven descargadas y se pueden cargar otra vez.

La atmósfera real de Venus contiene aproximadamente 96,5 % de CO₂ y 3,5 % de N₂.
Las bombonas son **unidades de proceso del juego**, con distinta cantidad de gas
según el contenido; separar una de cada tipo no representa una mezcla 50/50.
La electrólisis y la recuperación de ácido simplifican cantidades y reacciones:
no son ecuaciones químicas, equipos ni materiales certificados para Venus real.

## Montar la instalación

1. Analiza olivino venusiano, piroxeno o azufre para obtener informes de
   investigación. Las cuatro máquinas se fabrican con materiales iniciales,
   paneles presurizados e informes; no necesitan sus propios productos avanzados.
2. Coloca el **colector al aire libre en Venus**, con aire seco inmediatamente
   sobre él y sin bloques más arriba en esa columna. Un techo de cristal,
   hojas, otra máquina o una habitación cerrada bloquean la toma. No recoge
   aire de los hábitats ni funciona en otros planetas.
3. Protege el colector con filtros y revestimiento: trabaja expuesto a la
   corrosión. Prepara baterías, bombonas y traje venusiano para atenderlo.
4. Lleva la mezcla al separador. El separador y el procesador pueden funcionar
   dentro de una base certificada, protegidos de la atmósfera exterior.
5. Pon **lava justo debajo del horno**; una batería por lote alimenta su control
   y procesamiento. Si se retira la lava, se pausa el progreso. El generador
   geotérmico existente sirve para recargar las baterías de la instalación.

Las recetas aparecen en el visor con **K**. Los procesos coloniales muestran
todos sus productos al mismo tiempo y conservan las posiciones de entradas
vacías; las salidas del separador ya no alternan como si fueran ingredientes
intercambiables. Cuatro avances guían la captura, separación, refinado y compuesto.

Las casillas de entrada son las tres superiores del menú: material, batería y
reactivo/recipiente. Las tres de la segunda fila muestran producto, coproducto
y batería descargada. Los recipientes vacíos devueltos van a la primera casilla
de la tercera fila. Las tolvas superiores insertan material; las laterales,
batería/reactivo; las inferiores extraen las salidas. El inventario y el progreso
de una máquina colocada se guardan al cerrar/cargar el mundo.

## Utilidad de los materiales

| Producto | Uso |
|---|---|
| Panel compuesto venusiano | Cerramiento certificado: dureza 12 y resistencia a explosiones 120, frente a 6/30 del panel anterior. Receta de ocho bloques. Requiere pico de hierro o superior. |
| Refuerzo cerámico para máquinas | Clic derecho sobre cualquiera de las diez máquinas mantenibles: reduce el ataque químico antes del filtro. Se instala una sola vez y se conserva al recoger y colocar. |
| Placa de compuesto venusiano | Refuerza las cuatro piezas de traje en herrería; sirve para paneles, refuerzos de máquina y mejora de cohete. También repara el traje reforzado en el yunque. |
| Polvo de carbono | Recicla un filtro saturado junto con papel, como alternativa al carbón vegetal. |
| Nitrógeno | Gas inerte en refinado de azufre y fabricación de la mejora de cohete. |
| Azufre refinado | Fundente para paneles/refuerzos y para la herrería. |

El refuerzo cerámico reduce a la mitad el ataque previo al filtro. Con filtro
activo, el daño se redondea a un mínimo de una unidad por segundo de exposición:
calma 1, bruma 1 y fuente adyacente 2, frente a 1/2/4 sin refuerzo. La saturación
del filtro mantiene su ritmo original; el refuerzo protege la carcasa, no elimina
el gas. Se aplica incluso a máquinas ya desgastadas, sin repararlas ni rellenar
su capa. Reaplicarlo no consume otro objeto. Shift + clic derecho y el tooltip
de una máquina recogida indican el refuerzo instalado.

## Trajes y cohete

En la **mesa de herrería**, coloca azufre refinado en la casilla izquierda,
la pieza venusiana original en el centro y una placa de compuesto a la derecha.
Obtienes la pieza reforzada correspondiente, con el doble de durabilidad y
conservando daño acumulado, nombre y encantamientos. Usa el mismo aspecto en
el personaje; sus iconos de inventario distinguen los refuerzos.

Las cuatro piezas siguen perteneciendo a la categoría venusiana: admiten mezcla
de originales y reforzadas, conservan sus límites de presión/temperatura y usan
los espacios independientes de traje. La mejora aumenta la duración frente al
desgaste; no habilita por sí sola los destinos que necesitan un traje abisal.

La receta alternativa **Mejora de cohete venusiano** utiliza cuatro placas,
cerámica, dos bombonas de nitrógeno, un informe venusiano y una celda de
combustible vacía. Devuelve ambas bombonas vacías y produce la mejora ya
integrada en el sistema de cohetes. Conserva los modelos, capacidades y recetas
anteriores, de modo que no exige haber llegado a Venus para fabricar el primer
vehículo capaz de llegar allí.

## Integración y verificación

- `industry/IndustryContent.java`: materiales, gases y piezas reforzadas.
- `colony/ColonyRecipes.java`: fórmulas compartidas por procesamiento y visor.
- `colony/ColonyEnvironment.java`: comprobaciones de toma, planeta, luz y lava.
- `maintenance/MachineWear.java`: refuerzo persistente compatible con NBT previo.
- `tools/generate_industry_resources.py`: genera modelos, iconos, etiquetas,
  doce recetas nativas, traducciones y cuatro avances. Ejecútalo después de
  generadores generales de colonias/mantenimiento si regeneras los recursos.

Las comprobaciones automáticas usan inventarios, registros, codecs, herrería
y guardado nativos de Minecraft. Incluyen bloqueo ambiental/salidas, conservación
de bombonas, progreso parcial del horno, tags de traje y copia del refuerzo al
recoger las diez máquinas. Los modelos se interpretan por CPU y se validan las
referencias de texturas. Quedan pendientes de observar en una partida real la
toma bajo diferentes techos, interacción visual de los menús, recorrido completo
de la fábrica y sincronización multijugador.
