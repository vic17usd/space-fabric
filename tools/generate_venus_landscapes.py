#!/usr/bin/env python3
"""Generate native Venus biome zones and resources; no TerraBlender dependency."""
import copy,json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]/'src/main/resources';D=ROOT/'data/space_fabric';A=ROOT/'assets/space_fabric'
def write(p,data):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(data,indent=2,ensure_ascii=False)+'\n')
def main():
 names={
 'venus_vent_basalt':('Basalto de fumarola','Vent Basalt'),'venus_sulfur_crust':('Costra de azufre','Sulfur Crust'),
 'venus_ember_crust':('Costra incandescente','Ember Crust'),'venus_acid_pocket':('Depósito corrosivo','Corrosive Pocket'),
 'venus_basalt_substrate':('Sustrato basáltico venusiano','Venusian Basalt Substrate'),'venus_cracked_stone':('Roca volcánica agrietada','Cracked Volcanic Stone'),
 'venus_obsidian_slag':('Escoria de obsidiana','Obsidian Slag'),'venus_sulfur_crystal_block':('Cristales de azufre','Sulfur Crystals'),'venus_acid_pool':('Superficie de laguna corrosiva','Corrosive Lagoon Surface')}
 for locale,i in [('es_es',0),('en_us',1)]:
  p=A/f'lang/{locale}.json';lang=json.loads(p.read_text());lang.update({'block.space_fabric.'+k:v[i] for k,v in names.items()})
  lang.update({'biome.space_fabric.venus':['Llanuras volcánicas de Venus','Venus Volcanic Plains'][i],'biome.space_fabric.venus_fumaroles':['Campos de fumarolas','Fumarole Fields'][i],'biome.space_fabric.venus_obsidian_basins':['Cuencas de obsidiana','Obsidian Basins'][i],'structure.space_fabric.venus_sulfur_caldera':'Caldera de azufre de Venus'})
  write(p,lang)
 for tag,extra in [('mineable/pickaxe',list(names)),('needs_stone_tool',[k for k in names if k!='venus_obsidian_slag']),('needs_diamond_tool',['venus_obsidian_slag'])]:
  p=ROOT/f'data/minecraft/tags/block/{tag}.json';data=json.loads(p.read_text());data['values']=list(dict.fromkeys(data['values']+['space_fabric:'+k for k in extra]));write(p,data)
 p=D/'tags/block/venus_host_rocks.json';data=json.loads(p.read_text());data['values']=list(dict.fromkeys(data['values']+['space_fabric:'+k for k in names if 'acid' not in k and 'sulfur' not in k]));write(p,data)
 p=D/'tags/block/rocket_landing_surface.json';data=json.loads(p.read_text());data['values']=list(dict.fromkeys(data['values']+['space_fabric:'+k for k in names if 'acid' not in k and 'sulfur' not in k]));write(p,data)
 biomes=['space_fabric:venus','space_fabric:venus_fumaroles','space_fabric:venus_obsidian_basins']
 write(D/'tags/worldgen/biome/venus_biomes.json',{'replace':False,'values':biomes})
 write(D/'tags/worldgen/biome/has_structure/venus_settlements.json',{'replace':False,'values':['#space_fabric:venus_biomes']})
 base=json.loads((D/'worldgen/biome/venus.json').read_text())
 for name,sky,fog,end in [('venus_fumaroles','#b47a50','#725b4c',28),('venus_obsidian_basins','#a59161','#73704b',36)]:
  biome=copy.deepcopy(base);biome['attributes'].update({'minecraft:visual/sky_color':sky,'minecraft:visual/fog_color':fog,'minecraft:visual/fog_start_distance':0,'minecraft:visual/fog_end_distance':end});biome['has_precipitation']=False
  write(D/f'worldgen/biome/{name}.json',biome)
 dimension=json.loads((D/'dimension/venus.json').read_text());entries=[]
 for biome,humidity in zip(biomes,[[-.25,.25],[-1,-.25],[.25,1]]):
  params={key:[-2,2] for key in ['temperature','continentalness','erosion','depth','weirdness']};params.update({'humidity':humidity,'offset':0});entries.append({'biome':biome,'parameters':params})
 dimension['generator']['biome_source']={'type':'minecraft:multi_noise','biomes':entries};write(D/'dimension/venus.json',dimension)
 settings=json.loads((D/'worldgen/noise_settings/venus.json').read_text());sequence=settings['surface_rule']['sequence'];sequence=[x for x in sequence if not (x.get('if_true',{}).get('type')=='minecraft:biome')]
 for biome,block in reversed([('venus_fumaroles','venus_basalt_substrate'),('venus_obsidian_basins','venus_obsidian_slag')]):
  sequence.insert(1,{'type':'minecraft:condition','if_true':{'type':'minecraft:biome','biome_is':['space_fabric:'+biome]},'then_run':{'type':'minecraft:condition','if_true':{'type':'minecraft:stone_depth','add_surface_depth':False,'offset':2,'secondary_depth_range':0,'surface_type':'floor'},'then_run':{'type':'minecraft:block','result_state':{'Name':'space_fabric:'+block}}}})
 settings['surface_rule']['sequence']=sequence;write(D/'worldgen/noise_settings/venus.json',settings)
 for name,chance in [('venus_vent_field',1),('venus_acid_lakes',6),('venus_lava_fissures',12)]:
  write(D/f'worldgen/configured_feature/{name}.json',{'type':'space_fabric:'+name,'config':{}})
  write(D/f'worldgen/placed_feature/{name}.json',{'feature':'space_fabric:'+name,'placement':[{'type':'minecraft:rarity_filter','chance':chance},{'type':'minecraft:biome'}]})
 print('Generated three Venus climate zones, nine block names, tags and bounded terrain features.')
if __name__=='__main__':main()
