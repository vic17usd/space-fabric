#!/usr/bin/env python3
"""Deterministic original industry pixel art, native recipes, tags and discovery steps.

Run after the general colony/maintenance generators when regenerating all resources.
No imported rocket or equipment textures are modified.
"""
from pathlib import Path
import json
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1] / 'src/main/resources'
A, D = ROOT / 'assets/space_fabric', ROOT / 'data/space_fabric'

def write(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + '\n')

def tag(namespace, kind, name, values):
    path = ROOT / f'data/{namespace}/tags/{kind}/{name}.json'
    data = json.loads(path.read_text()) if path.exists() else {'replace': False, 'values': []}
    data['values'] = list(dict.fromkeys(data['values'] + values))
    write(path, data)

sf = lambda name: 'space_fabric:' + name
mc = lambda name: 'minecraft:' + name
BLOCKS = {
    'venus_atmospheric_collector': ('Colector atmosférico de Venus', 'Venus Atmospheric Collector'),
    'venus_gas_separator': ('Separador de gases', 'Gas Separator'),
    'venus_sulfur_processor': ('Procesador de azufre', 'Sulfur Processor'),
    'venus_ceramic_kiln': ('Horno de cerámica venusiana', 'Venus Ceramic Kiln'),
    'venus_composite_hull': ('Panel compuesto venusiano', 'Venus Composite Hull'),
}
ITEMS = {
    'venus_atmosphere_canister': ('Bombona de atmósfera venusiana', 'Venusian Atmosphere Canister'),
    'carbon_dioxide_canister': ('Bombona de dióxido de carbono', 'Carbon Dioxide Canister'),
    'nitrogen_canister': ('Bombona de nitrógeno', 'Nitrogen Canister'),
    'refined_sulfur': ('Azufre refinado', 'Refined Sulfur'),
    'pyroxene_ceramic': ('Cerámica de piroxeno', 'Pyroxene Ceramic'),
    'venus_composite_plate': ('Placa de compuesto venusiano', 'Venus Composite Plate'),
    'carbon_powder': ('Polvo de carbono', 'Carbon Powder'),
    'venus_machine_lining': ('Refuerzo cerámico para máquinas', 'Ceramic Machine Lining'),
    'reinforced_venus_suit_helmet': ('Casco venusiano reforzado', 'Reinforced Venus Helmet'),
    'reinforced_venus_suit_chestplate': ('Pechera venusiana reforzada', 'Reinforced Venus Chestplate'),
    'reinforced_venus_suit_leggings': ('Pantalones venusianos reforzados', 'Reinforced Venus Leggings'),
    'reinforced_venus_suit_boots': ('Botas venusianas reforzadas', 'Reinforced Venus Boots'),
}
HINTS = {
    'venus_atmosphere_canister': ('Mezcla de CO₂ y N₂. Procésala en el separador de gases.', 'CO₂/N₂ mixture. Process it in the gas separator.'),
    'carbon_dioxide_canister': ('Gas para cerámica o para obtener oxígeno y carbono por electrólisis.', 'Gas for ceramics, or oxygen and carbon through electrolysis.'),
    'nitrogen_canister': ('Atmósfera inerte para refinar azufre y montar mejoras venusianas.', 'Inert gas for sulfur refining and Venus upgrades.'),
    'refined_sulfur': ('Fundente para compuestos y refuerzo de trajes en la herrería.', 'Flux for composites and suit reinforcement in the smithing table.'),
    'pyroxene_ceramic': ('Aislante térmico fabricado con piroxeno en el horno con lava debajo.', 'Thermal insulator made from pyroxene in a kiln with lava underneath.'),
    'venus_composite_plate': ('Refuerzo de trajes, paneles, máquinas y mejoras del cohete venusiano.', 'Reinforces suits, hulls, machines and Venus rocket upgrades.'),
    'carbon_powder': ('Recicla filtros saturados junto con papel en la mesa de trabajo.', 'Recycle spent filters with paper in a crafting table.'),
    'venus_machine_lining': ('Clic derecho en una máquina: reduce a la mitad el ataque químico antes del filtro.', 'Right-click a machine: halves chemical attack before filtering.'),
}

