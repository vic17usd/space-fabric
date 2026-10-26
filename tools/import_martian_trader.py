#!/usr/bin/env python3
"""Importa únicamente los recursos del aldeano desde el JAR aportado, sin ejecutar sus clases."""
import argparse
import hashlib
import json
from pathlib import Path
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[1]
PREFIX = "assets/martian-copper-trader-envsqju5/"
FILES = {
    PREFIX + "textures/entity/martian_copper_trader_envsqju5.png": "src/main/resources/assets/space_fabric/textures/entity/martian_copper_trader.png",
    PREFIX + "textures/item/martian_copper_trader_envsqju5_spawn_egg.png": "src/main/resources/assets/space_fabric/textures/item/martian_copper_trader_spawn_egg.png",
    PREFIX + "sounds/alien_greeting.ogg": "src/main/resources/assets/space_fabric/sounds/alien_greeting.ogg",
    "LICENSE_martian_copper_trader_envsqju5": "third_party/martian_copper_trader/LICENSE.txt",
}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("jar", type=Path)
    args = parser.parse_args()
    manifest = {"source": "Aldeano Marciano.jar", "sha256": hashlib.sha256(args.jar.read_bytes()).hexdigest(), "files": {}}
    with ZipFile(args.jar) as jar:
        for source, destination in FILES.items():
            data = jar.read(source)
            path = ROOT / destination
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_bytes(data)
            manifest["files"][destination] = {"source": source, "sha256": hashlib.sha256(data).hexdigest()}
    path = ROOT / "third_party/martian_copper_trader/manifest.json"
    path.write_text(json.dumps(manifest, indent=2) + "\n")
    print("Importados textura, icono, sonido y licencia originales.")


if __name__ == "__main__":
    main()
