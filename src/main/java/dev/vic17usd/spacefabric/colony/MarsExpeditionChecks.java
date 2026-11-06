package dev.vic17usd.spacefabric.colony;

import dev.vic17usd.spacefabric.maintenance.MachineExposure;
import dev.vic17usd.spacefabric.planet.Planet;
import dev.vic17usd.spacefabric.space.RocketJourney;
import dev.vic17usd.spacefabric.transport.*;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Read-only evidence from the existing seal scanner, controller budgets and stored water. */
public final class MarsExpeditionChecks {
    public static final int OXYGEN = 600;
    public static final int ENERGY = 300 * ColonyBalance.TEMPERATE_POWER_PER_SECOND;
    public static final int WATER = 2 * TransportResource.WATER.quantum;
    private MarsExpeditionChecks() { }

    public static boolean landed(Planet current, Planet destination, Planet origin, int phase, boolean collision) {
        return current == Planet.MARS && destination == Planet.MARS && origin != null && origin != Planet.MARS
                && phase == RocketJourney.DESCENDING && collision;
    }

    public record Observation(boolean installed, boolean sealed, boolean operating, boolean inside,
                              int oxygen, int energy, int water) {
        public static final Observation ABSENT = new Observation(false, false, false, false, 0, 0, 0);
        public int flags() {
            if (!installed) return 0;
            int flags = MarsProgress.REFUGE;
            if (sealed) flags |= MarsProgress.SEALED;
            if (sealed && operating && oxygen >= OXYGEN && energy >= ENERGY) flags |= MarsProgress.SUPPLIES;
            if (water >= WATER) flags |= MarsProgress.WATER;
            return flags;
        }
        public boolean ready() { return inside && flags() == MarsProgress.REQUIREMENTS; }
    }

    public static Observation evaluate(SealScanner.Room room, SealScanner.World world, BlockPos head,
                                       LifeSupport support, boolean operating, int water) {
        boolean sealed = room != null && room.unchanged(world);
        return new Observation(true, sealed, operating && support.pressurized(),
                sealed && room.air().contains(head), support.oxygen(), support.energy(), water);
    }

    /** Slot 2 already accepts water and does not consume it on Mars. Industrial tanks are optional. */
    public static int storedWater(ColonyMachineBlockEntity controller, SealScanner.Room room,
                                  SealScanner.World world, Function<BlockPos, BlockEntity> loadedEntity) {
        var stack = controller.getItem(2);
        int amount = stack.is(ColonyContent.WATER_CANISTER) ? stack.getCount() * TransportResource.WATER.quantum : 0;
        if (room == null || !room.unchanged(world)) return Math.min(amount, WATER);
        for (var pos : room.boundary().keySet()) {
            if (loadedEntity.apply(pos) instanceof TransportTankBlockEntity tank && !tank.isRemoved()
                    && MachineExposure.enclosed(room, pos, world)) {
                amount += tank.buffer().available(TransportResource.WATER);
                if (amount >= WATER) break;
            }
        }
        return Math.min(amount, WATER);
    }
}
