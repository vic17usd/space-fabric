# Habitantes de Venus — Fabric 26.1, 0.15.0

Los cuatro mobs están integrados con sus modelos y animaciones originales.
No es necesario instalar los JAR externos.

| Mob | Peso | Grupo | Hábitat y comportamiento |
| --- | ---: | --- | --- |
| Acidstone Slime | 18 | 1–3 | Terrestre, evita jugadores y conserva sus animaciones de salto |
| Obsidian Lavafang | 10 | 1–2 | Depredador terrestre con mordida animada |
| Ashplume Emberwalker | 16 | 1–3 | Terrestre, pasea y huye al sufrir daño |
| Sulfurwing Copper Harbinger | 4 | 1 | Volador hostil, patrulla y realiza ataques aéreos |

Solo aparecen naturalmente en dimensión y bioma `space_fabric:venus`, al aire
libre y sobre suelo seco válido. Usan aparición inicial sobre el suelo; Sulfurwing
se eleva mediante navegación FlyingPathNavigation y FlyingMoveControl y patrulla
entre seis y catorce bloques por encima del terreno. Mantiene colisiones: no
atraviesa bloques. Los hostiles requieren dificultad superior a Pacífico, sin
restricción de oscuridad, para habitar la superficie iluminada de Venus.

La gravedad global aplica el factor venusiano 0,90 a sus atributos; en vuelo,
Sulfurwing sostiene su peso mediante su controlador y desactiva la caída por
gravedad. Los demás usan física terrestre. Acidstone deja de depender de agua.

VenusMob protege frente a hipoxia, descompresión, presión, calor y veneno. El
registro hace inmunes al fuego a los cuatro; la roca caliente de Venus no les
aplica lentitud. Las tormentas ácidas del sistema existente afectan a jugadores,
no a esta fauna. Estas adaptaciones no protegen al jugador ni otorgan invulnerabilidad
frente al combate ordinario.

Registros y comportamiento: `entity/venus/`; modelos y renderers: `client/venus/`.
Los pesos están en `worldgen/biome/venus.json`. Los huevos usan los identificadores
`space_fabric:<nombre_con_guiones_bajos>_spawn_egg`. Acidstone conserva el drop de
botella de ácido y Sulfurwing el núcleo de azufre; son materiales sin nuevas recetas.

Sustituye el JAR de Space Fabric en cliente y servidor y reinicia el mundo.
Las apariciones normales pueden ocurrir en chunks existentes bajo las reglas de
población, distancia y simulación vanilla; no se fuerzan mobs con temporizadores.
Se conservan los sistemas y la cabina del cohete de versiones anteriores.

Verificación: compilación, recursos y 230 pruebas, incluidos huevos y presencia
exclusiva en Venus. La apariencia, frecuencia de aparición y movimiento durante
una partida real aún requieren inspección en juego.

[Autoría, fuentes y licencias de los cuatro JAR](../third_party/venus_fauna/README.md).
