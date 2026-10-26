#!/usr/bin/env python3
"""Original 16px pixel art and data for Earth minerals, driven by earth_minerals.json.
No vanilla PNGs or third-party art are copied. Safe to rerun without rewriting other content.
"""
import hashlib
import json
import math
import random
from pathlib import Path
from generate_material_resources import ROOT, write, png
from generate_geology_resources import merge_tag

CATALOG = json.loads((Path(__file__).parent/'earth_minerals.json').read_text())

def shade(color,delta):
    return tuple(max(0,min(255,round(c+delta))) for c in color)+(255,)

def rng_for(name):
    return random.Random(int.from_bytes(hashlib.sha256(name.encode()).digest()[:8],'big'))

def rect(p,x,y,xx,yy,color):
    for row in range(max(0,y),min(16,yy+1)):
        for col in range(max(0,x),min(16,xx+1)):p[row][col]=color

def ore_pixels(m,deep):
    # Common original host rock, and the same mineral outlines in both depth variants.
    rng=rng_for('earth_host_'+str(deep));base=(65,66,70) if deep else (119,120,117)
    p=[[shade(base,rng.randint(-11,11)+(4 if (y%4==0 and deep) else 0)) for x in range(16)] for y in range(16)]
    rng=rng_for(m['id']);color=m['color']
    for cx,cy in [(2,3),(9,2),(6,8),(12,11),(2,13)]:
        x=cx+rng.randint(-1,1);y=cy+rng.randint(-1,1);style=m['pattern']
        if style=='nodules':
            for dy in range(-2,3):
                for dx in range(-2,3):
                    if dx*dx+dy*dy<=5 and 0<=x+dx<16 and 0<=y+dy<16:
                        p[y+dy][x+dx]=shade(color,-40 if dx*dx+dy*dy>=4 else 25-10*dy)
        elif style in ['cubes','zoned_cubes']:
            rect(p,x-1,y-1,x+2,y+2,shade(color,-35));rect(p,x-1,y-1,x+1,y+1,shade(color,35))
            rect(p,x,y,x+1,y+1,shade(m['metal_color'] if style=='zoned_cubes' else color,0))
            rect(p,x-1,y-1,x+1,y-1,shade(color,65))
        elif style=='veins':
            for d in range(4):
                xx=x+d//2;yy=y+d-1
                if 0<=xx<16 and 0<=yy<16:
                    p[yy][xx]=shade(color,35);p[yy][min(15,xx+1)]=shade(color,-25)
            rect(p,x,y,x,y,shade(color,100))
        elif style=='plates':
            rect(p,x-1,y-1,x+2,y,shade(color,-20));rect(p,x,y-1,x+2,y-1,shade(color,55))
            rect(p,x,y+1,x+2,y+1,shade(color,10))
        elif style=='prisms':
            rect(p,x,y-2,x+1,y+1,shade(color,15));rect(p,x-1,y-1,x-1,y,shade(color,-30));rect(p,x,y-2,x,y-1,shade(color,95))
        elif style=='facets':
            for dy in range(-2,3):
                for dx in range(-2,3):
                    if abs(dx)+abs(dy)<=2 and 0<=x+dx<16 and 0<=y+dy<16:
                        p[y+dy][x+dx]=shade(color,45 if dx+dy<0 else -20)
        else:
            rect(p,x-1,y,x+1,y+1,shade(color,-10));rect(p,x,y-1,x+1,y,shade(color,45))
    return p

def storage_pixels(m):
    rng=rng_for('earth_storage_'+m['id']);color=m['metal_color']
    p=[[shade(color,rng.randint(-5,5)+(10 if y<8 else -4)) for x in range(16)] for y in range(16)]
    if m['metal']:
        for y in range(16):
            for x in range(16):
                if x in [0,15] or y in [0,15]:p[y][x]=shade(color,-40)
                elif x==1 or y==1:p[y][x]=shade(color,40)
                elif x==14 or y==14:p[y][x]=shade(color,-20)
                elif (x+2*y)%17==0:p[y][x]=shade(color,20)
    else:
        for y in range(16):
            for x in range(16):
                zone=min(x,y,15-x,15-y)//2
                p[y][x]=shade(m['color'] if zone%2==0 else color,12 if x+y<15 else -12)
    return p

def in_polygon(x,y,points):
    inside=False
    for (a,b),(c,d) in zip(points,points[1:]+points[:1]):
        if (b>y)!=(d>y) and x<(c-a)*(y-b)/(d-b)+a:inside=not inside
    return inside

