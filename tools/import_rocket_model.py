#!/usr/bin/env python3
"""Importa el modelo suministrado sin dependencias de Blockbench en tiempo de ejecución."""
import base64
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'tools/import_sources/space-rocket.bbmodel'

def generate():
    doc = json.loads(SOURCE.read_text())
    elements = doc['elements']
    assert len(doc['textures']) == 1 and doc['resolution'] == {'width': 512, 'height': 512}
    assert all(e['type'] == 'cube' and not any(e.get('rotation', [])) for e in elements)
    assert set(doc['outliner']) == {e['uuid'] for e in elements}, 'No se admiten grupos transformados'
    texture = doc['textures'][0]
    png = base64.b64decode(texture['source'].split(',', 1)[1], validate=True)
    (ROOT / 'src/main/resources/assets/space_fabric/textures/entity/rocket.png').write_bytes(png)
    # El atlas vanilla de ítems solo descubre las carpetas item/ y block/.
    (ROOT / 'src/main/resources/assets/space_fabric/textures/item/rocket.png').write_bytes(png)
    def f(n): return f'{n:.7f}F'
    # Modelo de entidad: Y/Z invertidos para el espacio de ModelPart; escala uniforme.
    scale = 25.6 / 19
    lines = []
    mapping = ['up', 'down', 'west', 'south', 'east', 'north']
    for e in elements:
        a, b = e['from'], e['to']
        coords = [a[0]*scale, -b[1]*scale, -b[2]*scale,
                  (b[0]-a[0])*scale, (b[1]-a[1])*scale, (b[2]-a[2])*scale]
        uv = []
        for face in mapping:
            info = e['faces'][face]
            assert info['texture'] in (texture['uuid'], 0, '0') and not info.get('rotation', 0)
            uv.extend(info['uv'])
        lines.append('        cubes.add(cube(' + ', '.join(map(f, coords)) + ', new float[]{' + ', '.join(map(f, uv)) + '}));')
    java = '''package dev.vic17usd.spacefabric.client.rocket;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.ModelPart.Vertex;
import net.minecraft.client.model.geom.ModelPart.Polygon;
import net.minecraft.core.Direction;

/** Generado por tools/import_rocket_model.py desde el archivo del usuario. */
public final class RocketModel extends EntityModel<RocketRenderer.State> {
    public RocketModel() { super(createRoot()); }
    private static ModelPart createRoot() {
        List<Cube> cubes = new ArrayList<>();
''' + '\n'.join(lines) + '''
        return new ModelPart(cubes, Map.of());
    }
    private static Cube cube(float x, float y, float z, float w, float h, float d, float[] uv) {
        Cube cube = new Cube(0, 0, x, y, z, w, h, d, 0, 0, 0, false, 512, 512, EnumSet.allOf(Direction.class));
        Vertex a = vertex(x,y,z), b = vertex(x+w,y,z), c = vertex(x+w,y+h,z), e = vertex(x,y+h,z);
        Vertex f = vertex(x,y,z+d), g = vertex(x+w,y,z+d), i = vertex(x+w,y+h,z+d), j = vertex(x,y+h,z+d);
        cube.polygons[0] = face(Direction.DOWN, uv, 0, g,f,a,b);
        cube.polygons[1] = face(Direction.UP, uv, 4, c,e,j,i);
        cube.polygons[2] = face(Direction.WEST, uv, 8, a,f,j,e);
        cube.polygons[3] = face(Direction.NORTH, uv, 12, b,a,e,c);
        cube.polygons[4] = face(Direction.EAST, uv, 16, g,b,c,i);
        cube.polygons[5] = face(Direction.SOUTH, uv, 20, f,g,i,j);
        return cube;
    }
    private static Vertex vertex(float x, float y, float z) { return new Vertex(x,y,z,0,0); }
    private static Polygon face(Direction direction, float[] uv, int offset, Vertex... vertices) {
        return new Polygon(vertices, uv[offset], uv[offset+1], uv[offset+2], uv[offset+3], 512, 512, false, direction);
    }
}
'''
    (ROOT / 'src/main/java/dev/vic17usd/spacefabric/client/rocket/RocketModel.java').write_text(java)
    # El ítem usa geometría vanilla con coordenadas y UV en unidades de 16.
    item = {'textures': {'0': 'space_fabric:item/rocket', 'particle': 'space_fabric:item/rocket'}, 'elements': []}
    for e in elements:
        item['elements'].append({'from': [e['from'][0]*16/39+8, e['from'][1]*16/39, e['from'][2]*16/39+8],
            'to': [e['to'][0]*16/39+8, e['to'][1]*16/39, e['to'][2]*16/39+8],
            'faces': {side: {'uv': [v/32 for v in face['uv']], 'texture': '#0'} for side, face in e['faces'].items()}})
    item['display'] = {'gui': {'rotation': [30,225,0], 'translation':[0,0,0], 'scale':[1.1,1.1,1.1]},
        'ground': {'translation':[0,2,0], 'scale':[0.5,0.5,0.5]},
        'fixed': {'rotation':[0,180,0], 'scale':[0.8,0.8,0.8]},
        'thirdperson_righthand': {'rotation':[75,45,0], 'translation':[0,2,0], 'scale':[0.5,0.5,0.5]},
        'firstperson_righthand': {'rotation':[0,135,0], 'scale':[0.6,0.6,0.6]}}
    (ROOT / 'src/main/resources/assets/space_fabric/models/item/rocket.json').write_text(json.dumps(item, indent=2)+'\n')
    print(f'Importados {len(elements)} cubos, 288 caras y atlas 512x512; altura visible {39*scale/16:.3f} bloques.')

if __name__ == '__main__': generate()
