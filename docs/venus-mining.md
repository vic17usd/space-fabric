# Prospección y minería industrial de Venus — 0.45.0

Instala el JAR en cliente y servidor, sustituyendo el anterior. Funciona en Venus
de partidas existentes: lee y extrae las menas ya presentes, sin regenerar terreno.
No requiere un mod externo de energía o mapas. Recetas en el visor **K**.

## 1. Localizar un depósito

Fabrica un **Escáner geológico de Venus** con cobre, cuarzo, brújula, placas
compuestas y una batería vacía. Lleva baterías cargadas. Haz clic sobre el suelo
o usa el escáner en el aire para tomar como origen el bloque bajo tus pies.

- Examina **9×9 bloques**, desde el origen hasta 63 bloques más abajo: 64 capas.
- Detecta **mena de olivino venusiano, mena de piroxeno y depósito de azufre**.
- Muestra el número de bloques observados de cada mineral y las coordenadas de
  la muestra más cercana de cada tipo. No supone que formen una sola veta.
- Cada pulso consume una batería y devuelve su celda vacía; creativo no consume.
  Si el inventario no admite la celda vacía, se deja caer junto al jugador.
- Enfriamiento de **10 segundos** entre pulsos. Conserva el escáner en tu inventario
  y permanece en Venus hasta recibir el informe; desconectarte, morir, cambiar de
  dimensión o sacar el dispositivo cancela el sondeo, sin devolver la energía usada.
- Lee exclusivamente chunks ya cargados. El informe advierte cuántas celdas no
  pudo examinar. No utiliza `/locate`, genera chunks ni busca todo el planeta.

Los hallazgos quedan como puntos dentro del propio escáner, hasta 32, con dimensión,
coordenadas y nombre del mineral. **M → Importar** los copia al atlas de Venus.
También sigue importando navegadores. La importación omite coordenadas duplicadas,
no borra los puntos del dispositivo y respeta los 128 marcadores del atlas.
Estos puntos son observaciones: un depósito puede agotarse después.

## 2. Instalar la perforadora

Fabrica una **Perforadora de sondeo de Venus** y una **Broca de perforación venusiana**.
Colócala por encima de las coordenadas X/Z de una muestra. Extrae selectivamente
las menas dentro de **3×3 columnas y 96 bloques bajo la máquina**, empezando una
capa por debajo. No debe estar a más de 96 bloques por encima del depósito.

La máquina aparece apagada. Quien la coloca se convierte en su propietario.
**Shift + clic con la mano vacía** activa o detiene el trabajo. Solo el propietario
puede hacerlo. La máquina exige que su propietario esté conectado, tenga permiso
para construir y permanezca en Venus; no necesita estar junto a ella. Una señal
de redstone también la pausa. No funciona en otros planetas.

| Casilla, contando desde 1 | Función |
|---|---|
| 1, superior izquierda | Broca; dura **256 extracciones** |
| 2, superior central | Batería cargada |
| 3, superior derecha | Bombona de refrigerante frío |
| 4, 5, 8 y 9 | Minerales extraídos |
| 6 | Celdas de batería vacías |
| 7 | Agua devuelta tras calentarse el refrigerante |

Cada mena requiere **200 ticks de trabajo, 10 segundos a 20 TPS**, una batería
completa y una bombona de refrigerante frío. El tiempo de búsqueda y las pausas
son adicionales. Devuelve **un material por bloque de mena** y reemplaza ese bloque
por basalto vulcanizado. El rendimiento automático es fijo: no usa las tiradas
aleatorias de minería manual, Fortuna, Toque de seda ni concede experiencia.
La broca pierde un uso, en lugar de consumirse entera como una receta normal.

No perfora paredes arbitrarias, elimina estructuras ni abre un pozo por el que
pueda entrar la atmósfera. Es una abstracción de extracción y relleno de sondeos.
No extrae el ácido condensado ni los cristales decorativos: solo las tres menas
indicadas. El depósito se agota de verdad; no existe producción infinita por semilla.

