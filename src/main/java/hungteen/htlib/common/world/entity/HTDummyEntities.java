package hungteen.htlib.common.world.entity;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.common.world.raid.DefaultRaid;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 23:09
 **/
public class HTDummyEntities {

    private static final HTSimpleRegistry<DummyEntityType<?>> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("dummy_entity"));

    public static final DummyEntityType<DefaultRaid> DEFAULT_RAID = register(new DummyEntityType<>(HTLibHelper.prefix("default_raid"), DefaultRaid::new));

    public static Optional<? extends DummyEntityType<?>> getEntityType(ResourceLocation location){
        return registry().getValue(location);
    }

    public static Stream<ResourceLocation> getIds(){
        return registry().getKeys().stream();
    }

    public static Codec<DummyEntityType<?>> getCodec(){
        return registry().byNameCodec();
    }

    public static <T extends DummyEntity> DummyEntityType<T> register(DummyEntityType<T> type){
        return registry().register(type);
    }

    public static IHTSimpleRegistry<DummyEntityType<?>> registry(){
        return TYPES;
    }
}
