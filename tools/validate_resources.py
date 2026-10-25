#!/usr/bin/env python3
"""Comprueba referencias internas; no sustituye los codecs de Minecraft."""
import json
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
        if dimension["generator"]["type"] != "minecraft:noise":
            raise ValueError(f"Generador incorrecto: {planet}")
        dimension_type = documents[data / "dimension_type" / f"{planet}.json"]
        if dimension_type["height"] % 16 or dimension_type["min_y"] % 16:
            raise ValueError(f"Alturas no alineadas con secciones: {planet}")
        if dimension_type["has_ender_dragon_fight"]:
            raise ValueError(f"Pelea del dragón inesperada: {planet}")
    damage_ids = {f"space_fabric:{path.stem}" for path in (data / "damage_type").glob("*.json")}
    if damage_ids != {"space_fabric:decompression", "space_fabric:crushing_pressure", "space_fabric:hypoxia"}:
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
    print(f"OK: {len(documents)} JSON; nueve dimensiones y referencias internas coherentes.")
    print("Los codecs se prueban con ./gradlew test; la comprobación en el juego es independiente.")


if __name__ == "__main__":
    main()
