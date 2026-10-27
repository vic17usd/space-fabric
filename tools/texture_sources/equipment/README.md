# Equipment artwork — 0.20.0

`inventory_atlas.png` is original image-generated pixel art. The five-by-five
layout contains:

1. Vacuum helmet, chestplate, leggings, boots; Mercury upgrade.
2. Venus helmet, chestplate, leggings, boots; Venus upgrade.
3. Cryogenic helmet, chestplate, leggings, boots; Earth upgrade.
4. Abyssal helmet, chestplate, leggings, boots; Moon upgrade.
5. Mars, Jupiter, Saturn, Uranus and Neptune upgrades.

`armor_panels.png` was generated using vanilla humanoid/leggings UV templates as
layout references. The generated sheet is an art source, not a runtime UV map.
Rows contain vacuum, Venus, cryogenic and abyssal panels. The left side supplies
helmets/chest/arms/boots, and the right side supplies legs and waist bands.

`tools/import_equipment_textures.py` extracts source art into 25 inventory sprites
at 128x128, then maps individual panel regions onto native Minecraft cube UV faces
for eight 256x128 equipment textures. Technical face packing places the visor on
the front of the helmet and the console on the front of the chest. It also keeps
unused UV regions transparent and all worn surfaces opaque. Source sheets stay
unchanged; this script extracts/resizes/maps their regions rather than painting
new artistic details. Flat background colors fill transparent pixels inside a
mapped material patch so a sealed helmet cannot develop holes.

The importer writes the four `equipment/*.json` assets and all 25 inventory model
references. Both existing suit and rocket resource generators invoke it, retaining
this artwork when assets are regenerated. Building the checked-in project does
not require ImageMagick; reimporting artwork does. The optional preview script
requires Pillow and creates a front UV projection, not an in-game screenshot.
