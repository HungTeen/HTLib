package hungteen.htlib.common.impl.result;

import com.mojang.serialization.MapCodec;
import hungteen.htlib.api.raid.ResultComponent;
import hungteen.htlib.api.raid.ResultType;
import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 15:47
 */
public interface HTLibResultTypes {

    HTSimpleRegistry<ResultType<?>> TYPES = HTRegistryManager.simple(HTLibHelper.prefix("result_type"));

    ResultType<ItemStackResult> ITEM_STACK = register(new ResultTypeImpl<>("item_stack", ItemStackResult.CODEC));
    ResultType<ChestResult> CHEST = register(new ResultTypeImpl<>("chest", ChestResult.CODEC));
//    ResultType<EventResult> EVENT = register(new ResultTypeImpl<>("event", EventResult.CODEC));
    ResultType<FunctionResult> FUNCTION = register(new ResultTypeImpl<>("function", FunctionResult.CODEC));
    ResultType<CommandResult> COMMAND = register(new ResultTypeImpl<>("command", CommandResult.CODEC));

    static <T extends ResultComponent> ResultType<T> register(ResultType<T> type){
        return registry().register(type);
    }

    static HTSimpleRegistry<ResultType<?>> registry(){
        return TYPES;
    }

    record ResultTypeImpl<P extends ResultComponent>(String name, MapCodec<P> codec) implements ResultType<P> {

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
