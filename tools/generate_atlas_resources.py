#!/usr/bin/env python3
"""Bilingual controls for the personal planetary atlas. No third-party map dependency."""
import json
from pathlib import Path

root=Path(__file__).resolve().parents[1]/'src/main/resources/assets/space_fabric/lang'
texts={
    'title':('Atlas planetario','Planetary Atlas'),
    'overworld':('Tierra · Overworld','Earth · Overworld'),
    'new':('Aquí +','Here +'), 'import':('Importar','Import'),
    'markers':('Marcadores (%s/128)','Waypoints (%s/128)'),
    'edit':('Editar destino','Edit destination'), 'close':('Cerrar','Close'),
    'center':('Centrar','Recenter'), 'new_marker':('Guardar localización','Save location'),
    'edit_marker':('Editar localización','Edit location'),
    'name':('Nombre','Name'), 'default_name':('Localización','Location'),
    'x':('Coordenada X','X coordinate'),'y':('Altura Y','Y altitude'),'z':('Coordenada Z','Z coordinate'),
    'color':('Color','Color'), 'visible':('Visible','Visible'), 'hidden':('Oculto','Hidden'),
    'save':('Guardar','Save'), 'delete':('Eliminar','Delete'), 'cancel':('Cancelar','Cancel'),
    'limit':('Máximo: 128 marcadores por dimensión','Limit: 128 waypoints per dimension'),
    'invalid':('Revisa nombre y coordenadas','Check name and coordinates'),
    'save_failed':('No se guardó. Consulta el registro.','Not saved. Check the log.'),
    'controls':('Arrastrar · Rueda: zoom · Clic der.: marcar','Drag · Wheel: zoom · Right-click: mark'),
    'imported':('Importadas %s localizaciones del navegador en este planeta.','Imported %s navigator locations on this planet.'),
    'waiting':('Esperando la identidad del mundo. Cliente y servidor necesitan Space Fabric 0.44.0.','Waiting for world identity. Client and server need Space Fabric 0.44.0.'),
    'error':('No se pudo leer o guardar el atlas. Consulta el registro; los archivos están en config/space-fabric/maps.','Could not read or save the atlas. See the log; files are in config/space-fabric/maps.'),
}
keys={
    'key.space_fabric.atlas':('Abrir mapa planetario','Open planetary map'),
    'key.space_fabric.map_marker':('Guardar localización','Save location'),
    'key.space_fabric.minimap':('Mostrar/ocultar minimapa','Toggle minimap'),
}
for i,lang in enumerate(('es_es','en_us')):
    path=root/(lang+'.json');data=json.loads(path.read_text())
    data.update({'atlas.space_fabric.'+k:v[i] for k,v in texts.items()})
    data.update({k:v[i] for k,v in keys.items()})
    path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
