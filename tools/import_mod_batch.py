#!/usr/bin/env python3
"""Import the user's supplied replacement assets as data, never execute JAR code."""
import argparse,base64,hashlib,io,json,math,shutil
from pathlib import Path
from zipfile import ZipFile
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/space_fabric';S=ROOT/'tools/import_sources/mod_batch'
BLOCKS={
 'acido sulfurico.jar':'condensed_sulfuric_acid','acumulador electrico.jar':'energy_tank',
 'basalto terrestre.jar':'terrestrial_basalt','Cargador solar.jar':'solar_charger','CO2 congelado marciano.jar':'martian_frozen_co2',
 'Consola de expediciones.jar':'venus_expedition_console','Controlador de habitat.jar':'habitat_controller',
 'cristal de ares.jar':'ares_crystal_ore','Deposito de azufre.jar':'sulfur_deposit',
 'Deposito de sulfuto marciano.jar':'martian_sulfate_deposit',
 'electrolizador de agua.jar':'water_electrolyzer','Generador geotermico.jar':'geothermal_generator',
 'hielo de crater eterno.jar':'crater_ice','Hielo de los anillos de saturno.jar':'saturn_ring_ice',
 'Hielo glaciar marciano.jar':'martian_glacier_ice','Hielo subterraneo marciano.jar':'martian_ground_ice',
 'Horno de ceramica venusiana.jar':'venus_ceramic_kiln','Mena de fobosita.jar':'phobosite_ore',
 'Mena de hierro denso.jar':'dense_iron_ore','Mena de piroxeno.jar':'pyroxene_ore',
 'meteorito joviano.jar':'jovian_meteorite','Neptuno hielo de metano.jar':'neptunian_methane_ice',
 'Panel de compuesto venusiano.jar':'venus_composite_hull','Panel presurizado.jar':'pressure_hull',
 'Piedra con olivino marciano.jar':'martian_olivine_stone','refineria de combustible.jar':'fuel_refinery',
 'Regolito de oxido marciano.jar':'iron_oxide_regolith','silicatos de mercurio.jar':'mercury_silicate',
 'Unidad de refrigeracion venusiana.jar':'venus_cooling_unit','Urano hielo de metano.jar':'uranian_methane_ice'}
ITEMS={'Celda de energia vacia.png':'empty_power_cell','Celda de energia.png':'charged_power_cell',
 'Cristal de ares.png':'ares_crystal','Cristal de escarcha marciano.png':'martian_frost_crystal','Fobosita en bruto.png':'raw_phobosite',
 'Fragmento de cristal de energia.png':'energy_crystal_shard','Fragmento de hielo de crater.png':'crater_ice_shard',
 'mineral de hierro triturado.png':'crushed_iron_ore','Nucleo de azufre.png':'sulfur_core','Olivino venusiano.png':'venusian_olivine',
 'Polvo estelar condensado.png':'condensed_stardust','Sensor de vibracion.png':'vibration_sensor','Trozo de hierro denso.png':'dense_iron_chunk'}
for planet,filename in [('jupiter','Kit del cohete de jupiter.png'),('moon','kit del cohete de la luna.png'),('mars','Kit del cohete de marte.png'),('mercury','kit del cohete de mercurio.png'),('neptune','Kit del cohete de neptuno.png'),('saturn','Kit del cohete de saturno.png'),('uranus','Kit del cohete de urano.png'),('venus','Kit del cohete de venus.png')]:ITEMS[filename]=planet+'_rocket_kit'
MODELS={'Perforadora de sondeo venus.bbmodel':'venus_core_drill','Separador de gases industrial.bbmodel':'venus_gas_separator'}
def write(p,o):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(o,ensure_ascii=False,indent=2)+'\n')
def store(path,data):p=S/path;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(data)
def block_model(name,target):
 doc=json.loads((S/'blocks'/target/'model.json').read_text());source=json.loads((S/'blocks'/target/'source.json').read_text());prefix=source['id']+':block/'
 textures=list((S/'blocks'/target/'textures').glob('*.png'))
 if not doc.get('elements') and not any(source['id']+':' in str(v) for v in doc.get('textures',{}).values()):
  if len(textures)!=1:raise ValueError('Cannot infer the texture for '+target)
  doc={'parent':'minecraft:block/cube_all','textures':{'all':source['id']+':block/'+textures[0].stem}}
 if doc.get('elements') and not doc.get('parent'):doc['parent']='minecraft:block/block'
 if doc.get('elements') and 'particle' not in doc.get('textures',{}):
  doc['textures']['particle']='#'+next(iter(doc['textures']))
 for k,v in doc['textures'].items():
  if isinstance(v,str) and v.startswith(prefix):doc['textures'][k]='space_fabric:block/replacements/'+target+'/'+v[len(prefix):]
 for p in (S/'blocks'/target/'textures').glob('*.png'):
  out=A/'textures/block/replacements'/target/p.name;out.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(p,out)
 write(A/'models/block'/f'{target}.json',doc)
