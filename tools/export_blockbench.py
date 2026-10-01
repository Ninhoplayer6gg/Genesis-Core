"""Create native, textured Blockbench 5 projects from the runtime GeckoLib assets."""
import json,uuid,base64
from pathlib import Path
root=Path(__file__).resolve().parents[1];a=root/'src/main/resources/assets/genesis';out=root/'art/blockbench';out.mkdir(parents=True,exist_ok=True)
def uid(s):return str(uuid.uuid5(uuid.NAMESPACE_URL,'genesis-core:'+s))
for name in ('adaptaris','ferronox','colonyx','evolyss'):
 geo=json.loads((a/f'geo/{name}.geo.json').read_text())['minecraft:geometry'][0];anim=json.loads((a/f'animations/{name}.animation.json').read_text())['animations']
 groups=[];elements=[];nodes={};outline=[];animations=[]
 for b in geo['bones']:
  ident=uid(name+'/'+b['name']);p=b.get('pivot',[0,0,0]);groups.append({'name':b['name'],'uuid':ident,'origin':[-p[0],p[1],p[2]],'rotation':[-b.get('rotation',[0,0,0])[0],-b.get('rotation',[0,0,0])[1],b.get('rotation',[0,0,0])[2]],'export':True,'visibility':b['name'] not in {'thermal','cold','electric','impact','absolute','shield','blade','bloom'}});nodes[b['name']]={'uuid':ident,'isOpen':False,'children':[]}
  for i,c in enumerate(b.get('cubes',[])):
   x,y,z=c['origin'];w,h,d=c['size'];ident=uid(name+'/'+b['name']+'/'+str(i));elements.append({'name':b['name']+'_'+str(i),'type':'cube','uuid':ident,'from':[-x-w,y,z],'to':[-x,y+h,z+d],'box_uv':True,'uv_offset':c['uv'],'autouv':0,'inflate':c.get('inflate',0),'faces':{f:{'texture':0} for f in ('north','south','east','west','up','down')}});nodes[b['name']]['children'].append(ident)
 for b in geo['bones']:(nodes[b['parent']]['children'] if 'parent' in b else outline).append(nodes[b['name']])
 for n,v in anim.items():
  animators={}
  for bone,channels in v.get('bones',{}).items():
   frames=[]
   for channel,values in channels.items():
    if not isinstance(values,dict):values={'0':values}
    for time,vec in values.items():
     if isinstance(vec,dict):vec=vec.get('post',vec.get('pre',[0,0,0]))
     if not isinstance(vec,list):vec=[vec]*3
     vec=list(vec)
     for axis in ((0,) if channel=='position' else (0,1) if channel=='rotation' else ()):
      vec[axis]=-vec[axis] if isinstance(vec[axis],(int,float)) else f'-({vec[axis]})'
     frames.append({'channel':channel,'time':float(time),'interpolation':'linear','uuid':uid(name+n+bone+channel+time),'data_points':[dict(zip(('x','y','z'),map(str,vec)))]})
   animators[uid(name+'/'+bone)]={'name':bone,'type':'bone','keyframes':frames}
  animations.append({'name':n,'uuid':uid(name+'/animation/'+n),'loop':'loop' if v.get('loop') else 'once','length':v.get('animation_length',1),'animators':animators})
 texture=base64.b64encode((a/f'textures/entity/{name}.png').read_bytes()).decode()
 model={'meta':{'format_version':'5.0','model_format':'bedrock','box_uv':True},'name':name,'model_identifier':'genesis.'+name,'resolution':{'width':128,'height':128},'elements':elements,'groups':groups,'outliner':outline,'animations':animations,'textures':[{'name':name+'.png','uuid':uid(name+'/texture'),'id':'0','width':128,'height':128,'uv_width':128,'uv_height':128,'mode':'bitmap','internal':True,'source':'data:image/png;base64,'+texture}]}
 (out/(name+'.bbmodel')).write_text(json.dumps(model,ensure_ascii=False,indent=2)+'\n');print(name,len(elements),'cubes',len(animations),'animations')
