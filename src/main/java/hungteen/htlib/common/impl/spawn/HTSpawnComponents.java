package hungteen.htlib.common.impl.spawn;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.raid.IPositionComponent;
import hungteen.htlib.api.interfaces.raid.ISpawnComponent;
import hungteen.htlib.api.interfaces.raid.ISpawnType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 18:57
 **/
public class HTSpawnComponents {

//    public static final HTCodecRegistry<ISpawnComponent> SPAWNS = HTRegistryManager.create(ISpawnComponent.class, "custom_raid/spawns", HTSpawnComponents::getCodec, true);


//    public static final HTRegistryHolder<SpawnPlacement> DEFAULT = SPAWNS.innerRegister(
//            HTLib.prefix("default"), new CenterAreaPlacement(
//                    Vec3.ZERO, 0, 1, true, 0, true
//            )
//    );


    public static Codec<ISpawnComponent> getCodec(){
        return HTSpawnTypes.registry().byNameCodec().dispatch(ISpawnComponent::getType, ISpawnType::codec);
    }

    public static SpawnSettingBuilder builder(){
        return new SpawnSettingBuilder();
    }


    public static class SpawnSettingBuilder {

        private EntityType<?> entityType = EntityType.PIG;
        private CompoundTag nbt = new CompoundTag();
        private boolean enableDefaultSpawn = true;
        private IPositionComponent placeComponent = null;

        public SpawnComponent.SpawnSettings build() {
            return new SpawnComponent.SpawnSettings(entityType, nbt, enableDefaultSpawn, Optional.ofNullable(placeComponent));
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

        public SpawnSettingBuilder placement(IPositionComponent placeComponent){
            this.placeComponent = placeComponent;
            return this;
        }
    }
}
