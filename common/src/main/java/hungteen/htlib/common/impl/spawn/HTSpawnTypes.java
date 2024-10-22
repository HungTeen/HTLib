package hungteen.htlib.common.impl.spawn;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.raid.ISpawnComponent;
import hungteen.htlib.api.interfaces.raid.SpawnType;
import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.HTLibHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 16:05
 */
public interface HTSpawnTypes {

     HTSimpleRegistry<SpawnType<?>> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("spawn_type"));

    SpawnType<OnceSpawn> ONCE = register(new SpawnTypeImpl<>("once",  OnceSpawn.CODEC));
    SpawnType<DurationSpawn> DURATION = register(new SpawnTypeImpl<>("duration",  DurationSpawn.CODEC));

    static <T extends ISpawnComponent> SpawnType<T> register(SpawnType<T> type){
        return registry().register(type);
    }

    static HTSimpleRegistry<SpawnType<?>> registry(){
        return TYPES;
    }

    record SpawnTypeImpl<P extends ISpawnComponent>(String name, Codec<P> codec) implements SpawnType<P> {

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
