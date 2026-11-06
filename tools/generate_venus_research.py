#!/usr/bin/env python3
"""Original research diary and module icons; native diary recipe and localized campaign text."""
from pathlib import Path
import json
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1]/'src/main/resources';A=ROOT/'assets/space_fabric';D=ROOT/'data/space_fabric'
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
for name in ['venus_research_log','venus_rover_efficiency_module','venus_deep_survey_lens']:
 im=Image.new('RGBA',(32,32));dr=ImageDraw.Draw(im)
 if name.endswith('log'):
  dr.rectangle((5,4,26,28),fill=(40,73,82),outline=(190,188,146),width=2);dr.line((9,5,9,27),fill=(200,168,92),width=2)
  dr.ellipse((12,9,23,20),outline=(112,218,203),width=2);dr.line((13,23,22,23),fill=(211,219,205),width=1)
 elif name.endswith('module'):
  dr.rectangle((7,7,24,25),fill=(63,78,82),outline=(187,206,188),width=2)
  for x in (10,15,20):dr.rectangle((x,3,x+1,6),fill=(226,183,75));dr.rectangle((x,26,x+1,29),fill=(226,183,75))
  dr.polygon([(17,9),(11,17),(16,17),(14,23),(22,14),(17,14)],fill=(97,235,167))
 else:
  dr.ellipse((4,4,27,27),fill=(76,91,99),outline=(209,183,112),width=2);dr.ellipse((8,8,23,23),fill=(27,83,104),outline=(151,231,231),width=2);dr.arc((11,10,22,22),190,270,fill=(216,254,255),width=2)
 im.save(A/f'textures/item/{name}.png')
 write(A/f'models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/'+name}})
 write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+name}})
