package hungteen.htlib.common;

import hungteen.htlib.common.world.entity.DummyEntityManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;

/**
 * Subscribe events related to {@link hungteen.htlib.common.world.entity.DummyEntityManager}.
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/25 21:58
 **/
public class DummyEntityHandler {

    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel) {
            DummyEntityManager.get((ServerLevel) event.level).tick();
        }
    }
}
