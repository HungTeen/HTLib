package hungteen.htlib.common.registry;

import hungteen.htlib.api.registry.SimpleEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 14:36
 */
public final class HTSimpleRegistry<T extends SimpleEntry> extends HTCommonRegistry<T> implements hungteen.htlib.api.interfaces.HTSimpleRegistry<T> {

    HTSimpleRegistry(ResourceLocation registryName) {
        super(registryName);
    }

    HTSimpleRegistry(ResourceLocation registryName, Supplier<RegistryBuilder<T>> builderSup) {
        super(registryName, builderSup);
    }

}
