#!/usr/bin/env python3
"""Convert the supplied structure JSONs to heat-safe, tiled native Venus templates.
Reads archives as data. No classes or source-mod initializers are executed.
"""
import argparse,hashlib,json,gzip
from collections import deque
from pathlib import Path
from zipfile import ZipFile
from convert_creativemode_structure import convert
ROOT=Path(__file__).resolve().parents[1]
DATA=ROOT/'src/main/resources/data/space_fabric'
REMAP={'minecraft:stone':'minecraft:basalt','minecraft:cobblestone':'minecraft:blackstone',
 'minecraft:stone_bricks':'minecraft:polished_blackstone_bricks','minecraft:mossy_stone_bricks':'minecraft:weathered_cut_copper',
 'minecraft:cracked_stone_bricks':'minecraft:cracked_polished_blackstone_bricks','minecraft:andesite':'minecraft:polished_basalt',
 'minecraft:water':'minecraft:lime_stained_glass','minecraft:spruce_planks':'minecraft:polished_blackstone_bricks',
 'minecraft:dark_oak_planks':'minecraft:polished_blackstone','minecraft:spruce_log':'minecraft:basalt',
 'minecraft:dark_oak_log':'minecraft:polished_basalt'}

def ground_level(name,x,z):
 # The supplied village has three rigid storeys on solid display plinths.
 # Lower each storey as a whole; retain the architecture, not the editor's terrain.
 if name=='copper_crown_village':
  if 48<=x<=88 and 29<=z<=69:return 38
  if z<=68:return 14
 return 2

def neighbours(pos):
 x,y,z=pos
 return ((x+1,y,z),(x-1,y,z),(x,y+1,z),(x,y-1,z),(x,y,z+1),(x,y,z-1))

def support_parts(blocks,post):
 """Join detached caps with short masonry ties and give freestanding canopies legs.
 No post can exceed nine blocks; never fill a tall solid pedestal beneath a roof.
 """
 remaining=set(blocks);parts=[];max_x=max(x for x,y,z in blocks);max_z=max(z for x,y,z in blocks)
 while remaining:
  first=min(remaining);remaining.remove(first);queue=[first]
  for pos in queue:
   for other in neighbours(pos):
    if other in remaining:remaining.remove(other);queue.append(other)
  parts.append(queue)
 anchored=set();pending=[];added=0;longest=0
 for part in parts:
  if any(y<=1 for x,y,z in part):anchored.update(part) # Y=1 rests on the implicit natural floor at Y=0.
  else:pending.append(part)
 while pending:
  progress=False
  for part in pending[:]:
   low=min(y for x,y,z in part);high=max(y for x,y,z in part)
   xs=[x for x,y,z in part];zs=[z for x,y,z in part]
   if low<=9 and high-low<=2 and max(xs)-min(xs)>=3 and max(zs)-min(zs)>=3:
    # Four matching columns under a detached market awning, not a sheet of floating blocks.
    bottom=[p for p in part if p[1]==low]
    feet={min(bottom,key=lambda p:(p[0]-x)**2+(p[2]-z)**2) for x in (min(xs),max(xs)) for z in (min(zs),max(zs))}
    posts=[]
    for x,y,z in feet:
     posts.extend((x,h,z) for h in range(0,y) if (x,h,z) not in blocks)
   else:
    best=None
    for x,y,z in sorted(part):
     if (x,y-1,z) in blocks:continue
     for gap in range(1,10):
      below=(x,y-gap-1,z)
      if below[1]<0:break
      if below in anchored or below[1]==0:
       candidate=(gap,x,y,z)
       if best is None or candidate<best:best=candidate
       break
      if below in blocks:break
    if best is None:
     # Round tower cornices in the source sometimes meet only diagonally. Add a
     # one-to-three-block masonry tie instead of a pillar running down the tower.
     paths={};queue=deque();tie=None
     for pos in sorted(part):
      for other in neighbours(pos):
       if other in anchored:tie=[];break
       if other not in blocks and other not in paths and 0<=other[0]<=max_x and other[1]>=0 and 0<=other[2]<=max_z:
        paths[other]=[other];queue.append(other)
      if tie is not None:break
     while queue and tie is None:
      pos=queue.popleft();path=paths[pos]
      for other in neighbours(pos):
       if other in anchored:tie=path;break
       if len(path)<3 and other not in blocks and other not in paths and 0<=other[0]<=max_x and other[1]>=0 and 0<=other[2]<=max_z:
        paths[other]=path+[other];queue.append(other)
     if tie is None:continue
     posts=tie
    else:
     gap,x,y,z=best;posts=[(x,h,z) for h in range(y-gap,y) if (x,h,z) not in blocks]
   for pos in posts:blocks[pos]=post
   added+=len(posts);longest=max(longest,max((sum(1 for q in posts if q[0]==p[0] and q[2]==p[2]) for p in posts),default=0))
   anchored.update(part);anchored.update(posts);pending.remove(part);progress=True
  if not progress:raise ValueError('Unsupported Venus pieces need a deliberate redesign: '+str([(len(p),min(y for x,y,z in p)) for p in pending]))
 return added,longest

