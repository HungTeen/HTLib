package hungteen.htlib.util.interfaces;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleRegistry;
import hungteen.htlib.common.world.raid.PlaceComponent;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/24 10:48
 */
public interface IPlaceComponentType<P extends PlaceComponent> extends ISimpleRegistry {

    Codec<P> codec();

}