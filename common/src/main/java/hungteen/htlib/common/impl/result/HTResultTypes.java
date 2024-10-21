package hungteen.htlib.common.impl.result;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.HTSimpleRegistry;
import hungteen.htlib.api.interfaces.raid.IResultComponent;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.util.helper.HTLibHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 15:47
 */
public class HTResultTypes {

    private static final HTSimpleRegistry<hungteen.htlib.api.interfaces.raid.ResultType<?>> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("result_type"));

    public static final hungteen.htlib.api.interfaces.raid.ResultType<ItemStackResult> ITEM_STACK = register(new ResultType<>("item_stack", ItemStackResult.CODEC));
    public static final hungteen.htlib.api.interfaces.raid.ResultType<ChestResult> CHEST = register(new ResultType<>("chest", ChestResult.CODEC));
    public static final hungteen.htlib.api.interfaces.raid.ResultType<EventResult> EVENT = register(new ResultType<>("event", EventResult.CODEC));
    public static final hungteen.htlib.api.interfaces.raid.ResultType<FunctionResult> FUNCTION = register(new ResultType<>("function", FunctionResult.CODEC));
    public static final hungteen.htlib.api.interfaces.raid.ResultType<CommandResult> COMMAND = register(new ResultType<>("command", CommandResult.CODEC));

    public static <T extends IResultComponent> hungteen.htlib.api.interfaces.raid.ResultType<T> register(hungteen.htlib.api.interfaces.raid.ResultType<T> type){
        return registry().register(type);
    }

    public static HTSimpleRegistry<hungteen.htlib.api.interfaces.raid.ResultType<?>> registry(){
        return TYPES;
    }

    record ResultType<P extends IResultComponent>(String name, Codec<P> codec) implements hungteen.htlib.api.interfaces.raid.ResultType<P> {

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
