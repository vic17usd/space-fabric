# Venus ruins attribution

Original designs: **17usdd using [CreativeMode.net](https://creativemode.net)**.
The user supplied these JARs for integration. `manifest.json` records source pages,
authors, archive SHA-256 and conversion statistics. Each source license is retained
in its own directory and included in the distributed Space Fabric JAR.

Adaptations: editor plinths removed, short masonry supports added, heat-safe block
palette, decorative mineral blocks replaced to preserve progression, native tiled
Minecraft structure templates and Venus-only world generation. The damaged foundry
loses sixteen unsupported decorative voxels. No source Java classes, initializers,
network code or telemetry libraries are loaded or distributed.

`submitted_designs.json` audits all fourteen attachments. Four new builds were
selected. The two oversized towers were deferred; the remaining eight designs are
already integrated in previous releases (landscapes, settlements and ground blocks).
Original structure JSONs are retained under `tools/structure_sources/venus/ruins`.
Regenerate with `python3 tools/import_venus_ruins.py --stored-sources`.