def save(im, kind, name):
    path = A / f'textures/{kind}/{name}.png'
    path.parent.mkdir(parents=True, exist_ok=True)
    # Draw at native pixel scale and expand without smoothing for a Minecraft finish.
    im.resize((64, 64), Image.Resampling.NEAREST).save(path)

def casing():
    im = Image.new('RGBA', (32, 32), (61, 66, 69, 255))
    d = ImageDraw.Draw(im)
    d.rectangle((1, 1, 30, 30), outline=(170, 165, 143), width=1)
    d.rectangle((3, 3, 28, 28), outline=(30, 33, 36), width=1)
    for x in [3, 28]:
        for y in [3, 28]: d.point((x, y), fill=(217, 208, 178))
    d.rectangle((4, 24, 27, 26), fill=(157, 118, 42))
    for x in range(5, 27, 6): d.line((x, 24, x+2, 26), fill=(29, 31, 32), width=2)
    return im

for name in BLOCKS:
    im = casing(); d = ImageDraw.Draw(im)
    d.rectangle((6, 6, 25, 22), fill=(20, 28, 32), outline=(122, 137, 139))
    if name.endswith('collector'):
        d.ellipse((8, 7, 23, 22), outline=(203, 197, 172), width=2)
        for points in [[(15, 9),(18, 9),(16, 16)],[(21, 15),(21, 18),(16, 16)],[(15, 21),(12, 21),(16, 16)],[(10, 15),(10, 12),(16, 16)]]:
            d.polygon(points, fill=(123, 182, 171))
        d.rectangle((14, 14, 17, 17), fill=(221, 185, 71))
    elif name.endswith('separator'):
        for x, color in [(9, (220, 167, 74)), (19, (117, 165, 224))]:
            d.rectangle((x, 8, x+3, 20), fill=color)
            d.rectangle((x-1, 7, x+4, 9), fill=(172, 188, 190))
        d.line((13, 14, 18, 14), fill=(190, 199, 192), width=2)
    elif name.endswith('processor'):
        d.polygon([(12,8),(19,8),(19,13),(23,18),(22,21),(9,21),(8,18),(12,13)], fill=(130, 146, 137), outline=(214, 217, 181))
        d.rectangle((11, 17, 20, 19), fill=(230, 209, 60))
    elif name.endswith('kiln'):
        d.rectangle((8, 8, 23, 21), fill=(155, 137, 109), outline=(227, 208, 170))
        d.rectangle((11, 11, 20, 18), fill=(52, 35, 30))
        d.polygon([(12,17),(14,12),(16,15),(18,11),(20,17)], fill=(247, 152, 51))
    else:
        d.rectangle((5, 5, 26, 22), fill=(174, 167, 143), outline=(223, 217, 193))
        for x in range(8, 27, 6): d.line((x, 6, x, 21), fill=(104, 116, 112))
        d.rectangle((12, 10, 19, 17), fill=(63, 83, 83), outline=(223, 192, 99))
    save(im, 'block', name)
    write(A/f'models/block/{name}.json', {'parent': 'minecraft:block/cube_all', 'textures': {'all': sf('block/'+name)}})
    write(A/f'blockstates/{name}.json', {'variants': {'': {'model': sf('block/'+name)}}})
    write(A/f'items/{name}.json', {'model': {'type': 'minecraft:model', 'model': sf('block/'+name)}})
    entry = {'type': 'minecraft:item', 'name': sf(name)}
    if name != 'venus_composite_hull':
        entry['functions'] = [{'function': 'minecraft:copy_custom_data', 'source': 'block_entity',
                               'ops': [{'source': 'Maintenance', 'target': 'SpaceFabricMaintenance', 'op': 'replace'}]}]
    write(D/f'loot_table/blocks/{name}.json', {'type':'minecraft:block', 'pools':[{'rolls':1, 'entries':[entry], 'conditions':[{'condition':'minecraft:survives_explosion'}]}]})

