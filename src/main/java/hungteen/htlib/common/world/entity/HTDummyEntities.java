package hungteen.htlib.common.world.entity;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.common.world.raid.DefaultRaid;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 23:09
 **/
public class HTDummyEntities {

    private static final HTSimpleRegistry<DummyEntityType<?>> DUMMY_ENTITY_TYPES = HTRegistryManager.create(HTLib.prefix("dummy_entities"));

    public static final DummyEntityType<DefaultRaid> CUSTOM_RAID = new DummyEntityType<>(HTLib.prefix("custom_raid"), DefaultRaid::new);

    /**
     * {@link HTLib#HTLib()}
     */
    public static void registerStuffs(){
        List.of(CUSTOM_RAID).forEach(HTDummyEntities::register);
    }

    public static Optional<? extends DummyEntityType<?>> getEntityType(ResourceLocation location){
        return DUMMY_ENTITY_TYPES.getValue(location);
    }

    public static Stream<ResourceLocation> getIds(){
        return DUMMY_ENTITY_TYPES.getIds().stream();
    }

    public static Codec<DummyEntityType<?>> getCodec(){
        return DUMMY_ENTITY_TYPES.byNameCodec();
    }

    public static void register(DummyEntityType<?> entityType){
        DUMMY_ENTITY_TYPES.register(entityType);
    }

}
