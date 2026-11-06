#!/usr/bin/env python3
"""Convert the two attached sites to native 26.1 tiles; archives are read only as data.
Run with --bunker/--nest once, then --stored-sources for reproducible conversions.
"""
import argparse,gzip,hashlib,json
from pathlib import Path
from collections import deque
from zipfile import ZipFile
from convert_creativemode_structure import convert
from import_venus_structures import support_parts,neighbours
ROOT=Path(__file__).resolve().parents[1];DATA=ROOT/'src/main/resources/data/space_fabric';S=ROOT/'tools/structure_sources/venus/boss_sites'
MAPPING={
 'minecraft:water':'minecraft:lime_stained_glass', 'minecraft:dark_oak_planks':'minecraft:polished_blackstone_bricks',
 'minecraft:granite':'space_fabric:venus_basalt_substrate','minecraft:diorite':'space_fabric:venus_tessera_rock',
 'minecraft:sandstone':'space_fabric:venus_basalt_substrate','minecraft:smooth_sandstone':'space_fabric:venus_sulfur_crystal_block',
 'minecraft:prismarine':'space_fabric:venus_tessera_rock','minecraft:dark_prismarine':'minecraft:polished_blackstone',
 'minecraft:quartz_block':'space_fabric:venus_sulfur_crystal_block',
 'minecraft:iron_block':'minecraft:polished_basalt','minecraft:gold_block':'space_fabric:venus_sulfur_crystal_block',
 'minecraft:emerald_block':'space_fabric:venus_sulfur_crystal_block','minecraft:lapis_block':'minecraft:blue_terracotta',
 'minecraft:copper_block':'minecraft:waxed_weathered_cut_copper','minecraft:oxidized_copper':'minecraft:waxed_oxidized_cut_copper',
 'minecraft:redstone_block':'space_fabric:venus_ember_crust'}
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
def archive(jar,name,source):
 with ZipFile(jar) as z:
  meta=json.loads(z.read('fabric.mod.json'));assert meta['id']==source
  document=json.loads(z.read(f'data/{source}/structures/{name}.json'))
  S.mkdir(parents=True,exist_ok=True);(S/f'{name}.json.gz').write_bytes(gzip.compress(json.dumps(document,separators=(',',':')).encode(),mtime=0))
  for item in z.namelist():
   if item.startswith('LICENSE'):
    p=ROOT/f'third_party/venus_boss_sites/{name}/LICENSE.txt';p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(z.read(item))
  write(ROOT/f'third_party/venus_boss_sites/{name}/manifest.json',{'source':jar.name,'sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'source_id':source,'authors':meta['authors'],'license':meta['license'],'url':meta['contact']['sources']})
def remap(source):
 palette=[];index={}
 for i,old in enumerate(source['palette']):
  new=MAPPING.get(old,old)
  if new.endswith('_wool'):new=new.removesuffix('_wool')+'_terracotta'
  if new not in palette:palette.append(new)
  index[i]=palette.index(new)
 lo=source['bounds']['min'];blocks={(x-lo[0],y-lo[1],z-lo[2]):index[s] for x,y,z,s in source['blocks']}
 return palette,blocks

def bunker(source):
 palette,blocks=remap(source);w,h,d=source['bounds']['dimensions'];original=len(blocks)
 for b in ('minecraft:air','space_fabric:venus_basalt_substrate','minecraft:chest'):
  if b not in palette:palette.append(b)
 air=palette.index('minecraft:air');rock=palette.index('space_fabric:venus_basalt_substrate')
 # Native templates must contain explicit air underground. The editor omits it.
 # Keep native rock outside the main room; clear the original hall above its floor.
 floor={(x,z) for x,y,z in blocks if y==4}
 enclosed=set()
 for y in range(h):
  empty={(x,z) for x in range(w) for z in range(d) if (x,y,z) not in blocks}
  queue=deque(p for p in empty if p[0] in (0,w-1) or p[1] in (0,d-1))
  outside=set(queue)
  while queue:
   x,z=queue.popleft()
   for q in ((x-1,z),(x+1,z),(x,z-1),(x,z+1)):
    if q in empty and q not in outside:outside.add(q);queue.append(q)
  enclosed.update((x,y,z) for x,z in empty-outside)
 for x in range(w):
  for z in range(d):
   for y in range(h):
    p=(x,y,z)
    if p not in blocks:blocks[p]=air if p in enclosed or ((x,z) in floor and 5<=y<=27) else rock
 # Clear a generous central fighting area, including the Colossus's full collision box.
 for x in range(36,81):
  for z in range(37,82):
   blocks[(x,4,z)]=palette.index('minecraft:smooth_stone')
   for y in range(5,27):blocks[(x,y,z)]=air
 # Finite supplies in a side alcove; never give the boss core before the fight.
 chest=(31,5,59);blocks[chest]=palette.index('minecraft:chest')
 for y in (6,7):blocks[(31,y,59)]=air
 markers={chest:{'id':'minecraft:chest','LootTable':'space_fabric:chests/venus_colossus_bunker'}}
 return palette,blocks,[w,h,d],markers,[{'id':'space_fabric:sulfur_colossus','position':[58,5,59]}],{'original_blocks':original,'explicit_blocks':len(blocks),'arena_center':[58,5,59],'arena_clear_size':[45,22,45]}

def nest(source):
 palette,blocks=remap(source);original=len(blocks);post='minecraft:polished_blackstone'
 if post not in palette:palette.append(post)
 # Discard only detached sparks (1..8 blocks); retain substantial organic limbs.
 remaining=set(blocks);removed=0
 while remaining:
  start=min(remaining);remaining.remove(start);queue=[start]
  for p in queue:
   for q in neighbours(p):
    if q in remaining:remaining.remove(q);queue.append(q)
  if len(queue)<=8 and min(p[1] for p in queue)>1:
   for p in queue:del blocks[p]
   removed+=len(queue)
 added,longest=support_parts(blocks,palette.index(post))
 # Low roots meet the prepared natural floor. Never build a tall display plinth.
 columns={}
 for x,y,z in blocks:columns[(x,z)]=min(columns.get((x,z),y),y)
 roots=0
 for (x,z),y in columns.items():
  if 0<y<=8:
   for yy in range(y):blocks[(x,yy,z)]=palette.index(post);roots+=1
 dims=source['bounds']['dimensions']
 return palette,blocks,dims,{},[],{'original_blocks':original,'removed_detached_sparks':removed,'added_ties':added,'max_tie_height':longest,'root_blocks':roots,'blocks':len(blocks)}

def output(name,result,underground):
 palette,blocks,dims,markers,entities,stats=result;w,h,d=dims;tiles=[]
 groups={}
 for (x,y,z),s in sorted(blocks.items()):
  tx=x//16*16;tz=z//16*16;groups.setdefault((tx,tz),[]).append([x-tx,y,z-tz,s])
 for (tx,tz),values in sorted(groups.items()):
  tileid=f'{name}/tile_{tx}_{tz}';size=[min(16,w-tx),h,min(16,d-tz)]
  doc={'format':'creativemode.structure','version':1,'palette':palette,'bounds':{'min':[0,0,0],'max':[v-1 for v in size],'dimensions':size},'anchor':[0,0,0],'block_count':len(values),'blocks':values}
  residents=[{'id':e['id'],'position':[e['position'][0]-tx,e['position'][1],e['position'][2]-tz]} for e in entities if tx<=e['position'][0]<tx+16 and tz<=e['position'][2]<tz+16]
  be={(x-tx,y,z-tz):v for (x,y,z),v in markers.items() if tx<=x<tx+16 and tz<=z<tz+16}
  path=DATA/f'structure/{tileid}.nbt';path.parent.mkdir(parents=True,exist_ok=True);path.write_bytes(gzip.compress(convert(doc,residents,block_entities=be),mtime=0))
  tiles.append({'template':'space_fabric:'+tileid,'x':tx,'z':tz})
 structure={'type':'space_fabric:venus_bunker' if underground else 'space_fabric:venus_surface','biomes':'#space_fabric:has_structure/'+name,
    'spawn_overrides':{},'step':'underground_structures' if underground else 'surface_structures','terrain_adaptation':'none','width':w,'height':h,'depth':d,'tiles':tiles}
 if not underground:structure.update(padding=8,max_terrain_relief=16)
 write(DATA/f'worldgen/structure/{name}.json',structure)
 spacing,separation,salt=(256,128,93114657) if underground else (120,64,93114658)
 write(DATA/f'worldgen/structure_set/{name}.json',{'structures':[{'structure':'space_fabric:'+name,'weight':1}],'placement':{'type':'minecraft:random_spread','spacing':spacing,'separation':separation,'salt':salt}})
 biomes=['venus','venus_fumaroles','venus_obsidian_basins','venus_obsidian_riftlands','venus_sulfur_shardlands','venus_basalt_ashlands']
 write(DATA/f'tags/worldgen/biome/has_structure/{name}.json',{'replace':False,'values':['space_fabric:'+b for b in biomes]})
 alias='bunker_del_coloso' if underground else 'nido_de_azufre'
 write(DATA/f'tags/worldgen/structure/{alias}.json',{'replace':False,'values':['space_fabric:'+name]})
 write(ROOT/f'third_party/venus_boss_sites/{name}_conversion.json',stats|{'dimensions':dims,'tiles':len(tiles),'residents':entities})
 print(name,stats,'tiles',len(tiles))

def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--bunker',type=Path);parser.add_argument('--nest',type=Path);parser.add_argument('--stored-sources',action='store_true');a=parser.parse_args()
 if a.bunker:archive(a.bunker,'basalt_lifebunker','basalt-lifebunker-f2yaojrq')
 if a.nest:archive(a.nest,'sulfurfire_broodlands','sulfurfire-broodlands-wrlm5742')
 output('venus_colossus_bunker',bunker(json.loads(gzip.decompress((S/'basalt_lifebunker.json.gz').read_bytes()))),True)
 output('venus_sulfurfire_nest',nest(json.loads(gzip.decompress((S/'sulfurfire_broodlands.json.gz').read_bytes()))),False)
 credits=ROOT/'src/main/resources/assets/space_fabric/licenses/venus_boss_sites/SOURCES.txt'
 credits.parent.mkdir(parents=True,exist_ok=True);credits.write_text('Original designs: holasoynuevo using CreativeMode.net\nhttps://creativemode.net/mod/basalt-lifebunker-f2yaojrq\nhttps://creativemode.net/mod/sulfurfire-broodlands-wrlm5742\nAdapted as the Colossus Bunker and Sulfurfire Nest in Space Fabric.\n')
 for p in (ROOT/'third_party/venus_boss_sites').rglob('LICENSE.txt'):
  out=ROOT/'src/main/resources/assets/space_fabric/licenses/venus_boss_sites'/p.relative_to(ROOT/'third_party/venus_boss_sites');out.parent.mkdir(parents=True,exist_ok=True);out.write_bytes(p.read_bytes())
if __name__=='__main__':main()
