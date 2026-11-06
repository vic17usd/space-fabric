# Earth mineral artwork, 0.18.2

The two PNG sheets were generated with the image editing tool using the three ore
images attached by the user as visual references. They are **adaptations of the
reference style**, not byte-for-byte copies of the attached images.

Only the eight new terrestrial minerals are reskinned, as the user requested.
Vanilla gold, copper and iron are unchanged. The warm golden reference informs
sphalerite, the silver reference informs argentite (and pewter cassiterite), and
the red reference informs bauxite. Other minerals use the same stone, fragmented
mineral and highlight language with distinct palettes.

`reference_ores.png` row order:

1. Bauxite, cassiterite, sphalerite, galena in stone.
2. Argentite, pentlandite, ilmenite, fluorite in stone.
3. Row 1 minerals in deepslate.
4. Row 2 minerals in deepslate.

`reference_items.png` row order:

1. Raw bauxite, cassiterite, sphalerite, galena.
2. Raw argentite, pentlandite, ilmenite, fluorite crystal.
3. Aluminum, tin, zinc, lead ingots.
4. Silver, nickel, titanium ingots; unused last cell.

`layout.json` records normalized extraction bounds measured from the sheets. The
item row bounds account for the source layout rather than cropping a sprite at a
nominal grid line. ImageMagick extracts each region into a 128px game texture,
retaining aspect ratio/alpha for inventory icons. This step packages the artwork;
it does not paint new details or change the source colors.

Regenerate with `python3 tools/import_earth_reference_textures.py` (ImageMagick
required). `generate_earth_resources.py` also invokes this importer so regenerating
the geology no longer overwrites the reference-style artwork with the old 16px
procedural ore and item sprites. The eight storage block textures remain 16px.
