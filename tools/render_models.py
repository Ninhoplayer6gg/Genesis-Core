"""Orthographic previews rasterized directly from runtime geometry and UV textures."""
import json,math,sys
from pathlib import Path
import numpy as np
from PIL import Image,ImageDraw,ImageFont
ROOT=Path(__file__).resolve().parents[1];ASSETS=ROOT/'src/main/resources/assets/genesis';OUT=ROOT/'art/revision-2';OUT.mkdir(exist_ok=True)
HIDDEN={'thermal','cold','electric','impact','absolute','shield','blade','bloom'}
def rot(degrees):
 x,y,z=np.radians(degrees);cx,sx,cy,sy,cz,sz=np.cos(x),np.sin(x),np.cos(y),np.sin(y),np.cos(z),np.sin(z)
 rx=np.array([[1,0,0],[0,cx,-sx],[0,sx,cx]]);ry=np.array([[cy,0,sy],[0,1,0],[-sy,0,cy]]);rz=np.array([[cz,-sz,0],[sz,cz,0],[0,0,1]])
 return rz@ry@rx

def mesh(name,assets=ASSETS,shown=set()):
 geo=json.loads((assets/f'geo/{name}.geo.json').read_text())['minecraft:geometry'][0];bones={b['name']:b for b in geo['bones']};matrices={};hidden={}
 def transform(name):
  if name in matrices:return matrices[name]
  b=bones[name];p=np.array(b.get('pivot',[0,0,0]),dtype=float);r=rot(b.get('rotation',[0,0,0]));m=np.eye(4);m[:3,:3]=r;m[:3,3]=p-r@p
  hidden[name]=name in HIDDEN and name not in shown
  if b.get('parent'):
   m=transform(b['parent'])@m;hidden[name]=hidden[name] or hidden[b['parent']]
  matrices[name]=m;return m
 faces=[]
 for name,b in bones.items():
  m=transform(name)
  if hidden[name]:continue
  for c in b.get('cubes',[]):
   x,y,z=c['origin'];w,h,d=c['size'];u,v=c['uv'];n=c.get('inflate',0);xx=x+w+n;yy=y+h+n;zz=z+d+n;x-=n;y-=n;z-=n
   rects=[(u+d,v+d,w,h),(u+2*d+w,v+d,w,h),(u,v+d,d,h),(u+d+w,v+d,d,h),(u+d,v,w,d),(u+d+w,v,w,d)]
   quads=[[(x,yy,z),(xx,yy,z),(xx,y,z),(x,y,z)],[(xx,yy,zz),(x,yy,zz),(x,y,zz),(xx,y,zz)],[(x,yy,zz),(x,yy,z),(x,y,z),(x,y,zz)],[(xx,yy,z),(xx,yy,zz),(xx,y,zz),(xx,y,z)],[(x,yy,zz),(xx,yy,zz),(xx,yy,z),(x,yy,z)],[(x,y,z),(xx,y,z),(xx,y,zz),(x,y,zz)]]
   normals=[(0,0,-1),(0,0,1),(-1,0,0),(1,0,0),(0,1,0),(0,-1,0)]
   for q,rect,n in zip(quads,rects,normals):
    pts=(m@np.c_[np.array(q),np.ones(4)].T).T[:,:3];normal=m[:3,:3]@n
    a,b,cw,ch=rect;uv=np.array([[a,b],[a+cw,b],[a+cw,b+ch],[a,b+ch]],dtype=float)
    faces.append((pts,uv,normal))
 return faces

