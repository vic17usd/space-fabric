#!/usr/bin/env python3
"""Original compact mining textures, native models, recipes and bilingual instructions."""
from pathlib import Path
import json
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1]/'src/main/resources'
A=ROOT/'assets/space_fabric';D=ROOT/'data/space_fabric'
def write(p,d):
 p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
def tag(ns,name,values):
 p=ROOT/f'data/{ns}/tags/block/{name}.json';d=json.loads(p.read_text());d['values']=list(dict.fromkeys(d['values']+values));write(p,d)
# A dark composite casing, coolant channels, guarded drill and luminous control panel.
im=Image.new('RGBA',(32,32),(40,50,57));dr=ImageDraw.Draw(im)
dr.rectangle((0,0,31,31),outline=(163,177,178),width=2)
for x in (4,27):dr.line((x,3,x,29),fill=(69,185,197),width=2)
dr.rectangle((8,3,23,12),fill=(10,26,31),outline=(157,179,146))
for y in (6,9):dr.line((11,y,19,y),fill=(107,233,171),width=1)
dr.rectangle((8,15,23,28),fill=(17,21,26),outline=(184,142,59))
for y in range(16,28,3):dr.line((12,y,20,y+2),fill=(179,191,190),width=2)
im.save(A/'textures/block/venus_core_drill.png')
write(A/'models/block/venus_core_drill.json',{'parent':'minecraft:block/cube_all','textures':{'all':'space_fabric:block/venus_core_drill'}})
write(A/'blockstates/venus_core_drill.json',{'variants':{'':{'model':'space_fabric:block/venus_core_drill'}}})
write(A/'items/venus_core_drill.json',{'model':{'type':'minecraft:model','model':'space_fabric:block/venus_core_drill'}})
for name in ['venus_geological_scanner','venus_drill_bit']:
 im=Image.new('RGBA',(32,32));dr=ImageDraw.Draw(im)
 if name.endswith('scanner'):
  dr.rounded_rectangle((6,6,25,26),radius=3,fill=(69,86,94),outline=(192,200,190),width=2)
  dr.line((10,6,7,1),fill=(204,190,136),width=2)
  dr.rectangle((10,10,22,20),fill=(13,37,36),outline=(77,161,143))
  for r in (3,5):dr.ellipse((16-r,15-r,16+r,15+r),outline=(71,150,118))
  dr.line((16,15,20,12),fill=(128,246,173),width=1);dr.point((13,13),fill=(255,211,105))
  dr.rectangle((10,23,14,24),fill=(223,166,67));dr.rectangle((19,23,21,24),fill=(88,190,207))
 else:
  dr.rectangle((11,2,20,8),fill=(160,176,178),outline=(53,69,77));dr.rectangle((9,8,22,11),fill=(212,166,75))
  dr.polygon([(11,12),(20,12),(20,23),(15,30),(11,23)],fill=(139,166,173),outline=(41,60,74))
  for y in range(13,24,3):dr.line((11,y,20,y+3),fill=(211,225,221),width=2)
 im.save(A/f'textures/item/{name}.png')
 write(A/f'models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/'+name}})
 write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+name}})
