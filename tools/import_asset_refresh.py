#!/usr/bin/env python3
"""Apply the four supplied asset-only replacements; never load source mod code."""
import argparse,hashlib,json,shutil
from pathlib import Path
from zipfile import ZipFile
ROOT=Path(__file__).resolve().parents[1]
CACHE=ROOT/'tools/import_sources/asset_refresh'
A=ROOT/'src/main/resources/assets/space_fabric'
SOURCES={'bauxite_ore':'Mena de bauxita.jar','ares_crystal_ore':'Mena de ares.jar','venus':'Traje espacial para venus.jar','vacuum':'Traje espacial nivel 1.jar'}
def write(p,d):
 p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
def copy(src,dst):
 dst.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(src,dst)
def apply_suits():
 for tier in ('venus','vacuum'):
  c=CACHE/tier
  if not c.exists():continue
  meta=json.loads((c/'fabric.mod.json').read_text());ns=meta['id'];base=c/'assets'/ns
  equipment=json.loads(next((base/'equipment').glob('*.json')).read_text())
  for layer in ('humanoid','humanoid_leggings'):
   ref=equipment['layers'][layer][0]['texture'].split(':',1)[1]
   source=(base/'textures/entity/equipment'/layer/(ref+'.png')).resolve()
   if not source.is_relative_to(base.resolve()):raise ValueError('Equipment path outside source assets')
   for dest in (A/f'textures/entity/equipment/{layer}/{tier}_suit.png',ROOT/f'tools/texture_sources/suit_redesigns/{tier}/{layer}.png'):
    copy(source,dest)
  manifest_path=ROOT/'tools/texture_sources/suit_redesigns/manifest.json'
  manifest=json.loads(manifest_path.read_text())
  manifest[tier]={'archive':SOURCES[tier],'source_id':ns,'textures':{layer:hashlib.sha256((ROOT/f'tools/texture_sources/suit_redesigns/{tier}/{layer}.png').read_bytes()).hexdigest() for layer in ('humanoid','humanoid_leggings')}}
  write(manifest_path,manifest)
  for part in ('helmet','chestplate','leggings','boots'):
   source=next((base/'textures/item').glob('*_'+part+'.png'))
   ids=[tier+'_suit_'+part]
   if tier=='venus':ids+=['reinforced_venus_suit_'+part]
   for item in ids:
    copy(source,A/f'textures/item/{item}.png')
    write(A/f'models/item/{item}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/'+item}})
    write(A/f'items/{item}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+item}})
def apply():
 for target in ('bauxite_ore','ares_crystal_ore'):
  c=CACHE/target
  if not c.exists():continue
  ns=json.loads((c/'fabric.mod.json').read_text())['id'];base=c/'assets'/ns
  doc=json.loads(next((base/'models/block').glob('*.json')).read_text())
  for slot,ref in doc['textures'].items():
   if not ref.startswith(ns+':'):raise ValueError('Unexpected texture: '+ref)
   path=ref.split(':',1)[1];src=base/'textures'/(path+'.png')
   copy(src,A/f'textures/block/asset_refresh/{target}/{path}.png')
   doc['textures'][slot]=f'space_fabric:block/asset_refresh/{target}/{path}'
  write(A/f'models/block/{target}.json',doc)
 apply_suits()
def main():
 p=argparse.ArgumentParser();p.add_argument('--source-root',type=Path);args=p.parse_args()
 if args.source_root:
  manifest={}
  for target,name in SOURCES.items():
   matches=list(args.source_root.rglob(name))
   if len(matches)!=1:raise ValueError('Expected one input '+name)
   jar=matches[0]
   with ZipFile(jar) as z:
    meta=json.loads(z.read('fabric.mod.json'));ns=meta['id']
    for entry in z.namelist():
     if not entry.endswith('/') and (entry=='fabric.mod.json' or entry.startswith('assets/'+ns+'/') or entry.startswith('LICENSE')):
      dst=CACHE/target/entry
      if not dst.resolve().is_relative_to((CACHE/target).resolve()):raise ValueError('Invalid archive path')
      dst.parent.mkdir(parents=True,exist_ok=True);dst.write_bytes(z.read(entry))
    manifest[target]={'file':name,'sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'source_id':ns,'authors':meta.get('authors'),'license':meta.get('license'),'contact':meta.get('contact')}
    for license in (CACHE/target).glob('LICENSE*'):copy(license,A/'licenses/asset_refresh'/target/license.name)
  write(ROOT/'third_party/asset_refresh/manifest.json',manifest)
 apply()
 print('Applied bauxite, Ares, Venus suit and level-one vacuum suit assets.')
if __name__=='__main__':main()
