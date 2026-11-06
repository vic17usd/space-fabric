#!/usr/bin/env python3
"""Original colony machinery, aerostat geometry and compact procedural research sites."""
from pathlib import Path
import json,gzip,math
from PIL import Image,ImageDraw
from convert_creativemode_structure import convert
from venus_station_interiors import station
ROOT=Path(__file__).resolve().parents[1]/'src/main/resources';A=ROOT/'assets/space_fabric';D=ROOT/'data/space_fabric'
def write(p,data):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
def tag(ns,kind,name,values):
 p=ROOT/f'data/{ns}/tags/{kind}/{name}.json';d=json.loads(p.read_text()) if p.exists() else {'replace':False,'values':[]};d['values']=list(dict.fromkeys(d['values']+values));write(p,d)
names={'venus_greenhouse':('Módulo de invernadero','Greenhouse Module',(80,195,113)),'venus_life_reclaimer':('Recuperador de soporte vital','Life Support Reclaimer',(74,186,203)),'venus_service_dock':('Estación de servicio','Vehicle Service Dock',(223,166,70)),'venus_expedition_console':('Consola de expediciones','Expedition Console',(172,150,220)),'venus_grow_lamp':('Lámpara de cultivo','Grow Lamp',(221,238,191))}
for index,(name,(es,en,color)) in enumerate(names.items()):
 im=Image.new('RGBA',(32,32),(42,56,65,255));dr=ImageDraw.Draw(im);dr.rectangle((1,1,30,30),outline=(179,199,197),width=2);dr.rectangle((5,5,26,26),fill=(12,25,32),outline=color,width=2)
 if index==0:
  for x in (10,16,22):dr.line((x,24,x,12),fill=color,width=2);dr.polygon([(x,18),(x-3,14),(x,15),(x+3,11)],fill=color)
 elif index==1:
  dr.arc((8,8,24,24),30,300,fill=color,width=3);dr.polygon([(23,9),(23,16),(17,12)],fill=color)
 elif index==2:
  dr.line((9,10,9,21,23,21,23,10),fill=color,width=3);dr.rectangle((11,8,21,15),outline=color,width=2)
 elif index==3:
  for y in (10,15,20):dr.line((9,y,23,y),fill=color,width=2)
 else:
  for y in range(7,26,5):dr.line((7,y,25,y),fill=color,width=3)
 im.resize((64,64),Image.Resampling.NEAREST).save(A/f'textures/block/{name}.png')
 write(A/f'models/block/{name}.json',{'parent':'minecraft:block/cube_all','textures':{'all':'space_fabric:block/'+name}})
 write(A/f'blockstates/{name}.json',{'variants':{'':{'model':'space_fabric:block/'+name}}})
 write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'space_fabric:block/'+name}})
 entry={'type':'minecraft:item','name':'space_fabric:'+name}
 if index<4:entry['functions']=[{'function':'minecraft:copy_custom_data','source':'block_entity','ops':[{'source':src,'target':dst,'op':'replace'} for src,dst in [('Maintenance','SpaceFabricMaintenance'),('IndustrialHeat','IndustrialHeat'),('ThermalTrip','ThermalTrip'),('CoolingCharge','CoolingCharge')]]}]
 write(D/f'loot_table/blocks/{name}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[entry],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
 for ns,path in [('minecraft','mineable/pickaxe'),('minecraft','needs_iron_tool'),('space_fabric','pressure_rated_shell')]:tag(ns,'block',path,['space_fabric:'+name])
sf=lambda n:'space_fabric:'+n;mc=lambda n:'minecraft:'+n
def shaped(n,p,k):write(D/f'recipe/{n}.json',{'type':'minecraft:crafting_shaped','category':'misc','pattern':p,'key':k,'result':{'id':sf(n),'count':1}})
shaped('venus_greenhouse',['GLG','PWP','HBH'],{'G':mc('glass'),'L':sf('venus_grow_lamp'),'P':sf('venus_composite_plate'),'W':mc('wheat_seeds'),'H':sf('pressure_hull'),'B':sf('empty_power_cell')})
shaped('venus_life_reclaimer',['PCP','LFL','HBH'],{'P':sf('venus_composite_plate'),'C':mc('copper_ingot'),'L':sf('liquid_conduit'),'F':sf('acid_filter'),'H':sf('pressure_hull'),'B':sf('empty_power_cell')})
shaped('venus_service_dock',['PEP','LIL','HHH'],{'P':sf('venus_composite_plate'),'E':sf('energy_conduit'),'L':sf('liquid_conduit'),'I':sf('machine_interface'),'H':sf('pressure_hull')})
shaped('venus_expedition_console',['GRG','PCP','HHH'],{'G':mc('glass'),'R':sf('venus_research_report'),'P':sf('venus_composite_plate'),'C':mc('redstone'),'H':sf('pressure_hull')})
shaped('venus_grow_lamp',['GCG','RLR','GCG'],{'G':mc('glass'),'C':mc('copper_ingot'),'R':mc('redstone'),'L':mc('glowstone')})
shaped('venus_aerostat',['PPP','TRT','BCB'],{'P':sf('venus_composite_plate'),'T':sf('empty_canister'),'R':sf('venus_rover'),'B':sf('charged_power_cell'),'C':sf('venus_cooling_unit')})
# The rover ingredient must be pristine: a normal shapeless input would destroy saved vehicle data.
# Use a chassis-free bill of materials instead, keeping existing vehicles untouched.
p=D/'recipe/venus_aerostat.json';data=json.loads(p.read_text());data['key']['R']=sf('venus_research_report');write(p,data)
# Original capsule with two-sided inner walls, a visor and a buoyancy envelope, no wheel substitutions.
body=[];glass=[]
def box(out,lo,hi,color=-1):
 x,y,z=lo;X,Y,Z=hi
 faces=[((1,0,0),[(X,y,z),(X,Y,z),(X,Y,Z),(X,y,Z)]),((-1,0,0),[(x,y,Z),(x,Y,Z),(x,Y,z),(x,y,z)]),((0,1,0),[(x,Y,z),(x,Y,Z),(X,Y,Z),(X,Y,z)]),((0,-1,0),[(x,y,Z),(x,y,z),(X,y,z),(X,y,Z)]),((0,0,1),[(X,y,Z),(X,Y,Z),(x,Y,Z),(x,y,Z)]),((0,0,-1),[(x,y,z),(x,Y,z),(X,Y,z),(X,y,z)])]
 for normal,vertices in faces:
  for reverse in (False,True):
   v=vertices[::-1] if reverse else vertices;n=[-a for a in normal] if reverse else normal;q=list(n)+[color]
   for pos,uv in zip(v,[(.05,.05),(.05,.95),(.95,.95),(.95,.05)]):q+=list(pos)+list(uv)
   out.append(q)
box(body,(-1.35,0,-1.8),(1.35,.25,1.8));box(body,(-1.35,2.5,-1.8),(1.35,2.7,1.8))
box(body,(-1.35,.25,-1.8),(-1.15,2.5,1.8));box(body,(1.15,.25,-1.8),(1.35,2.5,1.8));box(body,(-1.35,.25,-1.8),(1.35,2.5,-1.6));box(body,(-1.35,.25,1.6),(1.35,1.35,1.8));box(glass,(-1.15,1.35,1.6),(1.15,2.5,1.68))
box(body,(-.55,.25,.8),(.55,.55,1.5));box(body,(-.55,.55,.65),(.55,1.35,.85))
for x in (-1.1,1.1):box(body,(x-.06,2.7,-1),(x+.06,4,1))
for lo,hi in [((-2,3.8,-2.2),(2,6.9,2.2)),((-2.8,4.4,-1.7),(2.8,6.3,1.7)),((-1.6,4.4,-2.7),(1.6,6.3,2.7))]:box(body,lo,hi,-2639004)
for part,quads in [('body',body),('glass',glass)]:write(A/f'model_mesh/venus_aerostat_{part}.json',{'texture':'space_fabric:textures/entity/venus_aerostat'+('_glass' if part=='glass' else '')+'.png','pivot':[0,0,0],'quads':quads})
for glasspart in (False,True):
 im=Image.new('RGBA',(32,32),(124,189,204,65) if glasspart else (174,188,183,255));dr=ImageDraw.Draw(im)
 dr.rectangle((0,0,31,31),outline=(43,68,78,210),width=2)
 if not glasspart:
  for y in (8,16,24):dr.line((0,y,31,y),fill=(75,102,114),width=1)
 im.save(A/('textures/entity/venus_aerostat'+('_glass.png' if glasspart else '.png')))
# A bespoke item icon, with no data-bearing vehicle consumed in its crafting recipe.
im=Image.new('RGBA',(32,32));dr=ImageDraw.Draw(im);dr.ellipse((3,2,28,18),fill=(215,190,120),outline=(61,78,86),width=2);dr.line((9,16,11,25,22,25,24,16),fill=(130,149,156),width=2);dr.rectangle((10,22,23,29),fill=(86,124,141),outline=(215,224,222));im.resize((64,64),Image.Resampling.NEAREST).save(A/'textures/item/venus_aerostat.png')
write(A/'models/item/venus_aerostat.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/venus_aerostat'}});write(A/'items/venus_aerostat.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/venus_aerostat'}})
# Compact research stations, with native template NBT and no executable imported content.
SITES=[('venus_research_station','laboratorio_venusiano',1),('venus_recovery_shelter','recuperacion_venusiana',2),('venus_cloud_station','estacion_atmosferica',3)]
for name,alias,mission in SITES:
 cloud=mission==3;n=29 if cloud else 13
 blocks,extra,properties=station(mission)
 palette=list(dict.fromkeys(blocks.values()));height=12 if cloud else 7
 doc={'format':'creativemode.structure','version':1,'palette':palette,'blocks':[[*pos,palette.index(block)] for pos,block in sorted(blocks.items())],'bounds':{'min':[0,0,0],'max':[n-1,height-1,n-1],'dimensions':[n,height,n]},'anchor':[0,0,0],'block_count':len(blocks)}
 out=D/f'structure/venus/colony/{name}.nbt';out.parent.mkdir(parents=True,exist_ok=True);out.write_bytes(gzip.compress(convert(doc,block_entities=extra,block_states=properties),mtime=0))
 tag('space_fabric','worldgen/biome','has_structure/'+name,['space_fabric:venus','space_fabric:venus_fumaroles','space_fabric:venus_obsidian_basins'])
 tag('space_fabric','worldgen/structure',alias,['space_fabric:'+name])
 settings={'type':'minecraft:jigsaw' if cloud else 'space_fabric:venus_surface','biomes':'#space_fabric:has_structure/'+name,'step':'surface_structures','spawn_overrides':{},'terrain_adaptation':'none'}
 if cloud:
  settings.update({'start_pool':'space_fabric:venus/cloud_station','size':1,'start_height':{'absolute':301},'max_distance_from_center':40,'use_expansion_hack':False})
  write(D/'worldgen/template_pool/venus/cloud_station.json',{'name':'space_fabric:venus/cloud_station','fallback':'minecraft:empty','elements':[{'weight':1,'element':{'element_type':'minecraft:single_pool_element','location':f'space_fabric:venus/colony/{name}','processors':'minecraft:empty','projection':'rigid'}}]})
 else:settings.update({'width':n,'height':height,'depth':n,'padding':8,'max_terrain_relief':8,'tiles':[{'template':f'space_fabric:venus/colony/{name}','x':0,'z':0}]})
 write(D/f'worldgen/structure/{name}.json',settings)
 write(D/f'worldgen/structure_set/{name}.json',{'structures':[{'structure':'space_fabric:'+name,'weight':1}],'placement':{'type':'minecraft:random_spread','spacing':96 if cloud else 80,'separation':48 if cloud else 40,'salt':774291+mission}})
