#!/usr/bin/env python3
"""Craftable service supplies and original icons; preserve existing machine inventory drops."""
from pathlib import Path
import json
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1]/'src/main/resources';A=ROOT/'assets/space_fabric';D=ROOT/'data/space_fabric'
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,indent=2,ensure_ascii=False)+'\n')
ITEMS={'acid_filter':('Filtro contra gases corrosivos','Corrosive Gas Filter'),'spent_acid_filter':('Filtro saturado','Spent Filter'),'anticorrosive_coating':('Revestimiento anticorrosivo','Anticorrosive Coating'),'machine_repair_kit':('Kit de reparación de máquinas','Machine Repair Kit')}
HINTS={'acid_filter':('Clic derecho en una máquina para instalarlo; reemplazable al saturarse.','Right-click a machine to install; replace when saturated.'),'spent_acid_filter':('Recárgalo con carbón vegetal y papel en la mesa de trabajo.','Recharge with charcoal and paper in a crafting table.'),'anticorrosive_coating':('Clic derecho en una máquina: añade protección química.','Right-click a machine to add chemical protection.'),'machine_repair_kit':('Clic derecho en una máquina: recupera hasta un 25 % de integridad.','Right-click a machine to restore up to 25% integrity.')}
MESSAGES={
 'integrity':('Integridad: %s%%','Integrity: %s%%'),'short_integrity':('%s%%','%s%%'),'item_integrity':('Integridad de la máquina: %s%%','Machine integrity: %s%%'),
 'coating':('Revestimiento: %s/%s','Coating: %s/%s'),'filter':('Filtro: %s/%s','Filter: %s/%s'),'item_coating':('Revestimiento: %s','Coating: %s'),'item_filter':('Filtro restante: %s','Filter remaining: %s'),
 'hint':('Usa filtro, revestimiento o kit con clic derecho sobre el bloque.','Right-click the block with a filter, coating or repair kit.'),
 'hud_title':('Hábitat · Integridad: %s%%','Habitat · Integrity: %s%%'),'controller_warning':('Controlador: necesita mantenimiento','Controller needs maintenance'),
 'inspect':('%s · Integridad %s%% · Revestimiento %s/%s · Filtro %s/%s · %s','%s · Integrity %s%% · Coating %s/%s · Filter %s/%s · %s'),
 'notice':('%s [%s]: %s','%s [%s]: %s'),
 'stage_service':('Corrosión detectada. Prepara un kit de reparación.','Corrosion detected. Prepare a repair kit.'),
 'stage_degraded':('Integridad al 50 %. La máquina necesita reparación.','50% integrity. The machine needs repair.'),
 'stage_critical':('Integridad crítica. Repara antes de que se detenga.','Critical integrity. Repair before shutdown.'),
 'stage_failed':('Máquina averiada. Usa un kit para recuperarla.','Machine failed. Use a repair kit to recover it.'),
 'filter_low':('Filtro próximo a saturarse. Prepara un repuesto.','Filter nearly saturated. Prepare a replacement.'),
 'filter_spent':('Filtro saturado: ya no reduce la corrosión.','Filter saturated: corrosion is no longer reduced.'),
 'coating_low':('Queda poco revestimiento protector.','Protective coating is running low.'),'coating_spent':('Revestimiento agotado: la carcasa está expuesta.','Coating depleted: the casing is exposed.'),
 'applied_repair':('Máquina reparada.','Machine repaired.'),'applied_coat':('Revestimiento aplicado.','Coating applied.'),'applied_filter':('Filtro instalado.','Filter installed.'),
 'unneeded_repair':('La máquina ya tiene toda su integridad.','Machine integrity is already full.'),'unneeded_coat':('No cabe una carga completa de revestimiento.','No space for a full coating charge.'),'unneeded_filter':('El filtro actual todavía funciona.','The current filter is still working.'),
 'risk_none':('Entorno aislado o no corrosivo','Shielded or non-corrosive environment'),'risk_atmosphere':('Atmósfera venusiana','Venusian atmosphere'),
 'risk_haze':('Bruma corrosiva intensa','Dense corrosive haze'),'risk_source':('Contacto cercano con ácido o fumarola','Nearby acid or vent'),
 'half_speed':('Procesamiento al 50 %','Processing at 50%'),'progress':('Proceso: %s/%s s','Process: %s/%s s')}
