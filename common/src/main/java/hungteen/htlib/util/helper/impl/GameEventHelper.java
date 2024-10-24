package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTVanillaRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/9 20:19
 */
public interface GameEventHelper extends HTVanillaRegistryHelper<GameEvent> {

    GameEventHelper HELPER = () -> BuiltInRegistries.GAME_EVENT;

    /* Common Methods */

    static HTVanillaRegistryHelper<GameEvent> get() {
        return HELPER;
    }

}
