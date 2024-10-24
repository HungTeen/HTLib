package hungteen.htlib.api.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * 玩家能力系统的数据管理接口。
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 21:12
 **/
public interface IPlayerDataManager {

    /**
     * Tick on server side.
     */
    default void tick(){
    }

    /**
     * @param data old player data.
     * @param died false if player return from the end.
     */
    void cloneFromExistingPlayerData(IPlayerDataManager data, boolean died);

    /**
     * Sync data from server to client side.
     */
    void syncToClient();

    /**
     * Save Data.
     * @return serialized nbt.
     */
    CompoundTag saveToNBT();

    /**
     * Load Data.
     * @param tag deserialized nbt.
     */
    void loadFromNBT(CompoundTag tag);

    /**
     * @return manager owner.
     */
    Player getPlayer();
}
