#!/usr/bin/env python3
"""Genera timelines de 26.1 a partir de la plantilla vanilla instalada por Gradle."""
import copy
import json
import os
from pathlib import Path
import zipfile

ROOT = Path(__file__).resolve().parents[1] / "src/main/resources"
PROFILES = {
    "mercury": ("#000000", "#000000", 1024, 4096, 1.0, 0.8, False),
    "venus": ("#cf8b40", "#b48236", 0, 28, 0.45, 0.0, True),
    "earth": ("#78a7ff", "#c0d8ff", 0, 256, 1.0, 0.5, False),
    "moon": ("#000000", "#000000", 1024, 4096, 1.0, 0.8, False),
    "mars": ("#b96540", "#a35d40", 0, 112, 0.8, 0.08, False),
    "jupiter": ("#cba27a", "#9e7758", 0, 32, 0.35, 0.0, False),
    "saturn": ("#dfc38d", "#bca776", 0, 40, 0.25, 0.0, False),
    "uranus": ("#93dfdf", "#7ac2c2", 0, 36, 0.16, 0.0, True),
    "neptune": ("#355cbb", "#2c4596", 0, 28, 0.12, 0.0, False),
}


def write(path, value):
    path = ROOT / path
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, indent=2, ensure_ascii=False) + "\n")


def main():
    caches = [Path(os.environ.get("GRADLE_USER_HOME", Path.home() / ".gradle")),
              Path("/workspace/.space-fabric-tools/gradle-cache")]
    jars = [path for cache in caches for path in cache.glob("caches/fabric-loom/26.1/minecraft-merged.jar")]
    if not jars:
        raise SystemExit("Ejecuta primero Gradle con Minecraft 26.1 para obtener la plantilla vanilla.")
    with zipfile.ZipFile(jars[0]) as jar:
        if json.loads(jar.read("version.json"))["id"] != "26.1":
            raise SystemExit("La plantilla debe corresponder a Minecraft 26.1 exacta")
        day = json.loads(jar.read("data/minecraft/timeline/day.json"))
        moon = json.loads(jar.read("data/minecraft/timeline/moon.json"))
        villagers = json.loads(jar.read("data/minecraft/timeline/villager_schedule.json"))
    for planet, (sky, fog, start, end, light, stars, retrograde) in PROFILES.items():
        write(f"data/space_fabric/world_clock/{planet}.json", {})
        timeline = copy.deepcopy(day)
        timeline["clock"] = "space_fabric:" + planet
        # No registra los marcadores vanilla repetidamente para nueve clocks diferentes.
        timeline.pop("time_markers", None)
        timeline["time_markers"] = {f"space_fabric:{planet}_noon": {"ticks": 6000, "show_in_commands": True},
                                     f"space_fabric:{planet}_midnight": {"ticks": 18000, "show_in_commands": True}}
        tracks = timeline["tracks"]
        if retrograde:
            for key in ["minecraft:visual/sun_angle", "minecraft:visual/star_angle", "minecraft:visual/moon_angle"]:
                for frame in tracks[key]["keyframes"]: frame["value"] = -frame["value"]
        brightness = tracks["minecraft:visual/star_brightness"]
        for frame in brightness["keyframes"]:
            frame["value"] = stars if planet in {"moon", "mercury"} else frame["value"] * stars / 0.5
        brightness.pop("modifier", None)
        if planet in {"mercury", "moon"}:
            tracks.pop("minecraft:visual/sky_color", None)
            tracks.pop("minecraft:visual/fog_color", None)
            tracks.pop("minecraft:visual/sunrise_sunset_color", None)
        write(f"data/space_fabric/timeline/{planet}_day.json", timeline)
        path = ROOT / f"data/space_fabric/dimension_type/{planet}.json"
        dimension = json.loads(path.read_text())
        dimension["default_clock"] = "space_fabric:" + planet
        dimension["timelines"] = ["space_fabric:" + planet + "_day"]
        attributes = dimension["attributes"]
        attributes.update({"minecraft:visual/sky_color": sky, "minecraft:visual/fog_color": fog,
            "minecraft:visual/fog_start_distance": start, "minecraft:visual/fog_end_distance": end,
            "minecraft:visual/sky_light_factor": light,
            "minecraft:visual/star_brightness": stars if planet in {"mercury", "moon"} else 0.0})
        if planet in {"mercury", "moon"}: attributes["minecraft:visual/sunrise_sunset_color"] = "#00000000"
        if planet == "earth":
            for suffix, template in [("moon", moon), ("villagers", villagers)]:
                template = copy.deepcopy(template)
                template["clock"] = "space_fabric:earth"
                template.pop("time_markers", None)
                write(f"data/space_fabric/timeline/earth_{suffix}.json", template)
                dimension["timelines"].append("space_fabric:earth_" + suffix)
        write(f"data/space_fabric/dimension_type/{planet}.json", dimension)
        biome_path = ROOT / f"data/space_fabric/worldgen/biome/{planet}.json"
        if biome_path.exists():
            biome = json.loads(biome_path.read_text())
            biome["attributes"].update({"minecraft:visual/sky_color": sky, "minecraft:visual/fog_color": fog,
                "minecraft:visual/fog_start_distance": start, "minecraft:visual/fog_end_distance": end})
            write(f"data/space_fabric/worldgen/biome/{planet}.json", biome)
    write("data/space_fabric/worldgen/placed_feature/mars_crater.json", {"feature": "space_fabric:crater",
        "placement": [{"type": "minecraft:rarity_filter", "chance": 4}, {"type": "minecraft:biome"}]})
    for planet, block in [("uranus", "uranian_methane_ice"), ("neptune", "neptunian_methane_ice")]:
        write(f"data/space_fabric/worldgen/configured_feature/{planet}_frozen_cave.json", {
            "type": "space_fabric:frozen_cave", "config": {"state": {"Name": "space_fabric:" + block}}})
        write(f"data/space_fabric/worldgen/placed_feature/{planet}_frozen_cave.json", {
            "feature": f"space_fabric:{planet}_frozen_cave", "placement": [
                {"type": "minecraft:rarity_filter", "chance": 16}, {"type": "minecraft:biome"}]})
    print("Generados nueve relojes, once timelines y cráteres/cavernas heladas.")


if __name__ == "__main__":
    main()
