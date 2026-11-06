#!/usr/bin/env python3
"""Import the user's console and rover, preserving both embedded PNG atlases exactly.

The solid rover hull becomes a hollow cabin with a visor; roof batteries move up
with the roof. All original six wheel assemblies retain their shapes and UVs.
"""
import base64, copy, hashlib, json
from pathlib import Path
from import_rocket_fleet import faces, rotate, normal
ROOT=Path(__file__).resolve().parents[1]; A=ROOT/'src/main/resources/assets/space_fabric'
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,separators=(',',':'))+'\n')
def source(name):
 p=ROOT/f'tools/import_sources/{name}.bbmodel';doc=json.loads(p.read_text())
 assert len(doc['textures'])==1 and set(doc['outliner'])=={e['uuid'] for e in doc['elements']}
 png=base64.b64decode(doc['textures'][0]['source'].split(',',1)[1],validate=True)
 return doc,png,{'source_sha256':hashlib.sha256(p.read_bytes()).hexdigest(),'texture_sha256':hashlib.sha256(png).hexdigest(),'original_cubes':len(doc['elements'])}
def atlas(png,name,folders):
 for folder in folders:
  p=A/f'textures/{folder}/{name}.png';p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(png)
def element(e,convert):
 result={'from':convert(e['from']),'to':convert(e['to']),'faces':{}}
 for side,info in e['faces'].items():
  result['faces'][side]={'uv':[x/32 for x in info['uv']],'texture':'#0'}
  if info.get('rotation'):result['faces'][side]['rotation']=info['rotation']
 for axis,angle in enumerate(e.get('rotation',[0,0,0])):
  if angle:result['rotation']={'origin':convert(e['origin']),'axis':'xyz'[axis],'angle':angle,'rescale':False}
 return result
def main():
 doc,png,console=source('futuristic-control-console');atlas(png,'foundry_console',['block','item'])
 transform=lambda p:[p[0]*.5+8,p[1]*.5,p[2]*.5+8]
 model={'textures':{'0':'space_fabric:block/foundry_console','particle':'space_fabric:block/foundry_console'},'elements':[element(e,transform) for e in doc['elements']],
        'display':{'gui':{'rotation':[25,225,0],'scale':[.9]*3},'firstperson_righthand':{'rotation':[0,135,0],'scale':[.65]*3}}}
 write(A/'models/block/foundry_console.json',model)
 write(A/'items/foundry_console.json',{'model':{'type':'minecraft:model','model':'space_fabric:block/foundry_console'}})
 write(A/'blockstates/foundry_console.json',{'variants':{'facing='+f:{'model':'space_fabric:block/foundry_console','y':r} for f,r in [('north',0),('east',90),('south',180),('west',270)]}})
 doc,png,rover=source('venusian-pressurized-land-vehicle');atlas(png,'venus_rover',['entity','item'])
 elements=[]
 for e in doc['elements']:
  if e['name']=='pressure-hull':
   panels=[('floor',[-12.5,8,-16],[12.5,9,16]),('left',[-12.5,9,-16],[-11.5,22.5,5.5]),
           ('right',[11.5,9,-16],[12.5,22.5,5.5]),('rear',[-11.5,9,4.5],[11.5,22.5,5.5]),
           ('roof',[-12.5,21.5,-16],[12.5,22.5,5.5]),('front_lower',[-11.5,9,-16],[11.5,13,-15]),
           ('visor',[-11.5,13,-16],[11.5,21.5,-15.8]),('seat',[-4,9,-9],[4,10,-3]),
           ('dashboard',[-9,11,-15],[9,12.5,-13.5])]
   for name,lo,hi in panels:
    new=copy.deepcopy(e);new.update(name=name,**{'from':lo,'to':hi});elements.append(new)
  else:
   new=copy.deepcopy(e)
   if e['name'] in ['battery-mounting-pad','top-battery-casing']:
    new['from'][1]+=4.5;new['to'][1]+=4.5
   elements.append(new)
 parts={'body':[],'glass':[]}
 for side in ['left','right']:
  for axle in ['front','middle','rear']:parts[side+'_'+axle]=[]
 for e in elements:
  group='glass' if e['name']=='visor' else 'body'
  for side in ['left','right']:
   for axle in ['front','middle','rear']:
    if e['name'].startswith(side+'-wheel-'+axle) or e['name']==side+'-wheel-hub-'+axle:group=side+'_'+axle
  parts[group].append(e)
 world=lambda p:[-p[0]/10,p[1]/10,-p[2]/10]
 pivots={};counts={}
 for group,es in parts.items():
  pivot=[0,0,0]
  if group not in ['body','glass']:
   points=[world(p) for e in es for ps in faces(e['from'],e['to']).values() for p in ps]
   pivot=[(min(p[i] for p in points)+max(p[i] for p in points))/2 for i in range(3)]
  pivots[group]=pivot;quads=[]
  for e in es:
   for side,points in faces(e['from'],e['to']).items():
    info=e['faces'][side];uv=[v/512 for v in info['uv']]
    pts=[[v-pivot[i] for i,v in enumerate(world(rotate(p,e)))] for p in points]
    tex=[[uv[0],uv[1]],[uv[0],uv[3]],[uv[2],uv[3]],[uv[2],uv[1]]]
    color=0x55FFFFFF if group=='glass' else -1
    quads.append([*normal(pts),color,*[v for p,t in zip(pts,tex) for v in p+t]])
    if group=='body':
     # The cabin has real inner walls, rather than disappearing when viewed from within.
     ps=list(reversed(pts));ts=list(reversed(tex));quads.append([*normal(ps),color,*[v for p,t in zip(ps,ts) for v in p+t]])
  counts[group]=len(quads)
  write(A/f'model_mesh/venus_rover_{group}.json',{'texture':'space_fabric:textures/entity/venus_rover.png','pivot':pivot,'quads':quads})
 icon=lambda p:[p[0]*.36+8,p[1]*.36+1,p[2]*.36+8]
 write(A/'models/item/venus_rover.json',{'textures':{'0':'space_fabric:item/venus_rover','particle':'space_fabric:item/venus_rover'},
       'elements':[element(e,icon) for e in elements],'display':{'gui':{'rotation':[25,225,0],'scale':[1.05]*3},'firstperson_righthand':{'rotation':[0,135,0],'scale':[.7]*3}}})
 write(A/'items/venus_rover.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/venus_rover'}})
 rover.update({'parts':counts,'pivots':pivots,'width':4.04,'height':2.92,'camera':[0,1.97,1.15],
               'adaptation':'Hollow pressure hull, glazed visor, inner seat/dashboard, roof and batteries raised 4.5 source units.'})
 write(ROOT/'tools/venus_expedition_models.json',{'console':console,'rover':rover})
 print('Imported original console and six-wheel rover atlases, native console/item models and eight animated rover mesh parts.')
if __name__=='__main__':main()
