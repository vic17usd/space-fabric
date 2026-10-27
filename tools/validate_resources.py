#!/usr/bin/env python3
"""Comprueba referencias internas; no sustituye los codecs de Minecraft."""
import json
import re
import struct
import zlib
from pathlib import Path


def main():
    root = Path(__file__).resolve().parents[1] / "src/main/resources"
    documents = {path: json.loads(path.read_text()) for path in root.rglob("*.json")}
    data = root / "data/space_fabric"
    expected = {"mercury", "venus", "earth", "moon", "mars", "jupiter", "saturn", "uranus", "neptune"}
    for directory in ("dimension", "dimension_type"):
        actual = {path.stem for path in (data / directory).glob("*.json")}
        if actual != expected:
            raise ValueError(f"{directory}: faltan/sobran planetas: {actual ^ expected}")
    for planet in expected:
        dimension = documents[data / "dimension" / f"{planet}.json"]
        if dimension["type"] != f"space_fabric:{planet}":
            raise ValueError(f"Referencia de dimension_type incorrecta: {planet}")
        if dimension["generator"]["type"] != ("space_fabric:orbital" if planet in {"jupiter", "saturn", "uranus", "neptune"} else "minecraft:noise"):
            raise ValueError(f"Generador incorrecto: {planet}")
        dimension_type = documents[data / "dimension_type" / f"{planet}.json"]
        if dimension_type["height"] % 16 or dimension_type["min_y"] % 16:
            raise ValueError(f"Alturas no alineadas con secciones: {planet}")
        if dimension_type["has_ender_dragon_fight"]:
            raise ValueError(f"Pelea del dragón inesperada: {planet}")
    damage_ids = {f"space_fabric:{path.stem}" for path in (data / "damage_type").glob("*.json")}
    if damage_ids != {"space_fabric:decompression", "space_fabric:crushing_pressure", "space_fabric:hypoxia", "space_fabric:thermal_exposure"}:
        raise ValueError("Tipos de daño incorrectos")
    for tag in ("bypasses_armor", "bypasses_shield"):
        path = root / "data/minecraft/tags/damage_type" / f"{tag}.json"
        if set(documents[path]["values"]) != damage_ids:
            raise ValueError(f"Etiqueta de daño incompleta: {tag}")
    for locale in ("es_es", "en_us"):
        translations = documents[root / "assets/space_fabric/lang" / f"{locale}.json"]
        for identifier in damage_ids:
            key = "death.attack." + identifier.replace(":", ".")
            if key not in translations:
                raise ValueError(f"Traducción ausente: {locale}: {key}")
    for part in ("helmets", "chestplates", "leggings", "boots"):
        if not documents[data / "tags/item/space_suit" / f"{part}.json"]["values"]:
            raise ValueError(f"Etiqueta del traje vacía: {part}")
    assets = root / "assets/space_fabric"
    for state in (assets / "blockstates").glob("*.json"):
        name = state.stem
        for path in (assets / "models/block" / f"{name}.json", assets / "items" / f"{name}.json",
                     data / "loot_table/blocks" / f"{name}.json"):
            if path not in documents:
                raise ValueError(f"Asset o loot ausente para {name}: {path}")
        for locale in ("es_es", "en_us"):
            if "block.space_fabric." + name not in documents[assets / "lang" / f"{locale}.json"]:
                raise ValueError(f"Falta nombre de bloque: {name}, {locale}")

    def references(value):
        if isinstance(value, dict):
            for key, child in value.items():
                if key in ("model", "parent") and isinstance(child, str) and child.startswith("space_fabric:"):
                    path = assets / "models" / (child.split(":", 1)[1] + ".json")
                    if not path.is_file(): raise ValueError(f"Modelo ausente: {child}")
                if key == "textures" and isinstance(child, dict):
                    for texture in child.values():
                        if isinstance(texture, dict): texture = texture.get("sprite", "")
                        if texture.startswith("space_fabric:"):
                            path = assets / "textures" / (texture.split(":", 1)[1] + ".png")
                            if not path.is_file(): raise ValueError(f"Textura ausente: {texture}")
                references(child)
        elif isinstance(value, list):
            for child in value: references(child)
    for path, value in documents.items():
        if assets in path.parents: references(value)
    # El atlas vanilla de objetos descubre item/ y block/, no textures/entity/.
    for model in (assets / "models/item").glob("*.json"):
        for texture in documents[model].get("textures", {}).values():
            if texture.startswith("space_fabric:") and not texture.split(":", 1)[1].startswith(("item/", "block/")):
                raise ValueError(f"Textura fuera del atlas vanilla de ítems: {model.name}: {texture}")
    # Ambos usos del modelo importado deben mantener el PNG original de Blockbench.
    import base64
    source = root.parents[2] / "tools/import_sources/space-rocket.bbmodel"
    rocket = json.loads(source.read_text())
    expected_texture = base64.b64decode(rocket["textures"][0]["source"].split(",", 1)[1], validate=True)
    for folder in ("entity", "item"):
        if (assets / "textures" / folder / "rocket.png").read_bytes() != expected_texture:
            raise ValueError(f"Atlas del cohete distinto al original: {folder}")
    # Every new fleet hull retains the exact texture embedded in its supplied .bbmodel.
    from import_rocket_fleet import SOURCES
    for name, filename in SOURCES.items():
        source = root.parents[2] / f"tools/import_sources/{filename}.bbmodel"
        hull = json.loads(source.read_text())
        expected = base64.b64decode(hull["textures"][0]["source"].split(",", 1)[1], validate=True)
        for folder in ("entity", "item"):
            if (assets / f"textures/{folder}/rocket_fleet/{name}.png").read_bytes() != expected:
                raise ValueError(f"Atlas de flota distinto al original: {name}/{folder}")
    # Los renderers de entidad declaran sus texturas en Java, fuera de los modelos JSON.
    client = root.parent / "java/dev/vic17usd/spacefabric/client"
    for source in client.rglob("*.java"):
        for namespace, texture in re.findall(
                r'Identifier\.fromNamespaceAndPath\(\s*"([^"]+)"\s*,\s*"(textures/[^"]+)"\s*\)',
                source.read_text()):
            path = root / "assets" / namespace / texture
            if not path.is_file():
                raise ValueError(f"Textura de entidad ausente en {source.name}: {namespace}:{texture}")
    for path in (assets / "textures").rglob("*.png"):
        content = path.read_bytes()
        if content[:8] != b"\x89PNG\r\n\x1a\n": raise ValueError(f"PNG inválido: {path}")
        offset, compressed = 8, b""
        width, height = struct.unpack(">II", content[16:24])
        color_type = content[25]
        if content[24] != 8 or color_type not in (2, 6) or content[26:29] != bytes([0, 0, 0]) or not (1 <= width <= 2048 and 1 <= height <= 2048):
            raise ValueError(f"Formato PNG no admitido: {path}")
        while offset < len(content):
            length = struct.unpack(">I", content[offset:offset + 4])[0]
            kind = content[offset + 4:offset + 8]
            payload = content[offset + 8:offset + 8 + length]
            crc = struct.unpack(">I", content[offset + 8 + length:offset + 12 + length])[0]
            if zlib.crc32(kind + payload) != crc: raise ValueError(f"CRC PNG incorrecto: {path}")
            if kind == b"IDAT": compressed += payload
            offset += length + 12
        channels = 3 if color_type == 2 else 4
        if len(zlib.decompress(compressed)) != height * (1 + width * channels):
            raise ValueError(f"Textura RGB/RGBA incompleta: {path}")
    print(f"OK: {len(documents)} JSON; nueve dimensiones y referencias internas coherentes.")
    print("Modelos, texturas PNG, loot y traducciones de materiales: OK.")
    print("Los codecs se prueban con ./gradlew test; la comprobación en el juego es independiente.")


if __name__ == "__main__":
    main()
