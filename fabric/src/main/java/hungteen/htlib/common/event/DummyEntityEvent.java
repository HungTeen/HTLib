package hungteen.htlib.common.event;

import hungteen.htlib.common.world.entity.DummyEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.Level;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-02-11 23:16
 **/
public interface DummyEntityEvent {

    Event<DummyEntityCallback> DUMMY_SPAWN = EventFactory.createArrayBacked(DummyEntityCallback.class,
            (listeners) -> (level, dummyEntity) -> {
                for (DummyEntityCallback listener : listeners) {
                    boolean result = listener.handle(level, dummyEntity);

                    if (result) {
                        return true;
                    }
                }
                return false;
            });

    interface DummyEntityCallback {

        boolean handle(Level level, DummyEntity dummyEntity);
    }
}
