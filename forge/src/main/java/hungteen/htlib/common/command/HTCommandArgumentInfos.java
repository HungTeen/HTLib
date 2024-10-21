package hungteen.htlib.common.command;

import hungteen.htlib.HTLib;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-01 19:40
 **/
public class HTCommandArgumentInfos {

    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, HTLib.MOD_ID);

    public static final RegistryObject<ArgumentTypeInfo<?, ?>> DUMMY_ENTITY = ARGUMENT_TYPES.register("dummy_entity", () -> {
        return ArgumentTypeInfos.registerByClass(DummyEntityArgument.class, SingletonArgumentInfo.contextFree(DummyEntityArgument::id));
    });

    public static void register(IEventBus event){
        ARGUMENT_TYPES.register(event);
    }
}
