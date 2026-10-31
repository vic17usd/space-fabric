#!/usr/bin/env python3
"""Adapt the supplied Glacial Frontier data to Mars; no source code or entrypoint is executed."""
import argparse,hashlib,json,shutil
from pathlib import Path
from zipfile import ZipFile
ROOT=Path(__file__).resolve().parents[1];SOURCE=ROOT/'tools/import_sources/mars_glacial';NS='glacial-frontier-kzj32637'
A=ROOT/'src/main/resources/assets/space_fabric';D=ROOT/'src/main/resources/data/space_fabric'
NAMES={'packed_glacier_ice':'martian_glacier_ice','frozen_co2':'martian_frozen_co2','subglacial_ore':'martian_subglacial_ore','frost_crystal_node':'martian_frost_crystal_node','frost_crystal':'martian_frost_crystal','heatstone':'martian_heatstone'}
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
def adapt(value):
 if isinstance(value,str):
  value=value.replace(NS+':','space_fabric:')
  for old,new in sorted(NAMES.items(),key=lambda e:-len(e[0])):
   if value.endswith('/'+old) or value=='space_fabric:'+old:return value[:-len(old)]+new
  return value
 if isinstance(value,list):return [adapt(v) for v in value]
 if isinstance(value,dict):return {k:adapt(v) for k,v in value.items()}
 return value

