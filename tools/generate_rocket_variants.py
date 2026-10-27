#!/usr/bin/env python3
"""Generate upgrade kits and recipes, then import the original rocket models unchanged."""
from pathlib import Path
import json
ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/'src/main/resources'
ASSETS=RES/'assets/space_fabric'
NAMES=['mercury','venus','earth','moon','mars','jupiter','saturn','uranus','neptune']
NAMES_ES=['Mercurio','Venus','la Tierra','la Luna','Marte','Júpiter','Saturno','Urano','Neptuno']
MATERIALS=['minecraft:quartz','minecraft:obsidian','minecraft:copper_ingot','minecraft:glass','minecraft:iron_ingot','minecraft:netherite_scrap','minecraft:gold_ingot','minecraft:blue_ice','minecraft:prismarine_crystals']
langs={lang:json.loads((ASSETS/f'lang/{lang}.json').read_text()) for lang in ['es_es','en_us']}
for name,es,material in zip(NAMES,NAMES_ES,MATERIALS):
    for suffix in ['rocket','rocket_kit']:
        path=f'{name}_{suffix}'
        (ASSETS/f'items/{path}.json').write_text(json.dumps({'model':{'type':'minecraft:model','model':f'space_fabric:item/{path}'}},indent=2)+'\n')
    (ASSETS/f'models/item/{name}_rocket_kit.json').write_text(json.dumps({'parent':'minecraft:item/generated','textures':{'layer0':f'space_fabric:item/{name}_rocket_kit'}},indent=2)+'\n')
    recipe={'type':'minecraft:crafting_shaped','category':'misc','pattern':['IPI','RCR','IPI'],'key':{'I':'minecraft:iron_ingot','P':material,'R':'minecraft:redstone','C':'minecraft:copper_block'},'result':{'id':f'space_fabric:{name}_rocket_kit','count':1}}
    (RES/f'data/space_fabric/recipe/{name}_rocket_kit.json').write_text(json.dumps(recipe,indent=2)+'\n')
    for lang,data in langs.items():
        data[f'item.space_fabric.{name}_rocket']=f'Cohete de {es}' if lang=='es_es' else f'{name.title()} Rocket'
        data[f'item.space_fabric.{name}_rocket_kit']=f'Kit del cohete de {es}' if lang=='es_es' else f'{name.title()} Rocket Upgrade Kit'
        data[f'rocket.space_fabric.variant.{name}']=es.removeprefix('la ').capitalize() if lang=='es_es' else name.title()
for lang,data in langs.items():
    data.update({'item.space_fabric.rocket_rating':'Casco: %s bar; %s a %s °C' if lang=='es_es' else 'Hull: %s bar; %s to %s °C','gui.space_fabric.route_safe':'Destino incluido en el nivel de este cohete.' if lang=='es_es' else 'Destination included in this rocket tier.','gui.space_fabric.route_unsafe':'La protección del cohete no permite este destino.' if lang=='es_es' else 'Rocket protection does not permit this destination.','rocket.space_fabric.variant.base':'Básico' if lang=='es_es' else 'Basic','gui.space_fabric.hull':'Casco: %s%%' if lang=='es_es' else 'Hull: %s%%','message.space_fabric.upgrade_empty':'Baja del cohete para instalar el kit.' if lang=='es_es' else 'Leave the rocket before installing the kit.','message.space_fabric.rocket_unsuitable':'El casco o la protección no permiten este viaje.' if lang=='es_es' else 'Hull or environmental protection does not permit this trip.','item.space_fabric.rocket_kit_help':'Clic derecho sobre un cohete vacío para instalar.' if lang=='es_es' else 'Right-click an unoccupied rocket to install.'})
    (ASSETS/f'lang/{lang}.json').write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
print('Generated nine upgrade kits, recipes and translations; rocket hulls stay original.')

from import_equipment_textures import import_art
import_art()

# Import only the supplied hull geometry; never add external decorations.
from import_rocket_fleet import generate as import_fleet
import_fleet()
