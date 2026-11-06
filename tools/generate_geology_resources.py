#!/usr/bin/env python3
"""Assets originales y worldgen de la geología solicitada. Ejecutar después del generador de materiales."""
import json
from generate_material_resources import ROOT, write, png, texture, ore

BLOCKS = {
    "mercury_silicate": ("Silicatos de Mercurio", "Mercurian Silicates", (119, 117, 108), None),
    "dense_iron_ore": ("Mena de hierro denso", "Dense Iron Ore", (103, 102, 97), (190, 175, 159)),
    "crater_ice": ("Hielo eterno de cráter", "Eternal Crater Ice", (161, 206, 216), None),
    "vulcanized_basalt": ("Basalto vulcanizado", "Vulcanized Basalt", (51, 47, 47), (217, 88, 31)),
    "venusian_olivine_ore": ("Mena de olivino venusiano", "Venusian Olivine Ore", (51, 47, 47), (137, 167, 57)),
    "pyroxene_ore": ("Mena de piroxeno", "Pyroxene Ore", (51, 47, 47), (64, 105, 70)),
    "sulfur_deposit": ("Depósito de azufre", "Sulfur Deposit", (188, 154, 36), (245, 215, 61)),
    "condensed_sulfuric_acid": ("Ácido sulfúrico condensado", "Condensed Sulfuric Acid", (133, 151, 57), (209, 219, 132)),
    "iron_oxide_regolith": ("Regolito de óxido de hierro", "Iron Oxide Regolith", (169, 66, 36), None),
    "martian_olivine_stone": ("Piedra con olivino marciano", "Martian Olivine Stone", (157, 66, 35), (145, 161, 54)),
    "martian_sulfate_deposit": ("Depósito de sulfatos marcianos", "Martian Sulfate Deposit", (157, 66, 35), (214, 210, 171)),
}
ITEMS = {
    "dense_iron_chunk": ("Trozo de hierro denso", "Dense Iron Chunk", (161, 155, 149)),
    "crater_ice_shard": ("Fragmento de hielo de cráter", "Crater Ice Shard", (168, 218, 238)),
    "venusian_olivine": ("Olivino venusiano", "Venusian Olivine", (148, 173, 52)),
    "pyroxene": ("Piroxeno", "Pyroxene", (61, 108, 69)),
    "sulfur_crystal": ("Cristal de azufre", "Sulfur Crystal", (241, 212, 44)),
    "acid_condensate": ("Condensado de ácido sulfúrico", "Sulfuric Acid Condensate", (191, 216, 113)),
    "iron_oxide_dust": ("Polvo de óxido de hierro", "Iron Oxide Dust", (190, 64, 29)),
    "martian_olivine": ("Olivino marciano", "Martian Olivine", (158, 165, 64)),
    "sulfate_dust": ("Polvo de sulfato", "Sulfate Dust", (224, 215, 176)),
}
DROPS = {
    "dense_iron_ore": ("dense_iron_chunk", 1, 3, True),
    "crater_ice": ("crater_ice_shard", 2, 4, False),
    "venusian_olivine_ore": ("venusian_olivine", 1, 1, True),
    "pyroxene_ore": ("pyroxene", 1, 1, True),
    "sulfur_deposit": ("sulfur_crystal", 1, 3, True),
    "condensed_sulfuric_acid": ("acid_condensate", 1, 1, False),
    "iron_oxide_regolith": ("iron_oxide_dust", 1, 1, False),
    "martian_olivine_stone": ("martian_olivine", 1, 1, True),
    "martian_sulfate_deposit": ("sulfate_dust", 1, 3, True),
}


def merge_tag(path, values):
    file = ROOT / path
    data = json.loads(file.read_text()) if file.exists() else {"replace": False, "values": []}
    for name in values:
        if name not in data["values"]: data["values"].append(name)
    write(path, data)


