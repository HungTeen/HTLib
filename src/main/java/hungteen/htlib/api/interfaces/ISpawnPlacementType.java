package hungteen.htlib.api.interfaces;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.SpawnPlacement;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/24 10:48
 */
public interface ISpawnPlacementType<P extends SpawnPlacement> extends ISimpleRegistry {

    Codec<P> codec();

}