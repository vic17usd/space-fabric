#!/usr/bin/env python3
"""Import selected attached builds as data, retaining no executable source-mod code.

Usage: import_venus_ruins.py /path/to/attachments, or --stored-sources.
"""
import argparse, gzip, hashlib, json
from pathlib import Path
from zipfile import ZipFile
from convert_creativemode_structure import convert
from import_venus_structures import REMAP, support_parts, neighbours

ROOT=Path(__file__).resolve().parents[1]
D=ROOT/'src/main/resources/data/space_fabric'
SELECTION={
 'venusian_ash_outpost':('Venus estructura.jar',17,'Puesto de ceniza de Venus','puesto_ceniza_venusiano',['venus','venus_obsidian_basins']),
 'venusian_sulfur_vault':('Venus estructura 3.jar',2,'Bóveda de azufre de Venus','boveda_venusiana',['venus_fumaroles']),
 'venus_basalt_haven':('Venus estructura 4.jar',18,'Refugio de basalto de Venus','refugio_venusiano',['venus','venus_obsidian_basins']),
 'rustfall_foundry':('Venus instalaciones averiadas.jar',2,'Fundición abandonada de Venus','fundicion_venusiana',['venus','venus_fumaroles']),
}
MAPPING=REMAP|{
 'minecraft:netherrack':'space_fabric:venus_cracked_stone',
 'minecraft:iron_block':'minecraft:polished_basalt',
 'minecraft:copper_block':'minecraft:waxed_weathered_cut_copper',
 'minecraft:oxidized_copper':'minecraft:waxed_oxidized_cut_copper',
 'minecraft:redstone_block':'space_fabric:venus_ember_crust',
 'minecraft:gold_block':'space_fabric:venus_sulfur_crystal_block',
 'minecraft:emerald_block':'space_fabric:venus_sulfur_crystal_block',
 'minecraft:diamond_block':'space_fabric:venus_sulfur_crystal_block',
 'minecraft:lapis_block':'minecraft:blue_terracotta',
 'minecraft:ancient_debris':'space_fabric:venus_basalt_substrate',
 'minecraft:moss_block':'space_fabric:venus_basalt_substrate',
 'minecraft:water':'minecraft:lime_stained_glass',
}
def write(p,d):
 p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
