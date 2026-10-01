"""Previews of the installed game assets; no concept art is used for the characters."""
from pathlib import Path
from PIL import Image,ImageDraw
import render_models as r
OUT=r.ROOT/'art/revision-3';OLD=OUT/'input/assets-r2'
r.sheet(title='Osso, metal biológico e tecido vivo.',path=OUT/'Genesis-Core-Texturas-R3.png',edition='TEXTURAS 03',captions={'adaptaris':'Osso estriado • pele rugosa • olhos claros','ferronox':'Cobre vivo • oxidação • reflexos metálicos','colonyx':'Quitina • membranas • detalhes violeta'})
for name in ('adaptaris','ferronox','colonyx'):
 image=Image.new('RGB',(1100,660),'#17242d');draw=ImageDraw.Draw(image)
 draw.text((30,24),name.upper()+'  /  MESMO MODELO, NOVA PINTURA',font=r.font(22,True),fill='#e6d9bc')
 for col,(assets,label) in enumerate(((OLD,'ANTES · R2'),(r.ASSETS,'AGORA · R3'))):
  im=r.render(name,540,525,assets=assets);image.paste(im,(col*550,79),im)
  draw.text((col*550+30,617),label,font=r.font(18,True),fill='#bdcdc5')
 image.save(OUT/f'{name}-antes-depois.png')
 canvas=Image.new('RGB',(1440,600),'#1b2832');draw=ImageDraw.Draw(canvas)
 for i,(angle,label) in enumerate([(0,'FRENTE'),(27,'3/4'),(155,'COSTAS')]):
  im=r.render(name,480,510,yaw=angle,pitch=12);canvas.paste(im,(i*480,55),im);draw.text((i*480+24,21),name.upper()+' / '+label,font=r.font(18,True),fill='#e4dccb')
 canvas.save(OUT/f'{name}-textura-tres-vistas.png')
 icon=r.render(name,100,100,yaw=0,pitch=8);icon=icon.crop(icon.getbbox());icon.thumbnail((18,18),Image.Resampling.NEAREST);tile=Image.new('RGBA',(20,20));tile.paste(icon,((20-icon.width)//2,(20-icon.height)//2));tile.save(r.ASSETS/f'textures/gui/{name}.png')
print(OUT/'Genesis-Core-Texturas-R3.png')
