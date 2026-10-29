"""Original, reproducible Venus station interiors, with real two-door vestibules."""


def station(mission):
    if mission not in (1, 2, 3):
        raise ValueError("Unknown Venus expedition")
    cloud = mission == 3
    width = 29 if cloud else 13
    blocks, entities, properties = {}, {}, {}

    def put(x, y, z, name, nbt=None, **state):
        pos = (x, y, z)
        blocks[pos] = name if ":" in name else "minecraft:" + name
        entities.pop(pos, None)
        properties.pop(pos, None)
        if nbt is not None:
            entities[pos] = nbt
        if state:
            properties[pos] = state

    def machine(x, y, z, name, **nbt):
        put(x, y, z, "space_fabric:" + name, {"id": "space_fabric:colony_machine", **nbt})

    hull = "space_fabric:venus_composite_hull"
    for x in range(width):
        for z in range(width):
            put(x, 0, z, hull)
    # Interior 9 x 4 x 9; the pressure scan remains below its 512-cell limit.
    for x in range(1, 12):
        for y in range(1, 7):
            for z in range(1, 12):
                shell = x in (1, 11) or y in (1, 6) or z in (1, 11)
                window = shell and y in (3, 4) and (x in (1, 11) or z == 11)
                put(x, y, z, "space_fabric:pressure_window" if window else hull if shell else "air")
    # Full-height partition. Closed inner door keeps the main room sealed while entering.
    for x in range(2, 11):
        for y in range(2, 6):
            put(x, y, 4, hull)
    for z in (1, 4):
        for y, half in ((2, "lower"), (3, "upper")):
            put(6, y, z, "space_fabric:airlock_door", facing="south", half=half,
                hinge="left", open="false", powered="false")
    for x, z, facing in ((7, 0, "north"), (5, 2, "south"), (5, 3, "north"), (7, 5, "south")):
        put(x, 3, z, "stone_button", face="wall", facing=facing, powered="false")
    put(6, 1, 0, "smooth_quartz_stairs", facing="south", half="bottom", shape="straight", waterlogged="false")
    # Color-coded vestibule and a continuous two-block-high route to every working area.
    accent = {1: "green_concrete", 2: "cyan_concrete", 3: "orange_concrete"}[mission]
    for x in (3, 4, 8, 9):
        put(x, 2, 2, accent)
    for x, z in ((3, 6), (8, 6), (3, 9), (8, 9)):
        put(x, 6, z, "space_fabric:venus_grow_lamp")
    machine(3, 2, 6, "habitat_controller", Respired=4800 if mission == 2 else 0)
    # Heat exchanger touches the controller; no stored coolant or free pressurization.
    machine(2, 2, 6, "venus_heat_exchanger", Maintenance={"damage": 5000})
    put(3, 2, 10, "crafting_table")
    put(4, 2, 10, "smooth_quartz_slab", type="top", waterlogged="false")
    put(2, 2, 9, "chest", {"id": "minecraft:chest", "LootTable":
        "space_fabric:chests/venus_" + {1: "laboratory", 2: "refuge", 3: "observatory"}[mission]},
        facing="east", type="single", waterlogged="false")

    if mission == 1:
        machine(7, 2, 8, "venus_expedition_console", NaturalMission=1)
        machine(8, 2, 8, "venus_greenhouse", Maintenance={"damage": 10000})
        machine(9, 2, 10, "sample_analyzer", Maintenance={"damage": 5000})
        # Enclosed specimen shelves and a work bench; the crops are machine-produced items.
        for x, color in ((7, "lime"), (8, "yellow"), (9, "orange")):
            put(x, 3, 10, color + "_stained_glass")
        put(9, 2, 6, "smooth_quartz_slab", type="top", waterlogged="false")
    elif mission == 2:
        machine(7, 2, 8, "venus_expedition_console", NaturalMission=2)
        machine(8, 2, 8, "venus_life_reclaimer", Maintenance={"damage": 10000})
        for z in (7, 9):
            # Bunks made from pads, not explosive vanilla beds in a hostile dimension.
            put(9, 2, z, "white_wool")
            put(10, 2, z, "cyan_wool")
            put(9, 4, z, "smooth_quartz_slab", type="bottom", waterlogged="false")
            put(10, 4, z, "smooth_quartz_slab", type="bottom", waterlogged="false")
        put(2, 2, 7, "space_fabric:gas_tank", {"id": "space_fabric:transport_tank"})
        put(2, 2, 8, "space_fabric:liquid_tank", {"id": "space_fabric:transport_tank"})
    else:
        machine(14, 1, 5, "venus_expedition_console", NaturalMission=3)
        machine(15, 1, 5, "venus_atmospheric_collector", Maintenance={"damage": 10000})
        machine(13, 1, 18, "venus_service_dock")
        machine(8, 2, 8, "sample_analyzer", Maintenance={"damage": 5000})
        for x in (8, 9, 10):
            put(x, 2, 10, "smooth_quartz_slab", type="top", waterlogged="false")
        # Markings remain flush with the deck: no obstacles in the aerostat's landing volume.
        for x in range(13, 24):
            for z in range(13, 24):
                if x in (13, 23) or z in (13, 23) or x == 18:
                    put(x, 0, z, "orange_concrete")
        for x in (2, 10):
            for z in (2, 10):
                for y in range(7, 9):
                    put(x, y, z, "iron_block")
        for x in range(1, 13):
            for z in range(1, 13):
                for y in range(9, 12):
                    put(x, y, z, hull)
    return blocks, entities, properties
