#!/usr/bin/env python3
"""Pack image-generated equipment art into item sprites and native humanoid UVs.
Uses ImageMagick for technical region extraction/resizing/UV assignment, not repainting.
"""
import json
import shutil
import subprocess
import tempfile
from pathlib import Path
BASE=Path(__file__).resolve().parents[1]
SOURCE=BASE/'tools/texture_sources/equipment'
ASSETS=BASE/'src/main/resources/assets/space_fabric'
TIERS=['vacuum','venus','cryo','abyssal']
PARTS=['helmet','chestplate','leggings','boots']
PLANETS=['mercury','venus','earth','moon','mars','jupiter','saturn','uranus','neptune']
COLORS={'vacuum':'#D5D9DC','venus':'#C39936','cryo':'#C9E2EC','abyssal':'#25314B'}
# Measured row bounds keep generated art in its own suit; coordinates refer to the saved panel sheet.
ROWS=[(0,304),(304,593),(593,886),(886,1122)]

def write(path,data):
    path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(data,indent=2)+'\n')

def import_art():
    magick=shutil.which('magick')
    if not magick:raise RuntimeError('ImageMagick is required to regenerate equipment artwork, not to build checked-in assets.')
    inventory=SOURCE/'inventory_atlas.png';panels=SOURCE/'armor_panels.png'
    w,h=map(int,subprocess.check_output([magick,'identify','-format','%w %h',str(inventory)],text=True).split())
    def sprite(index,name):
        col,row=index%5,index//5
        x,y=round(col*w/5),round(row*h/5);xx,yy=round((col+1)*w/5),round((row+1)*h/5)
        target=ASSETS/f'textures/item/{name}.png';target.parent.mkdir(parents=True,exist_ok=True)
        subprocess.run([magick,str(inventory),'-crop',f'{xx-x}x{yy-y}+{x}+{y}','+repage',
            '-filter','point','-resize','128x128!','-depth','8','-strip','PNG32:'+str(target)],check=True)
        write(ASSETS/f'models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/'+name}})
        write(ASSETS/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+name}})
    for row,tier in enumerate(TIERS):
        for col,part in enumerate(PARTS):sprite(row*5+col,tier+'_suit_'+part)
    for index,planet in zip([4,9,14,19,20,21,22,23,24],PLANETS):sprite(index,planet+'_rocket_kit')
    # UV coordinates below follow the native 64x32 humanoid model, at 4x resolution.
    # Explicit face packing keeps the front visor/chest console on the correct model faces.
    for tier,(start,end) in zip(TIERS,ROWS):
        height=end-start
        def region(x,ya,xx,yb):return (x,round(start+height*ya),xx,round(start+height*yb))
        regions={
            'visor':region(174,.025,292,.36),
            'head':region(178,.38,274,.56),
            'front':region(336,.59,451,.985),
            'back':region(452,.60,532,.985),
            'side':region(284,.60,331,.985),
            'cap':region(452,.60,532,.69),
            'arm':region(56,.38,112,.56),
            'arm_back':region(113,.38,169,.56),
            'boot':region(56,.72,112,.985),
            'boot_back':region(114,.72,168,.985),
            'sole':region(56,.93,112,.985),
            'leg':region(758,.38,851,.985),
            'leg_back':region(935,.38,1023,.985),
            'leg_side':region(852,.38,892,.985),
            'belt':region(1040,.82,1334,.985),
        }
        with tempfile.TemporaryDirectory(prefix='suit-uv-') as temp:
            patches={}
            def pack(layer,assignments):
                args=[magick,'-size','256x128','xc:none']
                for number,(source,rect) in enumerate(assignments):
                    x,y,ww,hh=rect;key=(source,ww,hh)
                    if key not in patches:
                        sx,sy,xx,yy=regions[source];patch=Path(temp)/f'{len(patches)}.png'
                        subprocess.run([magick,str(panels),'-crop',f'{xx-sx}x{yy-sy}+{sx}+{sy}','+repage',
                            '-filter','point','-resize',f'{ww*4}x{hh*4}!',
                            '-background',COLORS[tier],'-alpha','remove','-alpha','on','-depth','8','-strip','PNG32:'+str(patch)],check=True)
                        patches[key]=patch
                    args += [str(patches[key]),'-geometry',f'+{x*4}+{y*4}','-compose','over','-composite']
                target=ASSETS/f'textures/entity/equipment/{layer}/{tier}_suit.png';target.parent.mkdir(parents=True,exist_ok=True)
                subprocess.run(args+['-depth','8','-strip','PNG32:'+str(target)],check=True)
            def cube(u,v,dx,dy,dz,front,back,side,cap):
                return [(cap,(u+dz,v,dx,dz)),(cap,(u+dz+dx,v,dx,dz)),
                    (side,(u,v+dz,dz,dy)),(front,(u+dz,v+dz,dx,dy)),
                    (side,(u+dz+dx,v+dz,dz,dy)),(back,(u+2*dz+dx,v+dz,dx,dy))]
            top=cube(0,0,8,8,8,'visor','head','head','cap')
            top+=cube(16,16,8,12,4,'front','back','side','cap')
            top+=cube(40,16,4,12,4,'arm','arm_back','side','cap')
            for x,name in [(0,'boot_back'),(4,'boot'),(8,'boot_back'),(12,'boot_back')]:top.append((name,(x,28,4,4)))
            top.append(('sole',(8,16,4,4)))
            pack('humanoid',top)
            legs=cube(0,16,4,12,4,'leg','leg_back','leg_side','cap')
            for x,ww in [(16,4),(20,8),(28,4),(32,8)]:legs.append(('belt',(x,28,ww,4)))
            legs.append(('belt',(28,16,8,4)))
            pack('humanoid_leggings',legs)
        write(ASSETS/f'equipment/{tier}_suit.json',{'layers':{
            'humanoid':[{'texture':'space_fabric:'+tier+'_suit'}],
            'humanoid_leggings':[{'texture':'space_fabric:'+tier+'_suit'}]}})
    print('Equipment: 25 item sprites, eight wearable UV layers and four equipment assets imported.')

if __name__=='__main__':import_art()
