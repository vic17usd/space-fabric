#!/usr/bin/env python3
"""Original colony artwork, recipes and datapack resources. Safe, deterministic regeneration."""
import json
from pathlib import Path
from zipfile import ZipFile
from PIL import Image,ImageDraw,ImageFont
from generate_material_resources import ROOT,write
A=ROOT/'assets/space_fabric';D='data/space_fabric/'
BLOCKS={
 'pressure_hull':('Panel presurizado','Pressure Hull'), 'pressure_window':('Ventana presurizada','Pressure Window'),
 'airlock_door':('Puerta de esclusa','Airlock Door'), 'martian_ground_ice':('Hielo subterráneo marciano','Martian Ground Ice'),
 'habitat_controller':('Controlador de hábitat','Habitat Controller'), 'water_electrolyzer':('Electrolizador de agua','Water Electrolyzer'),
 'solar_charger':('Cargador solar','Solar Charger'), 'geothermal_generator':('Generador geotérmico','Geothermal Generator'),
 'sample_analyzer':('Analizador de muestras','Sample Analyzer')}
ITEMS={
 'empty_canister':('Bombona vacía','Empty Canister'), 'oxygen_canister':('Bombona de oxígeno','Oxygen Canister'),
 'water_canister':('Bombona de agua','Water Canister'), 'hydrogen_canister':('Bombona de hidrógeno','Hydrogen Canister'),
 'empty_power_cell':('Batería descargada','Empty Power Cell'), 'charged_power_cell':('Batería cargada','Charged Power Cell'),
 'blank_data_card':('Tarjeta de datos vacía','Blank Data Card'), 'mars_research_report':('Informe de investigación marciana','Mars Research Report'),
 'venus_research_report':('Informe de investigación venusiana','Venus Research Report')}
COLORS={'habitat_controller':(45,205,155),'water_electrolyzer':(65,185,240),'solar_charger':(55,100,225),'geothermal_generator':(245,130,45),'sample_analyzer':(180,110,240)}
def save(im,kind,name):
 p=A/f'textures/{kind}/{name}.png';p.parent.mkdir(parents=True,exist_ok=True);im.save(p)
def panel():
 im=Image.new('RGBA',(64,64),(65,77,88,255));d=ImageDraw.Draw(im)
 d.rectangle((2,2,61,61),outline=(155,177,188),width=2);d.rectangle((5,5,58,58),outline=(30,39,50),width=2)
 for x in [8,55]:
  for y in [8,55]:d.ellipse((x-2,y-2,x+2,y+2),fill=(185,201,207),outline=(25,30,40))
 return im
for name in BLOCKS:
 if name=='airlock_door':continue
 im=panel();d=ImageDraw.Draw(im)
 if name in COLORS:
  c=COLORS[name];d.rectangle((12,13,51,42),fill=(12,24,33),outline=c,width=2)
  if name=='solar_charger':
   for x in range(15,50,9):
    for y in range(16,40,8):d.rectangle((x,y,x+6,y+5),fill=c,outline=(110,160,255))
  elif name=='geothermal_generator':
   for x in range(16,49,7):d.rectangle((x,18,x+3,39),fill=c)
  else:
   for i in range(3):d.rectangle((17,19+i*7,44-i*7,21+i*7),fill=c)
  for x in [17,29,41]:d.rectangle((x,48,x+5,51),fill=c)
 elif name=='pressure_window':
  d.rectangle((7,7,56,56),fill=(158,218,238,38));d.line((12,43,43,12),fill=(215,245,255,140),width=2)
 elif name=='martian_ground_ice':
  im=Image.new('RGBA',(64,64),(126,65,48,255));d=ImageDraw.Draw(im)
  for i in range(14):
   x=(i*19)%64;y=(i*29)%64;d.polygon([(x,y),(x+9,y-3),(x+15,y+8),(x+4,y+14)],fill=(135+i*5,205+i*2,235,255),outline=(235,250,255))
 else:
  d.rectangle((13,13,50,50),fill=(105,125,137),outline=(185,202,212),width=2)
  for y in [22,32,42]:d.line((20,y,43,y),fill=(53,67,80),width=2)
 save(im,'block',name)
 if name=='pressure_window':tex={'sprite':'space_fabric:block/'+name,'force_translucent':True}
 else:tex='space_fabric:block/'+name
 write(f'assets/space_fabric/models/block/{name}.json',{'parent':'minecraft:block/cube_all','textures':{'all':tex}})
 write(f'assets/space_fabric/blockstates/{name}.json',{'variants':{'':{'model':'space_fabric:block/'+name}}})
 write(f'assets/space_fabric/items/{name}.json',{'model':{'type':'minecraft:model','model':'space_fabric:block/'+name}})
