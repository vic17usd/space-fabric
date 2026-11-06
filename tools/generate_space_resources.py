#!/usr/bin/env python3
"""Assets originales, traducciones y recetas del transporte espacial; ejecución determinista."""
import json
from generate_material_resources import ROOT, write, png, texture


def main():
    blocks = {
        "fuel_refinery": ("Refinería de combustible", "Fuel Refinery", (76, 100, 112)),
        "landing_pad": ("Plataforma de aterrizaje", "Landing Pad", (42, 61, 73)),
    }
    items = {
        "rocket": ("Cohete de exploración", "Exploration Rocket", (209, 221, 225)),
        "empty_fuel_cell": ("Celda de combustible vacía", "Empty Fuel Cell", (106, 137, 151)),
        "rocket_fuel_cell": ("Celda de combustible para cohetes", "Rocket Fuel Cell", (61, 216, 170)),
    }
    for name, (_, _, color) in blocks.items():
        write(f"assets/space_fabric/blockstates/{name}.json", {"variants": {"": {"model": f"space_fabric:block/{name}"}}})
        write(f"assets/space_fabric/models/block/{name}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"space_fabric:block/{name}"}})
        write(f"assets/space_fabric/items/{name}.json", {"model": {"type": "minecraft:model", "model": f"space_fabric:block/{name}"}})
        pixels = texture(name, color)
        for y in range(16):
            for x in range(16):
                if name == "landing_pad" and (x in (1, 14) or y in (1, 14)):
                    pixels[y][x] = ((233, 183, 62, 255) if (x + y) % 6 < 3 else (23, 29, 35, 255))
                elif name == "fuel_refinery" and 4 <= x <= 11 and 4 <= y <= 10:
                    pixels[y][x] = (30, 196, 153, 255) if y in (6, 8) else (17, 40, 53, 255)
        png(f"assets/space_fabric/textures/block/{name}.png", pixels)
        write(f"data/space_fabric/loot_table/blocks/{name}.json", {"type": "minecraft:block", "pools": [{
            "rolls": 1.0, "entries": [{"type": "minecraft:item", "name": "space_fabric:" + name}],
            "conditions": [{"condition": "minecraft:survives_explosion"}]}]})
    for name, (_, _, color) in items.items():
        pixels = texture(name, color, icon=True)
        if name == "rocket":
            pixels = [[(0, 0, 0, 0) for x in range(16)] for y in range(16)]
            for y in range(1, 15):
                for x in range(16):
                    if (5 <= x <= 10 and 4 <= y <= 12) or (7 <= x <= 8 and y <= 4) or (y >= 10 and 3 <= x <= 12):
                        pixels[y][x] = (58, 159, 189, 255) if 6 <= x <= 9 and 5 <= y <= 7 else (215, 225, 231, 255)
        png(f"assets/space_fabric/textures/item/{name}.png", pixels)
        write(f"assets/space_fabric/models/item/{name}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"space_fabric:item/{name}"}})
        write(f"assets/space_fabric/items/{name}.json", {"model": {"type": "minecraft:model", "model": f"space_fabric:item/{name}"}})
    pixels = []
    for y in range(128):
        row = []
        for x in range(128):
            color = (207 + (x + y) % 15, 219 + (x + y) % 15, 224 + (x + y) % 15, 255)
            if 18 <= y < 22: color = (202, 72, 51, 255)
            if y >= 48: color = (48, 70, 86, 255)
            if 80 <= x <= 103 and y < 20: color = (28, 148 + (x % 8) * 6, 190, 255)
            row.append(color)
        pixels.append(row)
    png("assets/space_fabric/textures/entity/rocket.png", pixels)
    extra = {
        "entity.space_fabric.rocket": ("Cohete de exploración", "Exploration Rocket"),
        "gui.space_fabric.board": ("Pilotar", "Board"),
        "gui.space_fabric.launch": ("Despegar", "Launch"),
        "gui.space_fabric.fuel": ("Comb.: %s/%s", "Fuel: %s/%s"),
        "gui.space_fabric.cargo": ("Carga", "Cargo"),
        "gui.space_fabric.cells": ("Celda → vacía", "Cell → empty"),
        "gui.space_fabric.refinery_slot_0": ("Base", "Base"),
        "gui.space_fabric.refinery_slot_1": ("React.", "React."),
        "gui.space_fabric.refinery_slot_2": ("Celda", "Cell"),
        "gui.space_fabric.refinery_slot_3": ("Fuel", "Fuel"),
        "message.space_fabric.launch_requirements": ("Debes pilotar, elegir otra dimensión y tener suficiente combustible.", "Board, select another dimension and load sufficient fuel."),
        "message.space_fabric.launch_blocked": ("Despegue bloqueado: comprueba el techo y el destino.", "Launch blocked: check overhead clearance and destination."),
        "message.space_fabric.no_landing": ("Vuelo detenido: no se encontró una llegada segura.", "Flight stopped: no safe arrival was found."),
    }
    names = [("overworld", "Overworld", "Overworld"), ("mercury", "Mercurio", "Mercury"), ("venus", "Venus", "Venus"),
             ("earth", "Tierra", "Earth"), ("moon", "Luna", "Moon"), ("mars", "Marte", "Mars"), ("jupiter", "Júpiter", "Jupiter"),
             ("saturn", "Saturno", "Saturn"), ("uranus", "Urano", "Uranus"), ("neptune", "Neptuno", "Neptune")]
    for name, es, en in names: extra["destination.space_fabric." + name] = (es, en)
    for locale, index in [("es_es", 0), ("en_us", 1)]:
        path = ROOT / f"assets/space_fabric/lang/{locale}.json"
        translations = json.loads(path.read_text())
        for name, values in blocks.items(): translations["block.space_fabric." + name] = values[index]
        for name, values in items.items(): translations["item.space_fabric." + name] = values[index]
        translations.update({key: values[index] for key, values in extra.items()})
        write(f"assets/space_fabric/lang/{locale}.json", translations)
    recipes = {
        "rocket": ([" I ", "ICI", "RFR"], {"I": "minecraft:iron_block", "C": "minecraft:copper_block", "R": "minecraft:redstone_block", "F": "minecraft:furnace"}, 1),
        "fuel_refinery": (["III", "IFI", "IRI"], {"I": "minecraft:iron_ingot", "F": "minecraft:furnace", "R": "minecraft:redstone"}, 1),
        "empty_fuel_cell": ([" I ", "IGI", " I "], {"I": "minecraft:iron_ingot", "G": "minecraft:glass"}, 4),
        "landing_pad": (["III", "SSS"], {"I": "minecraft:iron_ingot", "S": "minecraft:stone"}, 6),
    }
    for name, (pattern, key, count) in recipes.items():
        write(f"data/space_fabric/recipe/{name}.json", {"type": "minecraft:crafting_shaped", "category": "misc", "pattern": pattern, "key": key,
              "result": {"id": "space_fabric:" + name, "count": count}})
    for tag in ["mineable/pickaxe", "needs_stone_tool"]:
        path = ROOT / f"data/minecraft/tags/block/{tag}.json"
        data = json.loads(path.read_text())
        for name in blocks:
            if "space_fabric:" + name not in data["values"]: data["values"].append("space_fabric:" + name)
        write(f"data/minecraft/tags/block/{tag}.json", data)


if __name__ == "__main__":
    main()
    from import_rocket_model import generate
    generate()