## 3. Automatizar energía, refrigerante y retirada

```text
Acumulador ─ cable ─┐
                   ├─ interfaz orientada a la perforadora
Depósito frío ─ tubo┘                │
                           tolva inferior → cofre

Agua devuelta → depósito de agua → refrigerador → depósito frío
```

La interfaz reconoce automáticamente el contrato de suministro de la perforadora. Carga la broca manualmente
(o mediante una tolva superior) y proporciona a la interfaz **una bombona vacía
y una celda de batería vacía**, como en las otras máquinas. La red entrega
**2.400 unidades de energía y 1.000 de refrigerante frío** por extracción.
La interfaz devuelve los envases a su reserva y exporta el agua hacia un depósito
compatible. Reserva espacio para el agua: un depósito de refrigerante frío no
puede recibir agua mientras conserva ese tipo de contenido.

Los minerales sólidos salen mediante una **tolva por debajo**, hacia un cofre.
La tolva también puede sacar agua/celdas si las encuentra antes que la interfaz;
elige esa ruta o la interfaz según tu montaje. La red de tuberías existente
transporta líquidos, gases y energía; no transporta minerales sólidos.

El refrigerador vuelve a enfriar el agua usando energía adicional. Así, el agua
circula y la energía se consume; no hay un circuito de energía gratuita.

## 4. Calor, corrosión y pausas

La perforadora genera **55 unidades de calor por segundo activo** en el sistema
industrial existente. Usa un intercambiador térmico adyacente con carga o intégrala
en una sala con refrigeración dimensionada para ese trabajo. La bombona fría de
la extracción no sustituye la refrigeración del chasis. Sin evacuación de calor,
la máquina alcanza el umbral de parada y espera a enfriarse.

Se aplican el desgaste, filtros, revestimientos, reparación y fallos de las demás
máquinas venusianas. No arranca si está averiada. Muestra estados para apagado,
búsqueda, agotamiento, energía/materiales insuficientes, salidas llenas, protección,
propietario ausente, redstone y chunks descargados.

No consume recursos ni quita la mena si la salida está bloqueada, el chunk falta,
la extracción está protegida o falla la sustitución del bloque. La protección
consulta permisos, borde del mundo, protección de spawn y el evento Fabric
`PlayerBlockBreakEvents.BEFORE`. La compatibilidad con un mod de parcelas concreto
requiere comprobar que escucha ese evento.

Guarda propietario, encendido, cursor del sondeo, progreso, inventario y estado
industrial. Una recarga no aplica tiempo de trabajo offline. Detener y volver
activar conserva el cursor. Al agotarse el volumen hay que **mover la máquina**.
Romperla suelta su inventario y conserva el mantenimiento y calor del bloque como
las demás máquinas; el sondeo y propietario se asignan de nuevo al colocarla.

## Comprobaciones en partida pendientes

Las pruebas nativas cubren el motor de extracción, inventarios, interrupciones,
persistencia, volúmenes, recetas y puertos de transporte. No sustituyen una sesión
de Minecraft. Revisar en copias de una partida:

1. Sondear menas conocidas y comparar el informe; importar con M y cambiar de planeta.
2. Activar una perforadora encima de la veta y observar el relleno con basalto.
3. Completar el circuito de frío/agua/energía y comprobar retirada por tolva.
4. Pausar con redstone, llenar salidas, sobrecalentar, reparar y descargar el chunk.
5. Reconectar a mitad de una extracción y verificar que no duplica ni pierde recursos.
6. En multijugador, probar propietario ausente y cancelación con el mod de parcelas usado.

## Prospección mejorada

Proyecto Aurora, desde 0.46.0, permite instalar una lente que amplía el escáner
a 13×13 y 96 capas. Mantiene los presupuestos de trabajo, guardado y carga local.
[Cadena de investigación e instalación](venus-research.md).
