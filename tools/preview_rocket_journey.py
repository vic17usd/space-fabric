#!/usr/bin/env python3
"""Render the native emitter's exported cruise frames with Pillow/NumPy, without Minecraft/GPU.
Run ./gradlew exportRocketJourneyPreview first. This is a reference render, not a game recording.
"""
from pathlib import Path
import json,math
import numpy as np
from PIL import Image,ImageDraw,ImageFont
ROOT=Path(__file__).resolve().parents[1]
frames=json.loads((ROOT/'build/reports/rocket-journey-preview.json').read_text())
tex=np.asarray(Image.open(ROOT/'src/main/resources/assets/space_fabric/textures/entity/rocket.png').convert('RGBA'))
W,H=480,270;focal=H/(2*math.tan(math.radians(35)))
font=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',12)
rendered=[]
for index,frame in enumerate(frames):
 pixels=np.zeros((H,W,3),dtype=np.uint8);pixels[:]=[1,2,7];zbuf=np.full((H,W),np.inf)
 v=np.array(frame['vertices'],dtype=float)
 for start in range(0,len(v),4):
  quad=v[start:start+4];textured=start>=frame['backgroundVertices']
  if np.min(quad[:,2])<.03:continue
  xy=np.stack([W/2+quad[:,0]/quad[:,2]*focal,H/2-quad[:,1]/quad[:,2]*focal],axis=1)
  color=int(quad[0,3]);tint=np.array([(color>>16)&255,(color>>8)&255,color&255])/255
  for ids in [[0,1,2],[0,2,3]]:
   p=xy[ids];uv=quad[ids,4:6];iz=1/quad[ids,2]
   low=np.maximum(np.floor(p.min(axis=0)).astype(int),[0,0]);high=np.minimum(np.ceil(p.max(axis=0)).astype(int),[W-1,H-1])
   if (high<low).any():continue
   den=(p[1,1]-p[2,1])*(p[0,0]-p[2,0])+(p[2,0]-p[1,0])*(p[0,1]-p[2,1])
   if abs(den)<1e-8:continue
   yy,xx=np.mgrid[low[1]:high[1]+1,low[0]:high[0]+1];xx=xx+.5;yy=yy+.5
   a=((p[1,1]-p[2,1])*(xx-p[2,0])+(p[2,0]-p[1,0])*(yy-p[2,1]))/den
   b=((p[2,1]-p[0,1])*(xx-p[2,0])+(p[0,0]-p[2,0])*(yy-p[2,1]))/den;c=1-a-b
   invz=a*iz[0]+b*iz[1]+c*iz[2];depth=1/np.maximum(invz,1e-9)
   rect=np.s_[low[1]:high[1]+1,low[0]:high[0]+1];mask=(a>=0)&(b>=0)&(c>=0)&(depth<zbuf[rect])
   if not mask.any():continue
   if textured:
    sample=(a[...,None]*uv[0]*iz[0]+b[...,None]*uv[1]*iz[1]+c[...,None]*uv[2]*iz[2])/np.maximum(invz[...,None],1e-9)
    tx=np.clip((sample[:,:,0]*tex.shape[1]).astype(int),0,tex.shape[1]-1);ty=np.clip((sample[:,:,1]*tex.shape[0]).astype(int),0,tex.shape[0]-1)
    sampled=tex[ty,tx];mask&=sampled[:,:,3]>127;rgb=(sampled[:,:,:3]*tint).astype(np.uint8)
   else:rgb=np.broadcast_to((tint*255).astype(np.uint8),(*a.shape,3))
   pixels[rect][mask]=rgb[mask];zbuf[rect][mask]=depth[mask]
 image=Image.fromarray(pixels);draw=ImageDraw.Draw(image)
 draw.rectangle((0,0,W,32),fill=(9,16,24));draw.text((12,3),'Tierra -> Marte | Travesia espacial',font=font,fill=(201,232,239))
 draw.rectangle((12,23,468,26),fill=(38,53,66));draw.rectangle((12,23,12+int(456*index/(len(frames)-1)),26),fill=(90,218,184))
 draw.rectangle((0,H-18,W,H),fill=(9,16,24));draw.text((12,H-16),'Vista de referencia; no es una captura del juego',font=font,fill=(150,172,185))
 rendered.append(image)
assets=ROOT/'docs/assets';assets.mkdir(exist_ok=True)
rendered[6].save(assets/'rocket-journey-preview.png')
rendered[0].save(assets/'rocket-journey-preview.gif',save_all=True,append_images=rendered[1:],duration=250,loop=0)
print(assets/'rocket-journey-preview.gif')
