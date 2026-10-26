#!/usr/bin/env python3
"""Modelo de huevo, icono original y nombres del asentamiento y sus comerciantes."""
import json
from generate_material_resources import ROOT, write, png


def main():
    pixels = []
    for y in range(16):
        row = []
        for x in range(16):
            inside = ((x - 7.5) / (4 if y < 5 else 5.5)) ** 2 + ((y - 8) / 7) ** 2 <= 1
            color = (0, 0, 0, 0)
            if inside:
                color = (160, 91, 60, 255)
                if (x * 3 + y * 5) % 11 < 3: color = (79, 113, 109, 255)
                if 5 <= x <= 10 and y == 7: color = (159, 233, 235, 255)
                if x < 5: color = (106, 61, 43, 255)
            row.append(color)
        pixels.append(row)
    name = "martian_copper_trader_spawn_egg"
    png(f"assets/space_fabric/textures/item/{name}.png", pixels)
    write(f"assets/space_fabric/models/item/{name}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": "space_fabric:item/" + name}})
    write(f"assets/space_fabric/items/{name}.json", {"model": {"type": "minecraft:model", "model": "space_fabric:item/" + name}})
    for locale, es in [("es_es", True), ("en_us", False)]:
        path = ROOT / f"assets/space_fabric/lang/{locale}.json"
        values = json.loads(path.read_text())
        values["entity.space_fabric.martian_copper_trader"] = "Comerciante marciano de cobre" if es else "Martian Copper Trader"
        values["item.space_fabric." + name] = "Generador de comerciante marciano de cobre" if es else "Martian Copper Trader Spawn Egg"
        write(f"assets/space_fabric/lang/{locale}.json", values)


if __name__ == "__main__":
    main()
