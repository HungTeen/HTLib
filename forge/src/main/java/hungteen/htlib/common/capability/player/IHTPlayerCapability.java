package hungteen.htlib.common.capability.player;

import hungteen.htlib.common.capability.HTPlayerData;
import net.minecraft.world.entity.player.Player;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 15:23
 **/
public interface IHTPlayerCapability<T extends HTPlayerData> {

    /**
     * Initializes with player.
     */
    void init(Player player);

    /**
     * Get PlayerDataManager.
     */
    T get();
}
