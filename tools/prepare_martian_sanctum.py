#!/usr/bin/env python3
"""Prepara cámara, altar y cinco residentes sin ejecutar el JAR fuente."""
import copy
import gzip
import json
from pathlib import Path
from convert_creativemode_structure import convert
ROOT = Path(__file__).resolve().parents[1]
ENTITIES = [{'id':'space_fabric:basalt_ember_golem','position':[26,9,26]}] + [
    {'id':'space_fabric:basalt_bosswarden','position':[x,4,z]} for x,z in [(26,16),(36,26),(26,36),(16,26)]]

def prepare(source):
    doc = copy.deepcopy(source)
    blocks = {tuple(b[:3]):source['palette'][b[3]] for b in source['blocks']}
    # Sella huecos externos y excava explícitamente el interior. No se toca la superficie.
    for x in range(53):
        for y in range(32):
            for z in range(53):
                blocks.setdefault((x,y,z), 'minecraft:air' if (x-26)**2+(z-26)**2 < 24**2 and 3 < y < 28 else 'space_fabric:martian_stone')
    for x in range(23,30):
        for z in range(23,30):
            blocks[x,8,z]='minecraft:crying_obsidian' if x in (23,29) or z in (23,29) else 'minecraft:obsidian'
            for y in range(9,20): blocks[x,y,z]='minecraft:air'
    # Escalinata ancha: el jefe y los guardias pueden bajar y volver al altar.
    for y in range(4,9):
        radius=11-y
        for x in range(26-radius,27+radius):
            for z in range(26-radius,27+radius):
                blocks[x,y,z]='minecraft:polished_basalt' if y < 8 else blocks[x,y,z]
    for x in range(19,34):
        for z in range(19,34):
            top=11-max(abs(x-26),abs(z-26),3)
            for y in range(top+1,20):blocks[x,y,z]='minecraft:air'
    for resident in ENTITIES[1:]:
        x,y,z=resident['position']
        for dx in range(-1,2):
            for dz in range(-1,2):
                blocks[x+dx,3,z+dz]='minecraft:deepslate_bricks'
                for dy in range(4): blocks[x+dx,y+dy,z+dz]='minecraft:air'
    for x,z in [(18,18),(34,18),(18,34),(34,34)]:
        blocks[x,4,z]='minecraft:soul_sand'; blocks[x,5,z]='minecraft:soul_fire'
    palette=list(dict.fromkeys(blocks.values())); indexes={v:i for i,v in enumerate(palette)}
    doc['palette']=palette;doc['blocks']=[[*pos,indexes[state]] for pos,state in sorted(blocks.items())];doc['block_count']=len(blocks)
    return doc

def main():
    source=json.loads((ROOT/'tools/structure_sources/soulfire_netherite_sanctum.json').read_text())
    doc=prepare(source)
    (ROOT/'src/main/resources/data/space_fabric/structure/soulfire_netherite_sanctum.nbt').write_bytes(gzip.compress(convert(doc,ENTITIES),mtime=0))
    print('Santuario:',doc['bounds']['dimensions'],doc['block_count'],'bloques incluido aire;',len(ENTITIES),'residentes.')
if __name__=='__main__':main()
