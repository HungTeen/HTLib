package hungteen.htlib.common.impl.position;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.raid.PositionComponent;
import hungteen.htlib.api.interfaces.raid.PositionType;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.Vec3;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:37
 */
public interface HTLibPositionComponents {

    HTCodecRegistry<PositionComponent> PLACEMENTS = HTRegistryManager.codec(HTLibHelper.prefix("position"), HTLibPositionComponents::getDirectCodec);

    PositionComponent DEFAULT = new CenterAreaPosition(
            Vec3.ZERO, 0, 1, true, 0, true
    );

    ResourceKey<PositionComponent> TEST = create("test");
    ResourceKey<PositionComponent> COMMON = create("common");

    static void register(BootstrapContext<PositionComponent> context) {
        context.register(TEST, new CenterAreaPosition(
                Vec3.ZERO, 0, 1, true, 0, true
        ));
        context.register(COMMON, new CenterAreaPosition(
                Vec3.ZERO, 0, 10, true, 0, false
        ));
    }

    static Codec<PositionComponent> getDirectCodec() {
        return HTLibPositionTypes.registry().byNameCodec().dispatch(PositionComponent::getType, PositionType::codec);
    }

    static Codec<Holder<PositionComponent>> getCodec() {
        return registry().getHolderCodec(getDirectCodec());
    }

    static ResourceKey<PositionComponent> create(String name) {
        return registry().createKey(HTLibHelper.prefix(name));
    }

    static HTCodecRegistry<PositionComponent> registry() {
        return PLACEMENTS;
    }

}
