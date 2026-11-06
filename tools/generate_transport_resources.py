#!/usr/bin/env python3
"""Original industrial conduit, tank and sealed coupling art, recipes and bilingual UI."""
from pathlib import Path
import json
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1]/'src/main/resources';A=ROOT/'assets/space_fabric';D=ROOT/'data/space_fabric'
CHANNELS={'liquid':((64,167,224),('líquidos','Liquid')),'gas':((82,202,153),('gases','Gas')),'energy':((239,181,65),('energía','Energy'))}
def write(path,data):path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(data,indent=2,ensure_ascii=False)+'\n')
def tag(ns,kind,name,values):
 p=ROOT/f'data/{ns}/tags/{kind}/{name}.json';data=json.loads(p.read_text()) if p.exists() else {'replace':False,'values':[]};data['values']=list(dict.fromkeys(data['values']+values));write(p,data)
def texture(name,channel,design):
 c=CHANNELS[channel][0];im=Image.new('RGBA',(32,32),(56,70,81,255));d=ImageDraw.Draw(im)
 d.rectangle((1,1,30,30),outline=(167,190,196),width=1);d.rectangle((3,3,28,28),outline=(22,33,43),width=1)
 for x in [3,28]:
  for y in [3,28]:d.point((x,y),fill=(219,224,210))
 if design=='pipe':
  d.rectangle((5,0,10,31),fill=c);d.rectangle((21,0,26,31),fill=c)
  for y in [4,12,20,28]:d.line((11,y,20,y),fill=(136,151,157),width=2)
 elif design in ['open','closed']:
  d.ellipse((6,6,25,25),fill=(15,26,34),outline=c,width=3)
  if design=='closed':d.line((9,10,22,21),fill=(234,106,78),width=4)
  else:d.line((11,16,20,16),fill=c,width=2)
 elif design=='tank':
  d.rectangle((5,7,26,25),fill=(22,36,44),outline=c,width=2)
  if channel=='liquid':d.polygon([(16,10),(10,18),(12,21),(20,21),(22,18)],fill=c)
  elif channel=='gas':
   d.line((11,13,21,19,11,20,11,13),fill=c,width=2)
   for x,y in [(11,13),(21,19),(11,20)]:d.ellipse((x-2,y-2,x+2,y+2),fill=c)
  else:d.polygon([(17,10),(11,17),(16,17),(14,23),(22,15),(17,15)],fill=c)
 elif design=='target':
  d.rectangle((6,6,25,25),fill=(16,24,30),outline=c,width=3);d.rectangle((11,11,20,20),outline=(222,228,208),width=2)
 elif design=='port':
  for i,ch in enumerate(['liquid','gas','energy']):d.rectangle((7,6+i*7,24,9+i*7),fill=CHANNELS[ch][0])
  d.polygon([(12,27),(20,27),(16,30)],fill=(215,229,222))
 else:
  d.rectangle((5,5,26,26),outline=c,width=2);d.line((8,16,23,16),fill=c,width=4)
 p=A/f'textures/block/{name}.png';p.parent.mkdir(parents=True,exist_ok=True);im.resize((64,64),Image.Resampling.NEAREST).save(p)
def cuboid(lo,hi,texture_name):
 return {'from':lo,'to':hi,'faces':{face:{'texture':'#all','uv':[0,0,16,16]} for face in ['down','up','north','south','east','west']}}
