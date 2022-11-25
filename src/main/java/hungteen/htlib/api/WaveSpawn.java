package hungteen.htlib.api;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 */
public abstract class WaveSpawn {

    /**
     * Get the type of upcoming spawn entity.
     * @return entity type.
     */
    public abstract EntityType<?> getSpawnType();

    /**
     * Get the addition compound tag of the spawn entity.
     * @return compound tag.
     */
    public abstract CompoundTag getSpawnNBT();

    /**
     * Get the method to place the upcoming spawn entity.
     * @return placement method.
     */
    public abstract SpawnPlacement getSpawnPlacement();

    /**
     * Get the type of spawn.
     * @return wave type.
     */
    protected abstract IWaveSpawnType<?> getType();

    public interface IWaveSpawnType<P extends WaveSpawn> {

        /**
         * Get the method to codec spawn.
         * @return codec method.
         */
        Codec<P> codec();

    }
}
