package hungteen.htlib.common.event.events;

import hungteen.htlib.common.world.raid.AbstractRaid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-02-11 22:39
 **/
public abstract class RaidEvent extends Event {

    private final AbstractRaid raid;
    private final Level level;

    public RaidEvent(Level level, AbstractRaid raid) {
        this.level = level;
        this.raid = raid;
    }

    public AbstractRaid getRaid() {
        return raid;
    }

    public Level getLevel() {
        return level;
    }

    public static class RaidDefeatedEvent extends RaidEvent {

        public RaidDefeatedEvent(Level level, AbstractRaid raid) {
            super(level, raid);
        }
    }

    public static class RaidLostEvent extends RaidEvent {

        public RaidLostEvent(Level level, AbstractRaid raid) {
            super(level, raid);
        }
    }

    public static class RaidWaveStartEvent extends RaidEvent {

        private final int round;

        public RaidWaveStartEvent(Level level, AbstractRaid raid, int round) {
            super(level, raid);
            this.round = round;
        }

        public int getRound() {
            return round;
        }
    }

    public static class RaidWaveFinishEvent extends RaidEvent {

        private final int round;

        public RaidWaveFinishEvent(Level level, AbstractRaid raid, int round) {
            super(level, raid);
            this.round = round;
        }

        public int getRound() {
            return round;
        }
    }

    public static class RaidResultLevelEvent extends RaidEvent {

        private final ResourceLocation id;
        private final int tick;
        public RaidResultLevelEvent(Level level, AbstractRaid raid, ResourceLocation id, int tick) {
            super(level, raid);
            this.id = id;
            this.tick = tick;
        }

        public int getTick() {
            return tick;
        }

        public ResourceLocation getId() {
            return id;
        }
    }

    public static class RaidResultDefenderEvent extends RaidEvent {

        private final ResourceLocation id;
        private final Entity entity;
        private final int tick;

        public RaidResultDefenderEvent(Level level, AbstractRaid raid, ResourceLocation id, Entity entity, int tick) {
            super(level, raid);
            this.id = id;
            this.entity = entity;
            this.tick = tick;
        }

        public Entity getEntity() {
            return entity;
        }

        public int getTick() {
            return tick;
        }

        public ResourceLocation getId() {
            return id;
        }
    }

    public static class RaidResultRaiderEvent extends RaidEvent {

        private final ResourceLocation id;
        private final Entity entity;
        private final int tick;

        public RaidResultRaiderEvent(Level level, AbstractRaid raid, ResourceLocation id, Entity entity, int tick) {
            super(level, raid);
            this.id = id;
            this.entity = entity;
            this.tick = tick;
        }

        public Entity getEntity() {
            return entity;
        }

        public int getTick() {
            return tick;
        }

        public ResourceLocation getId() {
            return id;
        }
    }

}
