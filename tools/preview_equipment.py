#!/usr/bin/env python3
"""Review sheet and front UV projection, not an in-game screenshot (Pillow optional)."""
from pathlib import Path
from PIL import Image,ImageDraw
BASE=Path(__file__).resolve().parents[1]
TEX=BASE/'src/main/resources/assets/space_fabric/textures'
TIERS=['vacuum','venus','cryo','abyssal'];NAMES=['Vacío','Venus','Criogénico','Abisal']
canvas=Image.new('RGB',(900,650),(24,29,38));draw=ImageDraw.Draw(canvas)
draw.text((16,12),'Trajes: iconos y proyección frontal de las UV equipadas',fill='white')
for index,(tier,name) in enumerate(zip(TIERS,NAMES)):
    x=16+index*220;draw.text((x,40),name,fill='white')
    for n,part in enumerate(['helmet','chestplate','leggings','boots']):
        icon=Image.open(TEX/f'item/{tier}_suit_{part}.png').convert('RGBA').resize((48,48),Image.Resampling.NEAREST)
        canvas.paste(icon,(x+n*50,61),icon)
    upper=Image.open(TEX/f'entity/equipment/humanoid/{tier}_suit.png').convert('RGBA')
    lower=Image.open(TEX/f'entity/equipment/humanoid_leggings/{tier}_suit.png').convert('RGBA')
    figure=Image.new('RGBA',(64,128))
    def face(source,rect,pos,flip=False):
        part=source.crop(tuple(v*4 for v in rect))
        if flip:part=part.transpose(Image.Transpose.FLIP_LEFT_RIGHT)
        figure.alpha_composite(part,(pos[0]*4,pos[1]*4))
    face(upper,(8,8,16,16),(4,0));face(upper,(20,20,28,32),(4,8))
    face(upper,(44,20,48,32),(0,8));face(upper,(44,20,48,32),(12,8),True)
    face(lower,(4,20,8,32),(4,20));face(lower,(4,20,8,32),(8,20),True)
    face(upper,(4,28,8,32),(4,28));face(upper,(4,28,8,32),(8,28),True)
    figure=figure.resize((128,256),Image.Resampling.NEAREST);canvas.paste(figure,(x+36,128),figure)
draw.text((16,412),'Kits de mejora planetarios',fill='white')
for index,(planet,name) in enumerate(zip(['mercury','venus','earth','moon','mars','jupiter','saturn','uranus','neptune'],['Mercurio','Venus','Tierra','Luna','Marte','Júpiter','Saturno','Urano','Neptuno'])):
    x=16+index*98;icon=Image.open(TEX/f'item/{planet}_rocket_kit.png').convert('RGBA').resize((80,80),Image.Resampling.NEAREST)
    canvas.paste(icon,(x,443),icon);draw.text((x,532),name,fill='white')
draw.text((16,596),'Los colores y caras se leen de los PNG que utiliza el mod. No es una captura de Minecraft.',fill=(162,182,198))
p=BASE/'docs/assets/equipment-preview.png';p.parent.mkdir(exist_ok=True);canvas.save(p);print(p)
