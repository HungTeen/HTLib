package hungteen.htlib.common.impl.result;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTRegistry;
import hungteen.htlib.api.interfaces.raid.IResultComponent;
import hungteen.htlib.api.interfaces.raid.IResultType;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.nbt.CompoundTag;
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
public interface HTResultComponents {

    HTCodecRegistry<IResultComponent> RESULTS = HTRegistryManager.create(HTLibHelper.prefix("result"), HTResultComponents::getDirectCodec);

    ResourceKey<IResultComponent> TEST = create("test");
    ResourceKey<IResultComponent> COMMON_FUNCTION = create("common_function");
    ResourceKey<IResultComponent> COMMAND_FUNCTION = create("command_function");

    static void register(BootstapContext<IResultComponent> context) {
        context.register(TEST, new ItemStackResult(
                true, false,
                List.of(new ItemStack(Items.ACACIA_BOAT, 3, new CompoundTag()))
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

    static Codec<IResultComponent> getDirectCodec() {
        return HTResultTypes.registry().byNameCodec().dispatch(IResultComponent::getType, IResultType::codec);
    }

    static Codec<Holder<IResultComponent>> getCodec() {
        return RegistryFileCodec.create(registry().getRegistryKey(), getDirectCodec());
    }

    static ResourceKey<IResultComponent> create(String name) {
        return registry().createKey(HTLibHelper.prefix(name));
    }

    static IHTRegistry<IResultComponent> registry() {
        return RESULTS;
    }

}