def model(name,elements,tex):write(A/f'models/block/{name}.json',{'textures':{'all':'space_fabric:block/'+tex,'particle':'#all'},'elements':elements})
names=[];rated=[];translations={}
for channel,(color,labels) in CHANNELS.items():
 pipe=channel+'_conduit';valve=channel+'_bulkhead';tank=channel+'_tank';names.extend([pipe,valve,tank]);rated.extend([valve,tank])
 texture(pipe,channel,'pipe');texture(valve,channel,'side');texture(valve+'_open',channel,'open');texture(valve+'_closed',channel,'closed');texture(tank,channel,'tank')
 core=cuboid([5,5,5],[11,11,11],pipe);model(pipe,[core],pipe)
 arms={'down':([5,0,5],[11,5,11]),'up':([5,11,5],[11,16,11]),'north':([5,5,0],[11,11,5]),'south':([5,5,11],[11,11,16]),'west':([0,5,5],[5,11,11]),'east':([11,5,5],[16,11,11])}
 parts=[{'apply':{'model':'space_fabric:block/'+pipe}}]
 for direction,(lo,hi) in arms.items():
  model(pipe+'_'+direction,[cuboid(lo,hi,pipe)],pipe)
  parts.append({'when':{direction:'true'},'apply':{'model':'space_fabric:block/'+pipe+'_'+direction}})
 write(A/f'blockstates/{pipe}.json',{'multipart':parts})
 icon={'textures':{'all':'space_fabric:block/'+pipe,'particle':'#all'},'elements':[core,cuboid(*arms['north'],pipe),cuboid(*arms['south'],pipe)],'display':{'gui':{'rotation':[30,225,0],'scale':[1.15]*3}}}
 write(A/f'models/item/{pipe}.json',icon)
 write(A/f'items/{pipe}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+pipe}})
 for suffix in ['', '_closed']:
  end=valve+('_open' if not suffix else '_closed')
  write(A/f'models/block/{valve+suffix}.json',{'parent':'minecraft:block/cube','textures':{'particle':'space_fabric:block/'+valve,
        **{face:'space_fabric:block/'+(end if face in ['north','south'] else valve) for face in ['down','up','north','south','east','west']}}})
 variants={}
 for axis in ['x','y','z']:
  for enabled in [False,True]:
   for powered in [False,True]:
    v={'model':'space_fabric:block/'+valve+('' if enabled and not powered else '_closed')}
    if axis=='x':v['y']=90
    if axis=='y':v['x']=90
    variants[f'axis={axis},enabled={str(enabled).lower()},powered={str(powered).lower()}']=v
 write(A/f'blockstates/{valve}.json',{'variants':variants})
 model(tank,[cuboid([0,0,0],[16,16,16],tank)],tank)
 write(A/f'blockstates/{tank}.json',{'variants':{'':{'model':'space_fabric:block/'+tank}}})
 for name in [valve,tank]:write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'space_fabric:block/'+name}})
 translations['block.space_fabric.'+pipe]=('Cable de energía' if channel=='energy' else 'Tubería de '+labels[0],labels[1]+' Cable' if channel=='energy' else labels[1]+' Pipe')
 translations['block.space_fabric.'+valve]=('Pasamuros eléctrico' if channel=='energy' else 'Pasamuros para '+labels[0],labels[1]+' Sealed Bulkhead')
 translations['block.space_fabric.'+tank]=('Acumulador eléctrico' if channel=='energy' else 'Depósito de '+labels[0],labels[1]+' Accumulator' if channel=='energy' else labels[1]+' Tank')
names.append('machine_interface');rated.append('machine_interface');texture('machine_interface','energy','port')
texture('machine_interface_target','energy','target')
write(A/'models/block/machine_interface.json',{'parent':'minecraft:block/cube','textures':{'particle':'space_fabric:block/machine_interface',**{face:'space_fabric:block/machine_interface'+('_target' if face=='north' else '') for face in ['down','up','north','south','east','west']}}})
write(A/'blockstates/machine_interface.json',{'variants':{f'facing={face},powered={str(powered).lower()}':{'model':'space_fabric:block/machine_interface',**({'y':rot} if rot else {}),**({'x':x} if x else {})}
       for face,rot,x in [('north',0,0),('east',90,0),('south',180,0),('west',270,0),('up',0,270),('down',0,90)] for powered in [False,True]}})
