package hungteen.htlib.common.world.entity;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 23:09
 **/
public interface HTLibDummyEntities {

    HTCustomRegistry<DummyEntityType<?>> TYPES = HTRegistryManager.custom(HTLibHelper.prefix("dummy_entity"));

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
        return registry().register(type.getLocation(), type);
    }

    static HTCustomRegistry<DummyEntityType<?>> registry(){
        return TYPES;
    }
}
