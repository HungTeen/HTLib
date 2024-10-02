package hungteen.htlib.common.impl.result;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.api.interfaces.raid.IResultComponent;
import hungteen.htlib.api.interfaces.raid.IResultType;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.util.helper.HTLibHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 15:47
 */
public class HTResultTypes {

    private static final HTSimpleRegistry<IResultType<?>> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("result_type"));

    public static final IResultType<ItemStackResult> ITEM_STACK = register(new ResultType<>("item_stack", ItemStackResult.CODEC));
    public static final IResultType<ChestResult> CHEST = register(new ResultType<>("chest", ChestResult.CODEC));
    public static final IResultType<EventResult> EVENT = register(new ResultType<>("event", EventResult.CODEC));
    public static final IResultType<FunctionResult> FUNCTION = register(new ResultType<>("function", FunctionResult.CODEC));

    public static <T extends IResultComponent> IResultType<T> register(IResultType<T> type){
        return registry().register(type);
    }

    public static IHTSimpleRegistry<IResultType<?>> registry(){
        return TYPES;
    }

    record ResultType<P extends IResultComponent>(String name, Codec<P> codec) implements IResultType<P> {

        @Override
        public String getName() {
            return name();
        }

        @Override
        public String getModID() {
            return HTLib.MOD_ID;
        }
    }

}
