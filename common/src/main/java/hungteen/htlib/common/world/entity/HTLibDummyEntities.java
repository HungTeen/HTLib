package hungteen.htlib.common.world.entity;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.registry.HTSimpleRegistryImpl;
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
public interface HTLibDummyEntities {

    HTSimpleRegistryImpl<DummyEntityType<?>> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("dummy_entity"));

    DummyEntityType<DefaultRaid> DEFAULT_RAID = register(new DummyEntityType<>(HTLibHelper.prefix("default_raid"), DefaultRaid::new));

    static Optional<? extends DummyEntityType<?>> getEntityType(ResourceLocation location){
        return registry().getValue(location);
    }

    static Stream<ResourceLocation> getIds(){
        return registry().getKeys().stream();
    }

    static Codec<DummyEntityType<?>> getCodec(){
        return registry().byNameCodec();
    }

    static <T extends DummyEntity> DummyEntityType<T> register(DummyEntityType<T> type){
        return registry().register(type);
    }

    static HTSimpleRegistry<DummyEntityType<?>> registry(){
        return TYPES;
    }
}
