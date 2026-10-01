"""Check resource references, geometry, UV bounds and native project exports (Python 3)."""
import base64,json,struct
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/'src/main/resources';ASSETS=RES/'assets/genesis'
count=0
for path in RES.rglob('*.json'):
 json.loads(path.read_text());count+=1
for name in ('adaptaris','ferronox','colonyx','evolyss'):
 geo=json.loads((ASSETS/f'geo/{name}.geo.json').read_text())['minecraft:geometry'][0]
 bones={b['name']:b for b in geo['bones']}
 assert len(bones)==len(geo['bones']),f'{name}: duplicate bones'
 cubes=0
 for b in bones.values():
  chain=set();current=b
  while current.get('parent'):
   parent=current['parent'];assert parent in bones and parent not in chain,(name,b['name'],'hierarchy');chain.add(parent);current=bones[parent]
  for c in b.get('cubes',[]):
   cubes+=1;w,h,d=c['size'];u,v=c['uv']
   assert min(w,h,d)>0,(name,b['name'],'empty cube')
   assert 0<=u and 0<=v and u+2*(w+d)<=128 and v+h+d<=128,(name,b['name'],'UV overflow')
 png=(ASSETS/f'textures/entity/{name}.png').read_bytes()
 assert png[:8]==b'\x89PNG\r\n\x1a\n' and struct.unpack('>II',png[16:24])==(128,128)
 animations=json.loads((ASSETS/f'animations/{name}.animation.json').read_text())['animations']
 for key,a in animations.items():
  assert set(a.get('bones',{}))<=set(bones),(name,key,'unknown animated bone')
 native=json.loads((ROOT/f'art/blockbench/{name}.bbmodel').read_text())
 assert len(native['elements'])==cubes and len(native['animations'])==len(animations)
 assert base64.b64decode(native['textures'][0]['source'].split(',',1)[1])==png
 assert {b['name'] for b in native['groups']}==set(bones)
 print(f'{name}: {cubes} cubes, {len(animations)} animations, texture and native project OK')
expected={'adaptaris','ferronox','colonyx','adaptaris_absolute'}
powers=RES/'data/genesis/palladium/powers'
assert {p.stem for p in powers.glob('*.json')}==expected
assert (RES/'data/genesis/structures/oriel_observatory.nbt').is_file()
assert (RES/'data/genesis/structures/empty.nbt').is_file()
for path in ROOT.joinpath('src/main/java').rglob('*.java'):
 if 'import net.threetag.palladium' in path.read_text():assert '/palladium/' in path.as_posix(),f'Leaked Palladium dependency: {path}'
print(f'{count} JSON resources parsed; four powers and two structure templates present; bridge isolation OK')
