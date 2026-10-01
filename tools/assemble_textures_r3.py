"""Integrate the generated painting into the existing 128px UV layout.
ImageMagick performs format conversion, clipping to the original alpha and retention
of the tiny emissive markers. Geometry and animation bytes are never rewritten.
"""
import json,subprocess
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/genesis';ART=ROOT/'art/revision-3';OLD=ART/'input/assets-r2';G=ART/'generated'
def convert(*args):subprocess.run(['convert',*map(str,args)],check=True)
convert(G/'atlas-painted.png','-filter','box','-resize','384x128!',G/'atlas-native.png')
for index,name in enumerate(('adaptaris','ferronox','colonyx')):
 original=OLD/f'textures/entity/{name}.png';dest=A/f'textures/entity/{name}.png';raw=G/f'{name}-painted.png'
 convert(G/'atlas-native.png','-crop',f'128x128+{index*128}+0','+repage',raw)
 convert(original,raw,'-compose','Over','-composite',original,'-compose','CopyOpacity','-composite','+dither','-colors','128',dest)
 geo=json.loads((A/f'geo/{name}.geo.json').read_text())['minecraft:geometry'][0]
 # Preserve the subpixel-scale landmarks that define the face and the installed Core.
 rectangles=[]
 for bone in geo['bones']:
  if bone['name']=='head':
   cube=bone['cubes'][0];u,v=cube['uv'];w,h,d=cube['size'];rectangles.append((u+d,v+d,w,h))
  if bone['name']=='core':
   for c in bone['cubes']:
    if c['size']==[1,1,1]:u,v=c['uv'];rectangles.append((u,v,4,2))
 mask=G/f'{name}-marker-region.png';args=['-size','128x128','xc:black','-fill','white']
 for x,y,w,h in rectangles:args+=['-draw',f'rectangle {int(x)},{int(y)} {int(x+w-1)},{int(y+h-1)}']
 convert(*args,mask)
 glow=OLD/f'textures/entity/{name}_glowmask.png';emissive=G/f'{name}-marker-mask.png'
 convert(glow,'-alpha','extract',mask,'-compose','Multiply','-composite',emissive)
 keep=G/f'{name}-markers.png';convert(original,emissive,'-alpha','off','-compose','CopyOpacity','-composite',keep)
 convert(dest,keep,'-compose','Over','-composite',dest)
 # Same luminous regions, now matching the finished texture's color.
 convert(glow,'-alpha','extract',G/f'{name}-glow-alpha.png')
 convert(dest,G/f'{name}-glow-alpha.png','-alpha','off','-compose','CopyOpacity','-composite',A/f'textures/entity/{name}_glowmask.png')
 print(name,'128x128 RGBA; original UV footprint and face/core markers retained')
