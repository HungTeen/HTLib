package hungteen.htlib.common.event.events;

import hungteen.htlib.common.world.entity.DummyEntityImpl;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-02-11 23:16
 **/
public abstract class DummyEntityEvent extends Event {

    private final Level level;
    private final DummyEntityImpl dummyEntity;

    public DummyEntityEvent(Level level, DummyEntityImpl dummyEntity) {
        this.level = level;
        this.dummyEntity = dummyEntity;
    }

    public Level getLevel() {
        return level;
    }

    public DummyEntityImpl getDummyEntity() {
        return dummyEntity;
    }

    @Cancelable
    public static class DummyEntitySpawnEvent extends DummyEntityEvent {
        public DummyEntitySpawnEvent(Level level, DummyEntityImpl dummyEntity) {
            super(level, dummyEntity);
        }
    }
}
