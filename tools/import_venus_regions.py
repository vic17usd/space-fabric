#!/usr/bin/env python3
"""Import only art and source data from the three user biomes; adapt their registries to 26.1."""
import argparse,copy,hashlib,json
from pathlib import Path
from zipfile import ZipFile
ROOT=Path(__file__).resolve().parents[1];R=ROOT/'src/main/resources';A=R/'assets/space_fabric';D=R/'data/space_fabric';S=ROOT/'tools/import_sources/venus_regions'
REGIONS=[('obsidian-riftlands-fu2ghb3x','obsidian_riftlands',{'rift_obsidian':'venus_rift_obsidian','rift_iron':'venus_rift_iron','ember_crust':'venus_rift_ember_crust'},['rift_shard']),('sulfur-shardlands-36x5fjm7','sulfur_shardlands',{'tessera_rock':'venus_tessera_rock','ember_fissure':'venus_ember_fissure','sulfur_lantern':'venus_sulfur_lantern'},[]),('basalt-ashlands-iktgmxrq','basalt_ashlands',{'basalt_sheet':'venus_basalt_sheet','charred_bedrock':'venus_charred_rock','sulfur_ash':'venus_sulfur_ash'},['sulfur_dust'])]
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
def tag(ns,name,values):
 p=R/f'data/{ns}/tags/block/{name}.json';d=json.loads(p.read_text()) if p.exists() else {'replace':False,'values':[]};d['values']=list(dict.fromkeys(d['values']+values));write(p,d)
