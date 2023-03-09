package hungteen.htlib.util.helper.registry;

import net.minecraft.core.Registry;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/9 20:19
 */
public class GameEventHelper extends RegistryHelper<GameEvent> {

    private static final GameEventHelper HELPER = new GameEventHelper();

    /* Common Methods */

    public static GameEventHelper get() {
        return HELPER;
    }

    @Override
    public Registry<GameEvent> getRegistry() {
        return Registry.GAME_EVENT;
    }
}
