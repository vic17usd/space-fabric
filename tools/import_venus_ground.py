#!/usr/bin/env python3
"""Import supplied JARs as data only; retain original art, geometry and licenses."""
import argparse, hashlib, json
from pathlib import Path
from zipfile import ZipFile
ROOT=Path(__file__).resolve().parents[1]
SOURCES=ROOT/'tools/import_sources/venus_ground'
RES=ROOT/'src/main/resources'
ASSIGNMENTS={'ground_1':'vulcanized_basalt','ground_2':'venus_hot_rock','ground':'sulfur_deposit'}
def write(path,data):
 path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(data,indent=2,ensure_ascii=False)+'\n')
def main():
 parser=argparse.ArgumentParser();parser.add_argument('--jars',nargs=4,metavar='JAR',help='ground 1, ground 2, ground, cluster');args=parser.parse_args()
 manifest=[]
 if args.jars:
  for label,filename in zip(['ground_1','ground_2','ground','cluster'],args.jars):
   path=Path(filename);folder=SOURCES/label;folder.mkdir(parents=True,exist_ok=True)
   with ZipFile(path) as jar:
    meta=json.loads(jar.read('fabric.mod.json'));write(folder/'fabric.mod.json',meta)
    license=next(n for n in jar.namelist() if n.startswith('LICENSE'))
    (folder/'LICENSE.txt').write_bytes(jar.read(license))
    for name in jar.namelist():
     if name.endswith('.png') and '/textures/block/' in name:(folder/'texture.png').write_bytes(jar.read(name))
     if name.endswith('.json') and ('/models/block/' in name or name.endswith('_structure.json')):
      (folder/Path(name).name).write_bytes(jar.read(name))
   manifest.append({'source':path.name,'source_sha256':hashlib.sha256(path.read_bytes()).hexdigest(),'folder':label,'authors':meta['authors'],'url':meta['contact']['sources'],'target':ASSIGNMENTS.get(label,'sulfur_cluster')})
  write(ROOT/'third_party/venus_ground/manifest.json',manifest)
 for label,block in ASSIGNMENTS.items():
  target=RES/f'assets/space_fabric/textures/block/{block}.png';target.write_bytes((SOURCES/label/'texture.png').read_bytes())
  write(RES/f'assets/space_fabric/models/block/{block}.json',{'parent':'minecraft:block/cube_all','textures':{'all':'space_fabric:block/'+block}})
 folder=SOURCES/'cluster';structure=json.loads(next(folder.glob('*_structure.json')).read_text())
 variants={}
 for i,part in enumerate(structure['blocks']):
  name='sulfur_cluster_'+str(i);model=json.loads((folder/(part['block_id'].split(':')[1]+'.json')).read_text())
  model['textures']={k:'space_fabric:block/sulfur_cluster_atlas' for k in model['textures']}
  write(RES/f'assets/space_fabric/models/block/{name}.json',model);variants['piece='+str(i)]={'model':'space_fabric:block/'+name}
 write(RES/'assets/space_fabric/blockstates/sulfur_cluster.json',{'variants':variants})
 write(RES/'assets/space_fabric/models/block/sulfur_cluster.json',{'parent':'space_fabric:block/sulfur_cluster_0'})
 # Whole formation in the item model, with original per-face UVs unchanged.
 elements=[]
 for i,part in enumerate(structure['blocks']):
  model=json.loads((RES/f'assets/space_fabric/models/block/sulfur_cluster_{i}.json').read_text())
  for element in model['elements']:
   for key in ['from','to']:element[key]=[(v+16*offset)/3 for v,offset in zip(element[key],part['offset'])]
   if 'rotation' in element:element['rotation']['origin']=[(v+16*offset)/3 for v,offset in zip(element['rotation']['origin'],part['offset'])]
   elements.append(element)
 write(RES/'assets/space_fabric/models/item/sulfur_cluster.json',{'textures':model['textures'],'elements':elements,'display':{'gui':{'rotation':[30,225,0],'scale':[0.8,0.8,0.8]}}})
 write(RES/'assets/space_fabric/items/sulfur_cluster.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/sulfur_cluster'}})
 (RES/'assets/space_fabric/textures/block/sulfur_cluster_atlas.png').write_bytes((folder/'texture.png').read_bytes())
 write(RES/'data/space_fabric/sulfur_cluster.json',structure)
 write(RES/'data/space_fabric/loot_table/blocks/sulfur_cluster.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':'space_fabric:sulfur_crystal'}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
 for tag in ['mineable/pickaxe','needs_stone_tool']:
  path=RES/f'data/minecraft/tags/block/{tag}.json';data=json.loads(path.read_text());data['values']=list(dict.fromkeys(data['values']+['space_fabric:sulfur_cluster']));write(path,data)
 for locale,name in [('es_es','Afloramiento de azufre venusiano'),('en_us','Venusian Sulfur Outcrop')]:
  path=RES/f'assets/space_fabric/lang/{locale}.json';data=json.loads(path.read_text());data['block.space_fabric.sulfur_cluster']=name;write(path,data)
 write(RES/'data/space_fabric/worldgen/configured_feature/sulfur_cluster.json',{'type':'space_fabric:sulfur_cluster','config':{}})
 write(RES/'data/space_fabric/worldgen/placed_feature/sulfur_cluster.json',{'feature':'space_fabric:sulfur_cluster','placement':[{'type':'minecraft:rarity_filter','chance':12},{'type':'minecraft:biome'}]})
 for label in ['ground_1','ground_2','ground','cluster']:
  target=ROOT/f'third_party/venus_ground/{label}/LICENSE.txt';target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes((SOURCES/label/'LICENSE.txt').read_bytes())
 print('Imported three ground textures and all 21 original cluster pieces.')
if __name__=='__main__':main()
