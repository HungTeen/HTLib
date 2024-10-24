package hungteen.htlib.common.impl.result;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.raid.ResultComponent;
import hungteen.htlib.api.raid.ResultType;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-04 09:47
 **/
public interface HTLibResultComponents {

    HTCodecRegistry<ResultComponent> RESULTS = HTRegistryManager.codec(HTLibHelper.prefix("result"), HTLibResultComponents::getDirectCodec);

    ResourceKey<ResultComponent> TEST = create("test");
    ResourceKey<ResultComponent> COMMON_FUNCTION = create("common_function");
    ResourceKey<ResultComponent> COMMAND_FUNCTION = create("command_function");

    static void register(BootstrapContext<ResultComponent> context) {
        context.register(TEST, new ItemStackResult(
                true, false,
                List.of(new ItemStack(Items.ACACIA_BOAT, 3))
        ));
        context.register(COMMON_FUNCTION, new FunctionResult(
                List.of(),
                List.of(HTLibHelper.get().prefix("test")),
                List.of()
        ));
        context.register(COMMAND_FUNCTION, new CommandResult(
                Optional.empty(), Optional.of("give @s apple 1"), Optional.empty()
        ));
    }

    static Codec<ResultComponent> getDirectCodec() {
        return HTLibResultTypes.registry().byNameCodec().dispatch(ResultComponent::getType, ResultType::codec);
    }

    static Codec<Holder<ResultComponent>> getCodec() {
        return RegistryFileCodec.create(registry().getRegistryKey(), getDirectCodec());
    }

    static ResourceKey<ResultComponent> create(String name) {
        return registry().createKey(HTLibHelper.prefix(name));
    }

    static HTCodecRegistry<ResultComponent> registry() {
        return RESULTS;
    }

}
