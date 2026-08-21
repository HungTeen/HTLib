package hungteen.htlib.common.registry;

import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.api.interfaces.ISimpleEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

/**
 * HTCommonRegistry 的"简单条目"变体。 <br>
 *
 * <p>要求元素实现 {@link ISimpleEntry}（自带 getName / getModID / getLocation），
 * 因此可以直接用对象本身注册（{@code register(type)}），省去手动指定
 * ResourceLocation 的麻烦，其余行为与 {@link HTCommonRegistry} 完全一致：
 * 生命周期同为 NewRegistryEvent → RegisterEvent → FMLCommonSetupEvent。</p>
 *
 * <p>适用场景：轻量类型/选项对象的注册，如 HTLib 的
 * {@link hungteen.htlib.common.impl.BoatTypes}（船类型）
 * 与各组件体系（raid / wave / spawn / result）的 Type。</p>
 *
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 14:36
 */
public final class HTSimpleRegistry<T extends ISimpleEntry> extends HTCommonRegistry<T> implements IHTSimpleRegistry<T> {

    HTSimpleRegistry(ResourceLocation registryName) {
        super(registryName);
    }

    HTSimpleRegistry(ResourceLocation registryName, Supplier<RegistryBuilder<T>> builderSup) {
        super(registryName, builderSup);
    }

}