for part in ['bottom','top']:
 im=panel();d=ImageDraw.Draw(im)
 if part=='top':d.rectangle((15,12,48,50),fill=(80,145,160),outline=(205,235,240),width=3);d.line((20,42,43,19),fill=(165,220,235),width=2)
 else:
  for y in [21,29,37,45]:d.rectangle((15,y,48,y+3),fill=(25,37,45))
 d.rectangle((51,25,56,38),fill=(230,183,65));save(im,'block','airlock_door_'+part)
with ZipFile(Path(__file__).resolve().parents[1]/'.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-merged-4ac4624eb9/26.1/minecraft-merged-4ac4624eb9-26.1.jar') as jar:
 state=json.loads(jar.read('assets/minecraft/blockstates/iron_door.json'))
 for v in state['variants'].values():v['model']=v['model'].replace('minecraft:block/iron_door','space_fabric:block/airlock_door')
 write('assets/space_fabric/blockstates/airlock_door.json',state)
 for half in ['bottom','top']:
  for hinge in ['left','right']:
   for suffix in ['','_open']:
    variant=f'{half}_{hinge}{suffix}'
    write(f'assets/space_fabric/models/block/airlock_door_{variant}.json',{'parent':'minecraft:block/door_'+variant,'textures':{'bottom':'space_fabric:block/airlock_door_bottom','top':'space_fabric:block/airlock_door_top'}})
# The validator expects a named root model for every block, including multi-state doors.
write('assets/space_fabric/models/block/airlock_door.json',{'parent':'space_fabric:block/airlock_door_bottom_left'})
COL={'empty_canister':(125,140,155),'oxygen_canister':(65,210,155),'water_canister':(70,170,245),'hydrogen_canister':(220,160,245)}
for name in list(ITEMS)+['airlock_door']:
 im=Image.new('RGBA',(64,64));d=ImageDraw.Draw(im)
 if name in COL:
  c=COL[name];d.rounded_rectangle((17,12,46,58),radius=8,fill=(175,190,202),outline=(40,55,69),width=3)
  d.rectangle((20,25,43,45),fill=c);d.rectangle((27,5,36,13),fill=(90,110,125));d.rectangle((23,4,40,7),fill=c)
  d.rectangle((21,17,24,52),fill=(225,235,240))
 elif 'power_cell' in name:
  d.rounded_rectangle((16,11,47,57),radius=4,fill=(78,94,110),outline=(185,205,215),width=3);d.rectangle((26,6,37,11),fill=(175,192,204))
  for y in range(19,50,8):d.rectangle((23,y,40,y+4),fill=(65,230,150) if name=='charged_power_cell' else (30,45,59))
 elif name=='airlock_door':
  d.rectangle((16,3,47,60),fill=(125,147,163),outline=(40,52,64),width=3);d.rectangle((22,9,41,28),fill=(87,185,207));d.rectangle((39,34,43,43),fill=(225,180,68))
 else:
  c=(230,130,85) if 'mars' in name else (235,196,79) if 'venus' in name else (150,180,210)
  d.rectangle((13,7,50,57),fill=(225,233,234),outline=(65,85,100),width=3);d.ellipse((24,15,40,31),fill=c)
  for y in [36,42,48]:d.line((21,y,43,y),fill=(70,95,110),width=2)
 save(im,'item',name)
 write(f'assets/space_fabric/models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/'+name}})
 write(f'assets/space_fabric/items/{name}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+name}})
for name in BLOCKS:
 conditions=[{'condition':'minecraft:survives_explosion'}]
 if name=='airlock_door':conditions.append({'condition':'minecraft:block_state_property','block':'space_fabric:airlock_door','properties':{'half':'lower'}})
 write(D+f'loot_table/blocks/{name}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':'space_fabric:'+name}],'conditions':conditions}]})
# Extend only the relevant native tags.
def tag(namespace,kind,name,values):
 p=ROOT/f'data/{namespace}/tags/{kind}/{name}.json';data=json.loads(p.read_text()) if p.exists() else {'replace':False,'values':[]}
 data['values']=list(dict.fromkeys(data['values']+values));write(str(p.relative_to(ROOT)),data)
