#!/usr/bin/env python3
"""Suit recipes, tags, custom equipment materials and imported artwork."""
import json
from pathlib import Path
R=Path(__file__).resolve().parents[1];D=R/'src/main/resources/data';A=R/'src/main/resources/assets/space_fabric'
TIERS={'vacuum':('vacío','Vacuum'),'venus':('venusiano','Venus'),'cryo':('criogénico','Cryogenic'),'abyssal':('abisal','Abyssal')}
PARTS={'helmet':('casco','Helmet','helmets','HELMET'),'chestplate':('pechera','Chestplate','chestplates','CHESTPLATE'),'leggings':('pantalones','Leggings','leggings','LEGGINGS'),'boots':('botas','Boots','boots','BOOTS')}
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,indent=2,ensure_ascii=False)+'\n')
def main():
 fields=[]
 for tier,(es,en) in TIERS.items():
  for part,(pes,pen,tag,typ) in PARTS.items():
   name=tier+'_suit_'+part
   fields.append(f'    public static final Item {name.upper()} = register("{name}", dev.vic17usd.spacefabric.equipment.SuitItem::new, new Item.Properties().humanoidArmor(SuitMaterials.forTier("{tier}"), ArmorType.{typ}).fireResistant().component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_rating.{tier}"),net.minecraft.network.chat.Component.translatable("item.space_fabric.suit_complete")))));')
   write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'space_fabric:item/'+name}})
   write(A/f'models/item/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'space_fabric:item/'+name}})
   write(D/f'space_fabric/tags/item/suits/{tier}/{tag}.json',{'replace':False,'values':['space_fabric:'+name]})
   ing=['minecraft:iron_'+part,'minecraft:glass','space_fabric:empty_fuel_cell'] if tier=='vacuum' else ['space_fabric:vacuum_suit_'+part,'minecraft:netherite_ingot','minecraft:obsidian','space_fabric:sulfate_dust'] if tier=='venus' else ['space_fabric:vacuum_suit_'+part,'minecraft:packed_ice','minecraft:diamond','space_fabric:sulfate_dust'] if tier=='cryo' else ['space_fabric:cryo_suit_'+part,'space_fabric:obsidian_core','minecraft:netherite_ingot']
   write(D/f'space_fabric/recipe/{name}.json',{'type':'minecraft:crafting_shapeless','category':'equipment','ingredients':ing,'result':{'id':'space_fabric:'+name,'count':1}})
   for locale in ('es_es','en_us'):
    p=A/f'lang/{locale}.json';lang=json.loads(p.read_text());lang['item.space_fabric.'+name]=f'{pes.capitalize()} del traje {es}' if locale=='es_es' else f'{en} Suit {pen}';write(p,lang)
 for part,(_,_,tag,_) in PARTS.items():
  write(D/f'space_fabric/tags/item/space_suit/{tag}.json',{'replace':False,'values':['space_fabric:'+t+'_suit_'+part for t in TIERS]})
 p=D/'space_fabric/tags/item/thermal_boots.json';doc=json.loads(p.read_text())
 for tier in TIERS:
  value='space_fabric:'+tier+'_suit_boots'
  if value not in doc['values']:doc['values'].append(value)
 write(p,doc)
 p=R/'src/main/java/dev/vic17usd/spacefabric/item/ModItems.java';s=p.read_text();marker='    private static final Map<String, Item> ITEMS = new LinkedHashMap<>();';
 if 'public static final Item VACUUM_SUIT_HELMET' not in s:s=s.replace(marker,marker+'\n'+ '\n'.join(fields))
 else:
  start=s.index('    public static final Item VACUUM_SUIT_HELMET');end=s.index('\n',s.index('    public static final Item ABYSSAL_SUIT_BOOTS'))
  s=s[:start]+'\n'.join(fields)+s[end:]
 p.write_text(s)
 damage=['space_fabric:'+n for n in ('decompression','crushing_pressure','hypoxia','thermal_exposure')]
 write(D/'space_fabric/damage_type/thermal_exposure.json',{'message_id':'space_fabric.thermal_exposure','scaling':'never','exhaustion':0.0,'effects':'hurt'})
 for tag in ('bypasses_armor','bypasses_shield','bypasses_cooldown','bypasses_resistance','bypasses_enchantments','bypasses_invulnerability'):
  p=D/f'minecraft/tags/damage_type/{tag}.json';doc=json.loads(p.read_text()) if p.exists() else {'replace':False,'values':[]}
  for name in damage:
   if name not in doc['values']:doc['values'].append(name)
  write(p,doc)
 for locale in ('es_es','en_us'):
  p=A/f'lang/{locale}.json';doc=json.loads(p.read_text());doc.update({'death.attack.space_fabric.thermal_exposure':'%1$s sucumbió al ambiente planetario' if locale=='es_es' else '%1$s succumbed to the planetary environment','message.space_fabric.exposure':'¡Exposición en %s! Presión %s kPa — riesgo %s%%' if locale=='es_es' else 'Exposure on %s! Pressure %s kPa — danger %s%%'});write(p,doc)
if __name__=='__main__':
 main()
 from import_equipment_textures import import_art
 import_art()
