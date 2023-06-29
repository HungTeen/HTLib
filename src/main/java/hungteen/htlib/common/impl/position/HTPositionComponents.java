package hungteen.htlib.common.impl.position;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.api.interfaces.raid.IPositionComponent;
import hungteen.htlib.api.interfaces.raid.IPositionType;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.Vec3;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:37
 */
public class HTPositionComponents {

    public static final HTCodecRegistry<IPositionComponent> PLACEMENTS = HTRegistryManager.create(HTLibHelper.prefix("position"), HTPositionComponents::getDirectCodec, HTPositionComponents::getDirectCodec);

    public static final IPositionComponent DEFAULT = new CenterAreaPosition(
            Vec3.ZERO, 0, 1, true, 0, true
    );
    public static final ResourceKey<IPositionComponent> TEST = create("test");

    public static void register(BootstapContext<IPositionComponent> context) {
        context.register(TEST, new CenterAreaPosition(
                Vec3.ZERO, 0, 1, true, 0, true
        ));
    }

    public static Codec<IPositionComponent> getDirectCodec() {
        return HTPositionTypes.registry().byNameCodec().dispatch(IPositionComponent::getType, IPositionType::codec);
    }

    public static Codec<Holder<IPositionComponent>> getCodec() {
        return registry().getHolderCodec(getDirectCodec());
    }

    public static ResourceKey<IPositionComponent> create(String name) {
        return registry().createKey(HTLibHelper.prefix(name));
    }

    public static IHTCodecRegistry<IPositionComponent> registry() {
        return PLACEMENTS;
    }

}
