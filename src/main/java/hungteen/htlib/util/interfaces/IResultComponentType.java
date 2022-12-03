package hungteen.htlib.util.interfaces;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleRegistry;
import hungteen.htlib.common.world.raid.IResultComponent;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/24 10:48
 */
public interface IResultComponentType<P extends IResultComponent> extends ISimpleRegistry {

    Codec<P> codec();

}