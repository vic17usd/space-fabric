#!/usr/bin/env python3
"""Elimina los pedestales inferiores y apoya los pisos de las plantillas sobre suelo marciano implícito."""
import copy
import gzip
import json
from pathlib import Path
from convert_creativemode_structure import convert

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "tools/structure_sources"
TERRAIN = {"minecraft:red_sandstone", "minecraft:granite", "minecraft:terracotta", "minecraft:smooth_sandstone"}
SPECS = {"martian_breathforge": 1, "martian_haven_settlement": 3}


def prepare(document, cut):
    result = copy.deepcopy(document)
    result["blocks"] = [[x, y - cut, z, state] for x, y, z, state in document["blocks"]
                        if y >= cut and not (y == cut and document["palette"][state] in TERRAIN)]
    result["block_count"] = len(result["blocks"])
    result["bounds"]["max"][1] -= cut
    result["bounds"]["dimensions"][1] -= cut
    result["anchor"][1] = 0
    return result


def main():
    manifest = {}
    for name, cut in SPECS.items():
        original = json.loads((SOURCE / (name + ".json")).read_text())
        prepared = prepare(original, cut)
        entities = json.loads((SOURCE / "martian_haven_residents.json").read_text()) if name == "martian_haven_settlement" else []
        for entity in entities: entity["position"][1] -= cut
        path = ROOT / f"src/main/resources/data/space_fabric/structure/{name}_surface.nbt"
        path.write_bytes(gzip.compress(convert(prepared, entities, implicit_ground=0), mtime=0))
        manifest[name] = {"trimmed_layers": cut, "original_blocks": original["block_count"], "retained_blocks": prepared["block_count"],
                          "dimensions": prepared["bounds"]["dimensions"], "ground_level": 0, "terrain_palette_removed_at_ground": sorted(TERRAIN)}
        print(name, original["block_count"], "→", prepared["block_count"], prepared["bounds"]["dimensions"])
    (SOURCE / "surface_preparation.json").write_text(json.dumps(manifest, indent=2) + "\n")


if __name__ == "__main__":
    main()
