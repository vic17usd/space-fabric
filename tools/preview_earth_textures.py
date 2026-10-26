#!/usr/bin/env python3
"""Optional contact sheet for review (requires Pillow); game assets need only stdlib."""
from PIL import Image, ImageDraw
from pathlib import Path
import json
BASE=Path(__file__).resolve().parents[1]
minerals=json.loads((BASE/'tools/earth_minerals.json').read_text())
canvas=Image.new('RGB',(720,8*86+48),(25,29,36));draw=ImageDraw.Draw(canvas)
for x,label in [(14,'Mineral'),(180,'Piedra'),(284,'Pizarra profunda'),(410,'En bruto'),(510,'Refinado'),(620,'Bloque')]:draw.text((x,15),label,fill='white')
for n,m in enumerate(minerals):
    y=48+n*86;name=m['id'];product=(m['metal']+'_ingot') if m['metal'] else name+'_crystal';storage=(m['metal'] or name)+'_block'
    draw.text((14,y+22),m['es'].capitalize(),fill='white')
    icons=[('block',name+'_ore'),('block','deepslate_'+name+'_ore'),('item',('raw_'+name) if m['metal'] else product),('item',product),('block',storage)]
    for x,(folder,file) in zip([180,292,410,510,620],icons):
        icon=Image.open(BASE/f'src/main/resources/assets/space_fabric/textures/{folder}/{file}.png').convert('RGBA').resize((64,64),Image.Resampling.NEAREST)
        canvas.paste(icon,(x,y),icon)
(BASE/'docs/assets').mkdir(exist_ok=True);canvas.save(BASE/'docs/assets/earth-geology-preview.png')
print('Preview written: docs/assets/earth-geology-preview.png')
