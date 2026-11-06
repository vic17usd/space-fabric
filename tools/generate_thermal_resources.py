#!/usr/bin/env python3
"""Original thermal exchanger art and integration resources for the colony machinery."""
from pathlib import Path
import json
from PIL import Image, ImageDraw
ROOT=Path(__file__).resolve().parents[1]/'src/main/resources'
A=ROOT/'assets/space_fabric';D=ROOT/'data/space_fabric';NAME='venus_heat_exchanger'
def write(p,data):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
def texture(name,front):
 im=Image.new('RGBA',(32,32),(44,59,69,255));d=ImageDraw.Draw(im)
 d.rectangle((0,0,31,31),outline=(184,200,204),width=2)
 for x in (3,28):
  for y in (3,28):d.rectangle((x-1,y-1,x,y),fill=(207,177,99))
 if front:
  d.rectangle((6,5,25,26),fill=(15,24,30),outline=(85,180,213),width=2)
  for y in (9,13,17,21):d.line((9,y,22,y),fill=(135,205,216),width=2)
  d.rectangle((11,25,20,27),fill=(94,220,156))
 else:
  for y in (6,11,16,21):d.line((5,y,26,y),fill=(30,38,45),width=2)
  d.line((8,5,8,26),fill=(65,172,221),width=2);d.line((23,5,23,26),fill=(223,145,66),width=2)
 im.resize((64,64),Image.Resampling.NEAREST).save(A/f'textures/block/{name}.png')
texture(NAME,False);texture(NAME+'_front',True)
write(A/f'models/block/{NAME}.json',{'parent':'minecraft:block/cube','textures':{'all':f'space_fabric:block/{NAME}','particle':f'space_fabric:block/{NAME}',**{face:f'space_fabric:block/{NAME}'+('_front' if face in ('north','south') else '') for face in ('up','down','north','south','east','west')}}})
write(A/f'blockstates/{NAME}.json',{'variants':{'':{'model':f'space_fabric:block/{NAME}'}}})
write(A/f'items/{NAME}.json',{'model':{'type':'minecraft:model','model':f'space_fabric:block/{NAME}'}})
# Reuse the established machine mining contract, extending it with typed scalar thermal data only.
loot=json.loads((D/'loot_table/blocks/venus_cooling_unit.json').read_text())
loot['pools'][0]['entries'][0]['name']='space_fabric:'+NAME
write(D/f'loot_table/blocks/{NAME}.json',loot)
for p in (D/'loot_table/blocks').glob('*.json'):
 data=json.loads(p.read_text());changed=False
 for pool in data.get('pools',[]):
  for entry in pool.get('entries',[]):
   for fn in entry.get('functions',[]):
    if fn.get('function')=='minecraft:copy_custom_data' and any(op.get('source')=='Maintenance' for op in fn.get('ops',[])):
     for field in ('IndustrialHeat','ThermalTrip','CoolingCharge'):
      if not any(op.get('source')==field for op in fn['ops']):fn['ops'].append({'source':field,'target':field,'op':'replace'})
     changed=True
 if changed:write(p,data)
for namespace,tag in [('minecraft','mineable/pickaxe'),('minecraft','needs_iron_tool'),('space_fabric','pressure_rated_shell')]:
 p=ROOT/f'data/{namespace}/tags/block/{tag}.json';data=json.loads(p.read_text());value='space_fabric:'+NAME
 if value not in data['values']:data['values'].append(value)
 write(p,data)
write(D/f'recipe/{NAME}.json',{'type':'minecraft:crafting_shaped','category':'misc','pattern':['PCP','LUL','PBP'],'key':{'P':'space_fabric:venus_composite_plate','C':'minecraft:copper_ingot','L':'space_fabric:liquid_conduit','U':'space_fabric:venus_cooling_unit','B':'space_fabric:empty_power_cell'},'result':{'id':'space_fabric:'+NAME,'count':1}})
write(D/'advancement/industry/thermal.json',{'parent':'space_fabric:industry/transport','criteria':{'exchanger':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':'space_fabric:'+NAME}]}}},'rewards':{'recipes':['space_fabric:'+NAME]}})
text={
 'block.space_fabric.venus_heat_exchanger':('Intercambiador térmico','Thermal Exchanger'),
 'block.space_fabric.venus_heat_exchanger.hint':('Junto a una máquina o controlador. Consume refrigerante enfriado y energía; devuelve agua.','Place beside a machine or controller. Uses chilled coolant and power; returns water.'),
 'thermal.space_fabric.heat':('Calor: %s / 10000','Heat: %s / 10000'),
 'thermal.space_fabric.charge':('Frío: %s / 4800','Cooling: %s / 4800'),
 'thermal.space_fabric.warning':('Calor elevado: revisa la refrigeración','High heat: check cooling'),
 'thermal.space_fabric.trip':('Industria detenida por calor','Industrial thermal shutdown'),
 'thermal.space_fabric.normal':('Temperatura industrial recuperada','Industrial thermal recovery'),
 'thermal.space_fabric.notice':('%s (%s): %s','%s (%s): %s'),
 'colony.space_fabric.overheating':('Parada térmica: enfría hasta 5000','Thermal shutdown: cool to 5000'),
 'colony.space_fabric.cooling_ready':('Reserva de frío disponible','Cooling reserve available'),
 'transport.space_fabric.status_overheated':('Circuito industrial sobrecalentado','Industrial circuit overheated'),
}
for i,lang in enumerate(('es_es','en_us')):
 p=A/f'lang/{lang}.json';data=json.loads(p.read_text());data.update({k:v[i] for k,v in text.items()});write(p,data)
