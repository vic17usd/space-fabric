#!/usr/bin/env python3
"""Import all user Blockbench hulls. Preserve per-face UVs, pivots and rotations.
Entity quads and vanilla 3D inventory elements share the same source and scale.
No ModelPart render path, so optimizers cannot substitute cached box UVs.
"""
import base64, json, math
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'src/main/resources/assets/space_fabric'
SOURCES={
    'classic':'space-rocket', 'dieselpunk':'dieselpunk-space-rocket',
    'biomechanical':'biomechanical-exploration-rocket',
    'heavy':'two-stage-heavy-lift-orbital-rocket',
    'venus_lander':'venus-atmosphere-lander', 'modern':'modern-space-rocket',
    'vertical_lander':'venus-vertical-landing-rocket', 'mars_lander':'mars-landing-rocket'}
ASSIGNMENTS={'base':'classic','mercury':'dieselpunk','venus':'venus_lander',
    'earth':'modern','moon':'classic','mars':'heavy','jupiter':'vertical_lander',
    'saturn':'mars_lander','uranus':'biomechanical','neptune':'biomechanical'}

def rotate(p,e):
    pivot=e.get('origin',[0,0,0]);v=[p[i]-pivot[i] for i in range(3)]
    for axis,angle in enumerate(e.get('rotation',[0,0,0])):
        if not angle:continue
        a,b=[(1,2),(2,0),(0,1)][axis];c,s=math.cos(math.radians(angle)),math.sin(math.radians(angle))
        v[a],v[b]=v[a]*c-v[b]*s,v[a]*s+v[b]*c
    return [v[i]+pivot[i] for i in range(3)]

def faces(a,b):
    x,y,z=a;xx,yy,zz=b
    # Native FaceInfo order, also used by vanilla's cuboid inventory baker.
    return {'west':[[x,yy,z],[x,y,z],[x,y,zz],[x,yy,zz]],
        'east':[[xx,yy,zz],[xx,y,zz],[xx,y,z],[xx,yy,z]],
        'up':[[x,yy,z],[x,yy,zz],[xx,yy,zz],[xx,yy,z]],
        'down':[[x,y,zz],[x,y,z],[xx,y,z],[xx,y,zz]],
        'north':[[xx,yy,z],[xx,y,z],[x,y,z],[x,yy,z]],
        'south':[[x,yy,zz],[x,y,zz],[xx,y,zz],[xx,yy,zz]]}

def normal(points):
    a=[points[1][i]-points[0][i] for i in range(3)];b=[points[2][i]-points[0][i] for i in range(3)]
    n=[a[1]*b[2]-a[2]*b[1],a[2]*b[0]-a[0]*b[2],a[0]*b[1]-a[1]*b[0]]
    length=math.sqrt(sum(x*x for x in n));return [x/length for x in n]

def write(path,data):
    path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(data,separators=(',',':'))+'\n')

