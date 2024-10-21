package hungteen.htlib.common.event.events;

import hungteen.htlib.common.world.entity.DummyEntity;
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

    @Cancelable
    public static class DummyEntitySpawnEvent extends DummyEntityEvent {
        public DummyEntitySpawnEvent(Level level, DummyEntity dummyEntity) {
            super(level, dummyEntity);
        }
    }
}
