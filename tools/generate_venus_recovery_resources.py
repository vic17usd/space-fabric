#!/usr/bin/env python3
"""Generate recovery recipes, cooling machine resources and bilingual player guidance."""
import json
from pathlib import Path
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1]/'src/main/resources';A=ROOT/'assets/space_fabric';D=ROOT/'data/space_fabric'
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
def tag(ns,kind,path,values):
 p=ROOT/f'data/{ns}/tags/{kind}/{path}.json';d=json.loads(p.read_text()) if p.exists() else {'replace':False,'values':[]};d['values']=list(dict.fromkeys(d['values']+values));write(p,d)
def main():
 im=Image.new('RGBA',(32,32),(63,78,88,255));d=ImageDraw.Draw(im);d.rectangle((1,1,30,30),outline=(165,194,206));d.rectangle((5,5,26,26),fill=(21,40,55),outline=(93,201,236))
 for y in range(8,25,4):d.rectangle((8,y,23,y+1),fill=(93,175,208))
 for x,y in [(3,3),(28,3),(3,28),(28,28)]:d.point((x,y),fill=(223,231,235))
 p=A/'textures/block/venus_cooling_unit.png';p.parent.mkdir(parents=True,exist_ok=True);im.resize((64,64),Image.Resampling.NEAREST).save(p)
 write(A/'models/block/venus_cooling_unit.json',{'parent':'minecraft:block/cube_all','textures':{'all':'space_fabric:block/venus_cooling_unit'}})
 write(A/'blockstates/venus_cooling_unit.json',{'variants':{'':{'model':'space_fabric:block/venus_cooling_unit'}}})
 write(A/'items/venus_cooling_unit.json',{'model':{'type':'minecraft:model','model':'space_fabric:block/venus_cooling_unit'}})
 im=Image.new('RGBA',(32,32));d=ImageDraw.Draw(im);d.rectangle((10,7,22,27),fill=(158,185,199),outline=(27,48,65),width=2);d.rectangle((12,13,20,23),fill=(92,213,240));d.rectangle((14,3,18,7),fill=(218,229,234));d.line((16,15,16,21),fill='white');d.line((13,18,19,18),fill='white')
 p=A/'textures/item/chilled_coolant_canister.png';p.parent.mkdir(parents=True,exist_ok=True);im.resize((64,64),Image.Resampling.NEAREST).save(p)
 write(A/'models/item/chilled_coolant_canister.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/chilled_coolant_canister'}})
 write(A/'items/chilled_coolant_canister.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/chilled_coolant_canister'}})
 for name in ['foundry_console','venus_cooling_unit']:
  entry={'type':'minecraft:item','name':'space_fabric:'+name}
  if name=='venus_cooling_unit':entry['functions']=[{'function':'minecraft:copy_custom_data','source':'block_entity','ops':[{'source':'Maintenance','target':'SpaceFabricMaintenance','op':'replace'}]}]
  write(D/f'loot_table/blocks/{name}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[entry],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
 tag('minecraft','block','mineable/pickaxe',['space_fabric:foundry_console','space_fabric:venus_cooling_unit'])
 tag('space_fabric','block','pressure_rated_shell',['space_fabric:venus_cooling_unit'])
 def shaped(name,pattern,key):write(D/f'recipe/{name}.json',{'type':'minecraft:crafting_shaped','category':'misc','pattern':pattern,'key':key,'result':{'id':'space_fabric:'+name,'count':1}})
 sf=lambda n:'space_fabric:'+n;mc=lambda n:'minecraft:'+n
 shaped('foundry_console',['GPG','CRC','HHH'],{'G':mc('glass'),'P':sf('venus_composite_plate'),'C':mc('copper_ingot'),'R':mc('redstone'),'H':sf('pressure_hull')})
 shaped('venus_cooling_unit',['HCH','PRP','HBH'],{'H':sf('pressure_hull'),'C':mc('copper_ingot'),'P':sf('pyroxene_ceramic'),'R':sf('venus_research_report'),'B':sf('empty_power_cell')})
 shaped('venus_rover',['PCP','TRT','PBP'],{'P':sf('venus_composite_plate'),'C':sf('venus_cooling_unit'),'T':sf('empty_canister'),'R':sf('venus_research_report'),'B':sf('empty_power_cell')})
 messages={
  'block.space_fabric.foundry_console':('Consola de recuperación de la fundición','Foundry Recovery Console'),
  'block.space_fabric.venus_cooling_unit':('Unidad de refrigeración venusiana','Venus Cooling Unit'),
  'item.space_fabric.chilled_coolant_canister':('Bombona de refrigerante enfriado','Chilled Coolant Canister'),
  'item.space_fabric.venus_rover':('Rover presurizado de Venus','Venus Pressurized Rover'),
  'entity.space_fabric.venus_rover':('Rover presurizado de Venus','Venus Pressurized Rover'),
  'key.space_fabric.rover_inventory':('Abrir inventario del rover','Open rover inventory'),
 }
 foundry={
  'no_site':('Sin instalación vinculada en Venus','No Venus installation linked'),
  'restored':('Instalación recuperada · recompensa entregada','Installation recovered · reward delivered'),
  'objective':('Repara, instala filtros y completa un lote','Repair, fit filters and complete one batch'),
  'unit_0':('1. Generador geotérmico','1. Geothermal generator'),
  'unit_1':('2. Unidad de refrigeración','2. Cooling unit'),
  'unit_2':('3. Procesador de azufre','3. Sulfur processor'),
  'status_missing':('Falta el equipo en su posición original','Missing unit at its original position'),
  'status_unloaded':('Acércate al equipo: zona descargada','Approach the unit: chunk unloaded'),
  'status_repair':('Repara hasta superar el 70 % de integridad','Repair to at least 70% integrity'),
  'status_filter':('Instala un filtro contra gases corrosivos','Install an active corrosive gas filter'),
  'status_run':('Completa un lote en este equipo','Complete one batch in this unit'),
  'status_ready':('Equipo preparado','Unit ready'),
  'activate':('Reactivar instalación y recoger recompensa','Reactivate installation and claim reward'),
  'activated':('Fundición recuperada. Recibes un rover y cuatro placas de compuesto.','Foundry recovered. You receive a rover and four composite plates.'),
 }
 rover={
  'telemetry':('Batería %s · O₂ %s · Refrigerante %s · Casco %s%%','Battery %s · O₂ %s · Coolant %s · Hull %s%%'),
  'warning':('Cabina sin protección · Batería %s · O₂ %s · Refrigerante %s · Casco %s%%','Cabin unprotected · Battery %s · O₂ %s · Coolant %s · Hull %s%%'),
  'controls':('W/S: avanzar/retroceder · A/D: girar · Espacio: frenar · V: inventario','W/S: drive/reverse · A/D: steer · Space: brake · V: inventory'),
  'service':('Repara con placas de compuesto. Sin ocupante: Shift + golpe para recoger.','Repair with composite plates. Unoccupied: Shift + attack to pick up.'),
  'board':('Subir','Board'),'cargo':('Carga','Cargo'),'slots':('Batería / O₂ / Refrigerante → envases','Power / O₂ / Coolant → empties'),
  'slot_0':('Baterías cargadas: +2.400 de energía','Charged power cells: +2,400 energy'),
  'slot_1':('Bombonas de oxígeno: +4.800 de reserva','Oxygen canisters: +4,800 reserve'),
  'slot_2':('Agua: +2.400; refrigerante enfriado: +4.800','Water: +2,400; chilled coolant: +4,800'),
  'reserve_0':('Batería: %s/24.000','Battery: %s/24,000'),'reserve_1':('O₂: %s/9.600','O₂: %s/9,600'),'reserve_2':('Refrigerante: %s/9.600','Coolant: %s/9,600'),
 }
 messages.update({'foundry.space_fabric.'+k:v for k,v in foundry.items()});messages.update({'rover.space_fabric.'+k:v for k,v in rover.items()})
 messages['advancements.space_fabric.rover.title']=('Venus sobre ruedas','Venus on wheels')
 messages['advancements.space_fabric.rover.description']=('Obtén un rover presurizado para explorar Venus.','Obtain a pressurized rover to explore Venus.')
 write(D/'advancement/industry/rover.json',{'parent':'space_fabric:industry/composite','display':{'icon':{'id':sf('venus_rover')},
       'title':{'translate':'advancements.space_fabric.rover.title'},'description':{'translate':'advancements.space_fabric.rover.description'},
       'frame':'task','show_toast':True,'announce_to_chat':False,'hidden':False},
       'criteria':{'obtained':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':[sf('venus_rover')]}]}}},
       'requirements':[['obtained']],'rewards':{'recipes':[sf(n) for n in ['foundry_console','venus_cooling_unit','venus_rover']]}})
 for locale,i in [('es_es',0),('en_us',1)]:
  p=A/f'lang/{locale}.json';lang=json.loads(p.read_text());lang.update({k:v[i] for k,v in messages.items()});write(p,lang)
 print('Generated recovery/cooling resources, three crafting recipes, menus and rover controls.')
if __name__=='__main__':main()
