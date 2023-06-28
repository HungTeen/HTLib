package hungteen.htlib.common.impl.position;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.api.interfaces.raid.IPositionComponent;
import hungteen.htlib.api.interfaces.raid.IPositionType;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.util.helper.HTLibHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 15:51
 */
public class HTPositionTypes {

    private static final HTSimpleRegistry<IPositionType<?>> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("position_type"));

    public static final IPositionType<CenterAreaPosition> CENTER_AREA = register(new PositionType<>("center_area",  CenterAreaPosition.CODEC));
    public static final IPositionType<AbsoluteAreaPosition> ABSOLUTE_AREA = register(new PositionType<>("absolute_area",  AbsoluteAreaPosition.CODEC));

    public static <T extends IPositionComponent> IPositionType<T> register(IPositionType<T> type){
        return registry().register(type);
    }

    public static IHTSimpleRegistry<IPositionType<?>> registry(){
        return TYPES;
    }

    record PositionType<P extends IPositionComponent>(String name, Codec<P> codec) implements IPositionType<P> {

        @Override
        public String getName() {
            return name();
        }

        @Override
        public String getModID() {
            return HTLibHelper.get().getModID();
        }
    }

}
