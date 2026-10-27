#!/usr/bin/env python3
"""Convert the supplied structure JSONs to heat-safe, tiled native Venus templates.
Reads archives as data. No classes or source-mod initializers are executed.
"""
import argparse,hashlib,json,gzip
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

def prepare(document):
 palette=[];indices={}
 for index,block in enumerate(document['palette']):
  adapted=REMAP.get(block,block)
  if adapted.endswith('_wool'):adapted=adapted.removesuffix('_wool')+'_terracotta'
  if adapted not in palette:palette.append(adapted)
  indices[index]=palette.index(adapted)
 lo=document['bounds']['min'];size=document['bounds']['dimensions'];tiles={}
 for x,y,z,state in document['blocks']:
  x-=lo[0];y-=lo[1];z-=lo[2];key=(x//16*16,z//16*16)
  tiles.setdefault(key,[]).append([x-key[0],y,z-key[1],indices[state]])
 return palette,size,tiles

def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('jars',nargs='+',type=Path);args=parser.parse_args()
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
   palette,size,tiles=prepare(source);entries=[];total=0
   for (x,z),blocks in sorted(tiles.items()):
    dims=[min(16,size[0]-x),size[1],min(16,size[2]-z)]
    tile={'format':'creativemode.structure','version':1,'palette':palette,'blocks':blocks,'bounds':{'min':[0,0,0],'max':[d-1 for d in dims],'dimensions':dims},'anchor':[0,0,0],'block_count':len(blocks)}
    id=f'venus/{name}/tile_{x}_{z}';output=DATA/f'structure/{id}.nbt';output.parent.mkdir(parents=True,exist_ok=True);output.write_bytes(gzip.compress(convert(tile),mtime=0))
    entries.append({'template':'space_fabric:'+id,'x':x,'z':z});total+=len(blocks)
   assert total==source['block_count']
   config={'type':'space_fabric:venus_surface','biomes':'#space_fabric:has_structure/venus_settlements','step':'surface_structures','spawn_overrides':{},'terrain_adaptation':'none','width':size[0],'height':size[1],'depth':size[2],'padding':4,'tiles':entries}
   (DATA/f'worldgen/structure/{name}.json').write_text(json.dumps(config,indent=2)+'\n')
   manifest[name]={'jar':jar.name,'sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'source_page':meta['contact']['sources'],'authors':meta['authors'],'source_member':files[0],'original_blocks':total,'size':size,'tiles':len(entries),'palette_mapping':dict(zip(source['palette'],[REMAP.get(b,b).removesuffix('_wool')+'_terracotta' if b.endswith('_wool') else REMAP.get(b,b) for b in source['palette']]))}
   print(name,size,total,'blocks in',len(entries),'bounded tiles')
 out=ROOT/'third_party/venus_expansion/structures.json';out.write_text(json.dumps(manifest,indent=2)+'\n')
 tags=DATA/'tags/worldgen/biome/has_structure/venus_settlements.json';tags.parent.mkdir(parents=True,exist_ok=True);tags.write_text(json.dumps({'replace':False,'values':['space_fabric:venus']},indent=2)+'\n')
 (DATA/'worldgen/structure_set/venus_settlements.json').write_text(json.dumps({'structures':[{'structure':'space_fabric:'+name,'weight':1} for name in manifest],'placement':{'type':'minecraft:random_spread','spacing':128,'separation':64,'salt':86415037}},indent=2)+'\n')
if __name__=='__main__':main()