write(A/'items/machine_interface.json',{'model':{'type':'minecraft:model','model':'space_fabric:block/machine_interface'}})
for name in names:
 entry={'type':'minecraft:item','name':'space_fabric:'+name}
 if name.endswith('_tank'):entry['functions']=[{'function':'minecraft:copy_custom_data','source':'block_entity','ops':[{'source':'TransportTank','target':'TransportTank','op':'replace'}]}]
 write(D/f'loot_table/blocks/{name}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[entry],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
tag('minecraft','block','mineable/pickaxe',['space_fabric:'+n for n in names])
tag('minecraft','block','needs_iron_tool',['space_fabric:'+n for n in rated])
tag('space_fabric','block','pressure_rated_shell',['space_fabric:'+n for n in rated])
sf=lambda n:'space_fabric:'+n;mc=lambda n:'minecraft:'+n
def shaped(name,pattern,key,count=1):write(D/f'recipe/{name}.json',{'type':'minecraft:crafting_shaped','category':'misc','pattern':pattern,'key':key,'result':{'id':sf(name),'count':count}})
for channel in CHANNELS:
 pipe=channel+'_conduit'
 if channel=='energy':shaped(pipe,['CRC'],{'C':mc('copper_ingot'),'R':mc('redstone')},8)
 else:shaped(pipe,['ICI',' G ','ICI'],{'I':mc('iron_ingot'),'C':mc('copper_ingot'),'G':mc('glass') if channel=='liquid' else mc('quartz')},8)
 shaped(channel+'_bulkhead',['HPH','PCP','HPH'],{'H':sf('pressure_hull'),'P':sf(pipe),'C':sf('venus_composite_plate')},2)
 shaped(channel+'_tank',['HPH','GCG','HPH'],{'H':sf('pressure_hull'),'P':sf(pipe),'G':mc('glass'),'C':sf('venus_composite_plate')})
shaped('machine_interface',['CRC','PHP','CRC'],{'C':mc('copper_ingot'),'R':mc('redstone'),'P':sf('pyroxene_ceramic'),'H':sf('pressure_hull')})
translations['block.space_fabric.machine_interface']=('Interfaz de máquinas','Machine Interface')
TEXT={
 'stored':('%s · %s/%s unidades','%s · %s/%s units'),'network':('Red de %s · %s nodos · %s unidades','%s network · %s nodes · %s units'),
 'resource_none':('Sin fijar','Unassigned'),'resource_water':('Agua','Water'),'resource_chilled_coolant':('Refrigerante enfriado','Chilled coolant'),
 'resource_oxygen':('Oxígeno','Oxygen'),'resource_hydrogen':('Hidrógeno','Hydrogen'),'resource_venus_atmosphere':('Atmósfera venusiana','Venus atmosphere'),
 'resource_carbon_dioxide':('Dióxido de carbono','Carbon dioxide'),'resource_nitrogen':('Nitrógeno','Nitrogen'),'resource_power':('Energía','Energy'),
 'graph_no_storage':('Red sin depósito conectado','No storage connected'),'graph_unloaded':('Red en chunks descargados','Network crosses unloaded chunks'),
 'graph_too_large':('Red mayor de 128 nodos: divídela','More than 128 nodes: split this network'),'graph_mixed':('Contenidos incompatibles: separa las líneas','Incompatible contents: separate the lines'),
 'valve_open':('Paso abierto; la pared sigue sellada','Flow open; the wall stays sealed'),'valve_closed':('Paso cerrado: transferencia detenida','Flow closed: transfer stopped'),
 'target_face':('Máquina en dirección: %s','Target machine direction: %s'),
 'face_north':('norte','north'),'face_south':('sur','south'),'face_east':('este','east'),'face_west':('oeste','west'),'face_up':('arriba','up'),'face_down':('abajo','down'),
 'select_content':('Cambiar contenido (solo vacío)','Change content (only when empty)'),
 'drain_container':('Modo: vaciar envases en el depósito','Mode: empty containers into tank'),'fill_container':('Modo: llenar envases desde el depósito','Mode: fill containers from tank'),
 'tank_status_0':('En espera','Idle'),'tank_status_1':('Coloca un envase compatible','Insert a compatible container'),
 'tank_status_2':('Retira los objetos de la salida','Clear the output slot'),'tank_status_3':('Contenido o envase incompatible','Incompatible content or container'),
 'tank_status_4':('Depósito lleno','Storage full'),'tank_status_5':('No hay suficiente contenido','Insufficient contents'),'tank_status_6':('Envase procesado','Container processed'),
 'port_idle':('En espera','Idle'),'port_disabled':('Pausada o con señal de redstone','Paused or powered by redstone'),
 'port_no_machine':('Orienta la interfaz hacia una máquina','Point this interface at a machine'),'port_broken':('Repara la máquina antes de alimentarla','Repair the machine before supplying it'),
 'port_materials':('Revisa los materiales de la receta','Check the recipe materials'),'port_containers':('Faltan envases vacíos en la interfaz','Add empty containers to this interface'),
 'port_resource':('Esperando agua, gases o energía','Waiting for water, gases or power'),'port_space':('Libera salidas o amplía los depósitos','Clear outputs or add storage'),
 'port_working':('Suministros preparados','Supplies ready'),'port_unloaded':('Red en chunks descargados','Network crosses unloaded chunks'),
 'port_too_large':('Red mayor de 128 nodos: divídela','More than 128 nodes: split this network'),'port_mixed':('Contenidos incompatibles en la línea','Incompatible contents on this line'),
 'port_no_storage':('Conecta un depósito compatible','Connect compatible storage'),
 'recipe_auto':('Proceso: automático','Process: automatic'),'recipe_fixed':('Proceso: seleccionado','Process: selected'),
 'return_reserve':('Envases vacíos','Empty containers'),'reserve_0':('Bombonas vacías para alimentar y reciclar','Empty canisters for supply and recycling'),
 'reserve_1':('Baterías descargadas para alimentar y reciclar','Empty power cells for supply and recycling'),
 'pause':('Pausar interfaz','Pause interface'),'resume':('Activar interfaz','Enable interface'),'life_priority':('Prioridad: soporte vital','Life support first')}
translations.update({'transport.space_fabric.'+k:v for k,v in TEXT.items()})
translations['advancements.space_fabric.transport.title']=('Industria conectada','Connected industry')
translations['advancements.space_fabric.transport.description']=('Fabrica una interfaz para conectar máquinas y depósitos.','Craft an interface to connect machines and storage.')
for locale,i in [('es_es',0),('en_us',1)]:
 p=A/f'lang/{locale}.json';lang=json.loads(p.read_text());lang.update({k:v[i] for k,v in translations.items()});write(p,lang)
write(D/'advancement/industry/transport.json',{'parent':'space_fabric:industry/composite','display':{'icon':{'id':sf('machine_interface')},'title':{'translate':'advancements.space_fabric.transport.title'},'description':{'translate':'advancements.space_fabric.transport.description'},'frame':'task','show_toast':True,'announce_to_chat':False,'hidden':False},'criteria':{'obtained':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':[sf('machine_interface')]}]}}},'requirements':[['obtained']],'rewards':{'recipes':[sf(n) for n in names]}})
print('Generated 10 transport blocks, original textures, connected pipe models, sealed bulkheads, tank loot, recipes and translations.')
