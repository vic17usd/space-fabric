#!/usr/bin/env python3
"""Package the image-generated art into game sprites; no repainting or palette changes.
Requires ImageMagick. Source art is a four-by-four atlas; raster cells are extracted
proportionally because the image provider need not return the requested dimensions.
"""
import json
import shutil
import subprocess
from pathlib import Path

BASE = Path(__file__).resolve().parents[1]
SOURCES = BASE/'tools/texture_sources/earth'
TEXTURES = BASE/'src/main/resources/assets/space_fabric/textures'

def import_atlases():
    magick = shutil.which('magick')
    if not magick:
        raise RuntimeError('ImageMagick (magick) is required to reimport Earth artwork; building the checked-in assets does not require it.')
    catalog = json.loads((BASE/'tools/earth_minerals.json').read_text())
    sheets = {}
    layouts = json.loads((SOURCES/'layout.json').read_text())
    for kind in ['ores','items']:
        source = SOURCES/f'reference_{kind}.png'
        dimensions = subprocess.check_output([magick,'identify','-format','%w %h',str(source)],text=True)
        width,height = map(int,dimensions.split())
        if width != height or width < 512:
            raise ValueError(f'Expected a square four-by-four source atlas: {source}')
        sheets[kind] = (source,width,height)
    imported = []
    def cell(kind,index,folder,name):
        source,width,height = sheets[kind]
        col,row = index%4,index//4
        # Source art is not always laid out on mathematically exact row divisions.
        # Explicit measured source bounds keep pieces of the previous sprite out of an ingot.
        layout = layouts[kind]
        x,y = round(layout['columns'][col]*width),round(layout['rows'][row]*height)
        right,bottom = round(layout['columns'][col+1]*width),round(layout['rows'][row+1]*height)
        target = TEXTURES/folder/(name+'.png')
        target.parent.mkdir(parents=True,exist_ok=True)
        resize = ['-filter','point','-resize','128x128!'] if kind=='ores' else [
            '-filter','point','-resize','128x128','-background','none','-gravity','center','-extent','128x128']
        subprocess.run([magick,str(source),'-crop',f'{right-x}x{bottom-y}+{x}+{y}','+repage',
            *resize,'-depth','8','-strip','PNG32:'+str(target)],check=True)
        imported.append(str(target.relative_to(BASE)))
    for index,mineral in enumerate(catalog):
        name = mineral['id']
        cell('ores',index,'block',name+'_ore')
        cell('ores',index+8,'block','deepslate_'+name+'_ore')
        if mineral['metal']:
            cell('items',index,'item','raw_'+name)
            cell('items',index+8,'item',mineral['metal']+'_ingot')
        else:
            cell('items',7,'item','fluorite_crystal')
    print(f'Imported {len(imported)} Earth textures at 128x128 from the two reference-style art sheets.')
    return imported

if __name__ == '__main__':
    import_atlases()