tag('space_fabric','block','pressure_rated_shell',['space_fabric:'+n for n in BLOCKS if n!='martian_ground_ice'])
tag('minecraft','block','mineable/pickaxe',['space_fabric:'+n for n in BLOCKS])
tag('minecraft','block','doors',['space_fabric:airlock_door'])
tag('minecraft','item','doors',['space_fabric:airlock_door'])
def shaped(name,pattern,key,result=None,count=1):write(D+'recipe/'+name+'.json',{'type':'minecraft:crafting_shaped','category':'misc','pattern':pattern,'key':key,'result':{'id':'space_fabric:'+(result or name),'count':count}})
def loose(name,ingredients,result=None,count=1):write(D+'recipe/'+name+'.json',{'type':'minecraft:crafting_shapeless','category':'misc','ingredients':ingredients,'result':{'id':'space_fabric:'+(result or name),'count':count}})
mc=lambda n:'minecraft:'+n;sf=lambda n:'space_fabric:'+n
shaped('pressure_hull',['ICI','CIC'],{'I':mc('iron_ingot'),'C':mc('copper_ingot')},count=8)
shaped('pressure_window',['IGI','GCG'],{'I':mc('iron_ingot'),'G':mc('glass'),'C':mc('copper_ingot')},count=4)
shaped('airlock_door',['HGH','HDH','HGH'],{'H':sf('pressure_hull'),'G':mc('glass'),'D':mc('iron_door')})
shaped('habitat_controller',['HCH','ROG','HCH'],{'H':sf('pressure_hull'),'C':mc('copper_ingot'),'R':mc('redstone'),'O':mc('observer'),'G':mc('glass')})
shaped('water_electrolyzer',['HCH','WRW','HCH'],{'H':sf('pressure_hull'),'C':mc('copper_ingot'),'W':mc('water_bucket'),'R':mc('redstone')})
shaped('sample_analyzer',['HGH','CAR','HCH'],{'H':sf('pressure_hull'),'G':mc('glass'),'C':mc('copper_ingot'),'A':mc('amethyst_shard'),'R':mc('redstone')})
shaped('solar_charger',['GGG','CRC','HDH'],{'G':mc('glass'),'C':mc('copper_ingot'),'R':mc('redstone'),'H':sf('pressure_hull'),'D':sf('mars_research_report')})
shaped('geothermal_generator',['HFH','CDS','HRH'],{'H':sf('pressure_hull'),'F':mc('furnace'),'C':mc('copper_ingot'),'D':sf('venus_research_report'),'S':sf('sulfur_crystal'),'R':mc('redstone')})
shaped('empty_canister',[' I ','IGI',' I '],{'I':mc('iron_ingot'),'G':mc('glass')},count=4)
shaped('empty_power_cell',['ICI','IRI'],{'I':mc('iron_ingot'),'C':mc('copper_ingot'),'R':mc('redstone')},count=2)
loose('charged_power_cell',[sf('empty_power_cell'),mc('redstone'),mc('redstone'),mc('copper_ingot')])
loose('water_canister',[sf('empty_canister'),mc('water_bucket')])
write(D+'recipe/water_bucket_from_canister.json',{'type':'minecraft:crafting_shapeless','category':'misc','ingredients':[sf('water_canister'),mc('bucket')],'result':{'id':mc('water_bucket'),'count':1}})
loose('blank_data_card',[mc('paper'),mc('copper_ingot'),mc('redstone')],count=4)
loose('hydrogen_rocket_fuel',[sf('hydrogen_canister'),sf('sulfate_dust'),sf('empty_fuel_cell')],result='rocket_fuel_cell')
write(D+'worldgen/configured_feature/martian_ground_ice.json',{'type':'minecraft:ore','config':{'size':18,'discard_chance_on_air_exposure':0.2,'targets':[{'target':{'predicate_type':'minecraft:tag_match','tag':'space_fabric:mars_host_rocks'},'state':{'Name':sf('martian_ground_ice')}}]}})
write(D+'worldgen/placed_feature/martian_ground_ice.json',{'feature':sf('martian_ground_ice'),'placement':[{'type':'minecraft:count','count':4},{'type':'minecraft:in_square'},{'type':'minecraft:height_range','height':{'type':'minecraft:uniform','min_inclusive':{'absolute':8},'max_inclusive':{'absolute':48}}},{'type':'minecraft:biome'}]})
MESSAGES={
 'habitat_status':('Hábitat: %s | O₂ %s | Energía %s | Refrigerante %s | Volumen %s','Habitat: %s | O₂ %s | Energy %s | Coolant %s | Volume %s'),
 'machine_status':('%s · Progreso: %s ticks','%s · Progress: %s ticks'),
 'idle':('En espera','Idle'),'inputs':('Faltan materiales','Missing inputs'),'output_full':('Salida llena','Output full'),
 'sunlight':('Necesita sol directo; no funciona en Venus','Needs direct sun; unavailable on Venus'),'heat':('Necesita lava debajo','Needs lava below'),
 'processing':('Procesando','Processing'),'pressurized':('Presurizado y protegido','Pressurized and protected'),'supplies':('Falta oxígeno, energía o refrigerante','Needs oxygen, power or coolant'),
 'leak':('Fuga: revisa paredes y puertas','Leak: check walls and doors'),'too_large':('Habitación abierta o mayor de 512 bloques','Open room or more than 512 blocks'),
 'unloaded':('No se puede verificar una zona descargada','Cannot verify unloaded space'),'weak_wall':('Venus exige un cerramiento certificado','Venus requires a pressure-rated shell'),
 'blocked_vent':('Deja libre el bloque sobre el controlador','Leave the block above the controller free'),'unsupported':('Compatible con Tierra, Marte y Venus','Supports Earth, Mars and Venus')}
