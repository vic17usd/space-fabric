#!/usr/bin/env python3
"""Adapt user landscapes as native Fabric data; never run source-mod code."""
import argparse,gzip,hashlib,json
from pathlib import Path
from zipfile import ZipFile
from collections import defaultdict
from convert_creativemode_structure import convert
ROOT=Path(__file__).resolve().parents[1];RES=ROOT/'src/main/resources';A=RES/'assets/space_fabric';D=RES/'data/space_fabric';S=ROOT/'tools/import_sources/venus_landscapes'
BLOCKS={'vents':{'vent_basalt':'venus_vent_basalt','sulfur_crust':'venus_sulfur_crust','ember_crust':'venus_ember_crust','acid_pocket':'venus_acid_pocket'},'lakes':{'basalt_substrate':'venus_basalt_substrate','cracked_volcanic_stone':'venus_cracked_stone','obsidian_slag':'venus_obsidian_slag','sulfur_crystal':'venus_sulfur_crystal_block','acid_pool':'venus_acid_pool'}}
def write(p,d):
 p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,indent=2,ensure_ascii=False)+'\n')
def main():
 parser=argparse.ArgumentParser();parser.add_argument('--jars',nargs=3,help='volcano, acid vents, acid lakes');args=parser.parse_args()
 if args.jars:
  manifest=[]
  for label,filename in zip(['volcano','vents','lakes'],args.jars):
   p=Path(filename);folder=S/label;folder.mkdir(parents=True,exist_ok=True)
   with ZipFile(p) as z:
    meta=json.loads(z.read('fabric.mod.json'));write(folder/'fabric.mod.json',meta)
    (folder/'LICENSE.txt').write_bytes(z.read(next(n for n in z.namelist() if n.startswith('LICENSE'))))
    for n in z.namelist():
     if n.endswith('.json') and ('/worldgen/' in n or '/models/block/' in n or n.endswith('/sulfur_crown_caldera.json')) or n.endswith('.png') and '/textures/block/' in n:
      target=folder/n;target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(z.read(n))
   manifest.append({'source':p.name,'source_sha256':hashlib.sha256(p.read_bytes()).hexdigest(),'folder':label,'authors':meta['authors'],'url':meta['contact']['sources']})
  write(ROOT/'third_party/venus_landscapes/manifest.json',manifest)
 for label,mapping in BLOCKS.items():
  folder=S/label;meta=json.loads((folder/'fabric.mod.json').read_text());ns=meta['id']
  for old,new in mapping.items():
   texture=(folder/f'assets/{ns}/textures/block/{old}.png').read_bytes();target=A/f'textures/block/{new}.png';target.write_bytes(texture)
   model=json.loads((folder/f'assets/{ns}/models/block/{old}.json').read_text());model['textures']={k:'space_fabric:block/'+new for k in model['textures']}
   write(A/f'models/block/{new}.json',model);write(A/f'blockstates/{new}.json',{'variants':{'':{'model':'space_fabric:block/'+new}}})
   write(A/f'items/{new}.json',{'model':{'type':'minecraft:model','model':'space_fabric:block/'+new}})
   drop='space_fabric:sulfur_crystal' if 'sulfur' in new else 'space_fabric:acid_condensate' if 'acid' in new else 'space_fabric:'+new
   write(D/f'loot_table/blocks/{new}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':drop}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
 # Preserve the silhouette; replace fabric, metal treasure and emissive decoration with local geology.
 source=json.loads(next((S/'volcano').rglob('sulfur_crown_caldera.json')).read_text())
 remap={'minecraft:cracked_stone_bricks':'space_fabric:venus_cracked_stone','minecraft:deepslate_bricks':'space_fabric:venus_basalt_substrate','minecraft:black_concrete':'space_fabric:venus_obsidian_slag','minecraft:obsidian':'space_fabric:venus_obsidian_slag','minecraft:crying_obsidian':'space_fabric:venus_obsidian_slag','minecraft:lime_wool':'space_fabric:condensed_sulfuric_acid','minecraft:green_concrete':'space_fabric:condensed_sulfuric_acid','minecraft:emerald_block':'space_fabric:venus_sulfur_crystal_block','minecraft:gold_block':'space_fabric:venus_sulfur_crust','minecraft:yellow_wool':'space_fabric:venus_sulfur_crust','minecraft:yellow_concrete':'space_fabric:venus_sulfur_crust','minecraft:glowstone':'space_fabric:venus_sulfur_crystal_block','minecraft:sea_lantern':'space_fabric:venus_ember_crust','minecraft:glass':'space_fabric:venus_sulfur_crystal_block','minecraft:oxidized_copper':'space_fabric:venus_vent_basalt','minecraft:copper_block':'space_fabric:venus_vent_basalt','minecraft:redstone_block':'space_fabric:venus_ember_crust','minecraft:orange_wool':'space_fabric:venus_ember_crust','minecraft:netherrack':'space_fabric:venus_cracked_stone','minecraft:smooth_sandstone':'space_fabric:venus_sulfur_crust'}
 blocks={(x,y,z):remap[source['palette'][i]] for x,y,z,i in source['blocks']};cols=defaultdict(list)
 for x,y,z in blocks:cols[x,z].append(y)
 # Solidify beneath the imported skin so arches/towers cannot hover over an empty volume.
 for (x,z),ys in cols.items():
  for y in range(min(ys)):blocks[x,y,z]='space_fabric:vulcanized_basalt'
  blocks[x,0,z]='space_fabric:vulcanized_basalt'
 # A small sealed molten basin in the existing low central hollow; few fluid sources, no giant waterfall.
 for x in range(55,66):
  for z in range(56,67):
   if (x-60)**2+(z-61)**2<=25:
    cols.setdefault((x,z),[])
    for y in range(18):blocks[x,y,z]='space_fabric:vulcanized_basalt'
    blocks[x,18,z]='space_fabric:viscous_lava'
    for y in range(19,35):blocks.pop((x,y,z),None)
    for dx,dz in [(1,0),(-1,0),(0,1),(0,-1)]:
     if (x+dx-60)**2+(z+dz-61)**2>25:
      cols.setdefault((x+dx,z+dz),[])
      for y in range(19):blocks[x+dx,y,z+dz]='space_fabric:vulcanized_basalt'
 palette=['minecraft:air']+sorted(set(blocks.values()));idx={v:i for i,v in enumerate(palette)};tiles=defaultdict(list)
 # Sparse templates retain only the geology. The natural piece clears its footprint at placement time.
 for (x,y,z),material in sorted(blocks.items()):
  tiles[x//16*16,z//16*16].append([x%16,y,z%16,idx[material]])
 entries=[]
 for (x,z),data in sorted(tiles.items()):
  size=[min(16,120-x),94,min(16,123-z)];doc={'format':'creativemode.structure','version':1,'palette':palette,'blocks':data,'bounds':{'min':[0,0,0],'max':[v-1 for v in size],'dimensions':size},'anchor':[0,0,0],'block_count':len(data)}
  path=f'venus/caldera_v1/tile_{x}_{z}';target=D/f'structure/{path}.nbt';target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(gzip.compress(convert(doc),mtime=0));entries.append({'template':'space_fabric:'+path,'x':x,'z':z})
 write(D/'worldgen/structure/venus_sulfur_caldera.json',{'type':'space_fabric:venus_surface','biomes':'#space_fabric:venus_biomes','step':'surface_structures','spawn_overrides':{},'terrain_adaptation':'none','width':120,'height':94,'depth':123,'padding':0,'max_terrain_relief':8,'natural_terrain':True,'tiles':entries})
 write(D/'worldgen/structure_set/venus_calderas.json',{'structures':[{'structure':'space_fabric:venus_sulfur_caldera','weight':1}],'placement':{'type':'minecraft:random_spread','spacing':96,'separation':64,'salt':80321947,'exclusion_zone':{'other_set':'space_fabric:venus_settlements','chunk_count':16}}})
 write(D/'tags/worldgen/structure/caldera_venusiana.json',{'replace':False,'values':['space_fabric:venus_sulfur_caldera']})
 write(ROOT/'third_party/venus_landscapes/caldera.json',{'source_blocks':source['block_count'],'size':[120,94,123],'footprint_columns':len(cols),'solid_blocks':len(blocks),'tiles':len(entries),'explicit_cells':sum(len(v) for v in tiles.values()),'palette_remap':remap})
 for label in ['volcano','vents','lakes']:
  path=ROOT/f'third_party/venus_landscapes/{label}/LICENSE.txt';path.parent.mkdir(parents=True,exist_ok=True);path.write_bytes((S/label/'LICENSE.txt').read_bytes())
 print('Imported nine geology designs and natural caldera:',len(entries),'tiles,',len(blocks),'solid/fluid blocks.')
if __name__=='__main__':main()
