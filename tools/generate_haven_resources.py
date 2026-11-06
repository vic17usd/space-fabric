#!/usr/bin/env python3
"""Modelos y nombres del comerciante; conserva los PNG originales importados del JAR."""
import json
from generate_material_resources import ROOT, write


def main():
    name = "martian_copper_trader_spawn_egg"
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