# Crafting unlocks and Spanish/English usage.
write(D/'advancement/industry/venus_colony.json',{'parent':'space_fabric:industry/thermal','criteria':{'colony':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':'space_fabric:venus_greenhouse'}]}}},'rewards':{'recipes':['space_fabric:'+n for n in list(names)+['venus_aerostat']]}})
texts={**{'block.space_fabric.'+name:(es,en) for name,(es,en,c) in names.items()},'item.space_fabric.venus_aerostat':('Aerostato presurizado de Venus','Venus Pressurized Aerostat'),'entity.space_fabric.venus_aerostat':('Aerostato presurizado de Venus','Venus Pressurized Aerostat'),
 'block.space_fabric.venus_greenhouse.hint':('Dentro de un hábitat operativo y con luz 9+. Semilla/CO₂, batería y agua.','Requires an operating habitat and light 9+. Seed/CO₂, battery and water.'),
 'block.space_fabric.venus_life_reclaimer.hint':('Recupera 4800 unidades de respiración: dos bombonas vacías, batería y filtro.','Recovers 4800 respiration units: two empty canisters, a cell and a filter.'),
 'block.space_fabric.venus_service_dock.hint':('Shift + clic activa. Entradas: batería/O₂/refrigerante. Casilla 9: placas de reparación.','Sneak-click enables. Inputs: power/O₂/coolant. Slot 9: repair plates.'),
 'block.space_fabric.venus_expedition_console.hint':('Entradas: informe, batería y tarjeta. Shift + clic revisa o reclama la expedición.','Inputs: report, cell and data card. Sneak-click reviews or claims an expedition.'),
 'expansion.space_fabric.aerial_controls':('Solo Venus · W/S: vuelo según la mirada · A/D: girar · Espacio: subir · V: inventario','Venus only · W/S: fly along view · A/D: turn · Space: ascend · V: inventory'),
 'colony.space_fabric.greenhouse_room':('Necesita hábitat operativo y sellado','Needs an operating sealed habitat'),'colony.space_fabric.greenhouse_light':('Necesita iluminación de cultivo (9+)','Needs growing light (9+)'),'colony.space_fabric.recovery_wait':('Espera 4800 unidades de respiración','Waiting for 4800 respiration units'),
 'colony.space_fabric.dock_off':('Estación apagada · Shift + clic para activar','Dock disabled · Sneak-click to enable'),'colony.space_fabric.dock_wait':('Espera vehículo aparcado y desocupado','Waiting for a parked unoccupied vehicle'),'colony.space_fabric.dock_ready':('Vehículo atendido','Vehicle serviced'),'colony.space_fabric.survey_ready':('Consola · Shift + clic para revisar misión','Console · Sneak-click to review mission'),
 'expedition.space_fabric.no_site':('Consola sin expedición natural vinculada','Console has no natural expedition site'),'expedition.space_fabric.claimed':('Ya completaste esta expedición','You already completed this expedition'),'expedition.space_fabric.objective':('Misión %s: repara %s junto a la consola al 70%% y completa un lote. Carga informe venusiano, batería y tarjeta en las tres entradas.','Mission %s: repair the adjacent %s to 70%% and run one batch. Load a Venus report, charged cell and data card in the three inputs.'),'expedition.space_fabric.success':('Expedición %s completada. Recompensa entregada.','Expedition %s complete. Reward delivered.'),'expedition.space_fabric.guide':('Busca Laboratorio de Venus, Refugio de recuperación y Estación atmosférica con /estructuras. Repara su máquina, ejecuta un lote y entrega informe, batería y tarjeta en la consola. Shift + clic para reclamar. Una recompensa por tipo y jugador.','Find Venus Laboratory, Recovery Shelter and Cloud Station using /estructuras. Repair the machine, run one batch, and load a report, charged cell and data card in the console. Sneak-click to claim. One reward per type per player.'),
 'structure.space_fabric.venus_research_station':('Laboratorio de Venus','Venus Research Laboratory'),'structure.space_fabric.venus_recovery_shelter':('Refugio de recuperación de Venus','Venus Recovery Shelter'),'structure.space_fabric.venus_cloud_station':('Estación atmosférica de Venus','Venus Cloud Station')}
for i,lang in enumerate(['es_es','en_us']):
 p=A/f'lang/{lang}.json';d=json.loads(p.read_text());d.update({k:v[i] for k,v in texts.items()});write(p,d)