def item_pixels(m,raw):
    p=[[(0,0,0,0) for _ in range(16)] for _ in range(16)]
    color=m['color'] if raw else m['metal_color'];rng=rng_for('earth_icon_'+m['id'])
    for y in range(2,14):
        for x in range(2,15):
            pixel_color=color
            if not m['metal']:
                inside=abs(x-8)*.8+abs(y-7)<=6
                delta=(45 if x<8 else -20)+(16 if y<7 else -12)
                pixel_color=m["color"] if x>=8 else color
            elif raw:
                inside=(x-7)**2/31+(y-8)**2/24<1 and not(x==3 and y<7)
                delta=rng.randint(-18,18)+(25 if x+y<14 else -15)
            else:
                inside=in_polygon(x+.5,y+.5,[(6,4),(12,4),(15,7),(10,13),(2,13),(2,9)])
                delta=35 if y<9 and x+y<21 else -25
                if y==6 and 6<=x<=11:delta=65
                if y>=10:delta-=12
            if inside:p[y][x]=shade(pixel_color,delta)
    # A one-pixel dark outline; transparent silhouette, never a filled square icon.
    old=[row[:] for row in p]
    for y in range(1,15):
        for x in range(1,15):
            if old[y][x][3] and any(old[y+dy][x+dx][3]==0 for dx,dy in [(1,0),(-1,0),(0,1),(0,-1)]):p[y][x]=shade(color,-65)
    return p

def block_assets(name,pixels,loot):
    write(f'assets/space_fabric/blockstates/{name}.json',{'variants':{'':{'model':f'space_fabric:block/{name}'}}})
    write(f'assets/space_fabric/models/block/{name}.json',{'parent':'minecraft:block/cube_all','textures':{'all':f'space_fabric:block/{name}'}})
    write(f'assets/space_fabric/items/{name}.json',{'model':{'type':'minecraft:model','model':f'space_fabric:block/{name}'}})
    png(f'assets/space_fabric/textures/block/{name}.png',pixels)
    write(f'data/space_fabric/loot_table/blocks/{name}.json',loot)

def self_loot(name):
    return {'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':f'space_fabric:{name}'}]}]}

def ore_loot(name,drop):
    return {'type':'minecraft:block','random_sequence':f'space_fabric:blocks/{name}','pools':[{'rolls':1,'entries':[{'type':'minecraft:alternatives','children':[
        {'type':'minecraft:item','name':f'space_fabric:{name}','conditions':[{'condition':'minecraft:match_tool','predicate':{'predicates':{'minecraft:enchantments':[{'enchantments':'minecraft:silk_touch','levels':{'min':1}}]}}}]},
        {'type':'minecraft:item','name':f'space_fabric:{drop}','functions':[{'function':'minecraft:apply_bonus','enchantment':'minecraft:fortune','formula':'minecraft:ore_drops'},{'function':'minecraft:explosion_decay'}]}]}]}]}

def recipe(name,data,source):
    write(f'data/space_fabric/recipe/{name}.json',data)
    write(f'data/space_fabric/advancement/recipes/earth/{name}.json',{
        'parent':'minecraft:recipes/root','criteria':{
            'has_material':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':[f'space_fabric:{source}']}]}}
        },'requirements':[['has_material']],'rewards':{'recipes':[f'space_fabric:{name}']}})

