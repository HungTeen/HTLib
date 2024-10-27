package hungteen.htlib.common.event;

import hungteen.htlib.common.world.entity.DummyEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-02-11 23:16
 **/
public abstract class DummyEntityEvent extends Event {

    private final Level level;
    private final DummyEntity dummyEntity;

    public DummyEntityEvent(Level level, DummyEntity dummyEntity) {
        this.level = level;
        this.dummyEntity = dummyEntity;
    }

    public Level getLevel() {
        return level;
    }

    public DummyEntity getDummyEntity() {
        return dummyEntity;
    }

    public static class DummyEntitySpawnEvent extends DummyEntityEvent implements ICancellableEvent {
        public DummyEntitySpawnEvent(Level level, DummyEntity dummyEntity) {
            super(level, dummyEntity);
        }
    }
}