for name in ITEMS:
    im = Image.new('RGBA', (32, 32)); d = ImageDraw.Draw(im)
    if name.endswith('_canister'):
        color = {'venus_atmosphere_canister': (222, 166, 62), 'carbon_dioxide_canister': (188, 196, 198), 'nitrogen_canister': (95, 160, 222)}[name]
        d.rectangle((10, 7, 22, 27), fill=(135, 154, 160), outline=(32, 46, 57), width=2)
        d.rectangle((12, 13, 20, 22), fill=color); d.rectangle((14, 3, 18, 7), fill=(213, 214, 193))
        d.line((11, 9, 11, 24), fill=(229, 235, 226)); d.rectangle((12, 2, 20, 4), fill=color)
        if 'atmosphere' in name: d.line((14, 18, 19, 18), fill=(107, 76, 34), width=2)
        elif 'carbon' in name: d.rectangle((14, 15, 18, 19), outline=(63, 76, 83))
        else: d.line((14, 20, 14, 15, 18, 20, 18, 15), fill=(24, 58, 101))
    elif name == 'refined_sulfur':
        d.polygon([(5,21),(10,12),(16,7),(25,12),(27,21),(18,27),(9,26)], fill=(220, 194, 49), outline=(80, 67, 20))
        d.line((10,14,16,10,23,13), fill=(255, 241, 114), width=2); d.line((7,22,18,23,25,19), fill=(152, 121, 29), width=2)
    elif name == 'carbon_powder':
        d.polygon([(4,25),(11,19),(13,11),(20,14),(23,20),(28,25),(24,28),(7,28)], fill=(48, 56, 65), outline=(14, 24, 30))
        for x,y in [(12,17),(17,15),(19,22),(9,24),(23,25)]: d.rectangle((x,y,x+1,y+1), fill=(98, 111, 122))
    elif name in ['pyroxene_ceramic', 'venus_composite_plate', 'venus_machine_lining']:
        color=(203, 196, 164) if name=='pyroxene_ceramic' else (102, 128, 132)
        d.polygon([(4,11),(20,5),(28,13),(28,22),(12,28),(4,20)], fill=color, outline=(37, 49, 57))
        d.line((6,12,20,7,26,13,12,18,6,12), fill=(237, 224, 181), width=2)
        d.line((12,19,12,25,26,20), fill=(52, 72, 78), width=2)
        if name=='venus_machine_lining':
            d.rectangle((12,10,22,20), fill=(190, 159, 67), outline=(245, 223, 149))
            d.line((14,15,17,18,21,12), fill=(53, 69, 67), width=2)
    else:
        # Same Venus equipment asset on the character, distinct reinforced inventory icons.
        c=(176, 173, 139); edge=(40, 56, 59); gold=(224, 184, 67)
        if name.endswith('helmet'):
            d.rounded_rectangle((7,5,25,26), radius=5, fill=c, outline=edge, width=2)
            d.rectangle((9,10,23,18), fill=(41,87,95), outline=(225,210,166))
            d.line((10,11,21,11), fill=(105,198,201), width=1); d.rectangle((9,22,23,24), fill=gold)
        elif name.endswith('chestplate'):
            d.polygon([(8,5),(13,7),(19,7),(24,5),(29,10),(25,17),(23,15),(23,27),(9,27),(9,15),(7,17),(3,10)], fill=c, outline=edge, width=2)
            d.rectangle((12,10,20,20), fill=(63,104,108), outline=gold, width=2); d.rectangle((10,23,22,25), fill=gold)
        elif name.endswith('leggings'):
            d.polygon([(7,5),(25,5),(25,27),(18,27),(17,17),(15,17),(14,27),(7,27)], fill=c, outline=edge, width=2)
            d.rectangle((9,7,23,10), fill=gold); d.rectangle((9,19,12,22), fill=(62,94,97)); d.rectangle((20,19,23,22), fill=(62,94,97))
        else:
            for x in [5,18]:
                d.polygon([(x+3,5),(x+9,5),(x+9,26),(x,26),(x,19),(x+3,17)], fill=c, outline=edge, width=2)
                d.rectangle((x+1,22,x+8,24), fill=gold)
    save(im, 'item', name)
    write(A/f'models/item/{name}.json', {'parent': 'minecraft:item/generated', 'textures': {'layer0': sf('item/'+name)}})
    write(A/f'items/{name}.json', {'model': {'type': 'minecraft:model', 'model': sf('item/'+name)}})