entry={'type':'minecraft:item','name':'space_fabric:venus_core_drill','functions':[{'function':'minecraft:copy_custom_data','source':'block_entity','ops':[{'source':a,'target':b,'op':'replace'} for a,b in [('Maintenance','SpaceFabricMaintenance'),('IndustrialHeat','IndustrialHeat'),('ThermalTrip','ThermalTrip')]]}]}
write(D/'loot_table/blocks/venus_core_drill.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[entry],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
for ns,name in [('minecraft','mineable/pickaxe'),('minecraft','needs_iron_tool'),('space_fabric','pressure_rated_shell')]:tag(ns,name,['space_fabric:venus_core_drill'])
sf=lambda n:'space_fabric:'+n;mc=lambda n:'minecraft:'+n
def recipe(name,pattern,key):write(D/f'recipe/{name}.json',{'type':'minecraft:crafting_shaped','category':'misc','pattern':pattern,'key':key,'result':{'id':sf(name),'count':1}})
recipe('venus_geological_scanner',['CQC','PRP',' B '],{'C':mc('copper_ingot'),'Q':mc('quartz'),'P':sf('venus_composite_plate'),'R':mc('compass'),'B':sf('empty_power_cell')})
recipe('venus_drill_bit',[' D ','PDP',' C '],{'D':mc('diamond'),'P':sf('venus_composite_plate'),'C':sf('pyroxene_ceramic')})
recipe('venus_core_drill',['PCP','LML','HEH'],{'P':sf('venus_composite_plate'),'C':sf('pyroxene_ceramic'),'L':sf('liquid_conduit'),'M':mc('piston'),'H':sf('pressure_hull'),'E':sf('energy_conduit')})
write(D/'advancement/industry/mining.json',{'parent':'space_fabric:industry/thermal','criteria':{'mining':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':sf('venus_composite_plate')}]}}},'rewards':{'recipes':[sf(n) for n in ['venus_geological_scanner','venus_drill_bit','venus_core_drill']]}})
texts={
 'item.space_fabric.venus_geological_scanner':('Escáner geológico de Venus','Venus Geological Scanner'),
 'item.space_fabric.venus_drill_bit':('Broca de perforación venusiana','Venus Drill Bit'),
 'block.space_fabric.venus_core_drill':('Perforadora de sondeo de Venus','Venus Core Drill'),
 'block.space_fabric.venus_core_drill.hint':('Shift + clic vacío: activar. Broca, batería y refrigerante frío. Extrae minerales reales bajo un área de 3×3.','Sneak empty-click: enable. Bit, cell and chilled coolant. Extracts real minerals below a 3×3 area.'),
 'mining.space_fabric.scanner_hint':('Clic: sondeo de 9×9 y 64 bloques de profundidad; consume una batería. M → Importar para guardar hallazgos.','Click: survey 9×9 down 64 blocks; uses one cell. M → Import to save findings.'),
 'mining.space_fabric.bit_hint':('256 extracciones; no se consume entera por lote. Sin Fortuna ni Toque de seda.','256 extractions; wears instead of being consumed per batch. No Fortune or Silk Touch.'),
 'mining.space_fabric.venus_only':('El escáner solo funciona en Venus.','The scanner only works on Venus.'),
 'mining.space_fabric.busy':('Hay dos sondeos activos; vuelve a intentarlo en unos segundos.','Two surveys are active; try again in a few seconds.'),
 'mining.space_fabric.scan_power':('Necesitas una batería cargada en el inventario.','You need a charged power cell in your inventory.'),
 'mining.space_fabric.scan_start':('Sondeo geológico iniciado. Mantén el escáner en tu inventario.','Geological survey started. Keep the scanner in your inventory.'),
 'mining.space_fabric.scan_empty':('Sin minerales detectados. Celdas sin cargar: %s.','No minerals detected. Unloaded cells: %s.'),
 'mining.space_fabric.scan_done':('Sondeo terminado. M → Importar lee los puntos del escáner. Celdas sin cargar: %s.','Survey complete. M → Import reads scanner waypoints. Unloaded cells: %s.'),
 'mining.space_fabric.scan_found':('%s: %s bloques observados; muestra en %s (profundidad %s).','%s: %s observed blocks; sample at %s (depth %s).'),
 'mining.space_fabric.drill_enabled':('Perforadora activada. Alcance: 3×3, 96 bloques bajo la máquina.','Drill enabled. Range: 3×3, 96 blocks below the machine.'),
 'mining.space_fabric.drill_disabled':('Perforadora detenida; conserva el sondeo.','Drill stopped; survey progress retained.'),
 'mining.space_fabric.not_owner':('Solo quien colocó la perforadora puede activarla.','Only the player who placed the drill can enable it.'),
 'mining.space_fabric.browser':('Sondeo real: 1 mineral por bloque de mena, 10 s; broca pierde 1/256. Devuelve batería vacía y agua. No fabrica minerales sin yacimiento.','Real extraction: 1 mineral per ore block, 10 s; bit loses 1/256. Returns an empty cell and water. Requires an actual deposit.'),
 'colony.space_fabric.drill_off':('Perforadora apagada · Shift + clic','Drill off · Sneak-click'),
 'colony.space_fabric.drill_search':('Buscando mena bajo la máquina','Searching for ore below'),
 'colony.space_fabric.drill_depleted':('Sondeo agotado · cambia de lugar','Survey exhausted · relocate drill'),
 'colony.space_fabric.drill_protected':('Extracción bloqueada por protección','Extraction denied by protection'),
 'colony.space_fabric.drill_changed':('El bloque cambió · reintentando','Block changed · retrying'),
 'colony.space_fabric.drill_owner':('Propietario desconectado o fuera de Venus','Owner offline or outside Venus'),
 'colony.space_fabric.drill_redstone':('Pausa por señal de redstone','Paused by redstone'),
}
for index,lang in enumerate(['es_es','en_us']):
 p=A/f'lang/{lang}.json';d=json.loads(p.read_text());d.update({k:v[index] for k,v in texts.items()});write(p,d)