for name in ITEMS:
 im=Image.new('RGBA',(64,64));d=ImageDraw.Draw(im)
 if 'filter' in name:
  fresh=name=='acid_filter';color=(70,210,173) if fresh else (156,100,57)
  d.rounded_rectangle((12,10,51,56),radius=5,fill=(60,77,87),outline=(184,202,208),width=3)
  for x in range(19,46,6):
   for y in range(18,49,6):d.rectangle((x,y,x+3,y+3),fill=color)
  d.rectangle((24,4,39,11),fill=color)
  if not fresh:d.line((17,17,46,48),fill=(76,46,30),width=5)
 elif name=='anticorrosive_coating':
  d.rectangle((14,20,49,54),fill=(72,167,174),outline=(207,230,227),width=3);d.rectangle((12,15,51,21),fill=(110,135,148))
  d.arc((20,3,43,32),180,360,fill=(183,202,208),width=3);d.polygon([(25,29),(39,29),(37,40),(32,46),(27,40)],fill=(234,209,87))
 else:
  d.rounded_rectangle((9,18,54,54),radius=4,fill=(166,77,53),outline=(222,184,130),width=3);d.rectangle((23,10,40,18),outline=(180,201,207),width=3)
  d.line((22,46,41,27),fill=(214,231,233),width=7);d.arc((30,22,47,39),0,270,fill=(214,231,233),width=5);d.ellipse((18,42,27,51),outline=(214,231,233),width=3);d.rectangle((9,25,20,28),fill=(71,78,83))
 p=A/f'textures/item/{name}.png';p.parent.mkdir(parents=True,exist_ok=True);im.save(p)
 write(A/f'models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/'+name}})
 write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+name}})
for locale,index in [('es_es',0),('en_us',1)]:
 p=A/f'lang/{locale}.json';lang=json.loads(p.read_text());lang.update({'item.space_fabric.'+k:v[index] for k,v in ITEMS.items()});lang.update({'item.space_fabric.'+k+'.hint':v[index] for k,v in HINTS.items()});lang.update({'maintenance.space_fabric.'+k:v[index] for k,v in MESSAGES.items()})
 lang['colony.space_fabric.maintenance_failure']=['Averiada: usa un kit de reparación','Broken: use a repair kit'][index];lang['gui.space_fabric.habitat.maintenance_failure']=['Controlador averiado','Controller failed'][index]
 lang['advancements.space_fabric.maintenance.title']=['Mantenimiento preventivo','Preventive maintenance'][index];lang['advancements.space_fabric.maintenance.description']=['Prepara un kit de reparación para las máquinas de Venus.','Prepare a repair kit for Venus machines.'][index];write(p,lang)
def recipe(name,ingredients,result):write(D/f'recipe/{name}.json',{'type':'minecraft:crafting_shapeless','category':'misc','ingredients':ingredients,'result':{'id':'space_fabric:'+result,'count':1}})
recipe('acid_filter',['minecraft:charcoal','minecraft:paper','minecraft:iron_nugget','minecraft:copper_ingot'],'acid_filter')
recipe('anticorrosive_coating',['minecraft:quartz','minecraft:clay_ball','minecraft:slime_ball','minecraft:copper_ingot'],'anticorrosive_coating')
recipe('machine_repair_kit',['minecraft:iron_ingot','minecraft:iron_ingot','minecraft:copper_ingot','minecraft:copper_ingot','minecraft:redstone'],'machine_repair_kit')
recipe('recharge_acid_filter',['space_fabric:spent_acid_filter','minecraft:charcoal','minecraft:paper'],'acid_filter')
write(D/'tags/block/corrosive_sources.json',{'replace':False,'values':['space_fabric:venus_acid_pool','space_fabric:venus_acid_pocket','space_fabric:condensed_sulfuric_acid','space_fabric:venus_vent_basalt']})
for name in ['habitat_controller','water_electrolyzer','solar_charger','geothermal_generator','sample_analyzer','fuel_refinery']:
 p=D/f'loot_table/blocks/{name}.json';loot=json.loads(p.read_text())
 for pool in loot['pools']:
  for entry in pool['entries']:
   functions=entry.setdefault('functions',[]);functions[:]=[f for f in functions if f.get('function')!='minecraft:copy_custom_data']
   functions.append({'function':'minecraft:copy_custom_data','source':'block_entity','ops':[{'source':'Maintenance','target':'SpaceFabricMaintenance','op':'replace'}]})
 write(p,loot)
write(D/'advancement/colony/maintenance.json',{'parent':'space_fabric:colony/root','display':{'icon':{'id':'space_fabric:machine_repair_kit'},'title':{'translate':'advancements.space_fabric.maintenance.title'},'description':{'translate':'advancements.space_fabric.maintenance.description'},'frame':'task','show_toast':True,'announce_to_chat':False,'hidden':False},'criteria':{'obtained':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':['space_fabric:machine_repair_kit']}]}}},'requirements':[['obtained']],'rewards':{'recipes':['space_fabric:acid_filter','space_fabric:anticorrosive_coating','space_fabric:machine_repair_kit','space_fabric:recharge_acid_filter']}})
print('Generated four original maintenance icons, recipes, tooltips, advancement and wear-preserving machine loot.')