write(D/'recipe/venus_research_log.json',{'type':'minecraft:crafting_shapeless','category':'misc','ingredients':['minecraft:book','space_fabric:blank_data_card','minecraft:copper_ingot'],'result':{'id':'space_fabric:venus_research_log','count':1}})
write(D/'advancement/industry/research_log.json',{'parent':'space_fabric:industry/mining','criteria':{'log':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':'space_fabric:blank_data_card'}]}}},'rewards':{'recipes':['space_fabric:venus_research_log']}})
texts={
 'item.space_fabric.venus_research_log':('Diario del Proyecto Aurora','Project Aurora Journal'),
 'item.space_fabric.venus_rover_efficiency_module':('Módulo de eficiencia del rover','Rover Efficiency Module'),
 'item.space_fabric.venus_deep_survey_lens':('Lente de prospección profunda','Deep Survey Lens'),
 'research.space_fabric.intro':('Proyecto Aurora\n\nRecupera el refugio, restaura un laboratorio y toma datos de la estación atmosférica. Usa este diario con Shift + clic en sus consolas. Materiales del inventario.','Project Aurora\n\nRecover the refuge, restore a laboratory and survey the cloud station. Sneak-use this journal on their consoles. Materials come from your inventory.'),
 'research.space_fabric.current':('Proyecto: %s/%s\n\n%s','Project: %s/%s\n\n%s'),
 'research.space_fabric.bound':('Laboratorio vinculado en Venus:\n%s\n\nVuelve a esta consola para la calibración y la fabricación.','Bound Venus laboratory:\n%s\n\nReturn to this console for calibration and manufacturing.'),
 'research.space_fabric.page':('%s %s\n\n%s','%s %s\n\n%s'),
 'research.space_fabric.objective_0':('Recuperar archivos del refugio','Recover refuge archives'),
 'research.space_fabric.objective_1':('Restaurar el Laboratorio de Venus','Restore the Venus Laboratory'),
 'research.space_fabric.objective_2':('Sondear la estación atmosférica','Survey the cloud station'),
 'research.space_fabric.objective_3':('Regresar y calibrar el laboratorio vinculado','Return and calibrate the bound laboratory'),
 'research.space_fabric.objective_4':('Proyecto completado: fabricar mejoras en el laboratorio','Project complete: manufacture upgrades at the laboratory'),
 'research.space_fabric.detail_0':('Consola natural del Refugio de recuperación. Integridad 70%%+. Entrega 1 tarjeta y 1 batería cargada.','Natural Recovery Shelter console, 70%%+ integrity. Submit 1 data card and 1 charged cell.'),
 'research.space_fabric.detail_1':('Consola natural del laboratorio. Sala sellada y operativa; intercambiador cargado junto al controlador. Analizador: un lote venusiano. Invernadero: un lote de trigo. Integridad 70%%+. Entrega informe y batería.','Natural laboratory console. Operating sealed room; charged exchanger touching its controller. Analyzer: one Venus batch. Greenhouse: one wheat batch. 70%%+ integrity. Submit a report and charged cell.'),
 'research.space_fabric.detail_2':('Consola natural de la Estación atmosférica, Y 280–320. Repara el colector adyacente al 70%% y completa un lote. Entrega bombona vacía y batería cargada.','Natural Cloud Station console, Y 280–320. Repair its adjacent collector to 70%% and complete a batch. Submit an empty canister and charged cell.'),
 'research.space_fabric.detail_3':('Mismo laboratorio, operativo. Entrega 2 olivinos venusianos, 2 piroxenos, 2 cristales de azufre, informe, tarjeta y batería. Desbloquea las mejoras.','Same laboratory, operational. Submit 2 Venus olivines, 2 pyroxenes, 2 sulfur crystals, a report, card and charged cell. Unlocks upgrades.'),
 'research.space_fabric.manufacture':('Tras completar el proyecto: Shift + diario en el mismo laboratorio operativo. Cada par de módulos cuesta 4 placas compuestas, 2 cerámicas, 1 tarjeta y 1 batería. Devuelve la celda vacía.','After completion: sneak-use the journal on the same operating laboratory. Each module pair costs 4 composite plates, 2 ceramics, 1 card and 1 charged cell. Returns the empty cell.'),
 'research.space_fabric.wrong_site':('Usa el diario con Shift sobre una consola natural de expediciones de Venus.','Sneak-use the journal on a natural Venus expedition console.'),
 'research.space_fabric.missing':('Faltan materiales. Coste completo de esta entrega:','Missing supplies. Full cost of this submission:'),
 'research.space_fabric.cost':('%s × %s','%s × %s'),
 'research.space_fabric.advanced':('Proyecto Aurora: %s/4 completado. Consulta el diario para continuar.','Project Aurora: %s/4 complete. Check your journal to continue.'),
 'research.space_fabric.manufactured':('Fabricado un par de mejoras; celda vacía devuelta.','Manufactured one upgrade pair; empty cell returned.'),
 'research.space_fabric.lens_hint':('Escáner en la mano secundaria, lente en la principal: clic en el aire. Amplía el sondeo a 13×13 y 96 capas.','Scanner in offhand, lens in main hand: use in air. Expands surveys to 13×13 and 96 layers.'),
 'research.space_fabric.lens_installed':('Lente instalada. Los puntos del escáner se conservan.','Lens installed. Scanner waypoints preserved.'),
 'research.space_fabric.deep_installed':('Prospección mejorada: 13×13, 96 capas. Una batería por pulso; solo chunks cargados.','Upgraded survey: 13×13, 96 layers. One cell per pulse; loaded chunks only.'),
 'research.space_fabric.rover_hint':('Clic con el módulo sobre un rover aparcado y sin ocupantes. Reduce a la mitad el coste de tracción y el refrigerante; no el oxígeno. No se acumula ni se instala en aerostatos.','Use module on a parked unoccupied rover. Halves traction and coolant costs, not oxygen. Does not stack or fit aerostats.'),
 'research.space_fabric.rover_installed':('Rover optimizado: tracción y refrigerante al 50%%; consumo auxiliar y O₂ sin cambios.','Optimized rover: 50%% traction and coolant costs; auxiliary power and O₂ unchanged.'),
 'research.space_fabric.recipe_hint':('Fabricación en el laboratorio vinculado tras completar el Proyecto Aurora. Una entrega produce ambos módulos y devuelve la celda vacía.','Manufactured at the bound laboratory after Project Aurora. One submission produces both modules and returns the empty cell.')}
for n in range(5):
 titles=[('Proyecto Aurora · Archivos','Project Aurora · Archives'),('Proyecto Aurora · Laboratorio','Project Aurora · Laboratory'),('Proyecto Aurora · Atmósfera','Project Aurora · Atmosphere'),('Proyecto Aurora · Calibración','Project Aurora · Calibration'),('Proyecto Aurora completado','Project Aurora complete')]
 objectives=[('Fabrica el diario y visita el refugio','Craft the journal and visit the refuge'),('Restaura el laboratorio y su soporte vital','Restore the laboratory and life support'),('Lleva el diario a la estación atmosférica','Take the journal to the cloud station'),('Calibra el laboratorio vinculado','Calibrate your bound laboratory'),('Fabrica mejoras con el diario en tu laboratorio','Manufacture upgrades with the journal at your laboratory')]
 texts['venus.guide.tracker_title_research_'+str(n)]=titles[n];texts['venus.guide.tracker_objective_research_'+str(n)]=objectives[n]
for index,lang in enumerate(['es_es','en_us']):
 p=A/f'lang/{lang}.json';d=json.loads(p.read_text());d.update({k:v[index] for k,v in texts.items()});write(p,d)