def main():
 parser=argparse.ArgumentParser();parser.add_argument('--jar',type=Path);args=parser.parse_args()
 if args.jar:
  with ZipFile(args.jar) as z:
   meta=json.loads(z.read('fabric.mod.json'));assert meta['id']==NS
   for name in z.namelist():
    if name.startswith(('assets/'+NS+'/','data/'+NS+'/')) and name.endswith(('.png','.json')):
     p=SOURCE/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(z.read(name))
    if name.startswith('LICENSE'):
     p=ROOT/'third_party/mars_glacial/LICENSE.txt';p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(z.read(name))
   write(ROOT/'third_party/mars_glacial/manifest.json',{'source':args.jar.name,'sha256':hashlib.sha256(args.jar.read_bytes()).hexdigest(),'authors':meta['authors'],'url':meta['contact']['sources'],'license':meta['license'],'adaptation':'Mars-only polar biome, native cave generation and sealed resource recipes; original textures.'})
 for old,new in NAMES.items():
  for folder in ['blockstates','items','models/block','models/item']:
   p=SOURCE/f'assets/{NS}/{folder}/{old}.json'
   if p.exists():write(A/f'{folder}/{new}.json',adapt(json.loads(p.read_text())))
  for folder in ['block','item']:
   p=SOURCE/f'assets/{NS}/textures/{folder}/{old}.png'
   if p.exists():
    out=A/f'textures/{folder}/{new}.png';out.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(p,out)
  p=SOURCE/f'data/{NS}/loot_table/blocks/{old}.json'
  if p.exists():write(D/f'loot_table/blocks/{new}.json',adapt(json.loads(p.read_text())))
 write(D/'recipe/martian_heatstone.json',adapt(json.loads((SOURCE/f'data/{NS}/recipe/heatstone.json').read_text())))
 for name,inputs,output in [
  ('martian_glacier_water',['space_fabric:martian_glacier_ice','space_fabric:empty_canister','space_fabric:martian_heatstone'],'space_fabric:water_canister'),
  ('martian_co2_capture',['space_fabric:martian_frozen_co2','space_fabric:empty_canister'],'space_fabric:carbon_dioxide_canister')]:
  write(D/f'recipe/{name}.json',{'type':'minecraft:crafting_shapeless','category':'misc','ingredients':inputs,'result':{'id':output,'count':1}})
 for name in ['frost_crystal_veins','frozen_co2_strata','ice_cave_seams','subglacial_vein']:
  cf=adapt(json.loads((SOURCE/f'data/{NS}/worldgen/configured_feature/{name}.json').read_text()))
  cf['config']['targets']=[{'target':{'predicate_type':'minecraft:tag_match','tag':'space_fabric:mars_glacial_hosts'},'state':cf['config']['targets'][0]['state']}]
  write(D/f'worldgen/configured_feature/mars_{name}.json',cf)
  pf=json.loads((SOURCE/f'data/{NS}/worldgen/placed_feature/{name}.json').read_text());pf['feature']='space_fabric:mars_'+name;write(D/f'worldgen/placed_feature/mars_{name}.json',pf)
 write(D/'worldgen/configured_feature/mars_glacial_cave.json',{'type':'space_fabric:mars_glacial_cave','config':{'state':{'Name':'space_fabric:martian_glacier_ice'}}})
 write(D/'worldgen/placed_feature/mars_glacial_cave.json',{'feature':'space_fabric:mars_glacial_cave','placement':[{'type':'minecraft:rarity_filter','chance':8},{'type':'minecraft:in_square'},{'type':'minecraft:height_range','height':{'type':'minecraft:uniform','min_inclusive':{'absolute':-46},'max_inclusive':{'absolute':-16}}},{'type':'minecraft:biome'}]})
 biome=json.loads((D/'worldgen/biome/mars.json').read_text());biome.update(temperature=-.45,downfall=0,has_precipitation=False,spawners={})
 biome['attributes']['minecraft:visual/fog_color']='#9aaeb6'
 biome['features'][6]=['space_fabric:oxide_iron']+['space_fabric:mars_'+n for n in ['subglacial_vein','frost_crystal_veins','ice_cave_seams','frozen_co2_strata']]
 biome['features'][2]=['space_fabric:mars_glacial_cave'];biome['features'][7]=[];write(D/'worldgen/biome/mars_polar_ice_cap.json',biome)
 dimension=json.loads((D/'dimension/mars.json').read_text());dimension['generator']['biome_source']={'type':'space_fabric:mars_polar','temperate':'space_fabric:mars','polar':'space_fabric:mars_polar_ice_cap','polar_start':8192};write(D/'dimension/mars.json',dimension)
 settings=json.loads((D/'worldgen/noise_settings/mars.json').read_text());sequence=settings['surface_rule']['sequence']
 sequence[:]=[v for v in sequence if not(v.get('if_true',{}).get('biome_is')==['space_fabric:mars_polar_ice_cap'])]
 def floor(depth,block):return {'type':'minecraft:condition','if_true':{'type':'minecraft:stone_depth','add_surface_depth':False,'offset':depth,'secondary_depth_range':0,'surface_type':'floor'},'then_run':{'type':'minecraft:block','result_state':{'Name':'space_fabric:'+block}}}
 sequence.insert(1,{'type':'minecraft:condition','if_true':{'type':'minecraft:biome','biome_is':['space_fabric:mars_polar_ice_cap']},'then_run':{'type':'minecraft:sequence','sequence':[floor(0,'martian_frozen_co2'),floor(6,'martian_glacier_ice')]}});write(D/'worldgen/noise_settings/mars.json',settings)
 def tag(path,values):
  p=ROOT/'src/main/resources/data'/path;doc=json.loads(p.read_text()) if p.exists() else {'replace':False,'values':[]}
  for v in values:
   if v not in doc['values']:doc['values'].append(v)
  write(p,doc)
 blocks=['space_fabric:'+NAMES[n] for n in ['packed_glacier_ice','frozen_co2','subglacial_ore','frost_crystal_node']]
 tag('minecraft/tags/block/mineable/pickaxe.json',blocks)
 for t in ['needs_stone_tool','incorrect_for_wooden_tool','incorrect_for_gold_tool']:tag('minecraft/tags/block/'+t+'.json',['space_fabric:martian_subglacial_ore'])
 tag('space_fabric/tags/block/mars_glacial_hosts.json',['space_fabric:martian_stone','space_fabric:iron_oxide_regolith','space_fabric:martian_glacier_ice'])
 tag('space_fabric/tags/block/rocket_landing_surface.json',blocks[:2])
 for lang in ['es_es','en_us']:
  p=A/f'lang/{lang}.json';doc=json.loads(p.read_text());names=['Hielo glaciar marciano','CO₂ congelado marciano','Mena subglacial marciana','Nódulo de cristal de escarcha','Cristal de escarcha marciano','Piedra térmica marciana'] if lang=='es_es' else ['Martian Glacier Ice','Martian Frozen CO₂','Martian Subglacial Ore','Martian Frost Crystal Node','Martian Frost Crystal','Martian Heatstone']
  for i,(old,new) in enumerate(NAMES.items()):doc[('block' if i<4 else 'item')+'.space_fabric.'+new]=names[i]
  doc['biome.space_fabric.mars_polar_ice_cap']='Casquete polar marciano' if lang=='es_es' else 'Martian Polar Ice Cap';write(p,doc)
 out=A/'licenses/mars_glacial';out.mkdir(parents=True,exist_ok=True);shutil.copyfile(ROOT/'third_party/mars_glacial/LICENSE.txt',out/'LICENSE.txt');(out/'SOURCES.txt').write_text('Original design: holasoynuevo using CreativeMode.net\nhttps://creativemode.net/mod/glacial-frontier-kzj32637\n')
 print('Glacial Frontier adapted as Mars polar data; original textures preserved.')
if __name__=='__main__':main()
