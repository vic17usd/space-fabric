#!/usr/bin/env python3
"""Import the supplied Colossus art and decompiled rig as native classes; never execute the source mod."""
import argparse,hashlib,json
from pathlib import Path
from zipfile import ZipFile
ROOT=Path(__file__).resolve().parents[1];S=ROOT/'tools/import_sources/venus_colossus';A=ROOT/'src/main/resources/assets/space_fabric'
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
def main():
 parser=argparse.ArgumentParser();parser.add_argument('--jar',type=Path);parser.add_argument('--decompiled',type=Path);args=parser.parse_args();ns='sulfur-colossus-3bbkryhu'
 if args.jar:
  S.mkdir(parents=True,exist_ok=True)
  with ZipFile(args.jar) as z:
   meta=json.loads(z.read('fabric.mod.json'));assert meta['id']==ns;write(S/'fabric.mod.json',meta)
   for name in z.namelist():
    if name.startswith('LICENSE'):(S/'LICENSE.txt').write_bytes(z.read(name))
    if name.startswith(f'assets/{ns}/') and (name.endswith(('.png','.ogg','.bbmodel','.mob.json','.source.json','gameplay-contract.json')) or name.startswith(f'assets/{ns}/models/item/') and name.endswith('.json')):
     p=S/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(z.read(name))
  write(ROOT/'third_party/venus_colossus/manifest.json',{'source':args.jar.name,'sha256':hashlib.sha256(args.jar.read_bytes()).hexdigest(),'authors':meta['authors'],'url':meta['contact']['sources'],'license':meta['license'],'target':'space_fabric:sulfur_colossus','adaptation':'Native rig and original animations; Venus immunities, underground arena, local burst heights and server-controlled rewards.'})
  license_out=ROOT/'third_party/venus_colossus/LICENSE.txt';license_out.write_bytes((S/'LICENSE.txt').read_bytes())
 if args.decompiled:
  for p in (args.decompiled/'sulfur_colossus_3bbkryhu/entity').rglob('*.java'):
   if p.name=='SulfurColossusEntity.java':continue # Gameplay is adapted separately, never overwrite it.
   s=p.read_text().replace('sulfur_colossus_3bbkryhu.entity.renderer','dev.vic17usd.spacefabric.client.colossus').replace('sulfur_colossus_3bbkryhu.entity','dev.vic17usd.spacefabric.entity.colossus').replace('"sulfur-colossus-3bbkryhu"','"space_fabric"').replace('sulfur_colossus_3bbkryhu.png','sulfur_colossus.png')
   if p.name=='SulfurColossusRigEntity.java':
    s=s.replace('extends PathfinderMob','extends dev.vic17usd.spacefabric.entity.venus.VenusMob').replace('EntityType<? extends PathfinderMob>','EntityType<? extends dev.vic17usd.spacefabric.entity.venus.VenusMob>')
   if p.name=='SulfurColossusRenderState.java':s=s.replace('   public int actionIndex','   public boolean coreExposed;\n   public int actionIndex')
   if p.name=='SulfurColossusEntityRenderer.java':s=s.replace('      state.actionIndex =','      state.coreExposed=entity.isCoreExposed();\n      state.actionIndex =')
   if p.name=='SulfurColossusEntityModel.java':
    s=s.replace('   private static final float TEXTURE_WIDTH','   private final ModelPart chestLeft=root().createPartLookup().apply("chest_left");\n   private final ModelPart chestRight=root().createPartLookup().apply("chest_right");\n   private static final float TEXTURE_WIDTH')
    s=s.replace('         this.idleAnimation.apply(ambientMillis, 1.0F);\n      }\n   }','         this.idleAnimation.apply(ambientMillis, 1.0F);\n      }\n      // The vulnerable recovery window can outlast the attack animation.\n      if(state.coreExposed && state.actionIndex<0) {\n         chestLeft.yRot-=0.65F;\n         chestRight.yRot+=0.65F;\n      }\n   }')
   target=ROOT/'src/main/java/dev/vic17usd/spacefabric'/('client/colossus' if p.parent.name=='renderer' else 'entity/colossus')/p.name;target.parent.mkdir(parents=True,exist_ok=True);target.write_text(s)
 for old,new in [('textures/entity/sulfur_colossus_3bbkryhu.png','textures/entity/sulfur_colossus.png'),('textures/item/sulfur_colossus_3bbkryhu_spawn_egg.png','textures/item/sulfur_colossus_spawn_egg.png'),('sounds/sulfur_vent_hiss.ogg','sounds/sulfur_vent_hiss.ogg')]:
  p=A/new;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes((S/f'assets/{ns}/{old}').read_bytes())
 for name in ['colossus_core','sulfur_colossus_spawn_egg']:
  if name=='colossus_core':model=json.loads((S/f'assets/{ns}/models/item/colossus_core.json').read_text())
  else:model={'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/sulfur_colossus_spawn_egg'}}
  write(A/f'models/item/{name}.json',model);write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+name}})
 write(ROOT/'src/main/resources/data/space_fabric/loot_table/entities/sulfur_colossus.json',{'type':'minecraft:entity','pools':[]})
 p=A/'sounds.json';data=json.loads(p.read_text()) if p.exists() else {};data['sulfur_vent_hiss']={'sounds':['space_fabric:sulfur_vent_hiss']};write(p,data)
 p=A/'licenses/venus_colossus/LICENSE.txt';p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes((S/'LICENSE.txt').read_bytes());(p.parent/'SOURCES.txt').write_text('Original design: CreativeMode.net\nhttps://creativemode.net/mod/sulfur-colossus-3bbkryhu\n')
if __name__=='__main__':main()
