package hungteen.htlib.common.impl.position;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.raid.PositionComponent;
import hungteen.htlib.api.interfaces.raid.PositionType;
import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.HTLibHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 15:51
 */
public interface HTLibPositionTypes {

     HTSimpleRegistry<PositionType<?>> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("position_type"));

    PositionType<CenterAreaPosition> CENTER_AREA = register("center_area",  CenterAreaPosition.CODEC);
    PositionType<AbsoluteAreaPosition> ABSOLUTE_AREA = register("absolute_area",  AbsoluteAreaPosition.CODEC);

    static <T extends PositionComponent> PositionType<T> register(PositionType<T> type){
        return registry().register(type);
    }

    private static <T extends PositionComponent> PositionType<T> register(String name, Codec<T> codec){
        return register(new PositionTypeImpl<>(name, codec));
    }

    static HTSimpleRegistry<PositionType<?>> registry(){
        return TYPES;
    }

    record PositionTypeImpl<P extends PositionComponent>(String name, Codec<P> codec) implements PositionType<P> {

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
