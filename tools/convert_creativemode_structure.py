#!/usr/bin/env python3
"""Convierte el JSON adjunto a StructureTemplate NBT de Minecraft 26.1, sin dependencias."""
import argparse
import gzip
import json
from pathlib import Path
import re
import struct

ROOT = Path(__file__).resolve().parents[1]
DATA_VERSION = 4786  # world_version oficial de Minecraft Java 26.1.


def integer(value):
    return struct.pack(">i", value)


def string(value):
    # Los identificadores aceptados son ASCII, también válido en Modified UTF-8 de NBT.
    encoded = value.encode("ascii")
    return struct.pack(">H", len(encoded)) + encoded


def named(kind, name, payload):
    return bytes([kind]) + string(name) + payload


def tag_list(kind, entries):
    return bytes([kind]) + integer(len(entries)) + b"".join(entries)


def int_list(values):
    return tag_list(3, [integer(v) for v in values])


def entity_entry(specification, dimensions, block_map, implicit_ground=None):
    name = specification["id"]
    position = specification["position"]
    if not re.fullmatch(r"[a-z0-9_.-]+:[a-z0-9_./-]+", name) or len(position) != 3:
        raise ValueError("Entidad inválida")
    if any(type(v) is not int or not 0 <= v < dimensions[i] for i, v in enumerate(position)):
        raise ValueError("Posición de entidad fuera de la plantilla")
    x, y, z = position
    if ((x, y - 1, z) not in block_map and y - 1 != implicit_ground) or any((x, y + dy, z) in block_map for dy in (0, 1, 2)):
        raise ValueError("La entidad necesita suelo y tres bloques libres")
    floor = block_map.get((x, y - 1, z), "space_fabric:iron_oxide_regolith")
    if floor in {"minecraft:water", "minecraft:lava", "minecraft:packed_ice", "minecraft:air"}:
        raise ValueError("Suelo de entidad no seguro")
    coordinates = tag_list(6, [struct.pack(">d", v) for v in (x + 0.5, y, z + 0.5)])
    rotation = tag_list(5, [struct.pack(">f", 0), struct.pack(">f", 0)])
    nbt = named(8, "id", string(name)) + named(1, "PersistenceRequired", b"\1")
    nbt += named(9, "Pos", coordinates) + named(9, "Rotation", rotation) + b"\0"
    return named(9, "pos", coordinates) + named(9, "blockPos", int_list(position)) + named(10, "nbt", nbt) + b"\0"


def convert(document, entities=None, implicit_ground=None):
    if document.get("format") != "creativemode.structure" or document.get("version") != 1:
        raise ValueError("Se requiere creativemode.structure versión 1")
    palette = document["palette"]
    blocks = document["blocks"]
    lower = document["bounds"]["min"]
    upper = document["bounds"]["max"]
    dimensions = document["bounds"]["dimensions"]
    for coordinates in [lower, upper, dimensions, document["anchor"]]:
        if len(coordinates) != 3 or any(type(v) is not int for v in coordinates):
            raise ValueError("Coordenadas inválidas")
    if dimensions != [upper[i] - lower[i] + 1 for i in range(3)] or any(v < 1 for v in dimensions):
        raise ValueError("Límites y dimensiones inconsistentes")
    if document["block_count"] != len(blocks):
        raise ValueError("block_count no coincide con el número de bloques")
    if not palette or any(not isinstance(name, str) or not re.fullmatch(r"[a-z0-9_.-]+:[a-z0-9_./-]+", name) for name in palette):
        raise ValueError("Paleta inválida: se admiten identificadores de bloques sin propiedades")
    if len(set(palette)) != len(palette):
        raise ValueError("Paleta con identificadores duplicados")
    seen = set()
    entries = []
    for block in blocks:
        if not isinstance(block, list) or len(block) != 4 or any(type(v) is not int for v in block):
            raise ValueError("Cada bloque debe contener [x, y, z, índice de paleta]")
        x, y, z, state = block
        position = (x, y, z)
        if position in seen or not 0 <= state < len(palette) or any(not lower[i] <= position[i] <= upper[i] for i in range(3)):
            raise ValueError("Bloque duplicado, fuera de límites o con índice de paleta inválido")
        seen.add(position)
        local = [position[i] - lower[i] for i in range(3)]
        entries.append(named(9, "pos", int_list(local)) + named(3, "state", integer(state)) + b"\0")
    body = named(3, "DataVersion", integer(DATA_VERSION))
    body += named(9, "size", int_list(dimensions))
    body += named(9, "palette", tag_list(10, [named(8, "Name", string(name)) + b"\0" for name in palette]))
    body += named(9, "blocks", tag_list(10, entries))
    block_map = {tuple(block[i] - lower[i] for i in range(3)): palette[block[3]] for block in blocks}
    entities = entities or []
    if len({tuple(spec["position"]) for spec in entities}) != len(entities):
        raise ValueError("Entidades con posiciones duplicadas")
    body += named(9, "entities", tag_list(10, [entity_entry(spec, dimensions, block_map, implicit_ground) for spec in entities]))
    # Se preservan exactamente los bloques del archivo; no se inventa aire ni conectores jigsaw.
    # anchor/grid_size pertenecen al editor de origen, no al formato NBT de Minecraft.
    return named(10, "", body + b"\0")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("source", nargs="?", type=Path,
                        default=ROOT / "tools/structure_sources/martian_breathforge.json")
    parser.add_argument("output", nargs="?", type=Path,
                        default=ROOT / "src/main/resources/data/space_fabric/structure/martian_breathforge.nbt")
    parser.add_argument("--entities", type=Path, help="Lista JSON opcional de residentes definidos por el mod")
    args = parser.parse_args()
    document = json.loads(args.source.read_text())
    entities = json.loads(args.entities.read_text()) if args.entities else []
    encoded = gzip.compress(convert(document, entities), mtime=0)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_bytes(encoded)
    print(f"NBT generado: {len(document['blocks'])} bloques, {document['bounds']['dimensions']}, {len(encoded)} bytes")


if __name__ == "__main__":
    main()