def main():
 parser=argparse.ArgumentParser();parser.add_argument('--jars',nargs=3,help='Obsidian Riftlands, Sulfur Shardlands, Basalt Ashlands');args=parser.parse_args()
 if args.jars:
  manifest=[]
  for path,(ns,biome,blocks,items) in zip(args.jars,REGIONS):
   p=Path(path);folder=S/biome
   with ZipFile(p) as z:
    metadata=json.loads(z.read('fabric.mod.json'));assert metadata['id']==ns
    write(folder/'fabric.mod.json',metadata)
    license=next(n for n in z.namelist() if n.startswith('LICENSE'));folder.mkdir(parents=True,exist_ok=True);(folder/'LICENSE.txt').write_bytes(z.read(license))
    paths=[f'data/{ns}/worldgen/biome/{biome}.json']
    paths += [n for n in z.namelist() if n.startswith(f'data/{ns}/worldgen/') and n.endswith('.json')]
    paths += [f'assets/{ns}/{kind}/{old}.{ext}' for old in blocks for kind,ext in [('models/block','json'),('textures/block','png')]]
    paths += [f'assets/{ns}/textures/item/{old}.png' for old in items]
    for n in set(paths):
     target=folder/n;target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(z.read(n))
   manifest.append({'source':p.name,'sha256':hashlib.sha256(p.read_bytes()).hexdigest(),'authors':metadata['authors'],'url':metadata['contact']['sources'],'license':metadata['license'],'target_biome':'space_fabric:venus_'+biome,'scope':'Original biome art and geology; rewritten native terrain; existing Venus fauna, no source entrypoints or bundled telemetry.'})
   license_out=ROOT/'third_party/venus_regions'/biome/'LICENSE.txt';license_out.parent.mkdir(parents=True,exist_ok=True);license_out.write_bytes((folder/'LICENSE.txt').read_bytes())
  write(ROOT/'third_party/venus_regions/manifest.json',manifest)
 for ns,biome,blocks,items in REGIONS:
  license_out=A/f'licenses/venus_regions/{biome}.txt';license_out.parent.mkdir(parents=True,exist_ok=True);license_out.write_bytes((S/biome/'LICENSE.txt').read_bytes())
  folder=S/biome;mapping={f'{ns}:block/{old}':'space_fabric:block/'+new for old,new in blocks.items()}
  for old,new in blocks.items():
   (A/f'textures/block/{new}.png').write_bytes((folder/f'assets/{ns}/textures/block/{old}.png').read_bytes())
   model=json.loads((folder/f'assets/{ns}/models/block/{old}.json').read_text());model['textures']={k:mapping.get(v,v) for k,v in model['textures'].items()}
   if old=='sulfur_ash':model.setdefault('parent','minecraft:block/block')  # Native GUI transforms make the original top face visible as an item.
   write(A/f'models/block/{new}.json',model)
   write(A/f'blockstates/{new}.json',{'variants':{'':{'model':'space_fabric:block/'+new}}});write(A/f'items/{new}.json',{'model':{'type':'minecraft:model','model':'space_fabric:block/'+new}})
   drop='venus_rift_shard' if old=='rift_iron' else 'venus_sulfur_dust' if old=='sulfur_ash' else new
   entry={'type':'minecraft:item','name':'space_fabric:'+drop}
   if old in ['rift_iron','sulfur_ash']:entry['functions']=[{'function':'minecraft:set_count','count':{'type':'minecraft:uniform','min':1,'max':2 if old=='rift_iron' else 3}},{'function':'minecraft:explosion_decay'}]
   write(D/f'loot_table/blocks/{new}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[entry],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
   tag('minecraft','mineable/shovel' if old=='sulfur_ash' else 'mineable/pickaxe',['space_fabric:'+new])
   if old!='sulfur_ash':tag('minecraft','needs_diamond_tool' if old=='rift_obsidian' else 'needs_iron_tool' if old=='rift_iron' else 'needs_stone_tool',['space_fabric:'+new])
   if old not in ['rift_iron','sulfur_lantern','sulfur_ash']:
    tag('space_fabric','venus_host_rocks',['space_fabric:'+new]);tag('space_fabric','rocket_landing_surface',['space_fabric:'+new])
  for old in items:
   new='venus_'+old;(A/f'textures/item/{new}.png').write_bytes((folder/f'assets/{ns}/textures/item/{old}.png').read_bytes())
   write(A/f'models/item/{new}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/'+new}});write(A/f'items/{new}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+new}})
  original=json.loads((folder/f'data/{ns}/worldgen/biome/{biome}.json').read_text());base=copy.deepcopy(json.loads((D/'worldgen/biome/venus.json').read_text()));fx=original['effects']
  base['attributes'].update({'minecraft:visual/sky_color':'#%06x'%fx['sky_color'],'minecraft:visual/fog_color':'#%06x'%fx['fog_color'],'minecraft:visual/fog_end_distance':24 if biome=='basalt_ashlands' else 32})
  base['temperature']=2.0;base['downfall']=0.0;base['effects']={'water_color':'#%06x'%fx['water_color']}
  base['attributes']['minecraft:visual/water_fog_color']='#%06x'%fx['water_fog_color']
  if 'ambient_particle' in fx:base['attributes']['minecraft:visual/ambient_particles']=[{'particle':fx['ambient_particle']['options'],'probability':fx['ambient_particle']['probability']}]
  if 'mood_sound' in fx:base['attributes']['minecraft:audio/ambient_sounds']={'mood':fx['mood_sound']}
  if fx.get('music'):
   music=fx['music'][0]['data'];base['attributes']['minecraft:audio/background_music']={'default':{k:music[k] for k in ['sound','min_delay','max_delay']}}
  write(D/f'worldgen/biome/venus_{biome}.json',base)
  write(D/f'worldgen/configured_feature/venus_{biome}_terrain.json',{'type':'space_fabric:venus_'+biome+'_terrain','config':{}})
  write(D/f'worldgen/placed_feature/venus_{biome}_terrain.json',{'feature':'space_fabric:venus_'+biome+'_terrain','placement':[{'type':'minecraft:biome'}]})
 old=['space_fabric:venus','space_fabric:venus_fumaroles','space_fabric:venus_obsidian_basins'];new=['space_fabric:venus_'+r[1] for r in REGIONS]
 write(D/'tags/worldgen/biome/venus_biomes.json',{'replace':False,'values':old+new})
 # Keep original structure affinities and broaden them to their related new region.
 pairs=dict(zip(old,[new[1],new[2],new[0]]))
 for p in (D/'tags/worldgen/biome/has_structure').glob('*.json'):
  data=json.loads(p.read_text());values=data.get('values',[]);before=list(values)
  for source,target in pairs.items():
   if source in values and target not in values:values.append(target)
  if values!=before:write(p,data)
 dimension=json.loads((D/'dimension/venus.json').read_text());entries=[]
 for original,replacement,humidity in zip(old,[new[1],new[2],new[0]],[[-.25,.25],[-1,-.25],[.25,1]]):
  for biome,temperature in [(original,[-2,0]),(replacement,[0,2])]:
   params={k:[-2,2] for k in ['continentalness','erosion','depth','weirdness']};params.update({'temperature':temperature,'humidity':humidity,'offset':0});entries.append({'biome':biome,'parameters':params})
 dimension['generator']['biome_source']={'type':'minecraft:multi_noise','biomes':entries};write(D/'dimension/venus.json',dimension)
 settings=json.loads((D/'worldgen/noise_settings/venus.json').read_text());seq=settings['surface_rule']['sequence'];new_ids=set(new)
 seq=[r for r in seq if not new_ids.intersection(r.get('if_true',{}).get('biome_is',[]))]
 for biome,block in reversed([(new[0],'venus_rift_obsidian'),(new[1],'venus_tessera_rock'),(new[2],'venus_basalt_sheet')]):
  seq.insert(1,{'type':'minecraft:condition','if_true':{'type':'minecraft:biome','biome_is':[biome]},'then_run':{'type':'minecraft:condition','if_true':{'type':'minecraft:stone_depth','add_surface_depth':False,'offset':3,'secondary_depth_range':0,'surface_type':'floor'},'then_run':{'type':'minecraft:block','result_state':{'Name':'space_fabric:'+block}}}})
 settings['surface_rule']['sequence']=seq;write(D/'worldgen/noise_settings/venus.json',settings)
 write(D/'worldgen/configured_feature/venus_rift_iron.json',{'type':'minecraft:ore','config':{'size':5,'discard_chance_on_air_exposure':0,'targets':[{'target':{'predicate_type':'minecraft:tag_match','tag':'space_fabric:venus_host_rocks'},'state':{'Name':'space_fabric:venus_rift_iron'}}]}})
 write(D/'worldgen/placed_feature/venus_rift_iron.json',{'feature':'space_fabric:venus_rift_iron','placement':[{'type':'minecraft:count','count':6},{'type':'minecraft:in_square'},{'type':'minecraft:height_range','height':{'type':'minecraft:uniform','min_inclusive':{'absolute':-48},'max_inclusive':{'absolute':80}}},{'type':'minecraft:biome'}]})
 write(D/'recipe/venus_sulfur_dust_crystal.json',{'type':'minecraft:crafting_shapeless','ingredients':['space_fabric:venus_sulfur_dust']*4,'result':{'id':'space_fabric:sulfur_crystal','count':1}})
 for method in ['smelting','blasting']:write(D/f'recipe/venus_rift_iron_{method}.json',{'type':'minecraft:'+method,'category':'misc','ingredient':'space_fabric:venus_rift_shard','result':{'id':'minecraft:iron_ingot','count':1},'experience':.3,'cookingtime':200 if method=='smelting' else 100})
 write(D/'recipe/venus_sulfur_lantern.json',{'type':'minecraft:crafting_shaped','category':'misc','pattern':['TRT','RCR','TRT'],'key':{'T':'space_fabric:venus_tessera_rock','R':'space_fabric:venus_ember_fissure','C':'space_fabric:sulfur_core'},'result':{'id':'space_fabric:venus_sulfur_lantern','count':1}})
 names={'venus_obsidian_riftlands':('Grietas de obsidiana','Obsidian Riftlands'),'venus_sulfur_shardlands':('Mesetas de azufre','Sulfur Shardlands'),'venus_basalt_ashlands':('Yermos de ceniza basáltica','Basalt Ashlands')}
 blocks={'venus_rift_obsidian':('Obsidiana de grieta','Rift Obsidian'),'venus_rift_iron':('Mena de hierro de grieta','Rift Iron Ore'),'venus_rift_ember_crust':('Costra de grieta incandescente','Rift Ember Crust'),'venus_tessera_rock':('Roca de tesela venusiana','Venusian Tessera Rock'),'venus_ember_fissure':('Roca de fisura incandescente','Ember Fissure Rock'),'venus_sulfur_lantern':('Lámpara de azufre','Sulfur Lantern'),'venus_basalt_sheet':('Laja de basalto','Basalt Sheet'),'venus_charred_rock':('Roca carbonizada','Charred Rock'),'venus_sulfur_ash':('Ceniza de azufre','Sulfur Ash')}
 items={'venus_rift_shard':('Fragmento de hierro de grieta','Rift Iron Shard'),'venus_sulfur_dust':('Polvo de azufre venusiano','Venus Sulfur Dust')}
 for i,lang in enumerate(['es_es','en_us']):
  p=A/f'lang/{lang}.json';d=json.loads(p.read_text())
  for kind,values in [('biome',names),('block',blocks),('item',items)]:d.update({kind+'.space_fabric.'+k:v[i] for k,v in values.items()})
  write(p,d)
 (A/'licenses/venus_regions/SOURCES.txt').write_text('Source art and biome concepts: CreativeMode.net\n'+''.join(f'{ns}: https://creativemode.net/mod/{ns}\n' for ns,biome,blocks,items in REGIONS))
 print('Imported 3 Venus regions, 9 block textures, 2 materials and native biome resources.')
if __name__=='__main__':main()