def prepare(source,cut,broken=False):
 # Remove editor plinths as a rigid vertical cut, never move individual building floors.
 palette=[];indices={}
 for i,old in enumerate(source['palette']):
  new=MAPPING.get(old,old)
  if new.endswith('_wool'):new=new.removesuffix('_wool')+'_terracotta'
  if new not in palette:palette.append(new)
  indices[i]=palette.index(new)
 lo=source['bounds']['min']
 blocks={(x-lo[0],y-lo[1]-cut,z-lo[2]):indices[s] for x,y,z,s in source['blocks'] if y-lo[1]>=cut}
 retained=len(blocks);removed=0
 post='minecraft:polished_blackstone_bricks'
 if post not in palette:palette.append(post)
 try:added,longest=support_parts(blocks,palette.index(post))
 except ValueError:
  if not broken:raise
  # First retain every fragment that can be tied to the building. Only remove
  # the tiny unsupported debris left over after that attempt.
  remaining=set(blocks)
  while remaining:
   start=min(remaining);remaining.remove(start);queue=[start]
   for p in queue:
    for q in neighbours(p):
     if q in remaining:remaining.remove(q);queue.append(q)
   if len(queue)<=4 and min(p[1] for p in queue)>1:
    for p in queue:del blocks[p]
    removed+=len(queue)
  assert removed>0
  support_parts(blocks,palette.index(post))
  added=len(blocks)-retained+removed;longest=9
 dims=[max(p[i] for p in blocks)+1 for i in range(3)]
 assert dims[1]<=128
 tiles={}
 for (x,y,z),s in sorted(blocks.items()):
  key=(x//16*16,z//16*16);tiles.setdefault(key,[]).append([x-key[0],y,z-key[1],s])
 return palette,dims,tiles,{'removed_plinth_layers':cut,'removed_base_blocks':source['block_count']-retained,'removed_detached_debris':removed,'added_support_blocks':added,'max_support_height':longest,'blocks':len(blocks)}

def recovery_bay(palette,dims,tiles):
 blocks={(x+tx,y,z+tz):s for (tx,tz),values in tiles.items() for x,y,z,s in values}
 candidates=[]
 for x in range(3,dims[0]-14):
  for z in range(3,dims[2]-6):
   if all((xx,0,zz) in blocks and all((xx,y,zz) not in blocks for y in range(1,4)) for xx in range(x,x+12) for zz in range(z,z+5)):
    candidates.append((abs(x+6-dims[0]//2)+abs(z+2-dims[2]//2),x,z))
 if not candidates:raise ValueError('Foundry needs a clear, accessible commissioning bay')
 _,x,z=min(candidates);anchor=(x,1,z+2);markers={}
 for i,name in enumerate(['foundry_console','geothermal_generator','venus_cooling_unit','venus_sulfur_processor']):
  pos=(x+i*3,1,z+2);block='space_fabric:'+name
  if block not in palette:palette.append(block)
  blocks[pos]=palette.index(block)
  markers[pos]={'id':'space_fabric:foundry_console','NaturalSite':True} if i==0 else {
       'id':'space_fabric:colony_machine','Maintenance':{'damage':10000,'coating':0,'filter':0,'installed':False,'phase':0},'CompletedBatches':0}
 if 'minecraft:lava' not in palette:palette.append('minecraft:lava')
 blocks[(x+3,0,z+2)]=palette.index('minecraft:lava')
 result={}
 for (xx,y,zz),s in sorted(blocks.items()):
  key=(xx//16*16,zz//16*16);result.setdefault(key,[]).append([xx-key[0],y,zz-key[1],s])
 return result,markers,anchor
def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('attachments',nargs='?',type=Path);parser.add_argument('--stored-sources',action='store_true');args=parser.parse_args()
 if not args.stored_sources and args.attachments is None:parser.error('Provide attachments or --stored-sources')
 manifest={};audit=[]
 old=ROOT/'third_party/venus_ruins/manifest.json'
 previous=json.loads(old.read_text()) if old.exists() else {}
 if args.attachments:
  # Audit every submitted design; duplicates are recognized by the exact source ID/hash.
  filenames={v[0] for v in SELECTION.values()}|{'Venus acido.jar','Venus lagos.jar','Venus build 2.jar','Venus build.jar','Venus estructura 2.jar','Venus estructura 5.jar','Venus suelo 1.jar','venus suelo 2.jar','Venus Volcan.jar','venus suelo 4.jar'}
  for name in sorted(filenames):
   jar=next(args.attachments.rglob(name))
   with ZipFile(jar) as archive:
    meta=json.loads(archive.read('fabric.mod.json'))
    audit.append({'jar':name,'source_id':meta['id'],'sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'selected':name in {v[0] for v in SELECTION.values()}})
  write(ROOT/'third_party/venus_ruins/submitted_designs.json',audit)
 for name,(filename,cut,title,alias,biomes) in SELECTION.items():
  original=ROOT/f'tools/structure_sources/venus/ruins/{name}.json'
  if args.stored_sources:source=json.loads(original.read_text());metadata=previous[name]
  else:
   jar=next(args.attachments.rglob(filename))
   with ZipFile(jar) as archive:
    member=next(n for n in archive.namelist() if n.endswith('/'+name+'.json') and '/structures/' in n)
    raw=archive.read(member);source=json.loads(raw);original.parent.mkdir(parents=True,exist_ok=True);original.write_bytes(raw)
    meta=json.loads(archive.read('fabric.mod.json'));license=next(n for n in archive.namelist() if n.startswith('LICENSE'))
    target=ROOT/f'third_party/venus_ruins/{meta["id"]}/LICENSE.txt';target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(archive.read(license))
    metadata={'jar':filename,'sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'source_page':meta.get('contact',{}).get('sources'),'authors':meta.get('authors',[]),'source_member':member}
  palette,dims,tiles,adaptation=prepare(source,cut,name=='rustfall_foundry');entries=[];markers={};revision='ruins_v1'
  if name=='rustfall_foundry':
   tiles,markers,anchor=recovery_bay(palette,dims,tiles);revision='ruins_v2'
   adaptation.update({'recovery_anchor':anchor,'recovery_machines':3,'blocks':adaptation['blocks']+4})
  for (x,z),blocks in sorted(tiles.items()):
   size=[min(16,dims[0]-x),dims[1],min(16,dims[2]-z)]
   doc={'format':'creativemode.structure','version':1,'palette':palette,'blocks':blocks,'bounds':{'min':[0,0,0],'max':[v-1 for v in size],'dimensions':size},'anchor':[0,0,0],'block_count':len(blocks)}
   local_markers={(xx-x,y,zz-z):data for (xx,y,zz),data in markers.items() if x<=xx<x+16 and z<=zz<z+16}
   path=f'venus/{revision}/{name}/tile_{x}_{z}';target=D/f'structure/{path}.nbt';target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(gzip.compress(convert(doc,block_entities=local_markers),mtime=0));entries.append({'template':'space_fabric:'+path,'x':x,'z':z})
  write(D/f'worldgen/structure/{name}.json',{'type':'space_fabric:venus_surface','biomes':'#space_fabric:has_structure/'+name,'step':'surface_structures','spawn_overrides':{},'terrain_adaptation':'none','width':dims[0],'height':dims[1],'depth':dims[2],'padding':16,'max_terrain_relief':8,'tiles':entries})
  write(D/f'tags/worldgen/biome/has_structure/{name}.json',{'replace':False,'values':['space_fabric:'+b for b in biomes]})
  write(D/f'tags/worldgen/structure/{alias}.json',{'replace':False,'values':['space_fabric:'+name]})
  for locale in ['es_es','en_us']:
   path=ROOT/f'src/main/resources/assets/space_fabric/lang/{locale}.json';lang=json.loads(path.read_text());lang['structure.space_fabric.'+name]=title;write(path,lang)
  metadata.update({'size':dims,'tiles':len(entries),'adaptation':adaptation,'palette':palette});manifest[name]=metadata;print(name,dims,adaptation)
 write(old,manifest)
 write(D/'worldgen/structure_set/venus_ruins.json',{'structures':[{'structure':'space_fabric:'+n,'weight':1} for n in SELECTION],
       'placement':{'type':'minecraft:random_spread','spacing':96,'separation':48,'salt':73502916,'exclusion_zone':{'other_set':'space_fabric:venus_settlements','chunk_count':12}}})
if __name__=='__main__':main()
