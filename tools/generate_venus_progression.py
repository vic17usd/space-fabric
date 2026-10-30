#!/usr/bin/env python3
"""Bilingual arrival tutorial, visible milestones and finite station salvage."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1] / 'src/main/resources'
A = ROOT / 'assets/space_fabric'
D = ROOT / 'data/space_fabric'


def write(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + '\n')


texts = {
    'tracker_title_shelter': ('Un refugio en Venus', 'Shelter on Venus'),
    'tracker_title_power': ('Energía para la colonia', 'Power for the colony'),
    'tracker_title_cooling': ('Control térmico', 'Thermal control'),
    'tracker_title_automation': ('Una base conectada', 'A connected base'),
    'tracker_title_food': ('Primer cultivo', 'First harvest'),
    'tracker_title_expedition': ('Expedición venusiana', 'Venusian expedition'),
    'tracker_title_complete': ('Colonia de Venus', 'Venus colony'),
    'tracker_objective_shelter': ('Entra en un refugio protegido', 'Enter a protected shelter'),
    'tracker_objective_power': ('Genera energía sobre lava', 'Generate power above lava'),
    'tracker_objective_cooling': ('Carga el intercambiador térmico', 'Charge the heat exchanger'),
    'tracker_objective_automation': ('Abastece una máquina por tuberías', 'Supply a machine through pipes'),
    'tracker_objective_food': ('Completa una cosecha de trigo', 'Complete a wheat harvest'),
    'tracker_objective_expedition': ('Recupera una instalación abandonada', 'Recover an abandoned station'),
    'tracker_objective_complete': ('Guía completada', 'Guide completed'),
    'hud_title': ('VENUS · Misiones %s/6', 'VENUS · Objectives %s/6'),
    'hud_now': ('› Ahora: %s', '› Now: %s'),
    'hud_next': ('Después: %s', 'Next: %s'),
    'hud_done': ('✓ Guía completada', '✓ Guide completed'),
    'hud_help': ('/venus guia · instrucciones completas', '/venus guia · full instructions'),
    'hud_shelter': ('Sella una sala, carga O₂, energía y agua y entra cuando esté protegida.',
                    'Seal a room, supply O₂, power and water, then enter once protected.'),
    'hud_power': ('Sobre lava: batería vacía + agua. Completa un lote y acércate al generador.',
                  'Above lava: empty cell + water. Finish a batch and approach the generator.'),
    'hud_cooling': ('Junto al controlador, carga el intercambiador con refrigerante enfriado y batería.',
                    'Beside the controller, charge an exchanger with chilled coolant and a cell.'),
    'hud_automation': ('Conecta una interfaz, envases y depósitos abastecidos a una máquina próxima.',
                       'Connect an interface, carriers and supplied tanks to a nearby machine.'),
    'hud_food': ('En una sala protegida y con luz: semilla + batería + agua. Completa trigo.',
                 'In a protected, lit room: seed + cell + water. Finish a wheat batch.'),
    'hud_expedition': ('Recupera una instalación natural y reclama su misión. Consulta /expediciones.',
                       'Recover a natural station and claim its mission. See /expediciones.'),
    'welcome': ('Llegaste a Venus. Conserva traje y reservas: la exposición sin protección es mortal. Haz clic aquí o usa /venus para iniciar la guía.',
                'You reached Venus. Keep your suit and supplies: unprotected exposure is lethal. Click here or use /venus to start the guide.'),
    'next': ('Siguiente objetivo: %s', 'Next objective: %s'),
    'complete': ('Colonia iniciada. Revisa reservas, mantenimiento y nuevas expediciones con el navegador.',
                 'Colony established. Check supplies, maintenance and expeditions with the navigator.'),
    'commands': ('/venus guia: todas las etapas · /venus recursos 15 2 75: reservas para minutos, ocupantes y volumen. K: recetas.',
                 '/venus guia: all steps · /venus recursos 15 2 75: supplies for minutes, occupants and volume. K: recipes.'),
    'safety': ('No salgas sin traje de Venus, oxígeno y refrigeración. Una sala vacía no protege: necesita sellado, controlador operativo y reservas. La guía no concede protección.',
               'Keep a Venus suit, oxygen and cooling. An empty room is unsafe: it needs a seal, operating controller and supplies. The guide grants no protection.'),
    'page': ('%s %s\n\n%s', '%s %s\n\n%s'),
    'arrival': ('Llegar a Venus', 'Reach Venus'),
    'shelter': ('Activar un refugio', 'Activate a shelter'),
    'shelter.detail': ('Cierra la sala con paneles certificados y esclusa. Carga O₂, batería y agua. Entra y comprueba presión. K muestra las recetas.',
                        'Seal a room with rated panels and an airlock. Supply O₂, power and water. Enter and check pressure. K shows recipes.'),
    'power': ('Generar energía', 'Generate power'),
    'power.detail': ('Coloca el generador sobre lava. Carga batería vacía y agua; completa un lote. Desde 0.41 devuelve el agua. Acércate a 16 bloques.',
                     'Place a generator above lava. Add an empty cell and water; finish a batch. Since 0.41 water is returned. Observe within 16 blocks.'),
    'cooling': ('Cargar el intercambiador', 'Charge a heat exchanger'),
    'cooling.detail': ('Enfría agua con energía. Carga refrigerante enfriado y otra batería en el intercambiador, junto al controlador. Observa su reserva a 16 bloques.',
                       'Chill water with power. Charge the exchanger with chilled coolant and another cell, beside the controller. Observe its reserve within 16 blocks.'),
    'automation': ('Conectar una máquina', 'Connect a machine'),
    'automation.detail': ('Apunta una interfaz hacia una máquina. Añade envases y redes separadas con depósitos abastecidos. Acércate a 16 bloques cuando indique funcionamiento.',
                          'Point an interface at a machine. Add carriers and separate supplied tank networks. Observe within 16 blocks when its supply status is working.'),
    'food': ('Cosechar en el invernadero', 'Harvest greenhouse wheat'),
    'food.detail': ('Dentro de una sala operativa y con luz 9+: semilla, batería y agua. Completa trigo y acércate antes de cambiar de receta. Recupera semilla y envases.',
                    'In an operating room with light 9+: seed, cell and water. Finish wheat and observe before changing recipes. Reuse the seed and carriers.'),
    'expedition': ('Recuperar una instalación', 'Recover a station'),
    'expedition.detail': ('Repara un equipo natural al 70 %, completa un lote y entrega informe, batería y tarjeta en su consola. Shift + mano vacía reclama. /expediciones explica.',
                          'Repair natural equipment to 70%, finish a batch and load a report, cell and data card into its console. Sneak-use empty-handed to claim. See /expediciones.'),
    'supplies': ('Reservas de soporte\n%s min · %s personas\nVolumen: %s\n\nO₂: %s bombonas\nEnergía: %s baterías\nAgua: %s bombonas\n\nIncluye arranque. No incluye trajes, industria, vehículos, calor ni fugas.',
                 'Life support supplies\n%s min · %s people\nVolume: %s\n\nO₂: %s canisters\nPower: %s cells\nWater: %s canisters\n\nIncludes startup. Excludes suits, industry, vehicles, heat and leaks.'),
}
for i, lang in enumerate(('es_es', 'en_us')):
    path = A / f'lang/{lang}.json'
    data = json.loads(path.read_text())
    data.update({'venus.guide.' + key: value[i] for key, value in texts.items()})
    data['balance.space_fabric.geothermal'] = (
        'Circuito geotérmico: devuelve el agua; requiere lava debajo.' if i == 0 else
        'Geothermal loop: returns water; requires lava below.')
    data['balance.space_fabric.composite'] = (
        'Compuesto: cerámica + 2 lingotes de hierro (20 s), o hierro denso (15 s).' if i == 0 else
        'Composite: ceramic + 2 iron ingots (20 s), or dense iron (15 s).')
    write(path, data)

# Craft before the first Venus landing; does not need an on-planet research report.
write(D / 'advancement/industry/navigator.json', {
    'parent': 'space_fabric:colony/root',
    'criteria': {'card': {'trigger': 'minecraft:inventory_changed', 'conditions': {
        'items': [{'items': 'space_fabric:blank_data_card'}]}}},
    'rewards': {'recipes': ['space_fabric:venus_navigator']},
})

def entry(name, low=1, high=1):
    value = {'type': 'minecraft:item', 'name': name}
    if high != 1 or low != 1:
        value['functions'] = [{'function': 'minecraft:set_count', 'count': {
            'type': 'minecraft:uniform', 'min': low, 'max': high}}]
    return value


contents = {
    'laboratory': [entry('minecraft:wheat_seeds', 2, 4), entry('space_fabric:empty_canister', 2, 3),
                   entry('space_fabric:blank_data_card')],
    'refuge': [entry('space_fabric:acid_filter'), entry('space_fabric:empty_power_cell', 1, 2),
               entry('space_fabric:machine_repair_kit')],
    'observatory': [entry('space_fabric:charged_power_cell'), entry('space_fabric:empty_canister', 1, 2),
                    entry('space_fabric:blank_data_card')],
}
for site, entries in contents.items():
    write(D / f'loot_table/chests/venus_{site}.json', {
        'type': 'minecraft:chest',
        'pools': [{'rolls': 1, 'entries': [e]} for e in entries],
    })

# Keep the visual guide tree connected to the real observation flags (no item rewards).
steps = [('arrival', 'venus_navigator'), ('shelter', 'habitat_controller'),
         ('power', 'geothermal_generator'), ('cooling', 'venus_heat_exchanger'),
         ('automation', 'machine_interface'), ('food', 'venus_greenhouse'),
         ('expedition', 'venus_expedition_console')]
for index, (name, icon) in enumerate(steps):
    data = {
        'display': {'icon': {'id': 'space_fabric:' + icon},
                    'title': {'translate': 'venus.guide.' + name},
                    'description': {'translate': 'venus.guide.safety' if index == 0 else 'venus.guide.' + name + '.detail'},
                    'frame': 'task', 'show_toast': True, 'announce_to_chat': False, 'hidden': False},
        'criteria': {'observed': {'trigger': 'minecraft:impossible'}},
    }
    if index == 0:
        data['display']['background'] = 'minecraft:gui/advancements/backgrounds/stone'
    else:
        data['parent'] = 'space_fabric:venus_guide/' + steps[index - 1][0]
    write(D / f'advancement/venus_guide/{name}.json', data)
