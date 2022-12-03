package hungteen.htlib.util.interfaces;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleRegistry;
import hungteen.htlib.common.world.raid.ISpawnComponent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 16:56
 **/
public interface ISpawnComponentType<P extends ISpawnComponent> extends ISimpleRegistry {

    /**
     * Get the method to codec getSpawnEntities.
     * @return codec method.
     */
    Codec<P> codec();

}
