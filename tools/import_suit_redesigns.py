#!/usr/bin/env python3
"""Install the user's ready-made wearable PNGs unchanged, over a sealed lining.
No image processing dependency. Default reruns use checked-in PNG sources.
"""
import argparse,hashlib,json,shutil
from pathlib import Path
from zipfile import ZipFile
ROOT=Path(__file__).resolve().parents[1]
SOURCE=ROOT/'tools/texture_sources/suit_redesigns'
ASSETS=ROOT/'src/main/resources/assets/space_fabric'
TIERS=('venus','vacuum','abyssal','cryo')
LAYERS=('humanoid','humanoid_leggings')
def apply_redesign(refresh_lining=False):
 for tier in TIERS:
  equipment={}
  for layer in LAYERS:
   target=ASSETS/f'textures/entity/equipment/{layer}/{tier}_suit.png'
   lining=target.with_name(tier+'_suit_lining.png')
   if refresh_lining or not lining.exists():shutil.copyfile(target,lining)
   shutil.copyfile(SOURCE/tier/(layer+'.png'),target)
   equipment[layer]=[{'texture':'space_fabric:'+tier+'_suit_lining'},{'texture':'space_fabric:'+tier+'_suit'}]
  (ASSETS/f'equipment/{tier}_suit.json').write_text(json.dumps({'layers':equipment},indent=2)+'\n')
 # The inventory sheet follows the wearable redesign when present.
 from import_suit_inventory_icons import apply_inventory
 apply_inventory()
def main():
 parser=argparse.ArgumentParser(description=__doc__)
 for tier in TIERS:parser.add_argument('--'+tier,type=Path)
 args=parser.parse_args();manifest_path=SOURCE/'manifest.json'
 manifest=json.loads(manifest_path.read_text()) if manifest_path.exists() else {}
 for tier in TIERS:
  archive=getattr(args,tier)
  if archive is None:continue
  with ZipFile(archive) as z:
   entry={'archive':archive.name,'sha256':hashlib.sha256(archive.read_bytes()).hexdigest(),'textures':{}}
   for layer in LAYERS:
    data=z.read(layer+'.png');p=SOURCE/tier/(layer+'.png');p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(data)
    entry['textures'][layer]=hashlib.sha256(data).hexdigest()
   manifest[tier]=entry
 SOURCE.mkdir(parents=True,exist_ok=True);manifest_path.write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n')
 apply_redesign()
 print('Four supplied suit designs installed unchanged, over eight sealed lining textures.')
if __name__=='__main__':main()
