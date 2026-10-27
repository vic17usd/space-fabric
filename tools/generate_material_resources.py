#!/usr/bin/env python3
"""Genera assets originales de 16px y datos de los materiales, de forma determinista."""
import copy
import hashlib
import json
from pathlib import Path
import random
import struct
import zlib

ROOT = Path(__file__).resolve().parents[1] / "src/main/resources"
BLOCKS = {
    "mercury_regolith": ("Regolito de Mercurio", "Mercury Regolith", (122, 120, 114)),
    "lunar_regolith": ("Regolito lunar", "Lunar Regolith", (167, 167, 164)),
    "venus_hot_rock": ("Roca volcánica ardiente", "Hot Volcanic Rock", (61, 51, 45)),
    "martian_stone": ("Piedra marciana", "Martian Stone", (157, 66, 35)),
    "ares_crystal_ore": ("Mena de cristal de Ares", "Ares Crystal Ore", (157, 66, 35)),
    "phobosite_ore": ("Mena de fobosita", "Phobosite Ore", (157, 66, 35)),
    "uranian_methane_ice": ("Hielo de metano de Urano", "Uranian Methane Ice", (130, 208, 213)),
    "neptunian_methane_ice": ("Hielo de metano de Neptuno", "Neptunian Methane Ice", (54, 108, 190)),
    "terrestrial_basalt": ("Basalto terrestre", "Terrestrial Basalt", (57, 63, 57)),
    "jovian_meteorite": ("Meteorito joviano", "Jovian Meteorite", (111, 91, 70)),
    "saturn_ring_ice": ("Hielo de los anillos de Saturno", "Saturn Ring Ice", (212, 204, 165)),
}


def write(relative, data):
    path = ROOT / relative
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n")


def png(relative, pixels):
    def chunk(kind, body):
        return struct.pack(">I", len(body)) + kind + body + struct.pack(">I", zlib.crc32(kind + body))
    raw = b"".join(b"\0" + bytes(sum(row, ())) for row in pixels)
    contents = b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", len(pixels[0]), len(pixels), 8, 6, 0, 0, 0))
    contents += chunk(b"IDAT", zlib.compress(raw)) + chunk(b"IEND", b"")
    path = ROOT / relative
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(contents)


def texture(name, color, icon=False):
    rng = random.Random(int.from_bytes(hashlib.sha256(name.encode()).digest()[:8], "big"))
    rows = []
    ore_color = (58, 218, 202) if name == "ares_crystal_ore" else (197, 110, 224)
    for y in range(16):
        row = []
        for x in range(16):
            delta = rng.randint(-21, 21)
            pixel = tuple(max(0, min(255, c + delta)) for c in color) + (255,)
            if "_ore" in name and ((x * 3 + y * 5) % 19 < 3):
                pixel = ore_color + (255,)
            if name == "venus_hot_rock" and ((x + y * 3) % 13 == 0):
                pixel = (242, 107 + rng.randrange(45), 27, 255)
            if "ice" in name and (x + y * 2) % 11 == 0:
                pixel = tuple(min(255, c + 40) for c in color) + (255,)
            if icon and abs(x - 8) + abs(y - 8) > 6:
                pixel = (0, 0, 0, 0)
            row.append(pixel)
        rows.append(row)
    return rows


def ore(name, target, block, size, count, low, high, feature_type="minecraft:ore"):
    write(f"data/space_fabric/worldgen/configured_feature/{name}.json", {
        "type": feature_type, "config": {"size": size, "discard_chance_on_air_exposure": 0.0,
        "targets": [{"target": target, "state": {"Name": "space_fabric:" + block}}]}})
    write(f"data/space_fabric/worldgen/placed_feature/{name}.json", {
        "feature": "space_fabric:" + name,
        "placement": [{"type": "minecraft:count", "count": count}, {"type": "minecraft:in_square"},
        {"type": "minecraft:height_range", "height": {"type": "minecraft:uniform",
        "min_inclusive": {"absolute": low}, "max_inclusive": {"absolute": high}}}, {"type": "minecraft:biome"}]})


