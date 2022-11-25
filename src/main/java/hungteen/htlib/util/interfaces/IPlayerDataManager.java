package hungteen.htlib.util.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 21:12
 **/
public interface IPlayerDataManager {

    /**
     * Tick.
     */
    default void tick(){

    }

    void cloneFromExistingPlayerData(IPlayerDataManager data, boolean died);

    void syncToClient();

    /**
     * Save Data.
     */
    CompoundTag saveToNBT();

    /**
     * Load Data.
     */
    void loadFromNBT(CompoundTag tag);

    Player getPlayer();
}
