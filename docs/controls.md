# Teclas configurables — 0.45.1

Abre **Opciones → Controles → Asignación de teclas** y busca la sección
**Space Fabric — Controles**. Usa la lista nativa de Minecraft: pulsa la tecla
actual y después la nueva tecla o botón de ratón que quieras asignar. Puedes
restablecer cada acción con su botón de reinicio. Minecraft señala los conflictos
si dos acciones comparten la misma tecla.

| Acción | Asignación inicial |
|---|---|
| Abrir visor de recetas | K |
| Abrir espacios del traje | J |
| Abrir inventario del rover o aerostato | V |
| Abrir mapa planetario | M |
| Guardar localización | B |
| Mostrar/ocultar minimapa | N |
| Ver recetas del objeto bajo el cursor, dentro del visor | R |
| Ver usos del objeto bajo el cursor, dentro del visor | U |

R y U solo actúan dentro del visor de recetas, sobre un objeto y mientras no
estés escribiendo en la búsqueda. También admiten asignar botones del ratón.
Clic izquierdo y derecho mantienen sus funciones habituales de recetas y usos.
El mapa grande se cierra con la tecla asignada al mapa o con Esc.

La conducción y el ascenso del rover/aerostato usan los controles normales de
movimiento y salto de Minecraft. En el panel del cohete, el atajo de despegue
sigue la tecla de **Saltar**: Espacio de manera predeterminada. También se puede
pulsar el botón Despegar con el ratón.

Las asignaciones se guardan en el cliente mediante `options.txt`, como las teclas
de Minecraft. Al cambiar la categoría se conservan los identificadores anteriores,
por lo que no se reinician las preferencias existentes. Cambiar las teclas no
requiere modificar archivos ni reiniciar el servidor.

La compilación comprueba la API nativa de categorías y las referencias a las
asignaciones. Falta verificar visualmente la lista, los conflictos, las teclas
reasignadas y su persistencia al reiniciar un cliente gráfico.