def geometry(source,target):
 d=json.loads((S/'models'/source).read_text());elements=d['elements'];lo=[min(e['from'][i] for e in elements) for i in range(3)];hi=[max(e['to'][i] for e in elements) for i in range(3)]
 scale=16/max(hi[i]-lo[i] for i in range(3));offset=[8-(lo[0]+hi[0])*scale/2,-lo[1]*scale,8-(lo[2]+hi[2])*scale/2]
 def point(p):return [round(p[i]*scale+offset[i],6) for i in range(3)]
 texture=d['textures'][0];raw=base64.b64decode(texture['source'].split(',',1)[1],validate=True);out=A/'textures/block/replacements'/target/'atlas.png';out.parent.mkdir(parents=True,exist_ok=True);out.write_bytes(raw)
 w,h=d['resolution']['width'],d['resolution']['height'];mesh=[]
 for e in elements:
  if e.get('type')!='cube':raise ValueError('Unexpected non-cube geometry')
  cube={'from':point(e['from']),'to':point(e['to']),'faces':{}}
  rotation=e.get('rotation',[0,0,0]);axes=[i for i,v in enumerate(rotation) if v]
  if len(axes)>1:raise ValueError('Multiple rotation axes cannot be expressed in native block models')
  if axes:
   i=axes[0];angle=rotation[i]
   if angle not in (-45,-22.5,22.5,45):raise ValueError('Unsupported rotation angle')
   cube['rotation']={'origin':point(e['origin']),'axis':'xyz'[i],'angle':angle}
  for face,data in e['faces'].items():
   uv=data['uv'];entry={'uv':[round(uv[0]*16/w,6),round(uv[1]*16/h,6),round(uv[2]*16/w,6),round(uv[3]*16/h,6)],'texture':'#0'}
   if data.get('rotation'):entry['rotation']=data['rotation']
   cube['faces'][face]=entry
  mesh.append(cube)
 write(A/'models/block'/f'{target}.json',{'parent':'minecraft:block/block','textures':{'particle':'space_fabric:block/replacements/'+target+'/atlas','0':'space_fabric:block/replacements/'+target+'/atlas'},'elements':mesh})
 return {'elements':len(mesh),'source_bounds':[lo,hi],'scale':scale}
def main():
 p=argparse.ArgumentParser();p.add_argument('--zip',type=Path);args=p.parse_args();manifest={}
 if args.zip:
  with ZipFile(args.zip) as outer:
   for filename,target in BLOCKS.items():
    data=outer.read('Mod/'+filename)
    with ZipFile(io.BytesIO(data)) as z:
     metadata=json.loads(z.read('fabric.mod.json'));ns=metadata['id']
     models=[n for n in z.namelist() if n.startswith('assets/'+ns+'/models/block/') and n.endswith('.json')]
     if len(models)!=1:raise ValueError(filename+' needs one model')
     store(Path('blocks')/target/'model.json',z.read(models[0]));store(Path('blocks')/target/'source.json',json.dumps({'id':ns,'authors':metadata['authors'],'license':metadata['license'],'url':metadata.get('contact',{}).get('sources')}).encode())
     for n in z.namelist():
      if n.startswith('assets/'+ns+'/textures/block/') and n.endswith('.png'):store(Path('blocks')/target/'textures'/Path(n).name,z.read(n))
      if n.startswith('LICENSE'):store(Path('blocks')/target/'LICENSE.txt',z.read(n))
     manifest[filename]={'target':target,'source_id':ns,'sha256':hashlib.sha256(data).hexdigest(),'url':metadata.get('contact',{}).get('sources'),'license':metadata.get('license')}
   for filename,target in ITEMS.items():
    data=outer.read('Mod/'+filename);store(Path('items')/filename,data);manifest[filename]={'target':target,'sha256':hashlib.sha256(data).hexdigest()}
   for filename,target in MODELS.items():
    data=outer.read('Mod/'+filename);store(Path('models')/filename,data);manifest[filename]={'target':target,'sha256':hashlib.sha256(data).hexdigest()}
   # Keep the two nontrivial cases for explicit review.
   for filename in ['glacial-frontier-kzj32637.jar']:
    data=outer.read('Mod/'+filename);manifest[filename]={'status':'previously_integrated','sha256':hashlib.sha256(data).hexdigest()}
  write(ROOT/'third_party/mod_batch/manifest.json',manifest)
 for name,target in BLOCKS.items():block_model(name,target)
 for name,target in ITEMS.items():
  shutil.copyfile(S/'items'/name,A/'textures/item'/f'{target}.png')
  write(A/'models/item'/f'{target}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/'+target}})
  write(A/'items'/f'{target}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+target}})
 for name,target in MODELS.items():print(target,geometry(name,target))
 for p in (S/'blocks').rglob('LICENSE.txt'):
  out=A/'licenses/mod_batch'/p.parent.name/'LICENSE.txt';out.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(p,out)
 print('Imported',len(BLOCKS),'block appearances,',len(ITEMS),'item textures and',len(MODELS),'3D models.')
if __name__=='__main__':main()