for i,locale in enumerate(['es_es','en_us']):
 p=A/f'lang/{locale}.json';lang=json.loads(p.read_text());lang.pop('gui.space_fabric.recipe_kind.colony',None)
 lang.update({'block.space_fabric.'+n:names[i] for n,names in BLOCKS.items()});lang.update({'item.space_fabric.'+n:names[i] for n,names in ITEMS.items()})
 lang.update({'colony.space_fabric.'+n:txt[i] for n,txt in MESSAGES.items()})
 lang['gui.space_fabric.viewer.kind.colony']=['Máquinas coloniales','Colony machines'][i]
 write(str(p.relative_to(ROOT)),lang)
# A visible vanilla advancement chain gives the first colony concrete objectives.
for name,item,title,desc,parent in [
 ('root','habitat_controller','Primera colonia','Fabrica un controlador de hábitat.',None),
 ('water','water_canister','Agua para la colonia','Obtén una bombona de agua.','root'),
 ('oxygen','oxygen_canister','Un lugar para respirar','Produce oxígeno en el electrolizador.','water'),
 ('mars','mars_research_report','Geología marciana','Analiza una muestra de Marte para fabricar un cargador solar.','oxygen'),
 ('venus','venus_research_report','Industria bajo presión','Analiza una muestra de Venus para fabricar el generador geotérmico.','mars')]:
 display={'icon':{'id':sf(item)},'title':{'translate':'advancements.space_fabric.colony.'+name+'.title'},'description':{'translate':'advancements.space_fabric.colony.'+name+'.description'},'frame':'task','show_toast':True,'announce_to_chat':False,'hidden':False}
 if parent is None:display['background']='minecraft:gui/advancements/backgrounds/stone'
 advancement={'display':display,'criteria':{'obtained':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':[sf(item)]}]}}},'requirements':[['obtained']]}
 if parent:advancement['parent']=sf('colony/'+parent)
 write(D+'advancement/colony/'+name+'.json',advancement)
 for locale in ['es_es','en_us']:
  path=A/f'lang/{locale}.json';lang=json.loads(path.read_text())
  english={'root':('First colony','Craft a habitat controller.'),'water':('Water for the colony','Obtain a water canister.'),'oxygen':('A place to breathe','Produce oxygen in the electrolyzer.'),'mars':('Martian geology','Analyze a Mars sample to craft a solar charger.'),'venus':('Industry under pressure','Analyze a Venus sample to craft a geothermal generator.')}
  t,d=(title,desc) if locale=='es_es' else english[name]
  lang['advancements.space_fabric.colony.'+name+'.title']=t;lang['advancements.space_fabric.colony.'+name+'.description']=d
  write(str(path.relative_to(ROOT)),lang)
print('Generated colony blocks, original textures, recipes, ice generation, translations and progression.')