tag('space_fabric','block','pressure_rated_shell',[sf(n) for n in BLOCKS])
tag('minecraft','block','mineable/pickaxe',[sf(n) for n in BLOCKS])
tag('minecraft','block','needs_iron_tool',[sf('venus_composite_hull')])
tag('space_fabric','item','repairs_reinforced_venus_suit',[sf('venus_composite_plate')])
for part, plural in [('helmet','helmets'),('chestplate','chestplates'),('leggings','leggings'),('boots','boots')]:
    item=sf('reinforced_venus_suit_'+part)
    tag('space_fabric','item','suits/venus/'+plural,[item])
    tag('space_fabric','item','space_suit/'+plural,[item])
    if part=='boots': tag('space_fabric','item','thermal_boots',[item])
    write(D/f'recipe/reinforced_venus_suit_{part}.json', {'type':'minecraft:smithing_transform','template':sf('refined_sulfur'),
          'base':sf('venus_suit_'+part),'addition':sf('venus_composite_plate'),'result':{'id':item}})

def shaped(name, pattern, key, result=None, count=1):
    write(D/f'recipe/{name}.json', {'type':'minecraft:crafting_shaped','category':'misc','pattern':pattern,'key':key,
                                  'result':{'id':sf(result or name),'count':count}})

def loose(name, ingredients, result):
    write(D/f'recipe/{name}.json', {'type':'minecraft:crafting_shapeless','category':'misc','ingredients':ingredients,'result':{'id':sf(result),'count':1}})

shaped('venus_atmospheric_collector',['HGH','CRC','HPH'], {'H':sf('pressure_hull'),'G':mc('glass'),'C':mc('copper_ingot'),'R':sf('venus_research_report'),'P':mc('hopper')})
shaped('venus_gas_separator',['HCH','PRP','HTH'], {'H':sf('pressure_hull'),'C':mc('copper_ingot'),'P':mc('piston'),'R':sf('venus_research_report'),'T':sf('empty_canister')})
shaped('venus_sulfur_processor',['HCH','SRS','HFH'], {'H':sf('pressure_hull'),'C':mc('copper_ingot'),'S':sf('sulfur_crystal'),'R':sf('venus_research_report'),'F':mc('furnace')})
shaped('venus_ceramic_kiln',['HPH','ORO','HFH'], {'H':sf('pressure_hull'),'P':sf('pyroxene'),'O':mc('obsidian'),'R':sf('venus_research_report'),'F':mc('blast_furnace')})
shaped('venus_composite_hull',['PCP','CSC','PCP'], {'P':sf('venus_composite_plate'),'C':sf('pyroxene_ceramic'),'S':sf('refined_sulfur')},count=8)
loose('venus_machine_lining',[sf('venus_composite_plate'),sf('pyroxene_ceramic'),sf('refined_sulfur'),sf('anticorrosive_coating')],'venus_machine_lining')
loose('recharge_acid_filter_industry',[sf('spent_acid_filter'),sf('carbon_powder'),mc('paper')],'acid_filter')
shaped('venus_rocket_kit_industry',['PCP','NRN','PFP'], {'P':sf('venus_composite_plate'),'C':sf('pyroxene_ceramic'),
       'N':sf('nitrogen_canister'),'R':sf('venus_research_report'),'F':sf('empty_fuel_cell')},result='venus_rocket_kit')

