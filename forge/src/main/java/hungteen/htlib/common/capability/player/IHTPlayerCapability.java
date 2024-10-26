package hungteen.htlib.common.capability.player;

import hungteen.htlib.platform.IPlayerDataManager;
import net.minecraft.world.entity.player.Player;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 15:23
 **/
public interface IHTPlayerCapability<T extends IPlayerDataManager> {

    /**
     * Initializes with player.
     */
    void init(Player player);

    /**
     * Get PlayerDataManager.
     */
    T get();
}