def main():
    for name, (_, _, color, crystals) in BLOCKS.items():
        write(f"assets/space_fabric/blockstates/{name}.json", {"variants": {"": {"model": f"space_fabric:block/{name}"}}})
        write(f"assets/space_fabric/models/block/{name}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"space_fabric:block/{name}"}})
        write(f"assets/space_fabric/items/{name}.json", {"model": {"type": "minecraft:model", "model": f"space_fabric:block/{name}"}})
        pixels = texture("geology_" + name.replace("_ore", ""), color)
        if crystals:
            for y in range(16):
                for x in range(16):
                    if (3 * x + 5 * y) % 19 < 3:
                        pixels[y][x] = crystals + (255,)
        png(f"assets/space_fabric/textures/block/{name}.png", pixels)
        write(f"data/space_fabric/loot_table/blocks/{name}.json", {"type": "minecraft:block", "pools": [{"rolls": 1.0,
            "conditions": [{"condition": "minecraft:survives_explosion"}], "entries": [{"type": "minecraft:item", "name": "space_fabric:" + name}]}]})
    for name, (_, _, color) in ITEMS.items():
        png(f"assets/space_fabric/textures/item/{name}.png", texture("geology_" + name, color, icon=True))
        write(f"assets/space_fabric/models/item/{name}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"space_fabric:item/{name}"}})
        write(f"assets/space_fabric/items/{name}.json", {"model": {"type": "minecraft:model", "model": f"space_fabric:item/{name}"}})
    for locale in ["es_es", "en_us"]:
        text = json.loads((ROOT / f"assets/space_fabric/lang/{locale}.json").read_text())
        for name, entry in BLOCKS.items(): text["block.space_fabric." + name] = entry[0 if locale == "es_es" else 1]
        for name, entry in ITEMS.items(): text["item.space_fabric." + name] = entry[0 if locale == "es_es" else 1]
        write(f"assets/space_fabric/lang/{locale}.json", text)
    for block, (item, low, high, fortune) in DROPS.items():
        functions = []
        if low != 1 or high != 1:
            functions.append({"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": low, "max": high}})
        if fortune:
            functions.append({"function": "minecraft:apply_bonus", "enchantment": "minecraft:fortune", "formula": "minecraft:ore_drops"})
        functions.append({"function": "minecraft:explosion_decay"})
        write(f"data/space_fabric/loot_table/blocks/{block}.json", {"type": "minecraft:block", "pools": [{"rolls": 1.0,
            "entries": [{"type": "minecraft:alternatives", "children": [
                {"type": "minecraft:item", "name": "space_fabric:" + block, "conditions": [{"condition": "minecraft:match_tool", "predicate": {"predicates": {
                    "minecraft:enchantments": [{"enchantments": "minecraft:silk_touch", "levels": {"min": 1}}]}}}]},
                {"type": "minecraft:item", "name": "space_fabric:" + item, "functions": functions}]}]}],
            "random_sequence": "space_fabric:blocks/" + block})

    pickaxe = ["space_fabric:" + name for name in BLOCKS if name not in {"iron_oxide_regolith", "crater_ice"}]
    merge_tag("data/minecraft/tags/block/mineable/pickaxe.json", pickaxe + ["space_fabric:crater_ice"])
    merge_tag("data/minecraft/tags/block/mineable/shovel.json", ["space_fabric:iron_oxide_regolith"])
    merge_tag("data/minecraft/tags/block/needs_stone_tool.json", pickaxe)
    merge_tag("data/minecraft/tags/block/needs_diamond_tool.json", ["space_fabric:dense_iron_ore"])
    # La netherita usa su propia etiqueta de herramientas incorrectas, distinta de la de diamante.
    merge_tag("data/minecraft/tags/block/incorrect_for_diamond_tool.json", ["space_fabric:dense_iron_ore"])
    for planet, values in {
        "mercury": ["space_fabric:mercury_silicate", "minecraft:smooth_basalt"],
        "venus": ["space_fabric:vulcanized_basalt", "space_fabric:venus_hot_rock", "minecraft:basalt"],
        "mars": ["space_fabric:martian_stone", "space_fabric:iron_oxide_regolith"],
    }.items():
        merge_tag(f"data/space_fabric/tags/block/{planet}_host_rocks.json", values)
    tag_match = lambda planet: {"predicate_type": "minecraft:tag_match", "tag": f"space_fabric:{planet}_host_rocks"}
    for name, planet, block, size, count, low, high in [
        ("dense_iron", "mercury", "dense_iron_ore", 4, 3, -56, -16),
        ("venusian_olivine", "venus", "venusian_olivine_ore", 7, 6, -32, 100),
        ("pyroxene", "venus", "pyroxene_ore", 9, 8, -32, 128),
        ("sulfur", "venus", "sulfur_deposit", 8, 6, 16, 120),
        ("condensed_acid", "venus", "condensed_sulfuric_acid", 3, 2, 24, 120),
        ("iron_oxide", "mars", "iron_oxide_regolith", 24, 10, 0, 112),
        ("martian_olivine", "mars", "martian_olivine_stone", 8, 6, -48, 80),
        ("martian_sulfate", "mars", "martian_sulfate_deposit", 10, 8, -16, 100),
    ]:
        ore(name, tag_match(planet), block, size, count, low, high)
    write("data/space_fabric/worldgen/configured_feature/crater_ice.json", {
        "type": "space_fabric:shadow_crater_ice", "config": {"state": {"Name": "space_fabric:crater_ice"}}})
    write("data/space_fabric/worldgen/placed_feature/crater_ice.json", {"feature": "space_fabric:crater_ice", "placement": [
        {"type": "minecraft:rarity_filter", "chance": 16}, {"type": "minecraft:biome"}]})

    for planet, old, new in [("mercury", "minecraft:smooth_basalt", "space_fabric:mercury_silicate"),
                             ("venus", "minecraft:basalt", "space_fabric:vulcanized_basalt")]:
        path = ROOT / f"data/space_fabric/worldgen/noise_settings/{planet}.json"
        write(f"data/space_fabric/worldgen/noise_settings/{planet}.json", json.loads(path.read_text().replace(old, new)))
    # Conserva los depósitos previos aunque cambie la roca de la corteza.
    for name, planet in [("mercury_regolith", "mercury"), ("venus_hot_rock", "venus"), ("volcanic_magma", "venus")]:
        path = ROOT / f"data/space_fabric/worldgen/configured_feature/{name}.json"
        data = json.loads(path.read_text())
        for target in data["config"]["targets"]: target["target"] = tag_match(planet)
        write(f"data/space_fabric/worldgen/configured_feature/{name}.json", data)
    path = ROOT / "data/space_fabric/worldgen/noise_settings/mars.json"
    data = json.loads(path.read_text())
    data["surface_rule"]["sequence"] = [data["surface_rule"]["sequence"][0], {
        "type": "minecraft:condition", "if_true": {"type": "minecraft:stone_depth", "add_surface_depth": False,
        "offset": 2, "secondary_depth_range": 0, "surface_type": "floor"},
        "then_run": {"type": "minecraft:block", "result_state": {"Name": "space_fabric:iron_oxide_regolith"}}},
        {"type": "minecraft:block", "result_state": data["default_block"]}]
    write("data/space_fabric/worldgen/noise_settings/mars.json", data)
    merge_tag("data/space_fabric/tags/block/carver_replaceables.json", ["space_fabric:" + name for name in
        ["iron_oxide_regolith", "martian_olivine_stone", "martian_sulfate_deposit"]])

    for source, result, xp, time in [("dense_iron_chunk", "iron_ingot", 0.7, 400), ("iron_oxide_dust", "iron_nugget", 0.1, 200)]:
        for method, divisor in [("smelting", 1), ("blasting", 2)]:
            write(f"data/space_fabric/recipe/{source}_{method}.json", {"type": "minecraft:" + method, "category": "misc",
                "ingredient": "space_fabric:" + source, "result": {"id": "minecraft:" + result},
                "experience": xp, "cookingtime": time // divisor})
    write("data/space_fabric/recipe/iron_oxide_red_dye.json", {"type": "minecraft:crafting_shapeless", "category": "misc",
        "ingredients": ["space_fabric:iron_oxide_dust"], "result": {"id": "minecraft:red_dye", "count": 1}})
    print("Geología: 11 bloques, 9 materiales, 9 placed features y 5 recetas.")


if __name__ == "__main__":
    main()