def generate():
    metadata={}
    for variant,source in ASSIGNMENTS.items():
        doc=json.loads((ROOT/f'tools/import_sources/{SOURCES[source]}.bbmodel').read_text())
        assert set(doc['outliner'])=={e['uuid'] for e in doc['elements']},'Transformed groups require explicit support'
        assert len(doc['textures'])==1
        texture=doc['textures'][0];w,h=doc['resolution']['width'],doc['resolution']['height']
        png=base64.b64decode(texture['source'].split(',',1)[1],validate=True)
        for folder in ['entity','item']:
            target=ASSETS/f'textures/{folder}/rocket_fleet/{source}.png';target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(png)
        raw=[rotate(p,e) for e in doc['elements'] for face in faces(e['from'],e['to']).values() for p in face]
        lo=[min(p[i] for p in raw) for i in range(3)];hi=[max(p[i] for p in raw) for i in range(3)]
        cx,cz=(hi[0]+lo[0])/2,(hi[2]+lo[2])/2
        scale=min((3.284210526 if source=='classic' else 3.4)/(hi[1]-lo[1]),2.6/max(hi[0]-lo[0],hi[2]-lo[2]))
        def world(p):return [-(p[0]-cx)*scale,(p[1]-lo[1])*scale,-(p[2]-cz)*scale]
        # Fit wide landers in the inventory icon without distorting their proportions.
        icon_scale=16/max(hi[i]-lo[i] for i in range(3))
        def item(p):return [(p[0]-cx)*icon_scale+8,(p[1]-lo[1])*icon_scale,(p[2]-cz)*icon_scale+8]
        quads=[];elements=[];bounds=[]
        def emit(points,uv,color):
            bounds.extend(points);n=normal(points)
            if color>0x7FFFFFFF:color-=0x100000000
            tex=[[uv[0],uv[1]],[uv[0],uv[3]],[uv[2],uv[3]],[uv[2],uv[1]]]
            quads.append([*[round(v,7) for v in n],color,*[round(v,7) for p,t in zip(points,tex) for v in p+t]])
        for e in doc['elements']:
            assert e['type']=='cube' and sum(bool(a) for a in e.get('rotation',[0,0,0]))<=1
            element={'from':item(e['from']),'to':item(e['to']),'faces':{}}
            if any(e.get('rotation',[])):
                axis=next(i for i,a in enumerate(e['rotation']) if a)
                assert e['rotation'][axis] in [-45,-22.5,22.5,45]
                element['rotation']={'origin':item(e.get('origin',[0,0,0])),'axis':'xyz'[axis],'angle':e['rotation'][axis],'rescale':False}
            for side,points in faces(e['from'],e['to']).items():
                info=e['faces'][side];assert info['texture'] in [texture['uuid'],0,'0'] and not info.get('rotation',0)
                uv=info['uv'];emit([world(rotate(p,e)) for p in points],[uv[0]/w,uv[1]/h,uv[2]/w,uv[3]/h],-1)
                element['faces'][side]={'texture':'#0','uv':[uv[0]*16/w,uv[1]*16/h,uv[2]*16/w,uv[3]*16/h]}
            elements.append(element)
        width=2*max(max(abs(p[0]),abs(p[2])) for p in bounds)+.04;height=max(p[1] for p in bounds)+.02
        metadata[variant]={'source':source,'width':round(width,6),'height':round(height,6),'cubes':len(doc['elements']),'quads':len(quads),'scale':scale,'center':[cx,lo[1],cz]}
        write(ASSETS/f'rocket_mesh/{variant}.json',{'texture':f'space_fabric:textures/entity/rocket_fleet/{source}.png','quads':quads})
        textures={'0':f'space_fabric:item/rocket_fleet/{source}','particle':f'space_fabric:item/rocket_fleet/{source}'}
        path='rocket' if variant=='base' else variant+'_rocket'
        write(ASSETS/f'models/item/{path}.json',{'textures':textures,'elements':elements,'display':{
            'gui':{'rotation':[30,225,0],'scale':[1.1]*3},'ground':{'translation':[0,2,0],'scale':[.5]*3},
            'fixed':{'rotation':[0,180,0],'scale':[.8]*3},'thirdperson_righthand':{'rotation':[75,45,0],'translation':[0,2,0],'scale':[.5]*3},
            'firstperson_righthand':{'rotation':[0,135,0],'scale':[.6]*3}}})
    write(ROOT/'tools/rocket_fleet_manifest.json',metadata)
    java='''package dev.vic17usd.spacefabric.space;

/** Generated from rotated Blockbench bounds by tools/import_rocket_fleet.py. */
public final class RocketHullGeometry {
    private RocketHullGeometry() { }
    public static float width(RocketVariant variant){return switch(variant){
'''
    java+=''.join(f'        case {v.upper()} -> {m["width"]}F;\n' for v,m in metadata.items())+'    };}\n'
    java+='    public static float height(RocketVariant variant){return switch(variant){\n'
    java+=''.join(f'        case {v.upper()} -> {m["height"]}F;\n' for v,m in metadata.items())+'    };}\n}\n'
    (ROOT/'src/main/java/dev/vic17usd/spacefabric/space/RocketHullGeometry.java').write_text(java)
    print('Imported eight original hulls for ten rocket variants; no added geometry or tints.')

if __name__=='__main__':generate()