def render(name,width=480,height=500,yaw=27,pitch=15,assets=ASSETS,shown=set(),scale=None):
 faces=mesh(name,assets,shown);ya,pi=math.radians(yaw),math.radians(pitch)
 right=np.array([math.cos(ya),0,math.sin(ya)]);depth=np.array([math.sin(ya)*math.cos(pi),math.sin(pi),-math.cos(ya)*math.cos(pi)]);up=np.cross(depth,right)*-1
 camera=np.stack([right,up,depth]);allp=np.vstack([f[0] for f in faces])@camera.T
 bounds=np.stack([allp.min(axis=0),allp.max(axis=0)]);scale=scale or min((width-65)/(bounds[1,0]-bounds[0,0]),(height-60)/(bounds[1,1]-bounds[0,1]))
 offset=np.array([width/2-(bounds[0,0]+bounds[1,0])/2*scale,height-25+bounds[0,1]*scale])
 pixels=np.zeros((height,width,4),dtype=np.uint8);zbuf=np.full((height,width),-1e9,dtype=float)
 tex=np.array(Image.open(assets/f'textures/entity/{name}.png').convert('RGBA'));light=np.array([-.45,.8,-.75]);light/=np.linalg.norm(light)
 for pts,uv,normal in faces:
  if np.dot(normal,depth)<=0:continue
  camera_pts=pts@camera.T;screen=camera_pts[:,:2]*[scale,-scale]+offset;zs=camera_pts[:,2]
  lum=.72+.28*max(0,np.dot(normal,light));
  for indices in ((0,1,2),(0,2,3)):
   q=screen[list(indices)];uvs=uv[list(indices)];dz=zs[list(indices)]
   minx,miny=np.maximum(np.floor(q.min(0)).astype(int),[0,0]);maxx,maxy=np.minimum(np.ceil(q.max(0)).astype(int),[width-1,height-1])
   if minx>maxx or miny>maxy:continue
   yy,xx=np.mgrid[miny:maxy+1,minx:maxx+1];px=xx+.5;py=yy+.5
   (ax,ay),(bx,by),(cx,cy)=q;den=(by-cy)*(ax-cx)+(cx-bx)*(ay-cy)
   if abs(den)<1e-8:continue
   aa=((by-cy)*(px-cx)+(cx-bx)*(py-cy))/den;bb=((cy-ay)*(px-cx)+(ax-cx)*(py-cy))/den;cc=1-aa-bb
   z=aa*dz[0]+bb*dz[1]+cc*dz[2];mask=(aa>=-1e-8)&(bb>=-1e-8)&(cc>=-1e-8)&(z>zbuf[miny:maxy+1,minx:maxx+1])
   tu=np.clip(np.floor(aa*uvs[0,0]+bb*uvs[1,0]+cc*uvs[2,0]).astype(int),0,127);tv=np.clip(np.floor(aa*uvs[0,1]+bb*uvs[1,1]+cc*uvs[2,1]).astype(int),0,127)
   sample=tex[tv,tu].copy();mask &= sample[:,:,3]>0;sample[:,:,:3]=(sample[:,:,:3].astype(float)*lum).clip(0,255).astype(np.uint8)
   pixels[miny:maxy+1,minx:maxx+1][mask]=sample[mask];zbuf[miny:maxy+1,minx:maxx+1][mask]=z[mask]
 return Image.fromarray(pixels)

def font(size,bold=False):return ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans'+('-Bold' if bold else '')+'.ttf',size)
def sheet(assets=ASSETS,title='Três espécies. Três anatomias.',path=OUT/'Genesis-Core-Modelos-R2.png',edition='REVISÃO VISUAL 02',captions=None):
 image=Image.new('RGB',(1536,810),'#0d141a');d=ImageDraw.Draw(image)
 d.text((44,28),'GENESIS CORE  /  '+edition,font=font(17,True),fill='#d6b578');d.text((42,60),title,font=font(36,True),fill='#f1f2eb')
 specs=[('adaptaris','ADAPTARIS','Placas vivas • assimetria • adaptação','#cfbf93'),('ferronox','FERRONOX','Órgãos dipolares • cobre • condutores','#d99560'),('colonyx','COLONYX','Seis patas • lobos • consciência coletiva','#b69ed4')]
 for i,(name,label,caption,accent) in enumerate(specs):
  left=24+i*504
  if captions:caption=captions[name]
  d.rounded_rectangle((left,134,left+479,751),radius=12,fill='#1b2832',outline='#2c3b43',width=1)
  d.text((left+23,157),label,font=font(25,True),fill=accent);d.text((left+23,194),caption,font=font(15),fill='#b7c3c9')
  d.ellipse((left+103,655,left+375,690),fill='#121e26')
  im=render(name,480,448,assets=assets,yaw=27 if name!='colonyx' else 31,pitch=13 if name!='colonyx' else 21)
  image.paste(im,(left,232),im)
  d.text((left+23,712),{'adaptaris':'Humanoide pesado  /  placas assimétricas','ferronox':'Humanoide  /  49 cubos','colonyx':'Corpo colonial  /  lobos móveis'}[name],font=font(15),fill='#899da7')
 d.text((43,773),'Renderização dos arquivos reais .geo.json + texturas 128 × 128. Forma base; sem escudos ou habilidades ativados.',font=font(16),fill='#a9b9c1')
 image.save(path)

if __name__=='__main__':
 sheet()
 for name in ('adaptaris','ferronox','colonyx'):
  im=render(name,720,760);im.save(OUT/f'{name}-three-quarter.png')
  # Character sheet: front, three-quarter and rear, all from the actual mesh.
  canvas=Image.new('RGB',(1440,600),'#1b2832');draw=ImageDraw.Draw(canvas)
  for i,(angle,label) in enumerate([(0,'FRENTE'),(27,'3/4'),(155,'COSTAS')]):
   im=render(name,480,510,yaw=angle,pitch=12);canvas.paste(im,(i*480,55),im);draw.text((i*480+24,21),name.upper()+' / '+label,font=font(18,True),fill='#e4dccb')
  canvas.save(OUT/f'{name}-turnaround.png')
  icon=render(name,100,100,yaw=0,pitch=8);box=icon.getbbox();icon=icon.crop(box);icon.thumbnail((18,18),Image.Resampling.LANCZOS);tile=Image.new('RGBA',(20,20));tile.paste(icon,((20-icon.width)//2,(20-icon.height)//2));tile.save(ASSETS/f'textures/gui/{name}.png')
 print(OUT/'Genesis-Core-Modelos-R2.png')