def main():
    locales={locale:json.loads((ROOT/f'assets/space_fabric/lang/{locale}.json').read_text()) for locale in ['es_es','en_us']}
    java='''package dev.vic17usd.spacefabric.earth;

/** Generated from tools/earth_minerals.json by generate_earth_resources.py. */
public enum EarthMineral {
'''
    for i,m in enumerate(CATALOG):
        metal='"'+m['metal']+'"' if m['metal'] else 'null'
        java+=f'    {m["id"].upper()}("{m["id"]}",{metal},"{m["tool"]}",{m["size"]},{m["count"]},{m["min_y"]},{m["max_y"]})'+(';' if i==len(CATALOG)-1 else ',')+'\n'
    java+='''    public final String path, metal, tool;
    public final int veinSize, attempts, minY, maxY;
    EarthMineral(String path,String metal,String tool,int veinSize,int attempts,int minY,int maxY) {
        this.path=path;this.metal=metal;this.tool=tool;this.veinSize=veinSize;
        this.attempts=attempts;this.minY=minY;this.maxY=maxY;
    }
    public boolean isMetal() { return metal!=null; }
    public String oreId(boolean deep) { return (deep ? "deepslate_" : "")+path+"_ore"; }
    public String rawId() { return "raw_"+path; }
    public String productId() { return isMetal() ? metal+"_ingot" : path+"_crystal"; }
    public String storageId() { return (isMetal() ? metal : path)+"_block"; }
}
'''
    (ROOT.parent/'java/dev/vic17usd/spacefabric/earth/EarthMineral.java').write_text(java)
    for m in CATALOG:
        name=m['id'];product=m['metal']+'_ingot' if m['metal'] else name+'_crystal'
        raw='raw_'+name if m['metal'] else product
        storage=(m['metal'] or name)+'_block'
        for deep in [False,True]:
            block=('deepslate_' if deep else '')+name+'_ore'
            block_assets(block,ore_pixels(m,deep),ore_loot(block,raw))
            for locale,text in locales.items():
                text[f'block.space_fabric.{block}']=(f'Mena de {m["es"]}'+(' en pizarra profunda' if deep else '')) if locale=='es_es' else ('Deepslate ' if deep else '')+m['en']+' Ore'
        block_assets(storage,storage_pixels(m),self_loot(storage))
        for id,israw in ([(raw,True),(product,False)] if m['metal'] else [(product,False)]):
            png(f'assets/space_fabric/textures/item/{id}.png',item_pixels(m,israw))
            write(f'assets/space_fabric/models/item/{id}.json',{'parent':'minecraft:item/generated','textures':{'layer0':f'space_fabric:item/{id}'}})
            write(f'assets/space_fabric/items/{id}.json',{'model':{'type':'minecraft:model','model':f'space_fabric:item/{id}'}})
        for locale,text in locales.items():
            text[f'block.space_fabric.{storage}']=f'Bloque de {m["metal_es"] or m["es"]}' if locale=='es_es' else (m['metal_en'] or m['en'])+' Block'
            if m['metal']:
                text[f'item.space_fabric.{raw}']=f'{m["es"].capitalize()} en bruto' if locale=='es_es' else 'Raw '+m['en']
                text[f'item.space_fabric.{product}']=f'Lingote de {m["metal_es"]}' if locale=='es_es' else m['metal_en']+' Ingot'
            else:text[f'item.space_fabric.{product}']='Cristal de fluorita' if locale=='es_es' else 'Fluorite Crystal'
        ores=[name+'_ore','deepslate_'+name+'_ore']
        merge_tag('data/minecraft/tags/block/mineable/pickaxe.json',['space_fabric:'+s for s in ores+[storage]])
        merge_tag(f'data/minecraft/tags/block/needs_{m["tool"]}_tool.json',['space_fabric:'+s for s in ores])
        merge_tag('data/minecraft/tags/block/needs_stone_tool.json',['space_fabric:'+storage])
        for prefix in ['c','space_fabric']:
            merge_tag(f'data/{prefix}/tags/block/ores/{name}.json',['space_fabric:'+s for s in ores])
        merge_tag('data/c/tags/block/ores.json',[f'#c:ores/{name}'])
        merge_tag(f'data/c/tags/item/ores/{name}.json',['space_fabric:'+s for s in ores])
        merge_tag('data/c/tags/item/ores.json',[f'#c:ores/{name}'])
        merge_tag(f'data/c/tags/item/{"ingots" if m["metal"] else "gems"}/{m["metal"] or name}.json',['space_fabric:'+product])
        merge_tag(f'data/c/tags/item/{"ingots" if m["metal"] else "gems"}.json',['space_fabric:'+product])
        merge_tag(f'data/c/tags/block/storage_blocks/{m["metal"] or name}.json',['space_fabric:'+storage])
        merge_tag('data/c/tags/block/storage_blocks.json',[f'#c:storage_blocks/{m["metal"] or name}'])
        merge_tag(f'data/c/tags/item/storage_blocks/{m["metal"] or name}.json',['space_fabric:'+storage])
        merge_tag('data/c/tags/item/storage_blocks.json',[f'#c:storage_blocks/{m["metal"] or name}'])
        if m['metal']:
            merge_tag(f'data/c/tags/item/raw_materials/{m["metal"]}.json',['space_fabric:'+raw])
            for source in [raw]+ores:
                for method,time in [('smelting',200),('blasting',100)]:
                    recipe(f'{source}_{method}',{'type':'minecraft:'+method,'category':'misc','ingredient':'space_fabric:'+source,'result':{'id':'space_fabric:'+product},'experience':.7,'cookingtime':time},source)
        recipe(storage,{'type':'minecraft:crafting_shaped','category':'building','pattern':['MMM','MMM','MMM'],'key':{'M':'space_fabric:'+product},'result':{'id':'space_fabric:'+storage}},product)
        recipe(product+'_from_block',{'type':'minecraft:crafting_shapeless','category':'misc','ingredients':['space_fabric:'+storage],'result':{'id':'space_fabric:'+product,'count':9}},storage)
        write(f'data/space_fabric/worldgen/configured_feature/earth_{name}.json',{'type':'minecraft:ore','config':{'size':m['size'],'discard_chance_on_air_exposure':0.0,'targets':[
            {'target':{'predicate_type':'minecraft:tag_match','tag':host},'state':{'Name':'space_fabric:'+block}}
            for host,block in zip(['minecraft:stone_ore_replaceables','minecraft:deepslate_ore_replaceables'],ores)]}})
        write(f'data/space_fabric/worldgen/placed_feature/earth_{name}.json',{'feature':'space_fabric:earth_'+name,'placement':[
            {'type':'minecraft:count','count':m['count']},{'type':'minecraft:in_square'},
            {'type':'minecraft:height_range','height':{'type':'minecraft:uniform','min_inclusive':{'absolute':m['min_y']},'max_inclusive':{'absolute':m['max_y']}}},
            {'type':'minecraft:biome'}]})
    for locale,text in locales.items():write(f'assets/space_fabric/lang/{locale}.json',text)
    print('Earth: 8 minerals, 24 blocks, 15 items, 39 original textures, 8 placed features and 58 recipes/unlocks.')

if __name__=='__main__':main()
