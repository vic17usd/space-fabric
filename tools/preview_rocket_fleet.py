#!/usr/bin/env python3
"""CPU reference render of the shipped meshes/textures; not an in-game screenshot."""
import json
from pathlib import Path
import numpy as np
from PIL import Image,ImageDraw,ImageFont
ROOT=Path(__file__).resolve().parents[1];ASSETS=ROOT/'src/main/resources/assets/space_fabric'
ORDER=['base','earth','moon','venus','mars','mercury','jupiter','saturn','uranus','neptune']
NAMES=['Basico','Tierra','Luna','Venus','Marte','Mercurio','Jupiter','Saturno','Urano','Neptuno']
view=np.array([[.8,0,-.6],[-.223,.928,-.297],[.557,.371,.743]])
canvas=Image.new('RGB',(1250,680),(15,24,34));draw=ImageDraw.Draw(canvas)
font=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',16)
small=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',12)
manifest=json.loads((ROOT/'tools/rocket_fleet_manifest.json').read_text())
for index,(variant,name) in enumerate(zip(ORDER,NAMES)):
 x0=(index%5)*250;y0=(index//5)*330
 draw.rounded_rectangle((x0+5,y0+5,x0+245,y0+325),10,fill=(22,36,47),outline=(59,83,99))
 doc=json.loads((ASSETS/f'rocket_mesh/{variant}.json').read_text())
 tex=np.asarray(Image.open(ASSETS/doc['texture'].split(':')[1]).convert('RGBA'))
 pixels=np.zeros((260,230,4),dtype=np.uint8);zbuf=np.full((260,230),-np.inf)
 for q in doc['quads']:
  normal=np.array(q[:3]);verts=np.array(q[4:]).reshape(4,5);projection=verts[:,:3]@view.T
  xy=np.stack([projection[:,0]*60+115,230-projection[:,1]*60],axis=1)
  shade=.55+.45*max(0,float(normal@np.array([.3,.8,.5])/1.01))
  tint=np.array([(q[3]>>16)&255,(q[3]>>8)&255,q[3]&255])/255
  for ids in [[0,1,2],[0,2,3]]:
   p=xy[ids];uv=verts[ids,3:];depth=projection[ids,2]
   low=np.maximum(np.floor(p.min(axis=0)).astype(int),[0,0]);high=np.minimum(np.ceil(p.max(axis=0)).astype(int),[229,259])
   if (high<low).any():continue
   denominator=(p[1,1]-p[2,1])*(p[0,0]-p[2,0])+(p[2,0]-p[1,0])*(p[0,1]-p[2,1])
   if abs(denominator)<1e-8:continue
   yy,xx=np.mgrid[low[1]:high[1]+1,low[0]:high[0]+1];xx=xx+.5;yy=yy+.5
   a=((p[1,1]-p[2,1])*(xx-p[2,0])+(p[2,0]-p[1,0])*(yy-p[2,1]))/denominator
   b=((p[2,1]-p[0,1])*(xx-p[2,0])+(p[0,0]-p[2,0])*(yy-p[2,1]))/denominator;c=1-a-b
   dep=a*depth[0]+b*depth[1]+c*depth[2];sample=a[...,None]*uv[0]+b[...,None]*uv[1]+c[...,None]*uv[2]
   tx=np.clip((sample[:,:,0]*tex.shape[1]).astype(int),0,tex.shape[1]-1);ty=np.clip((sample[:,:,1]*tex.shape[0]).astype(int),0,tex.shape[0]-1)
   color=tex[ty,tx].copy();color[:,:,:3]=(color[:,:,:3]*shade*tint).astype(np.uint8)
   rect=np.s_[low[1]:high[1]+1,low[0]:high[0]+1];mask=(a>=0)&(b>=0)&(c>=0)&(dep>zbuf[rect])&(color[:,:,3]>127)
   pixels[rect][mask]=color[mask];zbuf[rect][mask]=dep[mask]
 canvas.paste(Image.fromarray(pixels),(x0+10,y0+12),Image.fromarray(pixels[:,:,3]))
 draw.text((x0+15,y0+275),name,fill=(223,240,250),font=font)
 draw.text((x0+15,y0+300),manifest[variant]['source'],fill=(130,171,190),font=small)
draw.text((12,663),'Vista de referencia generada de las mallas y texturas; no es una captura de Minecraft.',fill=(142,170,186),font=small)
path=ROOT/'docs/assets/rocket-fleet-preview.png';canvas.save(path);print(path)