MESSAGES = {
    'colony.space_fabric.collector_environment': ('Solo recolecta en Venus', 'Collects only on Venus'),
    'colony.space_fabric.collector_intake': ('Deja la toma superior al aire libre', 'Expose the top intake to open sky'),
    'block.space_fabric.venus_atmospheric_collector.hint': ('Solo en Venus y sin techo sobre la toma superior.', 'Only on Venus, with no roof above its top intake.'),
    'block.space_fabric.venus_ceramic_kiln.hint': ('Necesita lava justo debajo y una batería cargada por lote.', 'Requires lava directly underneath and a charged cell per batch.'),
    'industry.space_fabric.reinforced_suit': ('Doble durabilidad. Misma protección atmosférica venusiana.', 'Double durability. Same Venus atmospheric protection.'),
    'maintenance.space_fabric.reinforced': ('Carcasa reforzada: 50 % menos ataque químico antes del filtro.', 'Reinforced casing: 50% less chemical attack before filtering.'),
    'maintenance.space_fabric.applied_reinforce': ('Refuerzo cerámico instalado de forma permanente.', 'Permanent ceramic lining installed.'),
    'maintenance.space_fabric.unneeded_reinforce': ('La máquina ya tiene refuerzo cerámico.', 'This machine already has ceramic lining.'),
}
STEPS = [
    ('intake','venus_atmospheric_collector','venus','Capturar Venus','Capture Venus','Fabrica un colector y colócalo al aire libre en Venus.','Craft a collector and place it outdoors on Venus.',list(BLOCKS)[:4]),
    ('gases','nitrogen_canister','intake','Separar para sobrevivir','Separate to survive','Separa la atmósfera de Venus en CO₂ y nitrógeno.','Separate Venus atmosphere into CO₂ and nitrogen.',[]),
    ('sulfur','refined_sulfur','gases','Química bajo presión','Chemistry under pressure','Refina azufre usando nitrógeno como gas inerte.','Refine sulfur using nitrogen as an inert gas.',['recharge_acid_filter_industry']),
    ('composite','venus_composite_plate','sulfur','Materiales para Venus','Materials for Venus','Fabrica cerámica y una placa de compuesto en el horno.','Make ceramic and a composite plate in the kiln.',['venus_composite_hull','venus_machine_lining','venus_rocket_kit_industry']+['reinforced_venus_suit_'+p for p in ['helmet','chestplate','leggings','boots']]),
]
for name, item, parent, es, en, es_desc, en_desc, recipes in STEPS:
    MESSAGES[f'advancements.space_fabric.industry.{name}.title']=(es,en)
    MESSAGES[f'advancements.space_fabric.industry.{name}.description']=(es_desc,en_desc)
    parent_id=sf('colony/venus') if parent=='venus' else sf('industry/'+parent)
    data={'parent':parent_id,'display':{'icon':{'id':sf(item)},'title':{'translate':f'advancements.space_fabric.industry.{name}.title'},
          'description':{'translate':f'advancements.space_fabric.industry.{name}.description'},'frame':'task','show_toast':True,'announce_to_chat':False,'hidden':False},
          'criteria':{'obtained':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':[sf(item)]}]}}},'requirements':[['obtained']]}
    if recipes: data['rewards']={'recipes':[sf(r) for r in recipes]}
    write(D/f'advancement/industry/{name}.json',data)
for locale,index in [('es_es',0),('en_us',1)]:
    path=A/f'lang/{locale}.json'; lang=json.loads(path.read_text())
    lang.update({'block.space_fabric.'+n:v[index] for n,v in BLOCKS.items()})
    lang.update({'item.space_fabric.'+n:v[index] for n,v in ITEMS.items()})
    lang.update({'item.space_fabric.'+n+'.hint':v[index] for n,v in HINTS.items()})
    lang.update({k:v[index] for k,v in MESSAGES.items()});write(path,lang)
print('Generated Venus industry: four machines, hull, twelve items, twelve recipes, four advancements and tags.')
