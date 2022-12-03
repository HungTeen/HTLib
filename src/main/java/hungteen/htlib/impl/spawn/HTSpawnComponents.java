package hungteen.htlib.impl.spawn;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.common.world.raid.PlaceComponent;
import hungteen.htlib.common.world.raid.ISpawnComponent;
import hungteen.htlib.util.interfaces.ISpawnComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 18:57
 **/
public class HTSpawnComponents {

    public static final HTSimpleRegistry<ISpawnComponentType<?>> SPAWN_TYPES = HTRegistryManager.create(HTLib.prefix("spawn_type"));
    public static final HTCodecRegistry<ISpawnComponent> SPAWNS = HTRegistryManager.create(ISpawnComponent.class, "custom_raid/spawns", HTSpawnComponents::getCodec);

    /* Spawn types */

    public static final ISpawnComponentType<OnceSpawn> ONCE_SPAWN_TYPE = new DefaultWaveSpawn<>("once_spawn",  OnceSpawn.CODEC);
    public static final ISpawnComponentType<DurationSpawn> DURATION_SPAWN_TYPE = new DefaultWaveSpawn<>("duration_spawn",  DurationSpawn.CODEC);

    /* Spawns */

//    public static final HTRegistryHolder<SpawnPlacement> DEFAULT = SPAWNS.innerRegister(
//            HTLib.prefix("default"), new CenterAreaPlacement(
//                    Vec3.ZERO, 0, 1, true, 0, true
//            )
//    );

    /**
     * {@link HTLib#setUp(FMLCommonSetupEvent)}
     */
    public static void registerStuffs(){
        List.of(ONCE_SPAWN_TYPE, DURATION_SPAWN_TYPE).forEach(HTSpawnComponents::registerSpawnType);
    }

    public static void registerSpawnType(ISpawnComponentType<?> type){
        SPAWN_TYPES.register(type);
    }

    public static Codec<ISpawnComponent> getCodec(){
        return SPAWN_TYPES.byNameCodec().dispatch(ISpawnComponent::getType, ISpawnComponentType::codec);
    }

    public static SpawnSettingBuilder builder(){
        return new SpawnSettingBuilder();
    }

    protected record DefaultWaveSpawn<P extends ISpawnComponent>(String name, Codec<P> codec) implements ISpawnComponentType<P> {

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getModID() {
            return HTLib.MOD_ID;
        }
    }

    public static class SpawnSettingBuilder {

        private EntityType<?> entityType = EntityType.PIG;
        private CompoundTag nbt = new CompoundTag();
        private boolean enableDefaultSpawn = true;
        private PlaceComponent placeComponent = null;

        public BaseSpawn.SpawnSettings build() {
            return new BaseSpawn.SpawnSettings(entityType, nbt, enableDefaultSpawn, Optional.ofNullable(placeComponent));
        }

        public SpawnSettingBuilder entityType(EntityType<?> type) {
            this.entityType = type;
            return this;
        }

        public SpawnSettingBuilder nbt(CompoundTag tag){
            this.nbt = tag;
            return this;
        }

        public SpawnSettingBuilder enableDefaultSpawn(boolean flag){
            this.enableDefaultSpawn = flag;
            return this;
        }

        public SpawnSettingBuilder placement(PlaceComponent placeComponent){
            this.placeComponent = placeComponent;
            return this;
        }
    }
}
