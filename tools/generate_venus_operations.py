#!/usr/bin/env python3
"""Original navigator/recovery art and bilingual operations guidance."""
from pathlib import Path
from PIL import Image,ImageDraw
import json
R=Path(__file__).resolve().parents[1]/'src/main/resources';A=R/'assets/space_fabric';D=R/'data/space_fabric'
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,indent=2,ensure_ascii=False)+'\n')
for name,color in [('venus_navigator',(67,217,170)),('venus_black_box',(223,137,60)),('venus_air_sample',(110,190,232))]:
 im=Image.new('RGBA',(32,32));dr=ImageDraw.Draw(im);dr.rectangle((7,4,25,28),fill=(35,48,60),outline=(173,191,197),width=2)
 if name=='venus_navigator':
  dr.ellipse((10,9,23,22),outline=color,width=2);dr.line((16,7,16,24),fill=color);dr.line((9,15,24,15),fill=color);dr.rectangle((14,14,17,17),fill='white')
 elif name=='venus_black_box':
  for y in (10,15,20):dr.line((10,y,22,y),fill=color,width=2)
 else:dr.rectangle((11,8,21,24),fill=color);dr.rectangle((14,2,18,7),fill=(217,230,233))
 im.resize((64,64),Image.Resampling.NEAREST).save(A/f'textures/item/{name}.png')
 write(A/f'models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/'+name}});write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+name}})
write(D/'recipe/venus_navigator.json',{'type':'minecraft:crafting_shaped','category':'misc','pattern':['CRC','PMP','CBC'],'key':{'C':'minecraft:copper_ingot','R':'minecraft:redstone','P':'minecraft:paper','M':'minecraft:compass','B':'space_fabric:blank_data_card'},'result':{'id':'space_fabric:venus_navigator','count':1}})
t={
'item.space_fabric.venus_navigator':('Navegador de expediciones','Expedition Navigator'),'item.space_fabric.venus_black_box':('Datos de caja negra','Black Box Data'),'item.space_fabric.venus_air_sample':('Muestra atmosférica sellada','Sealed Atmospheric Sample'),
'operations.space_fabric.guide':('Clic derecho:\n\nAire: diario\nShift+aire: destino\nShift+bloque: baliza\nControlador: panel\n\nDetecta consolas\ncercanas (32 bloques).','Use item:\n\nAir: journal\nSneak+air: target\nSneak+block: waypoint\nController: dashboard\n\nDetects consoles\nwithin 32 blocks.'),
'operations.space_fabric.project_blackbox':('Caja negra\n\nLleva una tarjeta de datos.\nShift + clic con el navegador en la consola natural del refugio.','Black box\n\nBring a data card.\nSneak-use the navigator on the natural recovery shelter console.'),
'operations.space_fabric.project_sample':('Muestra atmosférica\n\nLleva bombona vacía y batería.\nShift + clic en la consola atmosférica, entre Y280 y Y320.','Atmospheric sample\n\nBring an empty canister and cell.\nSneak-use the cloud console at Y280–320.'),
'operations.space_fabric.project_delivery':('Entrega científica\n\nLleva los datos o muestra.\nShift + clic en un controlador operativo de Venus.\nPremio único: 4 placas por proyecto.','Scientific delivery\n\nBring the data or sample.\nSneak-use an operating Venus controller.\nOne reward: 4 plates per project.'),
'operations.space_fabric.other_dimension':('%s\n\nDestino en:\n%s','%s\n\nDestination in:\n%s'),
'operations.space_fabric.target':('%s\n\nCoordenadas:\n%s\nDistancia: %s bloques\nRumbo: %s','%s\n\nCoordinates:\n%s\nDistance: %s blocks\nBearing: %s'),
'operations.space_fabric.nav_hud':('%s · %s · %s bloques','%s · %s · %s blocks'),
'operations.space_fabric.condition':('Estado de la colonia\n\n%s\nEquipos con aviso: %s\n\nLectura actual de la sala y sus equipos cargados. No certifica una colonia entera.','Colony status\n\n%s\nUnits needing service: %s\n\nSnapshot of this room and its loaded units. Does not certify the entire colony.'),
'operations.space_fabric.projects':('Proyectos de campo\n\nCon tarjeta de datos: Shift + clic en consola del refugio para caja negra.\nCon bombona vacía y batería: Shift + clic en consola atmosférica (Y280–320) para muestra.\nEntrégalos con Shift + clic en un controlador operativo.','Field projects\n\nData card: sneak-use recovery console for black-box data.\nEmpty canister and cell: sneak-use cloud console (Y280–320) for sample.\nDeliver by sneak-using an operating habitat controller.'),
'operations.space_fabric.saved':('Baliza guardada en el navegador','Waypoint saved in navigator'),
'operations.space_fabric.delivered':('Proyecto completado: recibes cuatro placas. Una recompensa por proyecto y jugador.','Project complete: four plates awarded. One reward per project per player.'),
'operations.space_fabric.dashboard':('Colonia · Reservas\n\nO₂: %s\nEnergía: %s\nRefrigerante: %s\nResiduos: %s\nCalor: %s\nAutonomía local: %s s\nOcupantes: %s','Colony · Reserves\n\nO₂: %s\nPower: %s\nCoolant: %s\nWaste: %s\nHeat: %s\nLocal autonomy: %s s\nOccupants: %s'),
'operations.space_fabric.consumption':('Consumo de soporte\n\nO₂: %s/s\nEnergía: %s/s\nRefrigerante: %s/s\nEquipos cargados: %s\n\nEstimación local: excluye depósitos y otros consumidores. Reabre para actualizar.','Life support use\n\nO₂: %s/s\nPower: %s/s\nCoolant: %s/s\nLoaded units: %s\n\nLocal estimate excludes tanks and other consumers. Reopen to refresh.'),
'operations.space_fabric.machine':('%s\n%s\n\nIntegridad: %s%%\nFiltro: %s\nLotes: %s\nProgreso: %s ticks','%s\n%s\n\nIntegrity: %s%%\nFilter: %s\nBatches: %s\nProgress: %s ticks'),
'operations.space_fabric.flight':('Altura %s · Vertical %s b/s · Suelo %s','Altitude %s · Vertical %s b/s · Ground %s'),
'operations.space_fabric.flight_reserves':('Energía %s · O₂ %s · Refrigerante %s','Power %s · O₂ %s · Coolant %s')}
for i,lang in enumerate(('es_es','en_us')):
 p=A/f'lang/{lang}.json';d=json.loads(p.read_text());d.update({k:v[i] for k,v in t.items()});write(p,d)
# Luminous landing lenses; this is emissive artwork, not a dynamic world-light dependency.
quads=[]
for x in (-1.1,1.1):
 vertices=[(x-.12,.3,1.805),(x+.12,.3,1.805),(x+.12,.48,1.805),(x-.12,.48,1.805)]
 for reverse in (False,True):
  q=[0,0,-1 if reverse else 1,-1]
  for p,uv in zip(vertices[::-1] if reverse else vertices,[(.1,.1),(.9,.1),(.9,.9),(.1,.9)]):q+=list(p)+list(uv)
  quads.append(q)
write(A/'model_mesh/venus_aerostat_lights.json',{'texture':'space_fabric:textures/entity/venus_landing_lights.png','quads':quads})
Image.new('RGBA',(16,16),(171,243,255,255)).save(A/'textures/entity/venus_landing_lights.png')
