package hungteen.htlib.common.impl.spawn;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.HTSimpleRegistry;
import hungteen.htlib.api.interfaces.raid.ISpawnComponent;
import hungteen.htlib.api.interfaces.raid.SpawnType;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.util.helper.HTLibHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 16:05
 */
public class HTSpawnTypes {

    private static final HTSimpleRegistry<SpawnType<?>> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("spawn_type"));

    public static final SpawnType<OnceSpawn> ONCE = register(new WaveSpawn<>("once",  OnceSpawn.CODEC));
    public static final SpawnType<DurationSpawn> DURATION = register(new WaveSpawn<>("duration",  DurationSpawn.CODEC));

    public static <T extends ISpawnComponent> SpawnType<T> register(SpawnType<T> type){
        return registry().register(type);
    }

    public static HTSimpleRegistry<SpawnType<?>> registry(){
        return TYPES;
    }

    record WaveSpawn<P extends ISpawnComponent>(String name, Codec<P> codec) implements SpawnType<P> {

        @Override
        public String getName() {
            return name();
        }

        @Override
        public String getModID() {
            return HTLibHelper.get().getModID();
        }
    }
}