def prepare(document,name):
 palette=[];indices={}
 for index,block in enumerate(document['palette']):
  adapted=REMAP.get(block,block)
  if adapted.endswith('_wool'):adapted=adapted.removesuffix('_wool')+'_terracotta'
  if adapted not in palette:palette.append(adapted)
  indices[index]=palette.index(adapted)
 lo=document['bounds']['min'];size=list(document['bounds']['dimensions']);tiles={};blocks={}
 for x,y,z,state in document['blocks']:
  x-=lo[0];y-=lo[1];z-=lo[2]
  # Obsolete stair connecting the old 36-block keep plinth to the lower plaza.
  if name=='copper_crown_village' and 59<=x<=77 and 69<=z<=81 and y<=38:continue
  cut=ground_level(name,x,z)
  if y>=cut:blocks[x,y-cut,z]=indices[state]
 retained=len(blocks)
 post='minecraft:polished_blackstone_bricks' if name=='copper_crown_village' else 'minecraft:sandstone'
 if post not in palette:palette.append(post)
 added,longest=support_parts(blocks,palette.index(post))
 size[1]=max(y for x,y,z in blocks)+1
 for (x,y,z),state in sorted(blocks.items()):
  key=(x//16*16,z//16*16);tiles.setdefault(key,[]).append([x-key[0],y,z-key[1],state])
 return palette,size,tiles,{'retained_blocks':retained,'added_support_blocks':added,'max_support_height':longest,'removed_base_blocks':document['block_count']-retained,'ground_levels_removed':[2,14,38] if name=='copper_crown_village' else [2]}

def write_structure(source,name,metadata):
 palette,size,tiles,adaptation=prepare(source,name);entries=[];total=0
 # Existing saves store template IDs in their pieces. Never replace those old
 # resources with a different vertical layout or remove retired tiles they reference.
 output_directory=DATA/f'structure/venus/surface_v2/{name}';output_directory.mkdir(parents=True,exist_ok=True)
 for old in output_directory.glob('tile_*.nbt'):old.unlink()
 for (x,z),blocks in sorted(tiles.items()):
  dims=[min(16,size[0]-x),size[1],min(16,size[2]-z)]
  tile={'format':'creativemode.structure','version':1,'palette':palette,'blocks':blocks,'bounds':{'min':[0,0,0],'max':[d-1 for d in dims],'dimensions':dims},'anchor':[0,0,0],'block_count':len(blocks)}
  id=f'venus/surface_v2/{name}/tile_{x}_{z}';(DATA/f'structure/{id}.nbt').write_bytes(gzip.compress(convert(tile),mtime=0))
  entries.append({'template':'space_fabric:'+id,'x':x,'z':z});total+=len(blocks)
 assert total==adaptation['retained_blocks']+adaptation['added_support_blocks']
 config={'type':'space_fabric:venus_surface','biomes':'#space_fabric:has_structure/venus_settlements','step':'surface_structures','spawn_overrides':{},'terrain_adaptation':'none','width':size[0],'height':size[1],'depth':size[2],'padding':16,'max_terrain_relief':8,'tiles':entries}
 (DATA/f'worldgen/structure/{name}.json').write_text(json.dumps(config,indent=2)+'\n')
 metadata.update({'original_blocks':source['block_count'],'adapted_blocks':total,'size':size,'tiles':len(entries),'template_revision':'surface_v2','surface_adaptation':adaptation,
  'palette_mapping':dict(zip(source['palette'],[REMAP.get(b,b).removesuffix('_wool')+'_terracotta' if b.endswith('_wool') else REMAP.get(b,b) for b in source['palette']]))})
 print(name,size,total,'blocks in',len(entries),'tiles;',adaptation)
 return metadata

def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('jars',nargs='*',type=Path)
 parser.add_argument('--stored-sources',action='store_true',help='Rebuild from the checked-in original JSONs without reopening source JARs')
 args=parser.parse_args()
 if not args.jars and not args.stored_sources:parser.error('Provide structure JARs or --stored-sources')
 if args.jars and args.stored_sources:parser.error('Choose JARs or --stored-sources')
 manifest={}
 for jar in args.jars:
  with ZipFile(jar) as archive:
   meta=json.loads(archive.read('fabric.mod.json'));slug=meta['id']
   files=[n for n in archive.namelist() if '/structures/' in n and n.endswith('.json') and not n.endswith('manifest.json')]
   assert len(files)==1
   raw=archive.read(files[0]);source=json.loads(raw);name=Path(files[0]).stem
   original=ROOT/f'tools/structure_sources/venus/{name}.json';original.parent.mkdir(parents=True,exist_ok=True);original.write_bytes(raw)
   license_path=ROOT/f'third_party/venus_expansion/{slug}/LICENSE.txt';license_path.parent.mkdir(parents=True,exist_ok=True)
   license_path.write_bytes(archive.read(next(n for n in archive.namelist() if n.startswith('LICENSE'))))
   manifest[name]=write_structure(source,name,{'jar':jar.name,'sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'source_page':meta['contact']['sources'],'authors':meta['authors'],'source_member':files[0]})
 if args.stored_sources:
  previous=json.loads((ROOT/'third_party/venus_expansion/structures.json').read_text())
  for name,metadata in previous.items():manifest[name]=write_structure(json.loads((ROOT/f'tools/structure_sources/venus/{name}.json').read_text()),name,metadata)
 out=ROOT/'third_party/venus_expansion/structures.json';out.write_text(json.dumps(manifest,indent=2)+'\n')
 tags=DATA/'tags/worldgen/biome/has_structure/venus_settlements.json';tags.parent.mkdir(parents=True,exist_ok=True);tags.write_text(json.dumps({'replace':False,'values':['space_fabric:venus']},indent=2)+'\n')
 (DATA/'worldgen/structure_set/venus_settlements.json').write_text(json.dumps({'structures':[{'structure':'space_fabric:'+name,'weight':1} for name in manifest],'placement':{'type':'minecraft:random_spread','spacing':128,'separation':64,'salt':86415037}},indent=2)+'\n')
if __name__=='__main__':main()
