package hungteen.htlib.impl.spawn;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.world.raid.SpawnComponent;
import hungteen.htlib.util.interfaces.ISpawnComponentType;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;

import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 18:57
 **/
public class HTWaveSpawns {

    public static final HTSimpleRegistry<ISpawnComponentType<?>> SPAWN_TYPES = HTRegistryManager.create(HTLib.prefix("spawn_type"));
    public static final HTCodecRegistry<SpawnComponent> SPAWNS = HTRegistryManager.create(SpawnComponent.class, "wave_spawns", HTWaveSpawns::getCodec);

    /* Spawn types */

    public static final ISpawnComponentType<OnceWaveSpawn> ONCE_SPAWN_TYPE = new DefaultWaveSpawn<>("once_spawn",  OnceWaveSpawn.CODEC);
    public static final ISpawnComponentType<DurationWaveSpawn> DURATION_SPAWN_TYPE = new DefaultWaveSpawn<>("duration_spawn",  DurationWaveSpawn.CODEC);

    /* Spawns */

//    public static final HTRegistryHolder<SpawnPlacement> DEFAULT = SPAWNS.innerRegister(
//            HTLib.prefix("default"), new CenterAreaPlacement(
//                    Vec3.ZERO, 0, 1, true, 0, true
//            )
//    );

    /**
     * {@link HTLib#HTLib()}
     */
    public static void registerStuffs(){
        List.of(ONCE_SPAWN_TYPE, DURATION_SPAWN_TYPE).forEach(HTWaveSpawns::registerSpawnType);
    }

    public static void registerSpawnType(ISpawnComponentType<?> type){
        SPAWN_TYPES.register(type);
    }

    public static Codec<SpawnComponent> getCodec(){
        return SPAWN_TYPES.byNameCodec().dispatch(SpawnComponent::getType, ISpawnComponentType::codec);
    }

    protected record DefaultWaveSpawn<P extends SpawnComponent>(String name, Codec<P> codec) implements ISpawnComponentType<P> {

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getModID() {
            return HTLib.MOD_ID;
        }
    }
}
