#!/usr/bin/env python3
"""Native damage resources and bilingual messages for local Venus volcanic activity."""
from pathlib import Path
import json
ROOT=Path(__file__).resolve().parents[1]/'src/main/resources';A=ROOT/'assets/space_fabric';D=ROOT/'data/space_fabric'
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
for name in ['volcanic_gas','volcanic_ejecta']:
 write(D/f'damage_type/{name}.json',{'message_id':'space_fabric.'+name,'scaling':'never','exhaustion':0.0,'effects':'hurt'})
for name in ['bypasses_armor','bypasses_shield','is_fire']:
 p=ROOT/f'data/minecraft/tags/damage_type/{name}.json';d=json.loads(p.read_text()) if p.exists() else {'replace':False,'values':[]}
 d['values']=list(dict.fromkeys(d['values']+(['space_fabric:volcanic_ejecta'] if name=='is_fire' else ['space_fabric:volcanic_gas','space_fabric:volcanic_ejecta'])));write(p,d)
texts={
 'death.attack.space_fabric.volcanic_gas':('%1$s fue alcanzado por gases volcánicos de Venus','%1$s was overcome by Venus volcanic gases'),
 'death.attack.space_fabric.volcanic_ejecta':('%1$s quedó atrapado en una erupción venusiana','%1$s was caught in a Venus eruption'),
 'volcanism.space_fabric.gas_warning':('Fumarola: expulsión en %s s · %s m','Fumarole: venting in %s s · %s m'),
 'volcanism.space_fabric.gas_active':('Gas caliente: aléjate del chorro · %s s · %s m','Hot gas: move away from the jet · %s s · %s m'),
 'volcanism.space_fabric.eruption_warning':('Zona volcánica: erupción en %s s · %s m','Volcanic area: eruption in %s s · %s m'),
 'volcanism.space_fabric.eruption_active':('Erupción activa: busca cobertura · %s s · %s m','Active eruption: take cover · %s s · %s m'),
}
for index,lang in enumerate(['es_es','en_us']):
 p=A/f'lang/{lang}.json';d=json.loads(p.read_text());d.update({k:v[index] for k,v in texts.items()});write(p,d)