def main():
    for name, (_, _, color) in BLOCKS.items():
        write(f"assets/space_fabric/blockstates/{name}.json", {"variants": {"": {"model": f"space_fabric:block/{name}"}}})
        write(f"assets/space_fabric/models/block/{name}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"space_fabric:block/{name}"}})
        write(f"assets/space_fabric/items/{name}.json", {"model": {"type": "minecraft:model", "model": f"space_fabric:block/{name}"}})
        png(f"assets/space_fabric/textures/block/{name}.png", texture(name, color))
        write(f"data/space_fabric/loot_table/blocks/{name}.json", {"type": "minecraft:block", "pools": [{
            "rolls": 1.0, "entries": [{"type": "minecraft:item", "name": "space_fabric:" + name}],
            "conditions": [{"condition": "minecraft:survives_explosion"}]}]})

    for name, color in [("ares_crystal", (41, 214, 196)), ("raw_phobosite", (173, 91, 205))]:
        png(f"assets/space_fabric/textures/item/{name}.png", texture(name, color, icon=True))
        write(f"assets/space_fabric/models/item/{name}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"space_fabric:item/{name}"}})
        write(f"assets/space_fabric/items/{name}.json", {"model": {"type": "minecraft:model", "model": f"space_fabric:item/{name}"}})
    write("assets/space_fabric/items/thermal_boots.json", {"model": {"type": "minecraft:model", "model": "minecraft:item/netherite_boots"}})

    for locale in ["es_es", "en_us"]:
        path = ROOT / f"assets/space_fabric/lang/{locale}.json"
        text = json.loads(path.read_text())
        for name, (spanish, english, _) in BLOCKS.items():
            text[f"block.space_fabric.{name}"] = spanish if locale == "es_es" else english
        for name, spanish, english in [("ares_crystal", "Cristal de Ares", "Ares Crystal"),
                                       ("raw_phobosite", "Fobosita en bruto", "Raw Phobosite"),
                                       ("thermal_boots", "Botas térmicas", "Thermal Boots")]:
            text[f"item.space_fabric.{name}"] = spanish if locale == "es_es" else english
        text["itemGroup.space_fabric.materials"] = "Materiales del sistema solar" if locale == "es_es" else "Solar System Materials"
        write(f"assets/space_fabric/lang/{locale}.json", text)

    silk_condition = {"condition": "minecraft:match_tool", "predicate": {"predicates": {
        "minecraft:enchantments": [{"enchantments": "minecraft:silk_touch", "levels": {"min": 1}}]}}}
    for block, item in [("ares_crystal_ore", "ares_crystal"), ("phobosite_ore", "raw_phobosite")]:
        write(f"data/space_fabric/loot_table/blocks/{block}.json", {"type": "minecraft:block", "pools": [{
            "rolls": 1.0, "entries": [{"type": "minecraft:alternatives", "children": [
                {"type": "minecraft:item", "name": "space_fabric:" + block, "conditions": [copy.deepcopy(silk_condition)]},
                {"type": "minecraft:item", "name": "space_fabric:" + item, "functions": [
                    {"function": "minecraft:apply_bonus", "enchantment": "minecraft:fortune", "formula": "minecraft:ore_drops"},
                    {"function": "minecraft:explosion_decay"}]}]}]}], "random_sequence": "space_fabric:blocks/" + block})

    tag = lambda path, values: write(f"data/minecraft/tags/block/{path}.json", {"replace": False, "values": ["space_fabric:" + value for value in values]})
    tag("mineable/shovel", ["mercury_regolith", "lunar_regolith"])
    tag("mineable/pickaxe", [name for name in BLOCKS if "regolith" not in name])
    tag("needs_stone_tool", ["venus_hot_rock", "martian_stone", "terrestrial_basalt", "jovian_meteorite"])
    tag("needs_diamond_tool", ["ares_crystal_ore", "phobosite_ore"])
    write("data/space_fabric/tags/item/thermal_boots.json", {"replace": False, "values": ["space_fabric:thermal_boots"]})
    path = ROOT / "data/space_fabric/tags/item/space_suit/boots.json"
    data = json.loads(path.read_text())
    if "space_fabric:thermal_boots" not in data["values"]:
        data["values"].append("space_fabric:thermal_boots")
    write("data/space_fabric/tags/item/space_suit/boots.json", data)
    write("data/space_fabric/recipe/thermal_boots.json", {"type": "minecraft:crafting_shapeless", "category": "equipment",
        "ingredients": ["minecraft:netherite_boots", "space_fabric:ares_crystal", "minecraft:magma_cream"],
        "result": {"id": "space_fabric:thermal_boots", "count": 1}})

    match = lambda block: {"predicate_type": "minecraft:block_match", "block": block}
    ore("mercury_regolith", match("minecraft:smooth_basalt"), "mercury_regolith", 24, 8, -48, 96)
    ore("lunar_regolith", match("minecraft:smooth_basalt"), "lunar_regolith", 24, 8, -48, 96)
    ore("venus_hot_rock", match("minecraft:basalt"), "venus_hot_rock", 24, 12, -48, 128)
    ore("ares_crystal", match("space_fabric:martian_stone"), "ares_crystal_ore", 5, 3, -48, 24)
    ore("phobosite", match("space_fabric:martian_stone"), "phobosite_ore", 3, 2, -56, -8)
    ore("terrestrial_basalt", {"predicate_type": "minecraft:tag_match", "tag": "minecraft:base_stone_overworld"},
        "terrestrial_basalt", 32, 6, -48, 0, feature_type="space_fabric:terrestrial_basalt")
    for name in ["jovian_meteorite", "saturn_ring_ice", "uranian_methane_ice", "neptunian_methane_ice"]:
        write(f"data/space_fabric/worldgen/configured_feature/{name}.json", {"type": "space_fabric:orbital_material", "config": {"state": {"Name": "space_fabric:" + name}}})
        write(f"data/space_fabric/worldgen/placed_feature/{name}.json", {"feature": "space_fabric:" + name,
            "placement": [{"type": "minecraft:rarity_filter", "chance": 4}, {"type": "minecraft:biome"}]})

    for planet, regolith in [("mercury", "mercury_regolith"), ("moon", "lunar_regolith"), ("venus", "venus_hot_rock")]:
        path = ROOT / f"data/space_fabric/worldgen/noise_settings/{planet}.json"
        noise = json.loads(path.read_text())
        noise["surface_rule"]["sequence"] = [noise["surface_rule"]["sequence"][0], {
            "type": "minecraft:condition", "if_true": {"type": "minecraft:stone_depth", "add_surface_depth": False,
            "offset": 2, "secondary_depth_range": 0, "surface_type": "floor"},
            "then_run": {"type": "minecraft:block", "result_state": {"Name": "space_fabric:" + regolith}}},
            {"type": "minecraft:block", "result_state": noise["default_block"]}]
        write(f"data/space_fabric/worldgen/noise_settings/{planet}.json", noise)

    path = ROOT / "data/space_fabric/worldgen/noise_settings/mars.json"
    noise = json.loads(path.read_text().replace("minecraft:red_terracotta", "space_fabric:martian_stone"))
    write("data/space_fabric/worldgen/noise_settings/mars.json", noise)
    path = ROOT / "data/space_fabric/worldgen/configured_feature/oxide_iron.json"
    data = json.loads(path.read_text().replace("minecraft:red_terracotta", "space_fabric:martian_stone"))
    write("data/space_fabric/worldgen/configured_feature/oxide_iron.json", data)
    path = ROOT / "data/space_fabric/tags/block/carver_replaceables.json"
    data = json.loads(path.read_text())
    for name in ["martian_stone", "ares_crystal_ore", "phobosite_ore"]:
        if "space_fabric:" + name not in data["values"]: data["values"].append("space_fabric:" + name)
    write("data/space_fabric/tags/block/carver_replaceables.json", data)
    for planet in ["jupiter", "saturn", "uranus", "neptune"]:
        path = ROOT / f"data/space_fabric/dimension/{planet}.json"
        data = json.loads(path.read_text())
        data["generator"]["type"] = "space_fabric:orbital"
        data["generator"]["settings"]["features"] = True
        write(f"data/space_fabric/dimension/{planet}.json", data)
    print("Materiales: 11 bloques, 3 ítems, texturas, loot, traducciones y 10 placed features.")


if __name__ == "__main__":
    main()
