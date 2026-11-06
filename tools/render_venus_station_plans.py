#!/usr/bin/env python3
"""Draw an explanatory floor plan from the actual generated station blocks (not a game screenshot)."""
from html import escape
from pathlib import Path
from venus_station_interiors import station

OUT = Path(__file__).resolve().parents[1] / 'docs/images/venus-station-interiors.svg'
parts = ['<svg xmlns="http://www.w3.org/2000/svg" width="1120" height="510" viewBox="0 0 1120 510">',
         '<rect width="1120" height="510" fill="#12212b"/>',
         '<g font-family="sans-serif" fill="#e8f0ef">',
         '<text x="24" y="32" font-size="20">Venus: interiores de las expediciones</text>',
         '<text x="24" y="56" font-size="13">Plano de los bloques generados · Esquema, no captura del juego · Norte arriba</text>']
colors = {'air': '#30424b', 'venus_composite_hull': '#abb8b9', 'pressure_window': '#76cbd3',
          'airlock_door': '#eabd55', 'chest': '#ba8057', 'habitat_controller': '#55d1b4',
          'venus_expedition_console': '#b59bdd'}
symbols = {'habitat_controller': 'C', 'venus_expedition_console': 'M', 'chest': 'A',
           'venus_heat_exchanger': 'F', 'venus_greenhouse': 'H', 'venus_life_reclaimer': 'R',
           'sample_analyzer': 'L', 'venus_atmospheric_collector': 'G', 'venus_service_dock': 'V'}
titles = ['Laboratorio de Venus', 'Refugio de recuperación', 'Estación atmosférica']
notes = ['Muestras, cultivo y laboratorio', 'Literas, depósitos y recuperación', 'Observación, colector exterior y aterrizaje']
for index, title in enumerate(titles):
    mission = index + 1
    blocks, _, _ = station(mission)
    size = 29 if mission == 3 else 13
    cell = 10 if mission == 3 else 22
    ox, oz = 24 + index * 370, 110
    parts.append(f'<text x="{ox}" y="90" font-size="17">{escape(title)}</text>')
    for x in range(size):
        for z in range(size):
            y = 2 if 1 <= x <= 11 and 1 <= z <= 11 else 1
            name = blocks.get((x, y, z), blocks.get((x, 0, z), 'minecraft:air')).split(':')[1]
            color = colors.get(name, '#688491' if name in symbols else '#57636b')
            if name == 'orange_concrete':
                color = '#df9f54'
            px, pz = ox + x * cell, oz + z * cell
            parts.append(f'<rect x="{px}" y="{pz}" width="{cell-1}" height="{cell-1}" fill="{color}"/>')
            if name in symbols:
                parts.append(f'<text x="{px+cell/2}" y="{pz+cell*.75}" text-anchor="middle" fill="#101d25" font-size="{max(8, cell*.65)}">{symbols[name]}</text>')
    parts.append(f'<text x="{ox}" y="420" font-size="12">{escape(notes[index])}</text>')
parts.extend(['<text x="24" y="455" font-size="13">C: controlador · M: misión · F: intercambiador · A: almacén · L: analizador</text>',
              '<text x="24" y="477" font-size="13">H: invernadero · R: recuperador · G: colector · V: servicio · Amarillo: puertas</text>',
              '<text x="24" y="499" font-size="12">Los diseños nuevos requieren reparar máquinas y aportar reservas. La guía /venus explica la puesta en marcha.</text>',
              '</g></svg>'])
OUT.parent.mkdir(parents=True, exist_ok=True)
OUT.write_text('\n'.join(parts) + '\n')
print(OUT)
