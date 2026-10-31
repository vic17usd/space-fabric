#!/usr/bin/env python3
"""Pack image-generated item art into native inventory sprites, without repainting it."""
import argparse,hashlib,json,shutil,subprocess
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
SOURCE=ROOT/'tools/texture_sources/suit_inventory_redesign.png'
A=ROOT/'src/main/resources/assets/space_fabric'
TIERS=('vacuum','venus','cryo','abyssal');PARTS=('helmet','chestplate','leggings','boots')
def write(p,data):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(data,indent=2)+'\n')
def apply_inventory():
 if not SOURCE.exists():return
 magick=shutil.which('magick')
 if not magick:raise RuntimeError('ImageMagick is required only to reimport the inventory sheet.')
 w,h=map(int,subprocess.check_output([magick,'identify','-format','%w %h',str(SOURCE)],text=True).split())
 if w!=h:raise ValueError('Expected a square inventory sheet')
 # Measured transparent separators keep wide shoulders inside their own sprite.
 cuts=([0,314,633,940,1254],[0,314,639,940,1254],[0,314,640,940,1254],[0,314,640,940,1254]) if w==1254 else [list(round(i*w/4) for i in range(5))]*4
 for row,tier in enumerate(TIERS):
  for col,part in enumerate(PARTS):
   name=f'{tier}_suit_{part}';out=A/f'textures/item/{name}.png';out.parent.mkdir(parents=True,exist_ok=True)
   x=cuts[row][col];xx=cuts[row][col+1];y=round(row*h/4);yy=round((row+1)*h/4)
   subprocess.run([magick,str(SOURCE),'-crop',f'{xx-x}x{yy-y}+{x}+{y}','+repage','-trim','+repage','-filter','point','-resize','112x112','-gravity','center','-background','none','-extent','128x128','-depth','8','-strip','PNG32:'+str(out)],check=True)
   write(A/f'models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/'+name}})
   write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+name}})
 for part in PARTS:
  name='reinforced_venus_suit_'+part
  shutil.copyfile(A/f'textures/item/venus_suit_{part}.png',A/f'textures/item/{name}.png')
  write(A/f'models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/'+name}})
  write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+name}})
 write(ROOT/'tools/texture_sources/suit_inventory_redesign.json',{'source':SOURCE.name,'sha256':hashlib.sha256(SOURCE.read_bytes()).hexdigest(),'rows':TIERS,'columns':PARTS,'reinforced_venus':'Same artwork as Venus, matching its shared wearable design','icon_size':128})
 print('Updated 20 suit inventory sprites from the new designs.')
def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--sheet',type=Path);args=parser.parse_args()
 if args.sheet:shutil.copyfile(args.sheet,SOURCE)
 apply_inventory()
if __name__=='__main__':main()
