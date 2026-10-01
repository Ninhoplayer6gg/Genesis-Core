"""Genesis visual revision 2: authored cuboids, UV atlases and pixel motifs.
Run with Python 3 + Pillow. No generated concept image is used as game geometry.
"""
import json,math
from pathlib import Path
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1];ASSETS=ROOT/'src/main/resources/assets/genesis'

def rgb(s):return tuple(bytes.fromhex(s.lstrip('#')))
def shade(c,amount):return tuple(max(0,min(255,x+amount)) for x in c)
PALETTES={
 'adaptaris':{'skin':'75634e','dark':'303d38','plate':'d2c19a','edge':'a58b60','accent':'72c6a9','light':'d9ffe0','copper':'b68b4e','red':'f34846'},
 'ferronox':{'skin':'293d48','dark':'14222d','plate':'466675','edge':'25554f','accent':'dc9757','light':'ffe1a0','copper':'be7644','red':'f34846'},
 'colonyx':{'skin':'447c78','dark':'22383e','plate':'d5ded0','edge':'8aa695','accent':'a480c6','light':'e9c5ff','copper':'b68b4e','red':'f34846'}
}
class Model:
 def __init__(self,name):
  self.name=name;self.bones=[];self.lookup={};self.atlas=Image.new('RGBA',(128,128),(0,0,0,0));self.glow=Image.new('RGBA',(128,128),(0,0,0,0));self.rects=[];self.nets={};self.colors={k:rgb(v) for k,v in PALETTES[name].items()}
  # Fixed UV patches used by the existing first-person Java hand renderer.
  for x,y,w,h,mat in [(0,64,24,24,'skin'),(0,96,24,16,'plate'),(64,64,16,16,'skin')]:
   self.rects.append((x,y,w,h));self.face(x,y,w,h,mat,'',0)
  self.bone('root',[0,0,0],None)
 def bone(self,name,pivot,parent='body',rotation=None):
  b={'name':name,'pivot':pivot,'cubes':[]}
  if parent:b['parent']=parent
  if rotation:b['rotation']=rotation
  self.bones.append(b);self.lookup[name]=b;return name
 def face(self,x,y,w,h,mat,motif,side):
  x,y,w,h=map(int,(x,y,w,h));d=ImageDraw.Draw(self.atlas);c=self.colors[mat]
  d.rectangle((x,y,x+w-1,y+h-1),fill=(*c,255))
  if mat in ('accent','light','red'):
   gd=ImageDraw.Draw(self.glow);gd.rectangle((x,y,x+w-1,y+h-1),fill=(*c,255))
  if w>=3 and h>=3:
   d.line((x,y,x+w-1,y),fill=(*shade(c,16),255));d.line((x,y+h-1,x+w-1,y+h-1),fill=(*shade(c,-20),255))
   d.line((x+w-1,y+1,x+w-1,y+h-2),fill=(*shade(c,-9),255))
   if mat=='skin' and h>=6:
    d.line((x+1,y+h//2,x+1,y+h-2),fill=(*shade(c,10),255))
    if w>=6:d.rectangle((x+w-3,y+2,x+w-2,y+3),fill=(*shade(c,-13),255))
   if mat=='plate' and w>=4:
    d.line((x+1,y+1,x+w-2,y+1),fill=(*shade(c,10),255))
    if h>=5:d.line((x+w-3,y+h-3,x+w-2,y+h-3),fill=(*shade(c,-26),255))
   if mat=='copper' and h>=4:
    d.line((x+1,y+1,x+1,y+h-2),fill=(*shade(c,32),255))
    if w>=4:d.point((x+w-2,y+1),fill=(*self.colors['edge'],255))
  if side!=2:return
  maps={
   'adaptaris_face':['spppppps','PppppppP','PdEddEdP','sdddddds','sdEddEds','ssBssBss','ssBDDBss','sssBBsss'],
   'ferronox_face':['dPTTTPdd','dPTdTPdd','dPTATPdd','dPTATPdd','dTTATTdd','dddAdddd','ddTdTddd','dddTdddd'],
   'colonyx_face':['pppppp','EpmmpE','ppEEpp','dsppsd']}
  if motif in maps:
   colors={'s':'skin','p':'plate','P':'edge','d':'dark','D':'dark','E':'light','e':'accent','B':'edge','T':'copper','A':'light','m':'accent'}
   for yy,row in enumerate(maps[motif]):
    for xx,ch in enumerate(row):
     if xx<w and yy<h:
      col=(*self.colors[colors[ch]],255);d.point((x+xx,y+yy),fill=col)
      if ch in 'EA':ImageDraw.Draw(self.glow).point((x+xx,y+yy),fill=col)
  if motif=='ribs' and w>=8:
   for row in (2,5,8):
    if row>=h-1:continue
    for xx in (1,2,w-3,w-2):d.point((x+xx,y+row),fill=(*self.colors['edge'],255))
  if motif=='conduit' and h>5:
   for yy in range(1,h-1):d.point((x+w//2,y+yy),fill=(*self.colors['copper'],255))
 def uv(self,size,mat,motif=''):
  w,h,d=map(lambda v:max(1,math.ceil(v)),size);key=(w,h,d,mat,motif)
  if key in self.nets:return self.nets[key]
  rw,rh=2*(w+d),h+d
  found=None
  for y in range(129-rh):
   for x in range(129-rw):
    if all(x+rw<=a or a+c<=x or y+rh<=b or b+e<=y for a,b,c,e in self.rects):found=(x,y);break
   if found:break
  if not found:raise ValueError((self.name,'Atlas full',rw,rh,len(self.nets)))
  x,y=found;self.rects.append((x,y,rw,rh));self.nets[key]=[x,y]
  for side,(xx,yy,ww,hh) in enumerate([(x+d,y,w,d),(x+d+w,y,w,d),(x+d,y+d,w,h),(x,y+d,d,h),(x+d+w,y+d,d,h),(x+2*d+w,y+d,w,h)]):self.face(xx,yy,ww,hh,mat,motif,side)
  return [x,y]
 def cube(self,bone,origin,size,mat='skin',motif=''):
  cube={'origin':origin,'size':size,'uv':self.uv(size,mat,motif)}
  if mat in ('plate','edge','copper'):cube['inflate']=.03+.004*(len(self.lookup[bone]['cubes'])%5)
  self.lookup[bone]['cubes'].append(cube)
 def core(self,y,z):
  self.bone('core',[0,y,z]);self.cube('core',[-1.5,y-1.5,z],[3,3,1],'dark');self.cube('core',[-.5,y-.5,z-.5],[1,1,1],'red')
  self.bone('core_ring',[0,y,z],'core')
  for x,yy,w,h in [(-1.5,y+1,2,1),(-1,y-2,2,1),(1,y-1,1,2)]:self.cube('core_ring',[x,yy,z-.25],[w,h,1],'copper')
 def write(self):
  out={'format_version':'1.12.0','minecraft:geometry':[{'description':{'identifier':'geometry.genesis.'+self.name,'texture_width':128,'texture_height':128,'visible_bounds_width':4,'visible_bounds_height':4,'visible_bounds_offset':[0,1,0]},'bones':self.bones}]}
  (ASSETS/f'geo/{self.name}.geo.json').write_text(json.dumps(out,indent=2)+'\n');self.atlas.save(ASSETS/f'textures/entity/{self.name}.png');self.glow.save(ASSETS/f'textures/entity/{self.name}_glowmask.png')
  print(self.name,sum(len(b['cubes']) for b in self.bones),'cubes;',len(self.nets),'UV nets')

def humanoid(m):
 m.bone('body',[0,18,0],'root');m.cube('body',[-4,12,-2],[8,12,4],'skin','ribs')
 m.bone('head',[0,24,0]);m.cube('head',[-4,24,-4],[8,8,8],'skin',m.name+'_face')
 for sign,side in [(-1,'right'),(1,'left')]:
  m.bone(side+'_arm',[sign*5,22,0]);m.cube(side+'_arm',[-8 if sign<0 else 4,12,-2],[4,12,4],'skin','conduit' if m.name=='ferronox' else '')
  m.bone(side+'_leg',[sign*2,12,0],'root');m.cube(side+'_leg',[-4 if sign<0 else 0,0,-2],[4,12,4],'dark')
 m.core(19,-3)

def adaptaris():
 m=Model('adaptaris');humanoid(m)
 # An asymmetric, layered defensive anatomy, grown rather than worn.
 m.bone('right_plate',[-6,22,0],'right_arm',rotation=[0,0,-14]);m.cube('right_plate',[-11,21,-3],[7,4,6],'plate');m.cube('right_plate',[-10,24,-2],[5,2,5],'edge');m.cube('right_plate',[-11,19,-3],[5,2,5],'edge')
 m.bone('mantle_spur',[-8,24,1],'right_plate',rotation=[-15,0,-18]);m.cube('mantle_spur',[-9,24,0],[3,5,3],'plate')
 m.bone('left_plate',[6,22,0],'left_arm');m.cube('left_plate',[4,21,-3],[5,3,5],'plate');m.cube('left_plate',[5,23,-2],[3,2,4],'edge')
 for side,x in [('right',-9),('left',4)]:
  arm=side+'_arm';m.cube(arm,[x,12,-3],[5,5,6] if side=='right' else [4,4,5],'plate');m.cube(arm,[x,16,-3],[4,1,1],'edge')
  for off in (0,3):m.cube(arm,[x+off,10,-3],[1,3,2],'plate')
 for side,x in [('right',-4),('left',0)]:
  leg=side+'_leg';m.cube(leg,[x,4,-3],[4,5,1],'plate');m.cube(leg,[x,0,-3],[4,2,5],'edge')
 # Mantle edges leave the face open and readable at Minecraft texture scale.
 m.bone('jaw',[0,25,0],'head');m.cube('jaw',[-4,24,-4.5],[2,3,1],'plate');m.cube('jaw',[2,24,-4.5],[2,3,1],'plate');m.cube('jaw',[-3,23.5,-5],[2,2,2],'plate');m.cube('jaw',[1,23.5,-5],[2,2,2],'plate')
 m.bone('brow',[0,30,0],'head');m.cube('brow',[-4,31,-3],[8,1,6],'plate')
 m.bone('spine',[0,18,2]);
 for y,w in [(13,3),(17,4),(21,5)]:m.cube('spine',[-w/2,y,2],[w,2,2],'plate')
 m.bone('thermal',[0,20,2]);
 for x in (-3,2):m.cube('thermal',[x,14,3],[1,9,3],'copper')
 m.bone('cold',[0,20,0]);m.cube('cold',[-4,21,-3],[8,2,1],'plate')
 m.bone('electric',[0,20,0]);
 for x in (-4,3):m.cube('electric',[x,14,-3],[1,6,1],'accent')
 m.bone('impact',[0,19,0]);m.cube('impact',[-4,14,-3],[8,4,2],'plate')
 m.bone('absolute',[0,20,3]);
 for x,y in [(-5,24),(2,25),(-3,20)]:m.cube('absolute',[x,y,2],[3,6,3],'plate');m.cube('absolute',[x+1,y+5,2],[1,2,3],'accent')
 m.write()

def ferronox():
 m=Model('ferronox');humanoid(m)
 # Two open dipole organs, mounted behind the shoulders rather than shoulder pads.
 for sign,side in [(-1,'right'),(1,'left')]:
  m.bone(side+'_field',[sign*5,23,2],rotation=[0,0,sign*-12])
  x=-9 if sign<0 else 5
  for z in (0,4):m.cube(side+'_field',[x,22,z],[4,1,1],'copper');m.cube(side+'_field',[x,31,z],[4,1,1],'copper')
  m.cube(side+'_field',[x if sign<0 else x+3,23,0],[1,8,5],'plate')
  m.cube(side+'_field',[x if sign<0 else x+3,24,-.5],[1,3,1],'accent')
  arm=side+'_arm';x=-8 if sign<0 else 4
  m.cube(arm,[x,13,-3],[4,6,1],'copper');m.cube(arm,[x+1,14,-3.5],[2,4,1],'edge')
  for dx in (0,3):m.cube(arm,[x+dx,10,-2],[1,3,3],'plate')
  m.bone(side+'_temple',[sign*3,28,0],'head');m.cube(side+'_temple',[-5 if sign<0 else 4,25,0],[1,6,3],'copper')
  leg=side+'_leg';x=-4 if sign<0 else 0
  m.cube(leg,[x,0,-3],[4,2,5],'plate');m.cube(leg,[x+1,4,-2.5],[2,6,1],'copper')
 m.bone('vertebrae',[0,18,2]);m.cube('vertebrae',[-1,13,2],[2,10,2],'copper')
 for y in (14,18,22):m.cube('vertebrae',[-3,y,2],[6,1,1],'edge')
 m.bone('gills',[0,22,-2]);
 for x in (-4,2):
  for y in (20,22):m.cube('gills',[x,y,-3],[2,1,1],'copper')
 m.bone('shield',[0,18,-6]);
 for x,y,w,h in [(-6,12,2,12),(4,12,2,12),(-4,24,8,2),(-4,10,8,2)]:m.cube('shield',[x,y,-6],[w,h,1],'plate')
 m.write()

def colonyx():
 m=Model('colonyx');m.bone('body',[0,10,0],'root');m.cube('body',[-5,7,-5],[10,6,11],'skin')
 m.bone('abdomen',[0,10,5]);m.cube('abdomen',[-4,8,5],[8,5,5],'skin');m.cube('abdomen',[-3,9,9],[6,4,3],'edge')
 # Separated shell lobes expose a living purple tissue seam.
 m.cube('body',[-4,12,-3],[8,2,8],'accent')
 m.bone('left_lobe',[1,12,0],rotation=[0,0,-10]);m.cube('left_lobe',[.5,12,-4],[4,2,10],'plate');m.cube('left_lobe',[.5,14,-3],[3,2,8],'plate')
 m.bone('right_lobe',[-1,12,0],rotation=[0,0,10]);m.cube('right_lobe',[-4.5,12,-4],[4,2,10],'plate');m.cube('right_lobe',[-3.5,14,-3],[3,2,8],'plate')
 m.cube('abdomen',[-3,13,5],[6,2,6],'plate')
 m.bone('head',[0,10,-5]);m.cube('head',[-3,8,-9],[6,4,5],'skin','colonyx_face')
 m.cube('head',[-3,12,-8],[6,1,3],'plate')
 # Three narrow spore chimneys: a distributed sensory organ, no human face.
 m.bone('sensory_crown',[0,13,1]);
 for x,y,z,h in [(-2,14,2,4),(1,14,2,5),(0,14,-1,3)]:m.cube('sensory_crown',[x,y,z],[1,h,1],'edge');m.cube('sensory_crown',[x-.5,y+h-2,z-.5],[2,3,2],'accent')
 for sign,side in [(-1,'right'),(1,'left')]:
  for i,z in enumerate((-4,1,6)):
   name=side+'_leg'+str(i);m.bone(name,[sign*4,9,z],'root',rotation=[0,(i-1)*sign*12,0])
   m.cube(name,[-8 if sign<0 else 4,7,z-1],[4,2,2],'skin')
   m.cube(name,[-8 if sign<0 else 4,9,z-1],[4,1,2],'plate')
   shin=name+'_shin';m.bone(shin,[sign*7,8,z],name,rotation=[0,0,sign*15])
   m.cube(shin,[-8 if sign<0 else 6,1,z-1],[2,7,2],'skin')
   m.cube(shin,[-8 if sign<0 else 6,0,z-2],[2,2,3],'plate')
  arm=side+'_arm';m.bone(arm,[sign*3,10,-5]);m.cube(arm,[-5 if sign<0 else 3,7,-9],[2,3,5],'skin')
  claw=side+'_claw';m.bone(claw,[sign*4,8,-8],arm,rotation=[0,sign*-18,0]);m.cube(claw,[-5 if sign<0 else 3,8,-12],[2,2,4],'plate');m.cube(claw,[-4 if sign<0 else 2,7,-12],[2,1,1],'accent')
 m.core(7,-7)
 m.bone('blade',[0,10,-7]);m.cube('blade',[-1,9,-19],[2,2,12],'plate');m.cube('blade',[-.5,10,-18],[1,1,10],'accent')
 m.bone('shield',[0,10,-9]);m.cube('shield',[-6,5,-11],[12,11,1],'plate');m.cube('shield',[-5,6,-11.5],[10,9,1],'edge')
 m.bone('bloom',[0,14,2]);
 for sign in (-1,1):
  m.cube('bloom',[-10 if sign<0 else 3,15,3],[7,2,2],'skin');m.cube('bloom',[-10 if sign<0 else 8,15,-3],[2,2,8],'plate');m.cube('bloom',[-10 if sign<0 else 8,16,-4],[2,1,3],'accent')
 m.write()

def animations():
 for name in PALETTES:
  path=ASSETS/f'animations/{name}.animation.json';j=json.loads(path.read_text());a=j['animations']
  def wave(axis,lo,hi,length=2):
   def v(n):r=[0,0,0];r[axis]=n;return r
   return {'0':v(lo),str(length/2):v(hi),str(length):v(lo)}
  a['idle']['animation_length']=2
  a['idle']['bones']['body']['position']=wave(1,0,.15 if name=='adaptaris' else .22)
  if name=='ferronox':
   for side,sign in [('left',1),('right',-1)]:a['idle']['bones'][side+'_field']={'rotation':wave(1,-4*sign,4*sign)}
   for anim in ('attack','ultimate'):
    for side,sign in [('left',1),('right',-1)]:a[anim]['bones'][side+'_field']={'rotation':wave(1,0,18*sign,a[anim]['animation_length'])}
  if name=='adaptaris':
   a['idle']['bones']['right_plate']={'rotation':wave(2,-1,2)}
   for anim,length,stride in [('walk',.8,12),('run',.55,22)]:
    a[anim]={'loop':True,'animation_length':length,'bones':{}}
    for limb,sign in [('left_arm',1),('right_arm',-1),('left_leg',-1),('right_leg',1)]:
     a[anim]['bones'][limb]={'rotation':wave(0,-stride*sign,stride*sign,length)}
  if name=='colonyx':
   a['idle']['bones']['left_lobe']={'rotation':wave(2,0,-3)};a['idle']['bones']['right_lobe']={'rotation':wave(2,0,3)}
   a['idle']['bones']['sensory_crown']={'rotation':wave(0,-4,4)}
   for anim,length,stride in [('walk',.8,18),('run',.5,28)]:
    a[anim]={'loop':True,'animation_length':length,'bones':{}}
    for side,sgn in [('left',1),('right',-1)]:
     for i in range(3):
      polarity=sgn*(-1 if i%2 else 1);a[anim]['bones'][side+'_leg'+str(i)]={'rotation':wave(0,-stride*polarity,stride*polarity,length)}
    a[anim]['bones']['body']={'position':wave(1,0,.35,length)}
   a['ultimate']['bones']['left_lobe']={'rotation':wave(2,0,-30,a['ultimate']['animation_length'])};a['ultimate']['bones']['right_lobe']={'rotation':wave(2,0,30,a['ultimate']['animation_length'])}
  path.write_text(json.dumps(j,indent=2)+'\n')

if __name__=='__main__':
 adaptaris();ferronox();colonyx();animations()
